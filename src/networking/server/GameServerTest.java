package networking.server;

import gameEngine.aiControllers.TurretController;

import java.io.IOException;

public class GameServerTest {
	
	private long lastUpdateTime;
	private GameServerTCP testTCPServer; 
	private TurretController npcCtrl;
	private long startTime;
	private int numTurrets = 4;
	 
	 public GameServerTest() // constructor 
	 { startTime = System.nanoTime(); 
	 lastUpdateTime = startTime; 
	 npcCtrl = new TurretController(numTurrets); 
	 // start networking TCP server (as before) 
	 try {
		 testTCPServer = new GameServerTCP("30001", npcCtrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 // start NPC control loop 
	 npcCtrl.setupNPCs(); 
	 npcLoop(); 
	 } 
	 
	 public void npcLoop() // NPC control loop 
	 { 
		 while (true) 
		 { 
			 long frameStartTime = System.nanoTime(); 
			 float elapMilSecs = (frameStartTime-lastUpdateTime)/(1000000.0f); 
			 if (elapMilSecs >= 50.0f) 
			 { 
				 lastUpdateTime = frameStartTime; 
				 npcCtrl.updateNPCs();
				 testTCPServer.sendNPCinfo(); 
				 npcCtrl.updateBT(elapMilSecs);
			 } 
			 Thread.yield(); 
	 	 } 
	}
	public static void main(String[] args){	
				
		GameServerTest gst = new GameServerTest();
	}
}
