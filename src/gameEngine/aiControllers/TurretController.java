package gameEngine.aiControllers;

import java.util.UUID;

import networking.server.GameServerTCP;
import gameEngine.behaviorTree.action.NPCFireMissile;
import gameEngine.behaviorTree.action.RotateToAvatar;
import gameEngine.behaviorTree.condition.AvatarInLineOfSight;
import gameEngine.behaviorTree.condition.AvatarNear;
import gameEngine.behaviorTree.condition.OneSecPassed;
import games.GameObjects.TurretNPC;
import sage.ai.behaviortrees.BTCompositeType;
import sage.ai.behaviortrees.BTSequence;
import sage.ai.behaviortrees.BehaviorTree;

public class TurretController {

	 private BehaviorTree[] bt; 
	 private TurretNPC[] npc;	
	 private int numTurrets;
	 boolean isNear[], inSights[];
	 private GameServerTCP server;
	 private String rotateInThisDirection[];
	 private UUID targetAvatar[];
	 
	 public TurretController(int numTurrets){	
		 this.numTurrets = numTurrets;
		 bt = new BehaviorTree[numTurrets];
		 isNear = new boolean[numTurrets];
		 inSights = new boolean[numTurrets];
		 rotateInThisDirection = new String[numTurrets];
		 targetAvatar = new UUID[numTurrets];
		 for (int i = 0; i < numTurrets; i++){
			 bt[i] = new BehaviorTree(BTCompositeType.SEQUENCE);
			 isNear[i] = false;
			 rotateInThisDirection[i] = "";
			 inSights[i] = false;
			 targetAvatar[i] = null;
		 }	 
	 }
	 	 
	 public void setupNPCs() 
	 { 
		 System.out.println("setupNPC is TurretController entered.");		 
		 npc = new TurretNPC[numTurrets]; 
		 for (int i = 0; i < numTurrets; i++){
			 npc[i] = new TurretNPC(i+1);
		 }	
		 setupBehaviorTree();	 
		 System.out.println("setupNPC is TurretController exited.");
	 }
	 
	 public void updateNPCs() {
		 for (int i = 0; i < numTurrets; i++){
			 npc[i].updateLocation();
		 }
	 }
	 
	 public TurretNPC getNPC(int id) {
		 return npc[id];
	 }
	 
	 public int getNumNPCs() {
		 return numTurrets;
	 }
	 
	public void setupBehaviorTree() 
	{ 
		for (int i = 0; i < numTurrets; i++){
			bt[i].insertAtRoot(new BTSequence(10)); 
			bt[i].insert(10, new AvatarNear(server,this,npc[i],false)); 
			bt[i].insert(10, new RotateToAvatar(server,this,npc[i]));
			bt[i].insert(10, new AvatarInLineOfSight(server,this,npc[i],false));
			bt[i].insert(10, new OneSecPassed(npc[i],false));
			bt[i].insert(10, new NPCFireMissile(server,npc[i]));
		}
	}  
	
	public double getX(int id) 
	{
		return npc[id].getX();
	}
	
	public double getY(int id) 
	{
		return npc[id].getY();
	}
	
	public double getZ(int id) 
	{
		return npc[id].getZ();
	}
	
	public float getRotation(int id)
	{
		return npc[id].getRotation();
	}

	public void setNearFlag(String isNear, int id, UUID clientID) {
		if(isNear.equals("true")){
			this.isNear[id] = true;
			this.targetAvatar[id] = clientID;
		}	
		else {
			this.isNear[id] = false;
			this.targetAvatar[id] = null;
		}
	}
	
	public UUID getTargetAvatar(int id) {
		return targetAvatar[id];
	}
	
	public void setTargetAvatar(int id, UUID clientID) {
		targetAvatar[id] = clientID;
	}
	
	public void setServer(GameServerTCP tcp){
		server = tcp;
	}
 
	public boolean getNearFlag(int id) {
		return isNear[id];
	}
	
	public void updateBT(float elapsedTime){
		for(int i = 0; i < numTurrets; i++) {
			bt[i].update(elapsedTime);
//			System.out.println("BT[" + i + "] has been updated.");
		}
	}

	public void setRotationDir(String rotDir, int id, double degrees) {
		this.rotateInThisDirection[id] = rotDir;		
	}
	
	public String getRotationDir(int id) {
		return rotateInThisDirection[id];		
	}

	public boolean getSightFlag(int id) {		
		return inSights[id];
	}
	
	public void setSightFlag(int id, boolean b) {
		inSights[id] = b;
		npc[id].setSights(b);
	}

	public void clearFlags() {
		for(int i = 0;i<numTurrets;i++){
			targetAvatar[i] = null;
		}
	}
}
