package gameEngine.event;

import sage.event.*;
import java.util.UUID;

public class CrashEvent extends AbstractGameEvent{
	private int whichCrash;
	
	public CrashEvent(int n){
		whichCrash = n;
	}
	public int getWhichCrash(){
		return whichCrash;
	}
//	public static final long eventType = UUID.randomUUID().getLeastSignificantBits();
//	public String getName() {
//		return new String("Crash event");
//	}
//	public long getType() {
//		return eventType;
//	}
}
