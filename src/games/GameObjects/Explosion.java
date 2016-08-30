package games.GameObjects;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.TriMesh;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class Explosion extends Group{
	private Texture mistex = TextureManager.loadTexture2D("./images/explosion1.jpg");;
	private OBJLoader loader = new OBJLoader();
	private TriMesh mobject = loader.loadModel("./obj/explosion1.obj");
	private int lifetime = 50;
	private boolean used = false;
	
	public Explosion(){
//		mobject.scale(5f, 5f, 5f);
		mobject.setTexture(mistex);
		this.addChild(mobject);
	}
	public void setLifetime(){
		lifetime -= 1;
	}
	public int getLifetime(){
		return lifetime;
	}
	public boolean getBeingUsed(){
		return used;
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
	public void setbeingUsed(boolean u, Missile t){
		used  = u;
		if(used == true){
			lifetime = 30;
			this.rotate(180, new Vector3D(0,1,0));	// Flip the missile since we flip the tank 
			Matrix3D md = new Matrix3D();
			Matrix3D mr = new Matrix3D();
			
			md.concatenate((Matrix3D) t.getWorldTranslation().clone());	// Tanks translation
			mr.concatenate((Matrix3D) t.getWorldRotation().clone());	// Tanks rotation
			md.concatenate(mr);	// Multiples the location with the rotation

			this.setLocalTranslation(md);  // Translate to tanks location, without this puts it at the origin
			this.translate(0f, 0f, -4f);  // Once the setLocalTranslation is set this translates to the level of the cannon
		}
	}
}
