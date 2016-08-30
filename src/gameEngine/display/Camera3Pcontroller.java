package gameEngine.display;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;
import net.java.games.input.Component.Identifier.Key;
import net.java.games.input.Component.POV;
import net.java.games.input.Event;
import games.GameObjects.MyTank;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.camera.ICamera;
import sage.input.IInputManager;
import sage.input.action.AbstractInputAction;
import sage.input.action.IAction;
import sage.scene.SceneNode;
import sage.util.MathUtils;

public class Camera3Pcontroller {
	private ICamera cam;
	private SceneNode target;
	private float cameraAzimuth;
	private float cameraElevation;
	private float cameraDistanceFromTarget;
	private Point3D targetPos;
	private Vector3D tarVDir = new Vector3D(0,1,0);
	private float maxCamDist = 100, minCamDist = 15, rotationRate = 50, zoomRate = 30;
	private boolean okToUpdate = true;
	
	public Camera3Pcontroller(ICamera cam, SceneNode t, IInputManager inputMgr, String controllerName){
		this.cam = cam;
		this.target = t;

		new Vector3D(0,1,0);
		cameraDistanceFromTarget = 25.0f;
		cameraAzimuth = 0;
		cameraElevation = 20.0f;
		update(0.0f);
		setupInput(inputMgr, controllerName);
	}
	
	public void update(float time){
		if(okToUpdate ){
			updateTarget();
			updateCameraPosition();
			if(time == 0){
				cam.lookAt(targetPos, tarVDir);
			}
	//		cam.lookAt(targetPos,  worldUpVec);
			cam.lookAt(targetPos, tarVDir);
		}
	}
	private void updateTarget(){
		targetPos = new Point3D(target.getWorldTranslation().getCol(3));
	}
	private void updateCameraPosition(){
		double theta = cameraAzimuth;
		double phi = cameraElevation;
		double r = cameraDistanceFromTarget;
		
//		if(mShip.getCamLock() == true){
//			float rotUp = mShip.getMyRotate();
//			
//			cameraAzimuth =+ rotUp;
//		}
		Point3D relativePosition = MathUtils.sphericalToCartesian(theta, phi, r);
		Point3D desiredCameraLoc = relativePosition.add(targetPos);
		cam.setLocation(desiredCameraLoc);
	}
	private void setupInput(IInputManager im, String cn){
		IAction orbitAction = new OrbitAroundAction();
		IAction zoomAction = new ZoomInOutAction();
		new ElevateUpDownAction();
//		IAction orbitlockAction = new OrbitLockAction();
		im.associateAction(cn, Key.H, orbitAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Key.K, orbitAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Key.U, zoomAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Key.J, zoomAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Axis.RX, orbitAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Axis.RY, zoomAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//		im.associateAction(cn, Button._7, orbitlockAction, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
	}
	
	public float getAzimuth() {
		return cameraAzimuth;
	}
//	private class OrbitLockAction extends AbstractInputAction{
//		public void performAction(float time, Event evt){
//			float rotAmount;
//			rotAmount = mShip.getMyRotate();
//			System.out.println("rotUp"+rotAmount);
//			rotAmount += 180;
//			cameraAzimuth += rotAmount;
//		}
//	}
	private class OrbitAroundAction extends AbstractInputAction{
		public void performAction(float time, Event evt){
			float rotAmount;
			Component eComp = evt.getComponent();
			String eString = eComp.toString();
//			System.out.println(eString);
			if(eString == "H"){
//				rotAmount = 0.2f;
				rotAmount = rotationRate/1000 * time;
			}
			else if(eString == "K"){
//				rotAmount = -0.2f;
				rotAmount = -rotationRate/1000 * time;
			}
			else if(evt.getValue()<-0.2){
				rotAmount = rotationRate/1000 * time;
//				rotAmount = 0.2f;
			}
			else{
				if(evt.getValue()>0.2){
//					rotAmount = -0.2f;
					rotAmount = -rotationRate/1000 * time;
				}
				else{
					rotAmount = 0.0f;
				}
			}
			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
		}
	}
	private class ZoomInOutAction extends AbstractInputAction{
		public void performAction(float time, Event evt){
			float zoomAmount = 0.0f;
			Component eComp = evt.getComponent();
			String eString = eComp.toString();
			if(cameraDistanceFromTarget <= minCamDist){
				if(eString == "J"){
//					zoomAmount = 0.1f;
					zoomAmount = zoomRate/1000 * time;
				}
				else if(evt.getValue()>0.4){
//					zoomAmount = 0.1f;
					zoomAmount = zoomRate/1000 * time;
				}
			}
			else if(cameraDistanceFromTarget >= maxCamDist){
				if(eString == "U"){
//					zoomAmount = -0.1f;
					zoomAmount = -zoomRate/1000 * time;
				}
				else if(evt.getValue()<-0.4){
//					zoomAmount = -0.1f;
					zoomAmount = -zoomRate/1000 * time;
				}
			}
			else{
				if(eString == "J"){
//					zoomAmount = 0.1f;
					zoomAmount = zoomRate/1000 * time;
				}
				else if(eString == "U"){
//					zoomAmount = -0.1f;
					zoomAmount = -zoomRate/1000 * time;
				}
				else if(evt.getValue()<-0.4){
//					zoomAmount = -0.1f;
					zoomAmount = -zoomRate/1000 * time;
				}
				else if(evt.getValue()>0.4){
//					zoomAmount = 0.1f;
					zoomAmount = zoomRate/1000 * time;
				}
				else{
					zoomAmount = 0.0f;
				}
			}
			cameraDistanceFromTarget += zoomAmount;
		}
	}
	private class ElevateUpDownAction extends AbstractInputAction{
		public void performAction(float time, Event evt){
			if(evt.getValue()<-0.2){
				
			}
			else if(evt.getValue()>0.2){
				
			}
			else{
				
			}
		}
	}
	
	public void endgameCameraPosition() {
		okToUpdate = false;
		cam.setLocation(new Point3D(0,200,0));
		cam.setViewDirection(new Vector3D(0,-1,0));
	}
}
