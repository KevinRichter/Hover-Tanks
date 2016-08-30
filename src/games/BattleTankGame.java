package games;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import gameEngine.display.Camera3Pcontroller;
import gameEngine.display.MyDisplaySystem;
import gameEngine.event.MissileImpactEvent;
import gameEngine.input.action.FireMissileAction;
import gameEngine.input.action.MoveAvatarForwardAction;
import gameEngine.input.action.MoveAvatarLeftRightAction;
import games.GameObjects.EnemyTurret;
import games.GameObjects.Explosion;
import games.GameObjects.GhostAvatar;
import games.GameObjects.Missile;
import games.GameObjects.MyTank;
import games.GameObjects.SPHAlien;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.app.BaseGame;
import sage.audio.AudioManagerFactory;
import sage.audio.AudioResource;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.model.loader.OBJLoader;
import sage.networking.IGameConnection.ProtocolType;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.HUDString;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.scene.SkyBox;
import sage.scene.bounding.BoundingVolume;
import sage.scene.shape.Line;
import sage.scene.shape.Rectangle;
import sage.scene.state.RenderState;
import sage.scene.state.RenderState.RenderStateType;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.ImageBasedHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.*;
import sage.input.*;
import sage.input.action.*;
import sage.event.*;
import net.java.games.input.*;
import networking.client.MyClient;

public class BattleTankGame extends BaseGame{
	BoundingVolume bd;
	Camera3Pcontroller cc1, cc, cc2;
	Controller myCont;
	Group player1;
	Group playerG;
	Group galien;
	IAudioManager audioMgr;
	ICamera camera1;
	IDisplaySystem display;
	IEventManager eventMgr;
	IInputManager im;
	IRenderer renderer;
	Iterable<SceneNode> gameObjects, spinG, bounG;
	Iterable<SceneNode> removalList;
	Model3DTriMesh walien, aalien;
	MyClient myC;
	OBJLoader loader;
	Rectangle ground;
	SkyBox sBox;
	Sound tankSound1, tankSound2, ghostTankSound, turretSound; // moving sound sources
	TerrainBlock imageTerrain;
	TextureState texstate;
	
	private ArrayList<Explosion> explosivePool;
	private ArrayList<Missile> missilePool;
	private ArrayList<SceneNode> removeList;
	private ArrayList<SceneNode> addList;
	private Color gColor = new Color(255,255,255);
	private File scriptFile;
	private Explosion ep, explosion;
	private GraphicsDevice device;
	private HUDString p1HUD, timeString;
	private InetAddress serverAddress;
	private JTextField sName;
	private Missile missile, mp;
	private ProtocolType serverProtocol;
	private Random randomnum = new Random();
	private ScriptEngine engine;
	
	private boolean windowMode;
	private int tankcolor, tanktype;
	private int p1score = 0, p1Health = 10,  i;
	private int scrnheight, scrnwidth, scrnbitdepth, scrnrefreshrate;
	private int serverPort,colorchoice, typechoice;
	private int maxmissiles = 10;	// Sets the max missiles for the pool of missiles
	private long fileLastModifiedTime = 0;
	private float speed = 0.03f;
	private float time = 0;
	private String screenName = "player";
	private String laptopKB = "Keyboard Device Filter";
	private String kbName, gpName;
	private String scriptName = "./src/scripts/CreateWorld.js"; 
	private String[] TYPECHOICE = {"Light", "Heavy"};
	private String[] COLORCHOICES = {"Red", "Green", "Blue", "Gray", "Yellow", "Purple"};
	private boolean gameOver = false;
	
