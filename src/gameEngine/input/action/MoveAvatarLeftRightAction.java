package gameEngine.input.action;

import java.util.Iterator;

import sage.input.action.AbstractInputAction;
import sage.scene.Group;
import sage.scene.SceneNode;
import net.java.games.input.Component;
import net.java.games.input.Event;
import networking.client.MyClient;
import games.GameObjects.MyTank;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.terrain.*;

public class MoveAvatarLeftRightAction extends AbstractInputAction{
	
	private SceneNode avatar;
	private MyClient myClient;
	private float speeds;
//	private TerrainBlock terrain;
	
	
	public MoveAvatarLeftRightAction(SceneNode s, float speed, TerrainBlock t, MyClient myC){
		avatar = s;
		speeds = speed;
//		terrain = t;
		myClient = myC;		
	}
	public MoveAvatarLeftRightAction(Group g, float speed, TerrainBlock t, MyClient myC){
		avatar = g;
		speeds = speed;
//		terrain = t;
		myClient = myC;
		Iterator<SceneNode> children = ((Group) avatar).getChildren();
		children.next();
//		cannon = children.next();		
	}
//	public void updateVerticalPosition(){
//		Point3D avLoc = new Point3D(avatar.getLocalTranslation().getCol(3));
//		float terHeight = terrain.getHeightFromWorld(avLoc);
//		float desiredHeight = terHeight + (float)terrain.getOrigin().getY() + 3f;
//		avatar.getLocalTranslation().setElementAt(1, 3, desiredHeight);
//	}
	public void performAction(float time, Event e){
		float rotAmount = 0.4f;
		
		Component eComp = e.getComponent();
		String eString = eComp.toString();
		
		Matrix3D rot = avatar.getLocalRotation();
		Vector3D dir = new Vector3D(0,1,0);
		dir = dir.mult(rot);
		dir.scale((double)speeds*time);
		
		
		if(eString == "A"){
			((MyTank) avatar).myRotate(rotAmount, dir);
			myClient.sendRotateMessage(rotAmount, dir);
		}
		else if(eString == "D"){
			((MyTank) avatar).myRotate(-rotAmount, dir);
			myClient.sendRotateMessage(-rotAmount, dir);
		}
		else if(eString == "Left"){
			((MyTank) avatar).myRotate(rotAmount, dir);
			myClient.sendRotateMessage(rotAmount, dir);
		}
		else if(eString == "Right"){
			((MyTank) avatar).myRotate(-rotAmount, dir);
			myClient.sendRotateMessage(-rotAmount, dir);
		}
		else if(e.getValue()< -0.3){
			((MyTank) avatar).myRotate(rotAmount, dir);
			myClient.sendRotateMessage(rotAmount, dir);
		}
		else if(e.getValue()> 0.3){
			((MyTank) avatar).myRotate(-rotAmount, dir);
			myClient.sendRotateMessage(-rotAmount, dir);
		}
		else{
			
		}
	}
}
