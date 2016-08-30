package networking.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import sage.audio.IAudioManager;
import sage.networking.client.GameConnectionClient;
import sage.scene.Group;
import sage.scene.SceneNode;
import games.BattleTankGame;
import games.GameObjects.EnemyTurret;
import games.GameObjects.GhostAvatar;
import games.GameObjects.Missile;
import games.GameObjects.MyTank;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

public class MyClient extends GameConnectionClient{
	private BattleTankGame game;
	private UUID id;
	private Vector<Group> ghostAvatars;
	private int tankcolor, tanktype;
	private IAudioManager audioMgr;
	private ArrayList<EnemyTurret> enemyTurrets;
	
	public MyClient(InetAddress remAddr, int remPort, ProtocolType pType, BattleTankGame g)throws IOException{
		super(remAddr, remPort, pType);
		this.game = g;
		audioMgr = g.getAudioManager();
		enemyTurrets = new ArrayList<EnemyTurret>();
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<Group>();
	}
	protected void processPacket(Object msg){
		System.out.println("Client has processed this packet: " + msg);
		String message = (String)msg;
		String[] mTokens = message.split(",");
		if(mTokens[0].compareTo("join")==0){
			if(mTokens[1].compareTo("success")==0){
				game.setIsConnected(true);
				sendCreateMessage(game.getPlayerPosition(), game.getPlayerRotation(), game.getPlayerColor(), game.getPlayerTankType());
				System.out.println("Success was received");
			}
			if(mTokens[1].compareTo("failure")==0){
				game.setIsConnected(false);
				System.out.println("Fail was received");
			}
		}
		if(mTokens[0].compareTo("bye")==0){
			UUID ghostID = UUID.fromString(mTokens[1]);
			removeGhostAvatar(ghostID);
		}
		if(mTokens[0].compareTo("create")==0){
			UUID ghostID = UUID.fromString(mTokens[1]);	
			float x = Float.parseFloat(mTokens[2]);
			float y = Float.parseFloat(mTokens[3]);
			float z = Float.parseFloat(mTokens[4]);
			Vector3D ghostPosition = new Vector3D(x, y, z);
			float rotAmount = Float.parseFloat(mTokens[5]);
			tankcolor = (int)Float.parseFloat(mTokens[6]);
			tanktype = (int)Float.parseFloat(mTokens[7]);
			game.createGhostAvatar(ghostID, ghostPosition, rotAmount, tankcolor, tanktype);
		}
		if(mTokens[0].compareTo("wsds")==0){
//			System.out.println("MyCLient:wsds");
			UUID remID = UUID.fromString(mTokens[1]);
			Vector3D pos = game.getPlayerPosition();
			float rotAmount = game.getPlayerRotation();
			int tankcolor = game.getPlayerColor();
			int tanktype = game.getPlayerTankType();
			sendDetailsForMessage(remID, pos, rotAmount, tankcolor, tanktype);
		}
		if(mTokens[0].compareTo("dsms")==0){
//			System.out.println("MyCLient:dsms");
			UUID ghostID = UUID.fromString(mTokens[1]);
			float x = Float.parseFloat(mTokens[2]);
			float y = Float.parseFloat(mTokens[3]);
			float z = Float.parseFloat(mTokens[4]);
			Vector3D ghostPosition = new Vector3D(x, y, z);
			float rotAmount = Float.parseFloat(mTokens[5]);
			tankcolor = (int)Float.parseFloat(mTokens[6]);
			tanktype = (int)Float.parseFloat(mTokens[7]);
			game.createGhostAvatar(ghostID, ghostPosition, rotAmount, tankcolor, tanktype);
		}
		if(mTokens[0].compareTo("move")==0){
//			System.out.println("MyCLient:move");
			UUID ghostID = UUID.fromString(mTokens[1]);	
			float x = Float.parseFloat(mTokens[2]);
			float y = Float.parseFloat(mTokens[3]);
			float z = Float.parseFloat(mTokens[4]);
			Vector3D ghostPosition = new Vector3D(x, y, z);
			moveGhostAvatar(ghostID, ghostPosition);
		}
		if(mTokens[0].compareTo("rot")==0){
//			System.out.println("MyCLient:rot");
			UUID ghostID = UUID.fromString(mTokens[1]);
			float amount = Float.parseFloat(mTokens[2]);
			float x = Float.parseFloat(mTokens[3]);
			float y = Float.parseFloat(mTokens[4]);
			float z = Float.parseFloat(mTokens[5]);
			Vector3D ghostPosition = new Vector3D(x, y, z);
			rotateGhostAvatar(ghostID, amount, ghostPosition);
		}
		if(mTokens[0].compareTo("sky")==0){
//			System.out.println("MyCLient:sky");
			//UUID ghostID = UUID.fromString(mTokens[1]);
			Object view = mTokens[2];
			game.changeSky(view);
		}
		if(mTokens[0].compareTo("color")==0){
//			System.out.println("MyCLient:color");
			UUID ghostID = UUID.fromString(mTokens[1]);
			String color = mTokens[2];
			changeGhostAvatarColor(ghostID, color);
		}
		if(mTokens[0].compareTo("tnpc")==0){
//			System.out.println("MyCLient:tnpc");
			int turretID = Integer.parseInt(mTokens[1]); // This has already been decr 1 for indexing
			Point3D location = new Point3D();
			location.setX(Double.parseDouble((mTokens[2])));
			location.setY(Double.parseDouble((mTokens[3])));
			location.setZ(Double.parseDouble((mTokens[4])));
			float rot = Float.parseFloat(mTokens[5]);
			createEnemyTurrets(turretID, location, rot);
		}
		if(mTokens[0].compareTo("rnpc")==0){
//			System.out.println("MyCLient:rnpc");
			int turretID = Integer.parseInt(mTokens[1]);
			float rot = Float.parseFloat(mTokens[2]);
			rotateEnemyTurrets(turretID, rot);
		}
		if(mTokens[0].compareTo("nearnpc")==0){
//			System.out.println("MyCLient:nearnpc -- Avatar position: " + game.getPlayerPosition());
			double avaDist;
			int turretID ;
			Point3D location = new Point3D();
			location.setX(Double.parseDouble((mTokens[1])));
			location.setY(Double.parseDouble((mTokens[2])));
			location.setZ(Double.parseDouble((mTokens[3])));
			turretID = Integer.parseInt(mTokens[4]);
			if ((avaDist = Math.sqrt(Math.pow(location.getX()-game.getPlayerPosition().getX(), 2) + Math.pow(location.getZ()-game.getPlayerPosition().getZ(), 2))) < 50 ) {
//				System.out.println("Avatar is within distance of turret. Distance is: " + avaDist);
				sendAvatarNear(true, turretID);
			}
			else {
//				System.out.println("Avatar is NOT within distance of turret. Distance is: " + avaDist);
				sendAvatarNear(false, turretID);
			}
		}
		if(mTokens[0].compareTo("rotdir")==0){
//			System.out.println("MyCLient:rotdir");
			int turretID = Integer.parseInt(mTokens[1]);
			float npcX = Float.parseFloat((mTokens[2]));
			float npcZ = Float.parseFloat((mTokens[3]));
			float rotation = Float.parseFloat((mTokens[4]));
			double degrees = (float)Math.toDegrees(Math.atan2(game.getPlayerPosition().getZ() - npcZ, game.getPlayerPosition().getX() - npcX));
			if(degrees < 0) {
				degrees += 360;
			}
			if (degrees > rotation) {
				sendRotationMessage("dec", turretID, degrees);
//				System.out.println("Rotate message dec. The rotation of the turret is " + rotation + "degrees and the degrees between player and turret id#" + turretID + " is " +degrees);
			}
			else {
				sendRotationMessage("inc", turretID, degrees);
//				System.out.println("Rotate message inc. The rotation of the turret is " + rotation + "degrees and the degrees between player and turret id#" + turretID + " is " +degrees);
			}
		}
		if(mTokens[0].compareTo("line")==0){
//			System.out.println("MyCLient:line");
			boolean inSights = false;
			int turretID = Integer.parseInt(mTokens[4]);
			float rotation = Float.parseFloat((mTokens[1]));
			float npcX = Float.parseFloat((mTokens[2]));
			float npcZ = Float.parseFloat((mTokens[3]));
			double degrees = (float)Math.toDegrees(Math.atan2(game.getPlayerPosition().getZ() - npcZ, game.getPlayerPosition().getX() - npcX));
			if(degrees < 0) {
				degrees += 360;
			}
			if (Math.abs(degrees - rotation) < 1.5 ) {
				inSights = true;
				sendLineOfSightMessage(inSights, turretID);
//				System.out.println("Line of Sight true.");
			}
			else {
				inSights = false;
				sendLineOfSightMessage(inSights, turretID);
//				System.out.println("Line of Sight  false.");
			}
		}
		if(mTokens[0].compareTo("fire")==0){
//			System.out.println("MyCLient:fire");
			int turretID = Integer.parseInt(mTokens[1]);
			fireEnemyTurret(turretID);
		}
		if(mTokens[0].compareTo("gfire")==0){
			// For ghost avatar missiles
			UUID ghostID = UUID.fromString(mTokens[1]);
			fireGhostAvatar(ghostID);
		}
		if(mTokens[0].compareTo("rmav")==0){
			// For ghost avatar missiles
			UUID ghostID = UUID.fromString(mTokens[1]);
			removeGhostAvatar(ghostID);
		}
		if(mTokens[0].compareTo("win")==0){
			// For ghost avatar missiles
			int winningScore = Integer.parseInt((mTokens[1]));
			String winningPlayer = mTokens[2];
			displayWinner(winningScore, winningPlayer);
		}
		
	}
	private void displayWinner(int winningScore, String winningPlayer) {
		JPanel finalScore = new JPanel();
		JLabel player = new JLabel(winningPlayer);
		JLabel score = new JLabel(Integer.toString(winningScore));
		finalScore.add(player);
		finalScore.add(score);
		JOptionPane.showConfirmDialog(null, finalScore, "The Winner Is... ", JOptionPane.OK_OPTION);
	}
	private void fireGhostAvatar(UUID ghostID) {
		Missile missile = null;
		Iterable<SceneNode> it = game.getGameWorld(); // Get iterable collection
		Iterator<SceneNode> iter = it.iterator(); // Get iterator for collection
		 
		while(iter.hasNext()) { // Iterate through collection to update ghost avatar locations for 3d sound
			SceneNode node = (SceneNode) iter.next();
			if (node instanceof GhostAvatar) 
		 	{
		 		if(((GhostAvatar) node).getId().equals(ghostID)) {
//		 			missiles.add(new Missile((EnemyTurret)node));
//		 			missile = new Missile((GhostAvatar) node);
		 			missile = game.getMissileFromPool();
		 			missile.setbeingUsed(true, (GhostAvatar)node);
		 		}
		 	}
		}
		if (missile != null)
			game.addGameWorldObject(missile);
		
	}
	private void fireEnemyTurret(int turretID) {
		Missile missile = null;
		Iterable<SceneNode> it = game.getGameWorld(); // Get iterable collection
		Iterator<SceneNode> iter = it.iterator(); // Get iterator for collection
		 
		while(iter.hasNext()) { // Iterate through collection to update ghost avatar locations for 3d sound
			SceneNode node = (SceneNode) iter.next();
			if (node instanceof EnemyTurret) 
		 	{
		 		if(((EnemyTurret) node).getId() == turretID) {
		 			missile = game.getMissileFromPool();
		 			missile.setbeingUsed(true, (EnemyTurret)node);
//		 			System.out.println("TurretId " + turretID + " just fired a missile.");
		 		}
		 	}
		}
		if (missile != null) {
			game.addGameWorldObject(missile);
//			System.out.println("TurretId " + turretID + " just added a missile to gameworld");
		}
	}
	private void sendLineOfSightMessage(boolean b, int turretID) {
		String inSights;
		if(b) inSights = "true";
		else inSights = "false";
		String message = new String("line,"+ id.toString());
		message += "," + Integer.toString(turretID);
		message += "," + inSights; 
		try {
			sendPacket(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	private void rotateEnemyTurrets(int turretID, float rot) {
		Iterable<SceneNode> it = game.getGameWorld(); // Get iterable collection
		Iterator<SceneNode> iter = it.iterator(); // Get iterator for collection
		if (rot < 0) rot += 360; 
		while(iter.hasNext()) { // Iterate through collection to update ghost avatar locations for 3d sound
			SceneNode node = (SceneNode) iter.next();
			if (node instanceof EnemyTurret) 
		 	{
		 		if(((EnemyTurret) node).getId() == turretID) {
		 			((EnemyTurret) node).rotateCannon(rot);
		 		}
		 	}
		}			
	}
	private void sendRotationMessage(String rotDir, int turretID, double degrees) {
		String message = new String("rotdir,"+ id.toString());
		message += "," + rotDir; 
		message += "," + Integer.toString(turretID);
		message += "," + Double.toString(degrees);
		try {
			sendPacket(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void sendAvatarNear(boolean b, int turretID) {
		String isNear;
		if(b) isNear = "true";
		else isNear = "false";
		String message = new String("avnr,"+ id.toString());
		message += "," + Integer.toString(turretID);
		message += "," + isNear; 
		try {
			sendPacket(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void createEnemyTurrets(int turretID, Point3D loc, float rot) {
		System.out.println("createEnemyTurrets was entered for id " + turretID);
		EnemyTurret turret = new EnemyTurret(turretID, loc.getX(), loc.getY(), loc.getZ(), rot, audioMgr);
		game.addGameWorldObject(turret);
		enemyTurrets.add(turret);
	}
	public ArrayList<EnemyTurret> getEnemyTurrets() {
		return enemyTurrets;
	}
	public Vector<Group> getGhostAvatars() {
		return ghostAvatars;
	}
	private void changeGhostAvatarColor(UUID ghostID, Object color) {
		Iterable<SceneNode> it = game.getGameWorld(); // Get iterable collection
		 Iterator<SceneNode> iter = it.iterator(); // Get iterator for collection
		 
		 while(iter.hasNext()) { // Iterate through collection and check for collisions
			 SceneNode node = (SceneNode) iter.next();
			 if (node instanceof GhostAvatar) {
//				 System.out.println("THN-moveghostAvatar-node="+((GhostAvatar) node).getId()+", ghostID="+ghostID);
				 if (((GhostAvatar)node).getId() != null &&  ((GhostAvatar) node).getId().compareTo(ghostID)==0){					 
					 ((GhostAvatar) node).setShipColor(color);
				 }
			 }
		 }
		
	}
	private void rotateGhostAvatar(UUID ghostID, float amount, Vector3D ghostPosition) {
		 Iterable<SceneNode> it = game.getGameWorld(); // Get iterable collection
		 Iterator<SceneNode> iter = it.iterator(); // Get iterator for collection
		 
		 while(iter.hasNext()) { // Iterate through collection and check for collisions
			 SceneNode node = (SceneNode) iter.next();
			 if (node instanceof GhostAvatar) {
//				 System.out.println("THN-moveghostAvatar-node="+((GhostAvatar) node).getId()+", ghostID="+ghostID);
				 if (((GhostAvatar)node).getId() != null &&  ((GhostAvatar) node).getId().compareTo(ghostID)==0){
					 node.rotate(amount,  ghostPosition);
				 }
			 }
		 }		
	}
	
	public void addGhostAvatar(Group ghost) 
	{
		ghostAvatars.add(ghost);
	}
	public void sendCreateMessage(Vector3D pos, float rotAmount, int color, int type){
		try{
			String message = new String("create,"+ id.toString());
			message += "," + pos.getX()+","+pos.getY()+","+pos.getZ();
			message += "," + rotAmount;
			message += "," + color;
			message += "," + type;
			System.out.println("MyClient-sendCreateMessage="+ message);
			sendPacket(message);
//			System.out.println("Got into sendCreateMessage");
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public void sendJoinMessage(){
		try{
			sendPacket(new String("join,"+ id.toString()));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public void sendByeMessage(){
		String msg = "bye," + id.toString();
		try {
			sendPacket(msg);
			System.out.println(msg);
		} catch (IOException e) {
			System.out.println("Bye packet not sent");
			e.printStackTrace();
		}
	}
	public void sendDetailsForMessage(UUID remid, Vector3D pos, float rotAmount, int tankcolor, int tanktype){
		try{
			String message = new String("dsfr," + id.toString() + "," + remid.toString());
			message += "," + pos.getX()+","+pos.getY()+","+pos.getZ();
			message += "," + rotAmount;
			message += "," + tankcolor;
			message += "," + tanktype;
			sendPacket(message);
//			System.out.println("Got into sendDetailsForMessage");
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public void sendMoveMessage(Vector3D pos){
		String msg = "move," + id.toString() +"," + pos.getX()+","+pos.getY()+","+pos.getZ();
		try {
			sendPacket(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendRotateMessage(float amount, Vector3D pos){
		String msg = "rot," + id.toString() +"," + amount + "," + pos.getX()+","+pos.getY()+","+pos.getZ();
		try {
			sendPacket(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public void removeGhostAvatar(UUID ghostID) {
		 Iterable<SceneNode> it = game.getGameWorld(); // Get iterable collection
		 Iterator<SceneNode> iter = it.iterator(); // Get iterator for collection
		 SceneNode delete = null;
		 
		 while(iter.hasNext()) { // Iterate through collection and check for collisions
			 SceneNode node = (SceneNode) iter.next();
			 if (node instanceof GhostAvatar) {
//				 System.out.println("THN-moveghostAvatar-node="+((MyShip) node).getId()+", ghostID="+ghostID);
				 if (((GhostAvatar)node).getId() != null &&  ((GhostAvatar) node).getId().compareTo(ghostID)==0){					 
					 delete = node;
				 }
			 }
		 }
		 game.removeGameWorldObject(delete);
	}
	public boolean isGhostsEmpty() 
	{
		if(ghostAvatars.isEmpty()) return true;
		else return false;
	}
	public void moveGhostAvatar(UUID ghostID, Vector3D ghostPosition) {
		 Iterable<SceneNode> it = game.getGameWorld(); // Get iterable collection
		 Iterator<SceneNode> iter = it.iterator(); // Get iterator for collection
		 
		 while(iter.hasNext()) { // Iterate through collection and check for collisions
			 SceneNode node = (SceneNode) iter.next();
//			 System.out.println("MyClient-moveGhostAvatar-node="+node);
			 if (node instanceof GhostAvatar) {
//				 System.out.println("THN-moveghostAvatar-node="+((GhostAvatar) node).getId()+", ghostID="+ghostID);
				 if (((GhostAvatar)node).getId() != null &&  ((GhostAvatar) node).getId().compareTo(ghostID)==0){
//					 System.out.println("THN-moveghostAvatar-ghostID="+ghostID);
					 Matrix3D nodeM = new Matrix3D();
					 nodeM.setCol(3, ghostPosition);					 
//					 node.setLocalTranslation(nodeM);
					 node.translate((float)ghostPosition.getX(),(float)ghostPosition.getY(),(float)ghostPosition.getZ());
//					 System.out.println("THN-moveghostAvatar-nodeM="+nodeM);
//					 System.out.println("THN-moveghostAvatar-node.getWorldTranslation="+node.getWorldTranslation());
//					 System.out.println("THN-moveghostAvatar-node.getLocalTranslation="+node.getLocalTranslation());
				 }
			 }
		 }
	}
	public void sendChangeSkyMessage(Object view) {
		String message = new String("sky," + id.toString() + "," + view.toString());
		try {
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendChangeShipColorMessage(Object color) {
		String message = new String("color," + id.toString() + "," + color.toString());
		try {
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void sendCreateGhostMissileMessage() {
		// Creates ghost avatar missiles
		String msg = "gfire," + id.toString();
		try {
			sendPacket(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendGameOverMessage(int playerScore, String playerName) {
		String message = new String("over," + id.toString() + "," + Integer.toString(playerScore) + "," + playerName);
		try {
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
