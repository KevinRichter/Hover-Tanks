package games.GameObjects;

import graphicslib3D.Point3D;

public class TurretNPC {
	
	private Point3D location; // other state info goes here (FSM)
	private int spawnPoint = 0;
	private float rotation = 0;
	private boolean inSights = false;
	private boolean fireAway = false;
	
	public TurretNPC(int s) 
	{
		spawnPoint = s;
		location = new Point3D();	
		
		switch(spawnPoint){
		case 1:
			location.setX(40.0);
			location.setY(2.1);
			location.setZ(40.0);
			rotation = 90;
			break;
		
		case 2:
			location.setX(60.0);
			location.setY(2.1);
			location.setZ(13.0);
			rotation = 50;
			break;
			
		case 3:
			location.setX(100.0);
			location.setY(2.1);
			location.setZ(55.0);
			rotation = 130;
			break;
			
		case 4:
			location.setX(-50.0);
			location.setY(2.1);
			location.setZ(25.0);
			rotation = 240;
			break;	
		}
	}
	 
	public double getX() { return location.getX(); } 
	public double getY() { return location.getY(); } 
	public double getZ() { return location.getZ(); } 
	public float getRotation() { return rotation; }
	public void setRotation(float rotAmount) { rotation += rotAmount; }
	public int getID() { return spawnPoint; }
	public void updateLocation() { 
		
	 
	}

	public void rotateToAvatar(String dir) {
		if(dir != null && !inSights){
			if (dir.equals("inc")) {
				rotation -= 1.0f;
				rotation = (rotation % 360);
				if(rotation < 0){
					rotation = rotation + 360;
				}
			}
			else {
				rotation += 1.0f;
				rotation = (rotation % 360);
				if(rotation < 0){
					rotation = rotation + 360;
				}
			}
		}		
	} 
	 
	public void setSights(boolean b){
		inSights = b;
	}

	public void setFireFlag(boolean b) {
		fireAway  = b;		
	}
	
	public boolean getFireFlag() {
		return fireAway;		
	}
	
	public void fireMissile(){
		if (fireAway) {
			
		}
	}
}
