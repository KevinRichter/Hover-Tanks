package gameEngine.event;

import sage.event.*;

public class MissileImpactEvent extends AbstractGameEvent{
	private int whichCrash;
	
	public MissileImpactEvent(){
		
	}
	public int getWhichCrash(){
		return whichCrash;
	}
}
