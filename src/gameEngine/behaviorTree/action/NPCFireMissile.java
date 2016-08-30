package gameEngine.behaviorTree.action;

import games.GameObjects.TurretNPC;
import networking.server.GameServerTCP;
import sage.ai.behaviortrees.BTAction;
import sage.ai.behaviortrees.BTStatus;

public class NPCFireMissile extends BTAction{

	private TurretNPC npc;
	private GameServerTCP server;

	public NPCFireMissile(GameServerTCP s,TurretNPC n) {
		npc = n;
		server = s;
	}

	@Override
	protected BTStatus update(float arg0) {
		if(npc.getFireFlag())
			server.sendFireMissile(npc.getID()-1);
		npc.setFireFlag(false);
		return BTStatus.BH_SUCCESS;
	}

}
