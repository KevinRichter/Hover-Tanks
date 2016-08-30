package gameEngine.input.action;

import net.java.games.input.Component;
import net.java.games.input.Event;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;
import sage.terrain.*;

public class StrafeAvatarLeftRightAction extends AbstractInputAction{
//	private MyShip ship;
	private SceneNode avatar;
	private float speeds;
	private float xT, yT, zT;
	private TerrainBlock terrain;
	
	public StrafeAvatarLeftRightAction(SceneNode s, float speed, TerrainBlock t){
		avatar = s;
		speeds = speed;
		terrain = t;
		System.out.println(net.java.games.input.Component.Identifier.Button._4);
		System.out.println(net.java.games.input.Component.Identifier.Button._5);
	}
	public void updateVerticalPosition(){
		Point3D avLoc = new Point3D(avatar.getLocalTranslation().getCol(3));
		float x = (float) avLoc.getX();
		float y = (float) avLoc.getY();
		float z = (float) avLoc.getZ();
		x = x - (-terrain.getSize()/2);
		z = z - (-terrain.getSize()/2);
		float terHeight = terrain.getHeight(x,z);
		float desiredHeight = terHeight + (float)terrain.getOrigin().getY() + 10f;
		avatar.getLocalTranslation().setElementAt(1, 3, desiredHeight);
	}
	public void performAction(float time, Event e){
		float moveAmount = 0.1f;
//		Component b4 = net.java.games.input.Component.Identifier.Button._4;
//		Component b5 = (Component) net.java.games.input.Component.Identifier.Button._5;
		Component eComp = e.getComponent();
		String eString = eComp.toString();
		
		
		System.out.println("SALRA-button="+e.getComponent()+"-value="+e.getValue()+"-eString="+eString);

		Matrix3D rot = avatar.getLocalRotation();
		Vector3D dir = new Vector3D(0,0,1);
		
		dir = dir.mult(rot);
		dir.scale((double)(speeds*time));
		
		if(eString == "Button 4"){
			System.out.println("SALRA-4button="+e.getComponent());
			avatar.translate((float)dir.getX(),(float)dir.getY(),(float)dir.getZ());
		}
		else if(eString == "Button 5"){
			System.out.println("SALRA-5button="+e.getComponent());
			avatar.translate((float)dir.getX(),(float)dir.getY(),-(float)dir.getZ());
		}
		updateVerticalPosition();
	}
}
