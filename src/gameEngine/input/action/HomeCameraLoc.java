package gameEngine.input.action;

import javax.vecmath.Point3d;
import net.java.games.input.Event;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.input.action.AbstractInputAction;
import sage.camera.ICamera;

public class HomeCameraLoc extends AbstractInputAction{
	private ICamera camera;
//	private Point3D loc = new Point3D(10,20,100);
	private Point3D loc;
	private Vector3D vd, ud, rd;
	
	public HomeCameraLoc(ICamera c){
		camera = c;
		vd = camera.getViewDirection();
		ud = camera.getUpAxis();
		rd = camera.getRightAxis();
		loc = camera.getLocation();
	}
//	public void performAction(){
//		Point3D loc = new Point3D(10, 20, 100);
//		System.out.println("HomeCameraLoc = 1");
//		camera.setLocation(loc);
//		camera.setAxes(rd, ud, vd);
//	}
	public void performAction(float time, Event evt) {
//		System.out.println("HomeCameraLoc = 2");
		camera.setLocation(loc);
		camera.setAxes(rd, ud, vd);
	}
}
