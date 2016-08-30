package networking.server;

import gameEngine.aiControllers.TurretController;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;

public class GameServerTCP extends GameConnectionServer<UUID>{
	
	private int numTurrets, clientsInGame = 0;
	private TurretController npcCtrl;
	private String[] players; 
	private int[] scores;
	
	public GameServerTCP(String localPort, TurretController npcCtrl)throws IOException{
		super(Integer.parseInt(localPort), ProtocolType.TCP);
		System.out.println("The inet address is: " + this.getLocalInetAddress() +
							" and the port number is: " + this.getLocalPort());
		this.npcCtrl = npcCtrl;
		this.npcCtrl.setServer(this);
		this.numTurrets = npcCtrl.getNumNPCs();
		players = new String[20];
		scores = new int[20];
		for(int i = 0; i < 20; i++) {
			players[i] = "";
			scores[i] = -1;
		}
	}
	public void acceptClient(IClientInfo ci, Object o){
		String message = (String)o;
		String[] mTokens = message.split(",");
		if(mTokens[0].compareTo("join")==0){
			//format: join,localid
			UUID clientID = UUID.fromString(mTokens[1]); 
			addClient(ci, clientID);
			sendJoinedMessage(clientID, true);
			this.sendCreateTurretsMessage(clientID);
//			npcCtrl.clearFlags();
			this.clientsInGame++;
			System.out.println("Server acceptClient() finished successfully.");
		}
		System.out.println("Server acceptClient() finished UNsuccessfully.");
	}	
	public void processPacket(Object o, InetAddress senderIP, int sndPort){
		String message = (String)o;
		String[] mTokens = message.split(",");
		if(mTokens[0].compareTo("bye")==0){
			UUID clientID = UUID.fromString(mTokens[1]);
			sendByeMessages(clientID);
			removeClient(clientID);
			this.clientsInGame--;
		}
		if(mTokens[0].compareTo("create")==0){
			UUID clientID = UUID.fromString(mTokens[1]);
			String[] pos = {mTokens[2], mTokens[3], mTokens[4]};
			String rotAmount = mTokens[5];
			String tankcolor = mTokens[6];
			String tanktype = mTokens[7];
			sendCreateMessages(clientID, pos, rotAmount, tankcolor, tanktype);
			sendWantsDetailsMessages(clientID);
		}
		if(mTokens[0].compareTo("dsfr")==0){
			System.out.println("GameServerTCP-processpacket="+message);
			UUID clientID = UUID.fromString(mTokens[1]);
			UUID remID = UUID.fromString(mTokens[2]);
			String[] pos = {mTokens[3], mTokens[4], mTokens[5]};
			String rotAmount = mTokens[6];
			String tankcolor = mTokens[7];
			String tanktype = mTokens[8];
			sendDetailsMessages(clientID, remID, pos, rotAmount, tankcolor, tanktype);
		}
		if(mTokens[0].compareTo("move")==0){
			UUID clientID = UUID.fromString(mTokens[1]);
			String[] pos = {mTokens[2], mTokens[3], mTokens[4]};			
			sendMoveMessages(clientID, pos);
		}
		if(mTokens[0].compareTo("rot")==0){
			UUID clientID = UUID.fromString(mTokens[1]);
			String amount = mTokens[2];
			String[] pos = {mTokens[3], mTokens[4], mTokens[5]};			
			sendRotateMessages(clientID, amount, pos);
		}
		if(mTokens[0].compareTo("sky")==0){
			UUID clientID = UUID.fromString(mTokens[1]);
			String view = mTokens[2];
			sendChangeSkyMessages(clientID, view);
		}
		if(mTokens[0].compareTo("color")==0){
			UUID clientID = UUID.fromString(mTokens[1]);
			String color = mTokens[2];
			sendChangeAvatarColorMessage(clientID, color);
		}
		if(mTokens[0].compareTo("avnr")==0){
			UUID clientID = UUID.fromString(mTokens[1]);
			String ID = mTokens[2];
			int turretID =Integer.parseInt(ID);
			String isNear = mTokens[3];
			npcCtrl.setNearFlag(isNear, turretID, clientID);
		}
		if(mTokens[0].compareTo("rotdir")==0){
			UUID clientID = UUID.fromString(mTokens[1]);
			String rotDir = mTokens[2];
			int turretID = Integer.parseInt(mTokens[3]);
			double degrees = Double.parseDouble(mTokens[4]);
			npcCtrl.setRotationDir(rotDir, turretID, degrees);
		}
		if(mTokens[0].compareTo("line")==0){
			UUID clientID = UUID.fromString(mTokens[1]);
			String ID = mTokens[2];
			int turretID =Integer.parseInt(ID);
			String inSights = mTokens[3];
			boolean inSight = false;
			if(inSights.equals("true")) inSight = true;
			npcCtrl.setSightFlag(turretID, inSight);
		}
		if(mTokens[0].compareTo("gfire")==0){
			UUID clientID = UUID.fromString(mTokens[1]);			
			sendFireGhostMissileMessage(clientID);
		}
		if(mTokens[0].compareTo("over")==0){
			UUID clientID = UUID.fromString(mTokens[1]);
			int score = Integer.parseInt(mTokens[2]);
			scores[clientsInGame - 1] = score;
			String playerName = mTokens[3];
			players[clientsInGame - 1] = playerName;
			this.clientsInGame--;
			sendRemoveAvatar(clientID);
			if(clientsInGame == 1) {
				System.out.println("scores="+scores);
				computeWinner();
			}
		}
	}
	