	// This game associates specific IActions with controller components.
	// It assumes there is both a keyboard and a gamepad device connected.
	public BattleTankGame(){
		
		super();
		JPanel info = new JPanel();
		JTextField ipAddr = new JTextField(15);
		JTextField portNum = new JTextField(15);
		JComboBox typelist = new JComboBox(TYPECHOICE);
		JComboBox colorlist = new JComboBox(COLORCHOICES);
		JRadioButton windowedMode = new JRadioButton("Check for Full Screen Mode");
		sName = new JTextField(15);
		ipAddr.setText("192.168.1.102");
//		ipAddr.setText("192.168.1.115");
//		ipAddr.setText("192.168.1.69");
		portNum.setText("30001");
		sName.setText("player");
		info.add(new JLabel("Enter the server IP:"));
		info.add(ipAddr);
		info.add(new JLabel("Enter the server port:"));
		info.add(portNum);
		info.add(new JLabel("Enter your name:"));
		info.add(sName);
		info.add(windowedMode);
		info.add(typelist);
		info.add(colorlist);
		
		int infostate = JOptionPane.showConfirmDialog(null, info, "Information: ", JOptionPane.OK_CANCEL_OPTION);
		String inetAddr = ipAddr.getText();
		String portStr = portNum.getText();
		screenName = sName.getText();
		typechoice = typelist.getSelectedIndex() + 1;
		colorchoice = colorlist.getSelectedIndex();
		windowMode = windowedMode.isSelected();
		System.out.println("infostate="+infostate+"-inetAddr="+inetAddr+"-portStr="+portStr+"-screenName="+screenName+"-colorindex="+colorchoice);
		try {
			this.serverAddress = InetAddress.getByName(inetAddr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.serverPort = Integer.parseInt(portStr);
		this.serverProtocol = ProtocolType.TCP;
		if(infostate == 0){
			start();
		}
		else{
			System.out.println("OK was not pressed...GoodBye!");
		}
	}
	protected void initGame(){
		eventMgr = EventManager.getInstance();
		gameObjects = this.getGameWorld();
		removeList = new ArrayList<SceneNode>();
		addList = new ArrayList<SceneNode>();
//		display = getDisplaySystem();		//for window mode
		display = createDisplaySystem();	//for full screen mode
		renderer = getDisplaySystem().getRenderer();		
		
		initSky();
		initTerrain();
		initAudio();
		createPlayer();
		initNetwork();
		initInputs();		
		initGameObjects();		
		initScript();
				
		display.setTitle("BATTLE HOVER TANKS");
		
		// Create HUD objects
		timeString = new HUDString("Time= "+time);
		timeString.setLocation(.01, 0.15);
		addGameWorldObject(timeString);
		
	}
	private void initAudio() {		 
		 audioMgr = AudioManagerFactory.createAudioManager("sage.audio.joal.JOALAudioManager"); 
		 if(!audioMgr.initialize()) 
		 { 
			 System.out.println("Audio Manager failed to initialize!"); 
			 return; 
		 } 		
	}
	public void setEarParameters(){ 
		 Matrix3D avDir = (Matrix3D) (player1.getWorldRotation().clone()); 
		 float camAz = cc1.getAzimuth(); 
		 avDir.rotateY(camAz); 
		 Vector3D camDir = new Vector3D(0,0,1); 
		 camDir = camDir.mult(avDir); 
		 audioMgr.getEar().setLocation(camera1.getLocation()); 
		 audioMgr.getEar().setOrientation(camDir, new Vector3D(0,1,0)); 
	} 
	private void initScript() {
		System.out.println("initScript entered");
		ScriptEngineManager factory = new ScriptEngineManager(); 		
		
		// get the JavaScript engine 
		engine = factory.getEngineByName("js");
		scriptFile = new File(scriptName); 
		
		// run the script 
		this.runScript();
//		this.changeSky(engine.get("sky"));	
		 
		fileLastModifiedTime = scriptFile.lastModified();
		System.out.println("initScript exited");
	}
	private void runScript(){ 
		try { 
			FileReader fileReader = new FileReader(scriptFile); 
			engine.eval(fileReader); 
			fileReader.close(); 
		} catch (FileNotFoundException e1) { 
			System.out.println(scriptFile + " not found " + e1); 
		} catch (IOException e2) { 
			System.out.println("IO problem with " + scriptFile + e2); 
		} catch (ScriptException e3) { 
			System.out.println("ScriptException in " + scriptFile + e3); 
		} catch (NullPointerException e4) { 
			System.out.println ("Null ptr exception reading " + scriptFile + e4); 
		} 
	} 
	private IDisplaySystem createDisplaySystem(){
		// Gets the monitor settings to use current settings to create the display
		GraphicsEnvironment enviroment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = enviroment.getDefaultScreenDevice();
		DisplayMode curMode = device.getDisplayMode();
		scrnheight = curMode.getHeight();
		scrnwidth = curMode.getWidth();
		scrnbitdepth = curMode.getBitDepth();
		scrnrefreshrate = curMode.getRefreshRate();
		
		IDisplaySystem mdisplay = new MyDisplaySystem(scrnwidth,scrnheight,scrnbitdepth,scrnrefreshrate,windowMode,"sage.renderer.jogl.JOGLRenderer");
		System.out.println("\nWaiting for display creation...");
		int count = 0;
		
		while(!mdisplay.isCreated()){
			try{
				Thread.sleep(10);}
			catch(InterruptedException e){
				throw new RuntimeException("Unable to create display.");
			}
			
			count++;
			System.out.println("+");
			if(count % 80 == 0){
				System.out.println();
			}
			if(count > 2000){
				throw new RuntimeException("Unable to create display");
			}
		}
		System.out.println("Display created.");
		return mdisplay;
	}
	private void initTerrain(){ 
		// create height map and terrain block 
		System.out.println("initTerrain entered");
		ImageBasedHeightMap myHeightMap = new ImageBasedHeightMap("./images/testterrain1.jpg"); 
		myHeightMap.setHeightScale(0.25f);
		 
		imageTerrain = createTerBlock(myHeightMap); 
		 
		// create texture and texture state to color the terrain 
		TextureState grassState; 
		Texture grassTexture = TextureManager.loadTexture2D("./images/mars1.jpg"); 
		grassTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace); 
		grassState = (TextureState) renderer.createRenderState(RenderStateType.Texture); 
		grassState.setTexture(grassTexture,0); 
		grassState.setEnabled(true); 
		 
		// apply the texture to the terrain 
		imageTerrain.setRenderState(grassState); 
		addGameWorldObject(imageTerrain);
		System.out.println("initTerrain exited");
	} 
	private TerrainBlock createTerBlock(AbstractHeightMap heightMap) { 
		 float heightScale = .1f; 
		 Vector3D terrainScale = new Vector3D(1, heightScale, 1); 
		 
		 // use the size of the height map as the size of the terrain 
		 int terrainSize = heightMap.getSize(); 
		 
		 // specify terrain origin so heightmap (0,0) is at world origin 
		 float cornerHeight = heightMap.getTrueHeightAtPoint(0, 0) * heightScale; 
		 Point3D terrainOrigin = new Point3D(-heightMap.getSize()/2, -cornerHeight, -heightMap.getSize()/2); 
//		 Point3D terrainOrigin = new Point3D(0, -cornerHeight, 0);
		 
		 // create a terrain block using the height map 
		 String name = "Terrain:" + heightMap.getClass().getSimpleName(); 
		 TerrainBlock tb = new TerrainBlock(name, terrainSize, terrainScale, 
				 							heightMap.getHeightData(), terrainOrigin); 
		 return tb; 
	}
	private void initSky(){
		System.out.println("initSky entered");
//		Quad[] skyboxQuads = new Quad[6];
		sBox = new SkyBox("SBox", 500f,500f,500f);
		
		Texture northTex = TextureManager.loadTexture2D("./images/north0.jpg");
		Texture westTex = TextureManager.loadTexture2D("./images/west0.jpg");
		Texture eastTex = TextureManager.loadTexture2D("./images/east0.jpg");
		Texture southTex = TextureManager.loadTexture2D("./images/south0.jpg");
		Texture upTex = TextureManager.loadTexture2D("./images/up0.jpg");
		Texture downTex = TextureManager.loadTexture2D("./images/down0.jpg");
		
		sBox.setTexture(SkyBox.Face.North, northTex);
		sBox.setTexture(SkyBox.Face.West, westTex);
		sBox.setTexture(SkyBox.Face.East, eastTex);
		sBox.setTexture(SkyBox.Face.South, southTex);
		sBox.setTexture(SkyBox.Face.Up, upTex);
		sBox.setTexture(SkyBox.Face.Down, downTex);
		
		addGameWorldObject(sBox);
		System.out.println("initSky exited");
	}
	private void createPlayer(){
		System.out.println("createPlayer entered");
		
		texstate = (TextureState)display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
		player1 = new MyTank(colorchoice, typechoice, audioMgr);
				
		player1.scale(1.1f, 1.1f, 1.1f);
			
		player1.rotate(180, new Vector3D(0,1,0));
		player1.translate(50, 0, 100);

		addGameWorldObject(player1);
		
		player1.getChildren();
			
		camera1 = new JOGLCamera(renderer);
		camera1.setPerspectiveFrustum(45, 1, 1, 1000);
		createPlayerHUD();
		System.out.println("createPlayer exited");
	}
	private void createPlayerHUD(){
		p1HUD = new HUDString(screenName);
		p1HUD.setName("Pla");
		p1HUD.setLocation(0.01, 0.06);
		p1HUD.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		p1HUD.setColor(Color.red);
		p1HUD.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		camera1.addToHUD(p1HUD);
	}
	protected void initInputs(){
		System.out.println("initInputs entered");
		// get the InputManager defined in the parent (BaseGame) class
		im = getInputManager();
		// get controller names for the keyboard and gamepad
		kbName = im.getKeyboardName();
		myCont = im.getControllerByName(laptopKB);
		gpName = im.getFirstGamepadName();

		// build some action objects for doing things in response to user input
		IAction quitGame = new QuitGameAction(this);
		IAction fireMissile = new FireMissileAction(this, (MyTank) player1, myC);
		MoveAvatarForwardAction mvA1Forward = new MoveAvatarForwardAction(player1, speed, imageTerrain, myC);
		MoveAvatarLeftRightAction mvA1LR = new MoveAvatarLeftRightAction(player1, speed, imageTerrain, myC);
//		SetSpeedAction setSpeed = new SetSpeedAction();

		if(gpName != null){
			cc1 = new Camera3Pcontroller(camera1, player1, im, gpName);
			
			// Gamepad Controls
			// controls camera moving foward and backward
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y,
					mvA1Forward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			// controla the camera turning left to right
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, 
					mvA1LR, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			//	Fires using the right trigger button
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Z, 
					fireMissile, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._5, 
					fireMissile, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			//	allows for quitting the game from the controller
			im.associateAction (gpName, net.java.games.input.Component.Identifier.Button._6,
					quitGame, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			// allows the camera to be reset to original location
//			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._7,
//				hCamLoc, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//			im.associateAction (gpName, net.java.games.input.Component.Identifier.Button._4,
//					sA1LR, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//			im.associateAction (gpName, net.java.games.input.Component.Identifier.Button._5,
//					sA1LR, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		}
		//	Attaches a keyboard if there is no game pad
		else{
			if(myCont != null){
				cc1 = new Camera3Pcontroller(camera1, player1, im, laptopKB);
			}
			else{
				cc1 = new Camera3Pcontroller(camera1, player1, im, kbName);
			}
		}
		if(myCont != null){
			// Keyboard for my laptop
			im.associateAction (laptopKB, net.java.games.input.Component.Identifier.Key.ESCAPE, 
					quitGame, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			im.associateAction(laptopKB, net.java.games.input.Component.Identifier.Key.W, 
				mvA1Forward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);			
			im.associateAction(laptopKB, net.java.games.input.Component.Identifier.Key.S, 
				mvA1Forward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(laptopKB, net.java.games.input.Component.Identifier.Key.A, 
				mvA1LR, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(laptopKB, net.java.games.input.Component.Identifier.Key.D, 
				mvA1LR, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(laptopKB, net.java.games.input.Component.Identifier.Key.F, 
					fireMissile, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
		}
		else{
		// Keyboard Controls
		// First Keyboard Controls
		im.associateAction (kbName, net.java.games.input.Component.Identifier.Key.ESCAPE, 
				quitGame, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, 
				mvA1Forward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, 
				mvA1Forward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, 
				mvA1LR, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, 
				mvA1LR, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.F, 
				fireMissile, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
		}
		System.out.println("initInputs exited");
	}
	private void initGameObjects(){
		System.out.println("initGameObjects entered");
		// Axes colors
		Point3D origin = new Point3D(0,0,0);
		Point3D xEnd = new Point3D(100,0,0);
		Point3D yEnd = new Point3D(0,100,0);
		Point3D zEnd = new Point3D(0,0,100);
		Point3D negXEnd = new Point3D(-100,0,0);
		Point3D negYEnd = new Point3D(0,-100,0);
		Point3D negZEnd = new Point3D(0,0,-100);
		Line xAxis = new Line (origin, xEnd, Color.red, 2);
		Line yAxis = new Line (origin, yEnd, Color.green, 2);
		Line zAxis = new Line (origin, zEnd, Color.blue, 2);
		Line negXAxis = new Line (origin, negXEnd, Color.pink, 2);
		Line negYAxis = new Line (origin, negYEnd, Color.cyan, 2);
		Line negZAxis = new Line (origin, negZEnd, Color.magenta, 2);
		addGameWorldObject(xAxis); 
		addGameWorldObject(yAxis);
		addGameWorldObject(zAxis);
		addGameWorldObject(negXAxis);
		addGameWorldObject(negYAxis);
		addGameWorldObject(negZAxis);
		
		//Create Alien
		SPHAlien wgalien = new SPHAlien(1, imageTerrain);
		walien = wgalien.getSPHTrimesh();
		addGameWorldObject(wgalien);
		wgalien.walkAni();
		SPHAlien agalien = new SPHAlien(2, imageTerrain);
		aalien = agalien.getSPHTrimesh();
		addGameWorldObject(agalien);
		agalien.afraidAni();
		
		// Create a pool of missiles to pull from
		missilePool = new ArrayList<Missile>();
		for(i=0;i<maxmissiles;i++){
			missile = new Missile(eventMgr, audioMgr);
			missilePool.add(missile);
		}
		// Create a pool of explosions for the missiles
		explosivePool = new ArrayList<Explosion>();
		for(i=0;i<maxmissiles;i++){
			explosion = new Explosion();
			explosivePool.add(explosion);
		}
		super.update(0);
		System.out.println("initGameObjects exited");
	}
	private void initNetwork() {
		System.out.println("initNetwork entered");
		try {
			myC = new MyClient(serverAddress, serverPort, serverProtocol, this);
			myC.sendJoinMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("initNetwork exited");
	}
	protected void shutdown(){
		if (myC != null) {
			myC.sendByeMessage();
			try {
				myC.shutdown();
			} catch (IOException e) { e.printStackTrace(); }
		}
		// First release sounds 
		//tankSound1.release(audioMgr);
		 
		// Next release audio resources 
		//resource1.unload(); 
		 
		// Finally shut down the audio manager 
		audioMgr.shutdown();
		display.close();
	}
	protected void render(){
		renderer.setCamera(camera1);
		super.render();
	}
	public void update(float elapsedTimeMS){
		// check if the script has been modified 
		long modTime = scriptFile.lastModified(); 
		
		if (modTime > fileLastModifiedTime) { 
			fileLastModifiedTime = modTime; 
		    this.runScript(); 
			this.changeSky(engine.get("sky"));
			((MyTank)player1).setShipColor(engine.get("color"));
			myC.sendChangeSkyMessage(engine.get("sky"));
			myC.sendChangeShipColorMessage(engine.get("color"));
		} 

		p1HUD.setText(screenName + "     Score= "+p1score + "        Health= "+ p1Health);
		time+=elapsedTimeMS;
		DecimalFormat df = new DecimalFormat("0.0");
		timeString.setText("Time= "+df.format(time/1000));
		if (myC != null){
			myC.processPackets();
		}
		super.update(elapsedTimeMS);
		aalien.updateAnimation(elapsedTimeMS);
	 	walien.updateAnimation(elapsedTimeMS);
		// Find all ghost avatars and update their sounds
		Iterable<SceneNode> it = getGameWorld(); // Get iterable collection
		Iterator<SceneNode> iter = it.iterator(); // Get iterator for collection
		 
		while(iter.hasNext()) { // Iterate through collection to update ghost avatar locations for 3d sound
			SceneNode node = (SceneNode) iter.next();
		 	if (node instanceof GhostAvatar) 
		 	{
		 		((GhostAvatar) node).setSoundLocation(); 
		 	}
		 	else if(node instanceof Missile){
		 		if(((Missile)node).getLifetime() > 0 && ((Missile)node).getBeingUsed() == true){
		 			((Missile)node).moveMissile(time);
		 			// Check if a missile from a turret hit the player
		 			if (((Missile) node).getOwner().equals("turret") && node.getWorldBound().intersects(player1.getWorldBound())) {
			 			p1Health -= 2;
			 			explosion = this.getExplosionFromPool();
						explosion.setbeingUsed(true, ((Missile)node));
						addList.add(explosion);			 			
			 			((Missile) node).setbeingUsed(false);
			 			removeList.add(node);
			 			missilePool.add((Missile) node);
			 			MissileImpactEvent newCrash = new MissileImpactEvent(); 
						eventMgr.triggerEvent(newCrash);
						if(p1Health <= 0) {
							myC.sendGameOverMessage(p1score, screenName);
							gameOver  = true;
							cc1.endgameCameraPosition();
						}
			 		}
		 			//Check is a missile from a ghost avatar hit the player
		 			else if (((Missile) node).getOwner().equals("ghost") && node.getWorldBound().intersects(player1.getWorldBound())) {
		 				p1Health -= 4;
		 				explosion = this.getExplosionFromPool();
						explosion.setbeingUsed(true, ((Missile)node));
						addList.add(explosion);			 			
		 				((Missile) node).setbeingUsed(false);
			 			removeList.add(node);
			 			missilePool.add((Missile) node);
			 			MissileImpactEvent newCrash = new MissileImpactEvent(); 
						eventMgr.triggerEvent(newCrash);
						if(p1Health <= 0) {
							myC.sendGameOverMessage(p1score, screenName);
							gameOver  = true;
							cc1.endgameCameraPosition();
						}
		 			}
		 			// Check to see if players missile hit anything
		 			else if (((Missile) node).getOwner().equals("player")) {
		 				ArrayList<EnemyTurret> turrets = myC.getEnemyTurrets();
		 				//Loop through enemy turrets and see if any were hit by player missile
		 				for(int i = 0; i < turrets.size(); i++) {//Player missile hits turret
		 					if(node.getWorldBound().intersects(turrets.get(i).getWorldBound())){
		 						p1score += 12;
		 						explosion = this.getExplosionFromPool();
								explosion.setbeingUsed(true, ((Missile)node));
								addList.add(explosion);					 			
		 						((Missile) node).setbeingUsed(false);
		 			 			removeList.add(node);
		 			 			missilePool.add((Missile) node);
		 			 			MissileImpactEvent newCrash = new MissileImpactEvent(); 
								eventMgr.triggerEvent(newCrash);
		 					}		 					
		 				}
		 				// Loop through ghost avatars and see if a player missile hit them
		 				Vector<Group> ghosts = myC.getGhostAvatars();
		 				for(int i = 0; i < ghosts.size(); i++) {
		 					if(node.getWorldBound().intersects(ghosts.get(i).getWorldBound())){
		 						p1score += 25;
		 						explosion = this.getExplosionFromPool();
								explosion.setbeingUsed(true, ((Missile)node));
								addList.add(explosion);					 			
		 						((Missile) node).setbeingUsed(false);
		 			 			removeList.add(node);
		 			 			missilePool.add((Missile) node);
		 			 			MissileImpactEvent newCrash = new MissileImpactEvent(); 
								eventMgr.triggerEvent(newCrash);
		 					}
		 				}
		 			}
		 			//Check to see if any of the missiles hit the terrain
		 			else if (node.getWorldBound().contains(imageTerrain.getOrigin())){
		 				explosion = this.getExplosionFromPool();
						explosion.setbeingUsed(true, ((Missile)node));
						addList.add(explosion);
			 			((Missile) node).setbeingUsed(false);
			 			removeList.add(node);
			 			missilePool.add((Missile) node);
			 			MissileImpactEvent newCrash = new MissileImpactEvent(); 
						eventMgr.triggerEvent(newCrash);
		 			}
		 		}
		 		// If missile's lifetime has run out, get rid of it
		 		else{
		 			explosion = this.getExplosionFromPool();
					explosion.setbeingUsed(true, ((Missile)node));
					addList.add(explosion);
		 			((Missile) node).setbeingUsed(false);
		 			removeList.add(node);
		 			missilePool.add((Missile) node);
		 			MissileImpactEvent newCrash = new MissileImpactEvent(); 
					eventMgr.triggerEvent(newCrash);
		 		}		 		
		 	}		 	
		 	else if(node instanceof Explosion){
		 		if(((Explosion)node).getLifetime() > 0){
		 			((Explosion)node).setLifetime();
		 		}
		 		else{
		 			removeList.add(node);
		 			((Explosion)node).setbeingUsed(false);
		 			explosivePool.add((Explosion) node);
		 		}
		 	}
		 	else if(node instanceof SPHAlien){
		 	((SPHAlien) node).moveAlien(time);
		 	}
		}
		if(addList.isEmpty() == false){
			SceneNode rnode;
			for(i=0;i<addList.size();i++){
				rnode = addList.get(i);
				addList.remove(i);
				addGameWorldObject(rnode);
			}
		}
		if(removeList.isEmpty() == false){
			SceneNode rnode;
			for(i=0; i<removeList.size();i++){
				rnode = removeList.get(i);
				removeList.remove(i);
				removeGameWorldObject(rnode);
			}
		}	
		if(gameOver) this.removeGameWorldObject(player1);
		((MyTank) player1).setSoundLocation( );		
		setEarParameters();
		
		cc1.update(elapsedTimeMS);
	}
	public void changeSky(Object view) {
		System.out.println("changeSky entered");
		String q = view.toString();
		System.out.println(q);
		if(q.equals("0.0")){
			Texture northTex = TextureManager.loadTexture2D("./images/north0.jpg");
			Texture westTex = TextureManager.loadTexture2D("./images/west0.jpg");
			Texture eastTex = TextureManager.loadTexture2D("./images/east0.jpg");
			Texture southTex = TextureManager.loadTexture2D("./images/south0.jpg");
			Texture upTex = TextureManager.loadTexture2D("./images/up0.jpg");
			Texture downTex = TextureManager.loadTexture2D("./images/down0.jpg");
			
			sBox.setTexture(SkyBox.Face.North, northTex);
			sBox.setTexture(SkyBox.Face.West, westTex);
			sBox.setTexture(SkyBox.Face.East, eastTex);
			sBox.setTexture(SkyBox.Face.South, southTex);
			sBox.setTexture(SkyBox.Face.Up, upTex);
			sBox.setTexture(SkyBox.Face.Down, downTex);
		}
		else if(q.equals("1.0")){
			Texture northTex = TextureManager.loadTexture2D("./images/north1.jpg");
			Texture westTex = TextureManager.loadTexture2D("./images/west1.jpg");
			Texture eastTex = TextureManager.loadTexture2D("./images/east1.jpg");
			Texture southTex = TextureManager.loadTexture2D("./images/south1.jpg");
			Texture upTex = TextureManager.loadTexture2D("./images/up1.jpg");
			Texture downTex = TextureManager.loadTexture2D("./images/down1.jpg");
			
			sBox.setTexture(SkyBox.Face.North, northTex);
			sBox.setTexture(SkyBox.Face.West, westTex);
			sBox.setTexture(SkyBox.Face.East, eastTex);
			sBox.setTexture(SkyBox.Face.South, southTex);
			sBox.setTexture(SkyBox.Face.Up, upTex);
			sBox.setTexture(SkyBox.Face.Down, downTex);
		}
		else if(q.equals("2.0")){
			Texture northTex = TextureManager.loadTexture2D("./images/north2.jpg");
			Texture westTex = TextureManager.loadTexture2D("./images/west2.jpg");
			Texture eastTex = TextureManager.loadTexture2D("./images/east2.jpg");
			Texture southTex = TextureManager.loadTexture2D("./images/south2.jpg");
			Texture upTex = TextureManager.loadTexture2D("./images/up2.jpg");
			Texture downTex = TextureManager.loadTexture2D("./images/down2.jpg");
			
			sBox.setTexture(SkyBox.Face.North, northTex);
			sBox.setTexture(SkyBox.Face.West, westTex);
			sBox.setTexture(SkyBox.Face.East, eastTex);
			sBox.setTexture(SkyBox.Face.South, southTex);
			sBox.setTexture(SkyBox.Face.Up, upTex);
			sBox.setTexture(SkyBox.Face.Down, downTex);
		}
		else if(q.equals("3.0")){
			System.out.println("ChangeSky changed");
			Texture northTex = TextureManager.loadTexture2D("./images/north3.jpg");
			Texture westTex = TextureManager.loadTexture2D("./images/west3.jpg");
			Texture eastTex = TextureManager.loadTexture2D("./images/east3.jpg");
			Texture southTex = TextureManager.loadTexture2D("./images/south3.jpg");
			Texture upTex = TextureManager.loadTexture2D("./images/up3.jpg");
			Texture downTex = TextureManager.loadTexture2D("./images/down3.jpg");
			
			sBox.setTexture(SkyBox.Face.North, northTex);
			sBox.setTexture(SkyBox.Face.West, westTex);
			sBox.setTexture(SkyBox.Face.East, eastTex);
			sBox.setTexture(SkyBox.Face.South, southTex);
			sBox.setTexture(SkyBox.Face.Up, upTex);
			sBox.setTexture(SkyBox.Face.Down, downTex);
		}
		else {
			System.out.println("Invalid SkyBox value");
		}
		System.out.println("changeSky exited");
	}
	public void setIsConnected(boolean conn){
	}
	public void addGameWorldObject(SceneNode s){
		super.addGameWorldObject(s); 
	}
	public boolean removeGameWorldObject(SceneNode s){
		return super.removeGameWorldObject(s); 
	}
	public Vector3D getPlayerPosition() {
		Matrix3D p1Trans = player1.getLocalTranslation();
		Vector3D p1Vect = p1Trans.getCol(3);
		return p1Vect;
	}
	public float getPlayerRotation() {
		// TODO Auto-generated method stub
		return ((MyTank) player1).getMyRotate();
	}
	public int getPlayerColor(){
		return ((MyTank)player1).getTankColor();
	}
	public int getPlayerTankType(){
		return ((MyTank)player1).getTankType();
	}
	public void createGhostAvatar(UUID id, Vector3D ghostPos, float rotAmount, int color, int type) {
		tankcolor = color;
		tanktype = type;
		Group ghost = new GhostAvatar(tankcolor, id, tanktype, audioMgr);
		ghost.scale(1.1f, 1.1f, 1.1f);
		ghost.translate((float)ghostPos.getX(),1.5f, (float)ghostPos.getZ());
		ghost.rotate(rotAmount, new Vector3D(0,1,0));
//		System.out.println("THN-CreateghostAvatar-nodeM="+ghostPos);
		this.addGameWorldObject(ghost);
		myC.addGhostAvatar(ghost);
	}
	
	public IAudioManager getAudioManager(){
		return audioMgr;
	}
	
	public float getTime(){
		return time/1000;
	}
	public Missile getMissileFromPool(){
//		System.out.println("THN-getMissileFromPool="+missilePool.toString());
		if(missilePool.size() > 0){
			for(i=0;i<=missilePool.size();i++){
				mp = missilePool.get(i);
				if(mp.getBeingUsed()== false){
//					System.out.println("Missile being fired");
					missilePool.remove(i);
					break;
				}
			}
		}
		return mp;
	}
	public Explosion getExplosionFromPool(){
//		System.out.println("THN-getMissileFromPool="+missilePool.toString());
		if(explosivePool.size() > 0){
			for(i=0;i<=explosivePool.size();i++){
				ep = explosivePool.get(i);
				if(ep.getBeingUsed()== false){
//					System.out.println("Missile being fired");
					explosivePool.remove(i);
					break;
				}
			}
		}
		return ep;
	}
}