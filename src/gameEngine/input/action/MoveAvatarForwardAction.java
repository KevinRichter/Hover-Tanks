package gameEngine.input.action;

import net.java.games.input.Component;
import net.java.games.input.Event;
import networking.client.MyClient;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.input.action.AbstractInputAction;
import sage.scene.Group;
import sage.scene.SceneNode;
import sage.terrain.*;

public class MoveAvatarForwardAction extends AbstractInputAction{
	
	private SceneNode avatar;
	private float speeds;
	private MyClient myClient;
	private float previousXForward, previousYForward, previousZForward;
	private TerrainBlock terrain;
	private float desiredHeight, previousHeight, heightDelta;
	
	public MoveAvatarForwardAction(Group g, float speed, TerrainBlock t, MyClient myC){
		avatar = g;
		speeds = speed;
		terrain = t;
		myClient = myC;
	}
	
	public void updateVerticalPosition(){
		Point3D avLoc = new Point3D(avatar.getLocalTranslation().getCol(3));
		float x = (float) avLoc.getX();
		float z = (float) avLoc.getZ();
		x = x - (-terrain.getSize()/2);
		z = z - (-terrain.getSize()/2);
		float terHeight = terrain.getHeight(x,z);
		previousHeight = desiredHeight;
		desiredHeight = terHeight + (float)terrain.getOrigin().getY() + 2f;
		heightDelta = desiredHeight - previousHeight;
		avatar.getLocalTranslation().setElementAt(1, 3, desiredHeight);
	}	
	
	public void performAction(float time, Event e){
		
		Component eComp = e.getComponent();
		String eString = eComp.toString();
		
			int counter = 0;
			Matrix3D rot = avatar.getLocalRotation();
			Vector3D dir = new Vector3D(0,0,1);
	
			dir = dir.mult(rot);
			dir.scale((double)(speeds*time));
			Point3D loc = new Point3D(avatar.getWorldTranslation().getCol(3).getX(),
					avatar.getWorldTranslation().getCol(3).getY(),
					avatar.getWorldTranslation().getCol(3).getZ());
			
			if(eString == "W"){				
				if (terrain.getHeightFromWorld(loc) < 2){
//				if (heightDelta < .3){
					avatar.translate((float)dir.getX(),(float)dir.getY(),(float)dir.getZ());
					previousXForward = (float)dir.getX();
					previousYForward = (float)dir.getY();
					previousZForward = (float)dir.getZ();
					counter = 0;
					myClient.sendMoveMessage(dir);
				}
				else if (counter > 0){
					avatar.translate((float)dir.getX()*4,(float)dir.getY(),(float)dir.getZ()*4);
					Vector3D newDir = new Vector3D((float)dir.getX()*4,(float)dir.getY(),(float)dir.getZ()*4);
					myClient.sendMoveMessage(newDir);
					counter++;
				}
				else {
					avatar.translate(-previousXForward*4,-previousYForward*1, -previousZForward*4);
					Vector3D newDir = new Vector3D(-previousXForward*4,-previousYForward*1, -previousZForward*4);
					myClient.sendMoveMessage(newDir);
					counter++;
				}			
			}
			else if(eString == "S"){
				if (terrain.getHeightFromWorld(loc) < 2){
//				if (heightDelta < .3){
					avatar.translate((float)-dir.getX(),(float)-dir.getY(),(float)-dir.getZ());
					previousXForward = (float)-dir.getX();
					previousXForward = (float)-dir.getY();
					previousXForward = (float)-dir.getZ();
					counter = 0;
					Vector3D newDir = new Vector3D((float)-dir.getX(),(float)-dir.getY(),(float)-dir.getZ());
					myClient.sendMoveMessage(newDir);
				}
				else if (counter > 0){
					avatar.translate((float)-dir.getX()*4,(float)-dir.getY(),(float)-dir.getZ()*4);
					Vector3D newDir = new Vector3D((float)-dir.getX()*4,(float)-dir.getY(),(float)-dir.getZ()*4);
					myClient.sendMoveMessage(newDir);
					counter++;
				}
				else {
					avatar.translate(-previousXForward*4,-previousYForward*1,-previousZForward*4);
					Vector3D newDir = new Vector3D(-previousXForward*4,-previousYForward*1, -previousZForward*4);
					myClient.sendMoveMessage(newDir);
					counter++;
				}
			}
			else if(eString == "Up"){
				avatar.translate((float)dir.getX(),(float)0,(float)dir.getZ());
			}
			else if(eString == "Down"){
				avatar.translate((float)-dir.getX(),(float)0,(float)-dir.getZ());
			}
			else if(e.getValue() < -0.3){
				if (terrain.getHeightFromWorld(loc) < 2){
					avatar.translate((float)dir.getX(),(float)dir.getY(),(float)dir.getZ());
					previousXForward = (float)dir.getX();
					previousYForward = (float)dir.getY();
					previousZForward = (float)dir.getZ();
					counter = 0;
					myClient.sendMoveMessage(dir);
				}
				else if (counter > 0){
					avatar.translate((float)dir.getX()*4,(float)dir.getY(),(float)dir.getZ()*4);
					Vector3D newDir = new Vector3D((float)dir.getX()*4,(float)dir.getY(),(float)dir.getZ()*4);
					myClient.sendMoveMessage(newDir);
					counter++;
				}
				else {
					avatar.translate(-previousXForward*4,-previousYForward*1, -previousZForward*4);
					Vector3D newDir = new Vector3D(-previousXForward*4,-previousYForward*1, -previousZForward*4);
					myClient.sendMoveMessage(newDir);
					counter++;
				}		
			}
			else{
				if(e.getValue() > 0.3){
					if (terrain.getHeightFromWorld(loc) < 2){
						avatar.translate((float)-dir.getX(),(float)-dir.getY(),(float)-dir.getZ());
						previousXForward = (float)-dir.getX();
						previousXForward = (float)-dir.getY();
						previousXForward = (float)-dir.getZ();
						counter = 0;
						Vector3D newDir = new Vector3D((float)-dir.getX(),(float)-dir.getY(),(float)-dir.getZ());
						myClient.sendMoveMessage(newDir);
					}
					else if (counter > 0){
						avatar.translate((float)-dir.getX()*4,(float)-dir.getY(),(float)-dir.getZ()*4);
						Vector3D newDir = new Vector3D((float)-dir.getX()*4,(float)-dir.getY(),(float)-dir.getZ()*4);
						myClient.sendMoveMessage(newDir);
						counter++;
					}
					else {
						avatar.translate(-previousXForward*4,-previousYForward*1,-previousZForward*4);
						Vector3D newDir = new Vector3D(-previousXForward*4,-previousYForward*1, -previousZForward*4);
						myClient.sendMoveMessage(newDir);
						counter++;
					}
			}	
			
		}
		this.updateVerticalPosition();
	}
}
