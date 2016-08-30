package games.GameObjects;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.util.UUID;

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

public class MyTank extends Group{
	private boolean crash = false;
	private float rotate;
	private boolean lockCam = false;
	private UUID id;
	private Texture tanktex, cannontex;
	private int tcolor, ccolor, ttype;
	private TriMesh tobject, cobject;
	private Group cannong;
	private OBJLoader loader;
	private IAudioManager audioMgr;
	private Sound avatarSound;
	private AudioResource resource2;
	
	public MyTank(int c, int t, IAudioManager audMgr){
		audioMgr = audMgr;
		tcolor = c;
		rotate = 180;
		ttype = t;
		cannong = new Group();
		
//		this.loadModel(tankmod);
		if(t == 1){
			loader = new OBJLoader();
			tobject = loader.loadModel("./obj/tank1.obj");
			tobject.translate(0, 2, 0);
			setShipColor(tcolor, ttype);
			tobject.setTexture(tanktex);
			this.addChild(tobject);
		}
		else{
			loader = new OBJLoader();
			tobject = loader.loadModel("./obj/tank2.obj");
			tobject.translate(0, 2, 0);
			setShipColor(tcolor, ttype);
			tobject.setTexture(tanktex);
			this.addChild(tobject);
		}
		
		loader = new OBJLoader();
		cobject = loader.loadModel("./obj/cannon1.obj");
		cobject.translate(0, 2.8f, 0);
		cobject.scale(1.7f, 1.7f, 1.7f);
		setCannonColor(ccolor);
		cobject.setTexture(cannontex);
//		this.addChild(cobject);
		cannong.addChild(cobject);
		this.addChild(cannong);	
		initAudio();
	}

	private void initAudio() {
		resource2 = audioMgr.createAudioResource("./sounds/tankSound1.wav", AudioResourceType.AUDIO_SAMPLE); 
		avatarSound =new Sound(resource2, SoundType.SOUND_EFFECT, 200, true); 
		avatarSound.initialize(audioMgr);
		avatarSound.setMaxDistance(100.0f); 
		avatarSound.setMinDistance(5.0f); 
		avatarSound.setRollOff(5.0f); 
		avatarSound.setLocation(new Point3D(this.getWorldTranslation().getCol(3))); 
		avatarSound.play();
	}
	
	public void setSoundLocation() 
	{
		avatarSound.setLocation(new Point3D(this.getWorldTranslation().getCol(3)));
	}
	
	
	public void setShipColor(int color, int type){
		tcolor = color;
		ttype = type;
		if(ttype == 1){
			if(tcolor == 0){
				tanktex = TextureManager.loadTexture2D("./images/tank1color0.jpg");
			}
			else if(tcolor == 1){
				tanktex = TextureManager.loadTexture2D("./images/tank1color1.jpg");
			}
			else if(tcolor == 2){
				tanktex = TextureManager.loadTexture2D("./images/tank1color2.jpg");
			}
			else if(tcolor == 3){
				tanktex = TextureManager.loadTexture2D("./images/tank1color3.jpg");
			}
			else if(tcolor == 4){
				tanktex = TextureManager.loadTexture2D("./images/tank1color4.jpg");
			}
			else if(tcolor == 5){
				tanktex = TextureManager.loadTexture2D("./images/tank1color5.jpg");
			}
			else{
				System.out.println("Invalid ship color, no change");
			}
		}
		else{
			if(tcolor == 0){
				tanktex = TextureManager.loadTexture2D("./images/tank2color0.jpg");
			}
			else if(tcolor == 1){
				tanktex = TextureManager.loadTexture2D("./images/tank2color1.jpg");
			}
			else if(tcolor == 2){
				tanktex = TextureManager.loadTexture2D("./images/tank2color2.jpg");
			}
			else if(tcolor == 3){
				tanktex = TextureManager.loadTexture2D("./images/tank2color3.jpg");
			}
			else if(tcolor == 4){
				tanktex = TextureManager.loadTexture2D("./images/tank2color4.jpg");
			}
			else if(tcolor == 5){
				tanktex = TextureManager.loadTexture2D("./images/tank2color5.jpg");
			}
			else{
				System.out.println("Invalid ship color, no change");
			}
		}
	}
	public void setShipColor(Object color){
		
	}
	public void setCannonColor(int color){
		cannontex = TextureManager.loadTexture2D("./images/cannon1color1.jpg");
	}
	public int getTankColor(){
		return tcolor;
	}
	public int getTankType(){
		return ttype;
	}
	public int getCannonColor(){
		return ccolor;
	}
	public void myRotate(float r, Vector3D v3d){
		rotate += r;
		rotate = rotate %360;
		this.rotate(r, v3d);
	}
	public float getMyRotate(){
		return rotate;
	}
	public UUID getId(){
		return id;
	}
	public void setId(UUID id){
		this.id = id;
	}
	public void hasCrashed(){
		crash = true;
	}
	public boolean crashedState(){
		return crash;
	}
	public void camLock(boolean tf){
		lockCam = tf;
	}
	public boolean getCamLock(){
		return lockCam;
	}
}
