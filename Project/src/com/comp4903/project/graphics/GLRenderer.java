package com.comp4903.project.graphics;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;


import com.comp4903.project.GUI.GLText;
import com.comp4903.project.GUI.GameOver;
import com.comp4903.project.GUI.HUD;
import com.comp4903.project.GUI.MainMenu;
import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.GameState;
import com.comp4903.project.gameEngine.factory.MapFactory;
import com.comp4903.project.graphics.model.MaterialLibrary;
import com.comp4903.project.graphics.model.Model3D;
import com.comp4903.project.graphics.model.ModelLoader;
import com.comp4903.project.graphics.tile.Hexagon;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;

/* GLRENDERER
 * 
 * Represents the main rendering loop of the application
 * 
 * 
 * 
 */
public class GLRenderer implements android.opengl.GLSurfaceView.Renderer {

	public static int GLwidth;
	public static int GLheight;
	public static Point selectedTile = new Point(0,0);
	public static boolean isRenderingNow = false;
	public static boolean pauseRender = false;
	public static GameState state = GameState.Main_Menu;
	
	public boolean showmenu = false;
	
	public static float viewX; // co-ordinate of the location we are looking at
	public static float viewY;
	public static float viewZ;
	private float eyeX, eyeY, eyeZ; // location of camera
	private float distance; // distance of camera
	private float viewAngle; // angle of camera
	
	private float[] modelMatrix = new float[16];
	public static float[] viewMatrix = new float[16];
	public static float[] modelViewMatrix = new float[16]; // combined model+view, needed for openGL
	public static float[] projectionMatrix = new float[16];
	
	public Context context;
	
	private Hexagon hex; // get rid of this
	private Model3D[] testmodels;
	private int[][] tileset = new int[40][40]; // this too
	
