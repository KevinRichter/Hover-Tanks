importClass(Packages.sage.scene.SkyBox); 
importClass(Packages.sage.scene.shape.Quad); 
importClass(Packages.sage.scene.state.TextureState); 
importClass(Packages.sage.scene.state.ZBufferState); 
importClass(Packages.sage.scene.state.RenderState.RenderStateType); 
importClass(Packages.sage.renderer.IRenderer);
importClass(Packages.sage.texture.Texture); 
importClass(Packages.sage.texture.TextureManager);

SkyBox sBox = new SkyBox("SBox", 500f,500f,500f);

Texture northTex = TextureManager.loadTexture2D("./images/north.bmp");
Texture westTex = TextureManager.loadTexture2D("./images/west.bmp");
Texture eastTex = TextureManager.loadTexture2D("./images/east.bmp");
Texture southTex = TextureManager.loadTexture2D("./images/south.bmp");
Texture upTex = TextureManager.loadTexture2D("./images/up.bmp");

sBox.setTexture(SkyBox.Face.North, northTex);
sBox.setTexture(SkyBox.Face.West, westTex);
sBox.setTexture(SkyBox.Face.East, eastTex);
sBox.setTexture(SkyBox.Face.South, southTex);
sBox.setTexture(SkyBox.Face.Up, upTex);