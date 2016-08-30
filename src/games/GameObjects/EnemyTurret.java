package games.GameObjects;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.audio.AudioResource;
import sage.audio.AudioResourceType;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.audio.SoundType;
import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.TriMesh;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class EnemyTurret extends Group{
	int spawnPoint;
//	private RotationController rotator;
	private Group cannon;
	private TriMesh tobject, cobject;
	private Texture turretTex, cannonTex;
	private IAudioManager audioMgr;
	private Sound turretSound;
	private AudioResource resource2;
	private Point3D location;
	private float rotation;
	private int id;
	private float origRotation;
	
	public EnemyTurret(int id, double X, double Y, double Z, float R, IAudioManager audMgr) {
		this.setId(id); // Id is the index of the TurretNPC's stored on server
		location = new Point3D();
		location.setX(X);
		location.setY(Y);
		location.setZ(Z);
		setOrigRotation(R);
		rotation = 0;			
		audioMgr = audMgr;
		cannon = new Group();
		cannon.rotate(rotation, new Vector3D(0,1,0));
//		Vector3D rotatorV = new Vector3D(0,1,0);
//		rotator = new RotationController(getSpawnPointRotation(),rotatorV);
		
		OBJLoader loader = new OBJLoader();
		tobject = loader.loadModel("./obj/TurretBase.obj");
		turretTex = TextureManager.loadTexture2D("./images/TurretBase.jpg");
		tobject.setTexture(turretTex);
		tobject.translate((float)getBaseLocationX(),(float) getBaseLocationY(), (float)getBaseLocationZ());
		this.addChild(tobject);
		
		loader = new OBJLoader();
		cobject = loader.loadModel("./obj/TurretCannon.obj");
		cannonTex = TextureManager.loadTexture2D("./images/TurretCannon.jpg");
		cobject.setTexture(cannonTex);
		cobject.translate((float) (getBaseLocationX()-.2), (float) (getBaseLocationY()-2), (float)(getBaseLocationZ()));
		cannon.addChild(cobject);
//		rotator.addControlledNode(cannon);		
//		cannon.addController(rotator);
		this.addChild(cannon);
		this.scale(1.2f, 1.5f, 1.2f);
		
		initAudio();
	}

	private void initAudio() {
		resource2 = audioMgr.createAudioResource("./sounds/turretSound.wav", AudioResourceType.AUDIO_SAMPLE); 
		turretSound =new Sound(resource2, SoundType.SOUND_EFFECT, 100, true); 
		turretSound.initialize(audioMgr);
		turretSound.setMaxDistance(100.0f); 
		turretSound.setMinDistance(15.0f); 
		turretSound.setRollOff(5.0f); 
		turretSound.setLocation(new Point3D(this.getBaseLocationX(), this.getBaseLocationY(), this.getBaseLocationZ())); 
		turretSound.play();
	}

	private double getBaseLocationX() {
		return location.getX();
	}
	
	private double getBaseLocationY() {
		return location.getY();
	}

	private double getBaseLocationZ() {
		return location.getZ();
	}

	public float getRotation() {
		return rotation;
	}
	
	public void rotateCannon(float rotAmount) 
	{
//		System.out.println("rotateCannon id#" + getId() + " has rotation value of " + rotation + " and rotAmount of  " + rotAmount + " and the difference is " + (rotation - rotAmount));
		cannon.rotate((rotation - rotAmount), new Vector3D(0,1,0));
		rotation = rotAmount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getOrigRotation() {
		return origRotation;
	}

	public void setOrigRotation(float origRotation) {
		this.origRotation = origRotation;
	}

}