	private float[] ambientLight = { 0.4f, 0.4f, 0.4f, 1 };
	private float[] diffuseLight = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] lightPosition = { 10.0f, 10.0f, 10.0f, 10.0f };
	
	public HUD headsUpDisplay;
	public MainMenu mm;
	public GameOver gov;
	public GL10 myGL;
	public Unit character = null;
	public boolean update= false;
	private MapData mapData;
	
	private GLText glText;    
	
	/*	GLRENDERER
	 * 
	 * Constructor, need to provide app context for access to
	 * resource files such as textures	 * 
	 */
	public GLRenderer(Context c, MapData md)
	{
		context = c;
		selectedTile.x = 0;
		selectedTile.y = 0;	
		mapData = md;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 *
	 *	Initialize surface when created.  Move this code into a more
	 *  appropriate location
	 *
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
		
		gl.glDisable(GL10.GL_DITHER);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glClearColor(.5f, .5f, .5f, 1);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
		
		gl.glFrontFace(GL10.GL_CW);		
		gl.glEnable(GL10.GL_CULL_FACE); 
		gl.glCullFace(GL10.GL_BACK);
		glText = new GLText( gl, context.getAssets() );
		glText.load( "Roboto-Regular.ttf", 14, 2, 2 );
		//hex = new Hexagon(gl, context);
		headsUpDisplay = new HUD(context, GLwidth, GLheight,glText);
		headsUpDisplay.initialBoxTexture(gl);
		
		mm = new MainMenu(context,GLwidth,GLheight);
		mm.loadMenuTexture(gl);
		
		gov = new GameOver(context,GLwidth,GLheight);
		gov.loadGameOVerTexture(gl);
		
		MaterialLibrary.init(gl, context);
		
		RendererAccessor.init(gl,context);
						
		RendererAccessor.map.init(mapData.NumberOfColumns(), mapData.NumberOfRows());
		RendererAccessor.map.loadModels();
		RendererAccessor.map.defineMap(mapData);
		
		viewX = 20;
		viewY = 0;
		viewZ = 20;
		distance = 7;
		viewAngle = 0; //1.57f / 2f;
		
		//modeltest(gl);
		
	}	
	
	public void modeltest(GL10 gl) 
	{
		
		AssetManager am = context.getAssets();
		testmodels = new Model3D[5];
		
		for (int t = 0; t < 3; t++)
		{
			testmodels[t] = new Model3D();
			try {
				InputStream buf = null;
				if (t < 2)
					buf = am.open("models/testmodel.mdl");
				else
					buf = am.open("models/soldier.gmodel");
				ModelLoader.load(buf, testmodels[t]);
				testmodels[t].SetScale(.08f, .08f, .08f);
				testmodels[t].SetPosition(1, 1, 1);
				buf.close();
				
			} catch (IOException e)
			{ }			
		}
		testmodels[0].SetPosition(21,1,21);
		testmodels[1].YRotate(-0.4f);
		testmodels[1].SetPosition(23, 1, 23);
		testmodels[2].YRotate(1.6f);
		testmodels[2].XRotate(1.57f);
		testmodels[2].SetScale(1, 1, 1);
		testmodels[2].SetPosition(26, 0, 23);
		
	}
	
	/*
	 * ONDRAWFRAME(non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 * 
	 * Main drawing loop
	 */	
	public void onDrawFrame(GL10 gl) {
		
		if (pauseRender)
			return;
		
		isRenderingNow = true;
		
		switch (state)
		{
		case Main_Menu:
			drawMainMenu(gl);
			break;
		case Network_Menu:
			//drawNetworkMenu(gl);
			drawGameScreen(gl);
			break;
		case Game_Screen:
			drawGameScreen(gl);
			break;
		case Game_Over:
			drawGameOver(gl);
			break;
		}		
		
		isRenderingNow = false;
		
	}
	
	public void drawMainMenu(GL10 gl)
	{
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);			
		gl.glDisable(GL10.GL_DEPTH_TEST);
		headsUpDisplay.SwithToOrtho(gl);
		//headsUpDisplay.drawHUD(gl);
		mm.drawMainMenu(gl);
		headsUpDisplay.SwitchToPerspective(gl);
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}
	
	public void drawNetworkMenu(GL10 gl)
	{
		
	}
	public void drawGameOver(GL10 gl){
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);			
		gl.glDisable(GL10.GL_DEPTH_TEST);
		headsUpDisplay.SwithToOrtho(gl);
		//headsUpDisplay.drawHUD(gl);
		gov.DrawGameOver(gl);
		headsUpDisplay.SwitchToPerspective(gl);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
	}
	public void drawGameScreen(GL10 gl)
	{
		// clear the buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);		
						
		// compute camera position based on the target view point and
		// the distance
		eyeX = (float) ((Math.cos(viewAngle) - Math.sin(viewAngle)) * distance);		 
		eyeZ = (float) ((Math.sin(viewAngle) + Math.cos(viewAngle)) * distance);
		eyeX += viewX;
		eyeZ += viewZ;
		eyeY = distance*(distance / 7f);		
				
		Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, viewX, viewY, viewZ, 0f, 1f, 0f);
				
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		RendererAccessor.map.render(viewMatrix, projectionMatrix, viewX, viewY, viewZ);
			
		//draw(gl);
		
		gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);		
		headsUpDisplay.SwithToOrtho(gl);
		headsUpDisplay.drawHUD(gl);
		//gl.glDisable(GL10.GL_DEPTH_TEST);
		
		//gl.glEnable( GL10.GL_BLEND );                   // Enable Alpha Blend
		//gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );  // Set Alpha Blend Function
		
		//glText.begin( 1.0f, 1.0f, 1.0f, 1.0f );         // Begin Text Rendering (Set Color WHITE)
		//glText.setScale(4.0f);
		//glText.draw( "Attack!", 300, 200 );          // Draw Test String
		//glText.end();                                   // End Text Rendering
		
		gl.glDisable( GL10.GL_BLEND );                  // Disable Alpha Blend*/
		//mm.drawMainMenu(gl);
		headsUpDisplay.SwitchToPerspective(gl);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		//if(update){
			//headsUpDisplay.updateStatPanel(gl, character);
			//update = false;
		//}
		//headsUpDisplay.updateStatPanel(gl, character);
		
		
	}
	
	// Test routine to draw the models
	public void draw(GL10 gl)
	{
		
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLight, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseLight, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosition, 0);
		
		for (int t = 0; t < 3; t++)
			testmodels[t].display(gl, viewMatrix);
		gl.glDisable(GL10.GL_LIGHTING);
		
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
		// set window to screen size
		gl.glViewport(0, 0, width, height);
		
		GLwidth = width;
		GLheight = height;
				
		SetProjectionMatrix(gl);
		gl.glMatrixMode(GL10.GL_PROJECTION);	
		gl.glLoadMatrixf(projectionMatrix, 0);
		
		headsUpDisplay = new HUD(context, width, height,glText);
		headsUpDisplay.initialBoxTexture(gl);
	
		mm = new MainMenu(context,GLwidth,GLheight);
		mm.loadMenuTexture(gl);
		
		gov = new GameOver(context,GLwidth,GLheight);
		gov.loadGameOVerTexture(gl);
	} 
	
	// processes a scaling request ( which is interpreted as moving the camera closer
	// or farther from the map)
	// Amount > 1 moves closer
	// Amount < 1 moves out
	public void scaleRequest(float amount)
	{
		if (amount < 1)
			distance += 0.1f;
		if (amount > 1)
			distance -= 0.1f;
		
		if (distance < 6)
			distance = 6;
		
		if (distance > 10)
			distance = 10;	
	}
	
	// moves the view camera by an arbitrary amount
	public void cameraMoveRequest(float dx, float dy)
	{
		float xAngle = viewAngle + 1.57f;
		float zAngle = viewAngle;
		viewX += (float) ((Math.cos(xAngle) - Math.sin(xAngle)) * dx * distance * 0.002f );		 
		viewZ += (float) ((Math.sin(xAngle) + Math.cos(xAngle)) * dx * distance *0.002f );
		
		viewX -= (float) ((Math.cos(zAngle) - Math.sin(zAngle)) * dy * distance * 0.002f);		 
		viewZ -= (float) ((Math.sin(zAngle) + Math.cos(zAngle)) * dy * distance *0.002f);
		
		//viewX -= dx * 0.03f;
		//viewZ -= dy * 0.03f;
	}
	
	// a crazy way of doing picking.  Don't look too closely at this code.
	// Given two screen co-ordinates, will return
	// a Point, containing the x and y map co-ordinates selected.
	public Point pick(float x, float y)
	{			
		return RendererAccessor.map.pick(x, y);
	}
	
	// Makes tile x,y the selected tile
	public void selectTile(int x, int y)
	{
		RendererAccessor.map.selectTile(x, y);
		//selectedTile.x = x;
		//selectedTile.y = y;
	}
	
	// Makes tile x,y the selected tile
	// can be used directly with pick (ex...   selectTile(pick(screenX, screenY)); )
	public void selectTile(Point s)
	{
		RendererAccessor.map.selectTile(s.x, s.y);
		selectedTile = s;		
	}
	
	public void SetProjectionMatrix(GL10 gl)
	{		
		Matrix.setIdentityM(projectionMatrix, 0);

		projectionMatrix[15] = 0; // 3,3

		float near_plane = 1;
		float far_plane = 1000;
		
		//h_fov = 30.0 * ((float)width_ / (float)height_);
		//v_fov = 60.0 * ((float)height_ / (float)width_);

		float h_fov = (float) (30.0 * ((float)GLwidth / (float)GLheight));
		float v_fov = 30.0f;
		
		float aspect = (float)GLwidth / (float)GLheight;
		float f = (float) (1.0f / Math.tan(v_fov / 180.0f * 3.141593f * 0.5f));		

		float w = (float) (1.0f / Math.tan(h_fov / 180.0f * 3.141593f * 0.5f));
		float h = (float) (1.0f / Math.tan(v_fov / 180.0f * 3.141593f * 0.5f));
		float q = far_plane / (far_plane - near_plane);
		
		/*projectionMatrix[0] = w;
		projectionMatrix[5] = h; // 1, 1
		projectionMatrix[10] = q; // 2,2
		projectionMatrix[11] = -q*near_plane; // 3,2
		projectionMatrix[14] = 1; // 2,3*/	
		
		projectionMatrix[0] = f / aspect;
		projectionMatrix[5] = f;
		projectionMatrix[10] = (far_plane + near_plane) / (near_plane - far_plane);
		projectionMatrix[11] = -1;
		projectionMatrix[14] = (2f * far_plane * near_plane) / (near_plane - far_plane);
		projectionMatrix[15] = 0;
			
	}
	
	public boolean checkHUD(int x, int y){
		return this.headsUpDisplay.checkTouchingMenu(x, y);
	}
	
	/*
	 * return value from 1 to 3 to indicate which button is pressed
	 * 1- Attack button
	 * 2- Ability button
	 * 3- Wait button
	 */
	public int setSelectedHUD(int x , int y){
		/*if(flag){
			this.headsUpDisplay.action.pressed = true; 
			int result = this.headsUpDisplay.action.checkListItem(y);
			this.headsUpDisplay.action.menuSelected =  result;
			return result;
		}else{
			this.headsUpDisplay.action.pressed = false;
			this.headsUpDisplay.action.menuSelected = -1;
			return -1;
		}*/
		
		int result = headsUpDisplay.action.checkPressingBox(x, y);
		headsUpDisplay.action.menuSelected = result;
		return result;
	}
	
	
	public int setSelectMainMenu(int x, int y){
		
		if(!mm.checkPressingMenu(x, y)){
			mm.selected = -1;
			return -1;
		}else{
			int result = mm.checkListItem(y);
			mm.selected = result;
			return result;
		}
		
		
	}
	
	public void updateHUDPanel(Unit abc){
		character = abc;
		update = true;
		headsUpDisplay.updateStatPanel( abc);
	}
}
