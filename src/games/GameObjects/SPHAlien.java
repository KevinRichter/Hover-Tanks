package games.GameObjects;

import sage.model.loader.ogreXML.*;
import sage.scene.Group;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.scene.TriMesh;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.util.Random;
import java.util.TreeMap;

public class SPHAlien extends Group{
	int spawnpoint;
	float spawnx = 1, spawny = 1, spawnz = 1;
	private Group galien;
	private Model3DTriMesh alien;
	private Texture aTex;
	private float speeds = 0.01f, turn, desiredHeight, previousHeight, heightDelta;
	private Random randomnum = new Random();
	private int randrot;
	private TerrainBlock terrain;
	
	public SPHAlien(int sp){
		spawnpoint = sp;
		galien = new Group();
		
		OgreXMLParser oxmloader = new OgreXMLParser();
		try{
			galien = oxmloader.loadModel("./obj/SPHAlien.mesh.xml", "./obj/SPHAlienPaint.material", "./obj/SPHAlien.skeleton.xml");
			galien.updateGeometricState(0, true);
			java.util.Iterator<SceneNode> modeliterator = galien.iterator();
			alien = (Model3DTriMesh) modeliterator.next();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		alien.translate(setSpawnX(), setSpawnY(), setSpawnZ());
		aTex = TextureManager.loadTexture2D("./images/roundedbody.jpg");
		alien.setTexture(aTex);
		galien.addChild(alien);
		this.addChild(galien);
//		System.out.println("Alien animations="+alien.getAnimations());
		
		
//		OgreXMLAnimationParser aniloader = new OgreXMLAnimationParser(galien);
		
	}
	public SPHAlien(int sp, TerrainBlock t){
		spawnpoint = sp;
		galien = new Group();
		terrain = t;
		
		OgreXMLParser oxmloader = new OgreXMLParser();
		try{
			galien = oxmloader.loadModel("./obj/SPHAlien.mesh.xml", "./obj/SPHAlienPaint.material", "./obj/SPHAlien.skeleton.xml");
			galien.updateGeometricState(0, true);
			java.util.Iterator<SceneNode> modeliterator = galien.iterator();
			alien = (Model3DTriMesh) modeliterator.next();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		alien.scale(2, 2, 2);
		alien.translate(setSpawnX(), setSpawnY(), setSpawnZ());
		aTex = TextureManager.loadTexture2D("./images/roundedbody.jpg");
		alien.setTexture(aTex);
		galien.addChild(alien);
		this.addChild(galien);
//		System.out.println("Alien animations="+alien.getAnimations());
		
		
//		OgreXMLAnimationParser aniloader = new OgreXMLAnimationParser(galien);
		
	}
	public Model3DTriMesh getSPHTrimesh(){
		return alien;
	}
	public void walkAni(){
		alien.stopAnimation();
		alien.startAnimation("NormalWalk");
	}
	public void afraidAni(){
		alien.stopAnimation();
		alien.startAnimation("Afraid");
	}
	private float setSpawnX(){
		switch(spawnpoint){
		case 1:
			spawnx = 45;
			return spawnx;
		case 2:
			spawnx = 60;
			return spawnx;
		}
		return spawnx;
	}
	private float setSpawnY(){
		switch(spawnpoint){
		case 1:
			spawny = 0f;
			return spawny;
		case 2:
			spawny = 0f;
			return spawny;
		}
		return spawny;
	}
	private float setSpawnZ(){
		switch(spawnpoint){
		case 1:
			spawnz = 45;
			return spawnz;
		case 2:
			spawnz = 60;
			return spawnz;
		}
		return spawnz;
	}
	public void moveAlien(float time){
		// Rotation of the alien
		Matrix3D rot = alien.getLocalRotation();
		Vector3D dir = new Vector3D(0,1,0);
		dir = dir.mult(rot);
		dir.scale((double)speeds*5);
		turn = (time%50);
//		System.out.println("SPHA-move-turn"+turn);
		if(turn < 1){
//			System.out.println("SPHA-move-turn"+turn);
			randrot = randomnum.nextInt(90);
			alien.rotate(randrot, dir);
		}
		// Translation of the alien
		Matrix3D rot2 = alien.getLocalRotation();
		Vector3D dir2 = new Vector3D(0,0,1);
		dir2 = dir2.mult(rot2);
		dir2.scale((double)speeds*20);
		alien.translate(-(float)dir2.getX(), (float)dir2.getY(), -(float)dir2.getZ());
		// Update the Vertical position of the alien
		Point3D aloc = new Point3D(alien.getLocalTranslation().getCol(3));
		float x = (float) aloc.getX();
		float z = (float) aloc.getZ();
		x = x - (-terrain.getSize()/2);
		z = z - (-terrain.getSize()/2);
		float terHeight = terrain.getHeight(x,z);
		previousHeight = desiredHeight;
		desiredHeight = terHeight + (float)terrain.getOrigin().getY() + 0f;
		heightDelta = desiredHeight - previousHeight;
		alien.getLocalTranslation().setElementAt(1,3,desiredHeight);
		
	}

}
