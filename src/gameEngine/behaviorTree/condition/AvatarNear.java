package gameEngine.behaviorTree.condition;

import gameEngine.aiControllers.TurretController;
import games.GameObjects.TurretNPC;
import graphicslib3D.Point3D;
import networking.server.GameServerTCP;
import sage.ai.behaviortrees.BTCondition;

public class AvatarNear extends BTCondition{

	private GameServerTCP server;
	private TurretController npcCtrl;
	private TurretNPC npc;
	
	public AvatarNear(GameServerTCP s, TurretController t, TurretNPC n, boolean toNegate) {
		super(toNegate);
		server = s;
		npcCtrl = t;
		npc = n;
	}

	@Override
	protected boolean check() {
		Point3D npcP = new Point3D(npc.getX(), npc.getY(), npc.getZ());
		boolean returnValue;
		if(!npcCtrl.getNearFlag(npc.getID()-1)){ // If no avatar near npc then do this
			server.sendCheckForAvatarNear(npcP, npc.getID()-1);
			returnValue = npcCtrl.getNearFlag(npc.getID()-1);
		}
		else { //If avatar is near, this check() would have locked the target client on previous pass
			   // This time through it will only look at that target.
			server.sendCheckForAvatarStillNear(npcP, npc.getID()-1, npcCtrl.getTargetAvatar(npc.getID()-1));
			returnValue = npcCtrl.getNearFlag(npc.getID()-1);
		}
//		System.out.println("The return value for ID#" + (npc.getID()-1) + " with a value of " + returnValue);
		return returnValue;
	}

}