	private void computeWinner() {
		int maxIndex = 0;
		//Find max score
		for(int i = 1; i < 20; i++) {
			if (scores[i-1] < scores[i]){
				maxIndex = i;
			}
		}
		sendWinnerMessage(scores[maxIndex], players[maxIndex]);
	}
	private void sendWinnerMessage(int winningScore, String winningPlayer) {
		String message = new String("win," + Integer.toString(winningScore) + "," + winningPlayer);
		try {
			sendPacketToAll(message);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	private void sendRemoveAvatar(UUID clientID) {
		String message = new String("rmav," + clientID.toString());
		try {
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	private void sendFireGhostMissileMessage(UUID clientID) {
		String message = new String("gfire," + clientID.toString());
		try {
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	public void sendCreateTurretsMessage(UUID clientID) 
	{	
		for (int id = 0; id < numTurrets; id++) {
			System.out.println("sendCreateTurretsMessage was entered for id " + id);
			try {
				String message = new String("tnpc," + Integer.toString(id));
				message += "," + npcCtrl.getNPC(id).getX();
				message += "," + npcCtrl.getNPC(id).getY();
				message += "," + npcCtrl.getNPC(id).getZ();
				message += "," + npcCtrl.getNPC(id).getRotation();
				sendPacket(message, clientID);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void sendChangeAvatarColorMessage(UUID clientID, String color) {
		String message = new String("color," + clientID.toString() + "," + color);
		try {
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void sendChangeSkyMessages(UUID clientID, String view) {
		String message = new String("sky," + clientID.toString() + "," + view);
		try {
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendJoinedMessage(UUID clientID, boolean success){
		try{
			String message = new String("join,");
			if(success) message += "success";
			else message += "failure";
			sendPacket(message, clientID);
		}
		catch(IOException e){e.printStackTrace();}
	}
	public void sendCreateMessages(UUID clientID, String[] position, String rotAmount, String tankcolor, String tanktype){
		try{
			String message = new String("create,"+clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			message += "," + rotAmount;
			message += "," + tankcolor;
			message += "," + tanktype;
			forwardPacketToAll(message, clientID);
			System.out.println("GameServerTCP-sendCreateMessage=" +message);
		}
		catch(IOException e){e.printStackTrace();}
	}
	public void sendDetailsMessages(UUID clientID, UUID remoteID, String[] position, String rotAmount, String tankcolor, String tanktype){
		String message = new String("dsms," + clientID.toString());
		message += "," + position[0];
		message += "," + position[1];
		message += "," + position[2];
		message += "," + rotAmount;
		message += "," + tankcolor;
		message += "," + tanktype;
		try {
			sendPacket(message, remoteID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendWantsDetailsMessages(UUID clientID){
		String msg = new String("wsds," + clientID.toString());
		try {
			forwardPacketToAll(msg, clientID);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	public void sendMoveMessages(UUID clientID, String[] position){
		String message = new String("move," + clientID.toString()); 
		message += "," + position[0];
		message += "," + position[1];
		message += "," + position[2];
		try {
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendRotateMessages(UUID clientID, String amount , String[] position){
		String message = new String("rot," + clientID.toString()); 
		message += "," + amount;
		message += "," + position[0];
		message += "," + position[1];
		message += "," + position[2];
		try {
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendByeMessages(UUID clientID){
		String message = new String("bye," + clientID.toString()); 
		try {
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendNPCinfo() // informs clients of new NPC positions 
	{ 
		for (int i=0; i< npcCtrl.getNumNPCs(); i++) 	
		{ 
			try 
			{ 
				String message = new String("rnpc," + Integer.toString(i));
//				message += "," + (npcCtrl.getNPC(i)).getX(); 
//				message += "," + (npcCtrl.getNPC(i)).getY(); 
//				message += "," + (npcCtrl.getNPC(i)).getZ(); 
				message += "," + (npcCtrl.getNPC(i)).getRotation();
				sendPacketToAll(message); 
			} catch (IOException e) {
				e.printStackTrace();				
			}
		}
	}
	public void sendCheckForAvatarNear(Point3D npcP, int turretID) {
		String message = new String("nearnpc");
		message += "," + npcP.getX(); 
		message += "," + npcP.getY(); 
		message += "," + npcP.getZ(); 
		message += "," + Integer.toString(turretID);
		try {
			sendPacketToAll(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void sendDirectionToRotate(Float rotation, int id, UUID clientID) {
		String message = new String("rotdir,"  + Integer.toString(id-1));//-1 gets the right index value
		message += "," + (npcCtrl.getNPC(id-1)).getX();
		message += "," + (npcCtrl.getNPC(id-1)).getZ();
		message += "," + rotation; 
		try {
			sendPacket(message, clientID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void sendCheckForLineOfSight(float rotation, int turretID, UUID clientID) {//ID already decremented 1
		String message = new String("line");
		message += "," + Float.toString(rotation);
		message += "," + (npcCtrl.getNPC(turretID)).getX();
		message += "," + (npcCtrl.getNPC(turretID)).getZ();
		message += "," + Integer.toString(turretID);
		try {
			sendPacket(message, clientID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	public void sendFireMissile(int turretID) { //turretID already decremented for indexing
		String message = new String("fire");
		message += "," + Integer.toString(turretID);
		try {
			sendPacketToAll(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void sendCheckForAvatarStillNear(Point3D npcP, int turretID, UUID targetAvatar) {
		String message = new String("nearnpc");
		message += "," + npcP.getX(); 
		message += "," + npcP.getY(); 
		message += "," + npcP.getZ(); 
		message += "," + Integer.toString(turretID);
		try {
			sendPacket(message, targetAvatar);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
}
