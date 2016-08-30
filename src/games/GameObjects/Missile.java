package games.GameObjects;

import java.util.Iterator;

import gameEngine.event.MissileImpactEvent;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.audio.AudioResource;
import sage.audio.AudioResourceType;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.audio.SoundType;
import sage.event.IEventListener;
import sage.event.IEventManager;
import sage.event.IGameEvent;
import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.SceneNode;
import sage.scene.TriMesh;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class Missile extends Group implements IEventListener{
	private Texture mistex = TextureManager.loadTexture2D("./images/missile1.jpg");;
	private OBJLoader loader = new OBJLoader();
	private TriMesh mobject = loader.loadModel("./obj/missile.obj");
	private float rotate;
	private int lifetime = 160;
	private float missilespeed = 0.03f;
	private boolean used = false;
	private String owner = "n";	// t for tank, r for turret, g for ghostavatar
	private String rmode = "turret", tmode = "player", gmode = "ghost";
	private IEventManager eventMgr;
	private IAudioManager audioMgr;
	private Sound missileSound, impactSound;
	private AudioResource resource1, resource2;
	
	public Missile(IEventManager eventMgr, IAudioManager audMgr){
//		System.out.println("Missile created-"+count);
		audioMgr = audMgr;
		this.eventMgr = eventMgr;
		eventMgr.addListener( this, MissileImpactEvent.class);
		mobject.scale(0.4f, 0.4f, 0.4f);
		mobject.setTexture(mistex);
//		mobject.setShowBound(true);
		this.addChild(mobject);
		initAudio();
	}

	private void initAudio() {
		resource1 = audioMgr.createAudioResource("./sounds/missileSound1.wav", AudioResourceType.AUDIO_SAMPLE); 
		resource2 = audioMgr.createAudioResource("./sounds/missileImpact.wav", AudioResourceType.AUDIO_SAMPLE);
		missileSound =new Sound(resource1, SoundType.SOUND_EFFECT, 200, true); 
		missileSound.initialize(audioMgr);
		missileSound.setMaxDistance(150.0f); 
		missileSound.setMinDistance(15.0f); 
		missileSound.setRollOff(5.0f); 
		missileSound.setLocation(new Point3D(this.getWorldTranslation().getCol(3)));
		impactSound = new Sound(resource2, SoundType.SOUND_EFFECT, 100, true);
		impactSound.initialize(audioMgr);
		impactSound.setMaxDistance(150.0f); 
		impactSound.setMinDistance(15.0f); 
		impactSound.setRollOff(5.0f);		
	}
	public void setSoundLocation(){
		
	}
	public void myRotate(float r, Vector3D v){
		rotate += r;
		rotate = rotate %360;
		this.rotate(r, v);
	}
	public float getMyRotate(){
		return rotate;
	}
	public void updateRot(Matrix3D m){
		mobject.setLocalRotation(m);
	}
	public void setLifetime(int l){
		lifetime += l;
	}
	public int getLifetime(){
		return lifetime;
	}
	private void setSpeed(float t){
		missilespeed += t;
	}
	public float getSpeed(){
		return missilespeed;
	}
	public void moveMissile(float time){
		Matrix3D rot = this.getLocalRotation();
		Vector3D dir = new Vector3D(0,0,1);
		dir = dir.mult(rot);
		dir.scale((double)(missilespeed*20));
		this.translate(-(float)dir.getX(), (float)dir.getY(), -(float)dir.getZ());
		this.setLifetime(-2);
		missileSound.setLocation(new Point3D(this.getWorldTranslation().getCol(3)));
	}
	public void setbeingUsed(boolean u){
		used  = u;
		if(used == false){
			this.setLocalTranslation(new Matrix3D());
			this.setLocalRotation(new Matrix3D());
			this.setWorldTranslation(new Matrix3D());
			this.setWorldRotation(new Matrix3D());
		}
	}
	public void setbeingUsed(boolean u, MyTank t){
		used  = u;
		if(used == true){
			missilespeed = .065f;
			lifetime = 160;
			this.setOwner(tmode);
			this.rotate(180, new Vector3D(0,1,0));	// Flip the missile since we flip the tank 
			Matrix3D md = new Matrix3D();
			Matrix3D mr = new Matrix3D();
			
			md.concatenate((Matrix3D) t.getWorldTranslation().clone());	// Tanks translation
			mr.concatenate((Matrix3D) t.getWorldRotation().clone());	// Tanks rotation
			md.concatenate(mr);	// Multiples the location with the rotation

			this.setLocalTranslation(md);  // Translate to tanks location, without this puts it at the origin
			this.translate(0f, 2.8f, 5f);  // Once the setLocalTranslation is set this translates to the level of the cannon
			missileSound.play(150, false); // Play the missile sound
		}
		else missileSound.stop();
	}
	public void setbeingUsed(boolean u, GhostAvatar t){
		used  = u;
		if(used == true){
			missilespeed = .065f;
			lifetime = 160;
			this.setOwner(gmode);
			this.rotate(180, new Vector3D(0,1,0));	// Flip the missile since we flip the tank 
			Matrix3D md = new Matrix3D();
			Matrix3D mr = new Matrix3D();
			
			md.concatenate((Matrix3D) t.getWorldTranslation().clone());	// Tanks translation
			mr.concatenate((Matrix3D) t.getWorldRotation().clone());	// Tanks rotation
			md.concatenate(mr);	// Multiples the location with the rotation

			this.setLocalTranslation(md);  // Translate to tanks location, without this puts it at the origin
			this.translate(0f, 2.8f, 5f);  // Once the setLocalTranslation is set this translates to the level of the cannon
			missileSound.play(150, false); // Play the missile sound
		}
		else missileSound.stop();
	}
	public void setbeingUsed(boolean u, EnemyTurret t){
		used = u;
		if(used == true){
			lifetime = 160;
			this.setOwner(rmode);
			this.rotate(-90, new Vector3D(0,1,0));	// Flip the missile since we flip the tank 
			Matrix3D md = new Matrix3D();
			Matrix3D mr = new Matrix3D();
			Group cannon = null;
			Iterator iter = t.getChildren();
			while(iter.hasNext()) {
				SceneNode node = (SceneNode) iter.next();
//				System.out.println(node.toString());
				if(node instanceof Group) {
					cannon = (Group) node;
				}
			}
			md.concatenate((Matrix3D) t.getChild("./obj/TurretBase.obj").getWorldTranslation());	// Tanks translation		
			mr.concatenate((Matrix3D) cannon.getWorldRotation());	// Tanks rotation
			md.concatenate(mr);	// Multiples the location with the rotation

			this.setLocalTranslation(md);  // Translate to tanks location, without this puts it at the origin
			this.translate(3f, 2.3f, 0.4f);  // Once the setLocalTranslation is set this translates to the level of the cannon
			missileSound.play(150, false); // Play the missile sound
		}
		else missileSound.stop();
	}
	public boolean getBeingUsed(){
		return used;
	}
	public void setOwner(String s){
		owner = s;
	}
	public String getOwner(){
		return owner;
	}
	public void stopMissileSound() {
		missileSound.stop();
	}
	@Override
	public boolean handleEvent(IGameEvent event) {
		missileSound.stop();
		impactSound.setLocation(new Point3D(this.getWorldTranslation().getCol(3)));
		impactSound.play(200, false);
		return false;
	}
}
