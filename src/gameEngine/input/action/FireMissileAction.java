package gameEngine.input.action;

import javax.swing.Timer;

import games.BattleTankGame;
import games.GameObjects.Missile;
import games.GameObjects.MyTank;
import sage.input.action.AbstractInputAction;
import net.java.games.input.Component;
import net.java.games.input.Event;
import networking.client.MyClient;

public class FireMissileAction extends AbstractInputAction{
	private BattleTankGame game;
	private Missile missile;
	private MyTank myt;
	private MyClient myClient;
	private Timer timer;
	private float curtime = 0, lasttime = -5, timediff = 0;
	
	public FireMissileAction(BattleTankGame t, MyTank s, MyClient myC){
		game = t;
		myt = s;
		myClient = myC;
//		this.setSpeed(200);
		
	}
	public void performAction(float time, Event e){
		Component eComp = e.getComponent();
		String estring = eComp.toString();
		curtime = game.getTime();
		timediff = curtime - lasttime;
		if(timediff > 4){
			if(estring == "F"){
//				missile = new Missile(myt);
				missile = game.getMissileFromPool();
				missile.setbeingUsed(true, myt);
				game.addGameWorldObject(missile);
				lasttime = game.getTime();
				myClient.sendCreateGhostMissileMessage();
			}
			else if(e.getValue() < -0.8 && e.getValue() > -1){
				lasttime = game.getTime();
//				missile = new Missile(myt);
				missile = game.getMissileFromPool();
				missile.setbeingUsed(true, myt);
				game.addGameWorldObject(missile);
				myClient.sendCreateGhostMissileMessage();
			}
		}
	}
}
