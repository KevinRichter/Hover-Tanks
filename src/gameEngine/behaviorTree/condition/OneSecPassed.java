package gameEngine.behaviorTree.condition;

import sage.ai.behaviortrees.BTCondition;
import games.GameObjects.TurretNPC;

public class OneSecPassed extends BTCondition{
	
	private TurretNPC npc;
	private float lastUpdateTime;

	public OneSecPassed(TurretNPC n, boolean toNegate) 
	{ 
		super(toNegate);
		npc = n; 
		lastUpdateTime = System.nanoTime(); 
	} 
	
	protected boolean check() 
	{ 
		float elapsedMilliSecs = (System.nanoTime()-lastUpdateTime)/(1000000.0f); 
//		System.out.println("elaspedMilliSecs is: " + elapsedMilliSecs + " $$$$$$$$$$$$$$$$$$$ npc[" + npc.getID() + "] ");
		if (elapsedMilliSecs >= 6000.0f) 
		{ 
//			System.out.println("FIREDDDDDDDD");
			lastUpdateTime = System.nanoTime(); 
			npc.setFireFlag(true); 
			return true; 
		} 
		
		else {			
			npc.setFireFlag((false));
			return false; 
		}
	}

}
