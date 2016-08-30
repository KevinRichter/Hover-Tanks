package gameEngine.behaviorTree.action;

import networking.server.GameServerTCP;
import gameEngine.aiControllers.TurretController;
import games.GameObjects.TurretNPC;
import sage.ai.behaviortrees.BTAction;
import sage.ai.behaviortrees.BTStatus;

public class RotateToAvatar extends BTAction{

	private TurretNPC npc;
	private GameServerTCP server;
	private TurretController npcCtrl;
	
	public RotateToAvatar(GameServerTCP s,TurretController t,TurretNPC n) {
		npc = n;
		server = s;
		npcCtrl = t;
	}

	@Override
	protected BTStatus update(float arg0) {
		float rotation = npc.getRotation();
		// The BT sequence will only get to this point if a client already has the target lock
		server.sendDirectionToRotate(rotation, npc.getID(), npcCtrl.getTargetAvatar(npc.getID()-1));
		String dir = npcCtrl.getRotationDir(npc.getID()-1);
		npc.rotateToAvatar(dir);
		return BTStatus.BH_SUCCESS;
	}

}
