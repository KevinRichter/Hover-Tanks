package gameEngine.behaviorTree.condition;

import sage.ai.behaviortrees.BTCondition;
import gameEngine.aiControllers.TurretController;
import games.GameObjects.TurretNPC;
import networking.server.GameServerTCP;

public class AvatarInLineOfSight extends BTCondition{

	private GameServerTCP server;
	private TurretController npcCtrl;
	private TurretNPC npc;
	
	public AvatarInLineOfSight(GameServerTCP s, TurretController t, TurretNPC n, boolean toNegate) {
		super(toNegate);
		server = s;
		npcCtrl = t;
		npc = n;
	}

	@Override
	protected boolean check() {
		float rotation = npc.getRotation();
		// The BT sequence will only get to this point if a client already has the target lock
		server.sendCheckForLineOfSight(rotation, npc.getID()-1, npcCtrl.getTargetAvatar(npc.getID()-1));
		boolean returnValue = npcCtrl.getSightFlag(npc.getID()-1);
//		System.out.println("The return value for ID#" + (npc.getID()-1) + " with a value of " + returnValue);
		return returnValue;
	}

}
