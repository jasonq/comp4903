
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
import com.comp4903.project.GUI.NetworkInterface;
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
		
	public static float[] viewMatrix = new float[16];
	public static float[] modelViewMatrix = new float[16]; // combined model+view, needed for openGL
	public static float[] projectionMatrix = new float[16];
	
	public Context context;
	
	public HUD headsUpDisplay;
	public MainMenu mm;
	public GameOver gov;
	public NetworkInterface network;
	
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
	 *	Initialize surface when created.  
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
		
		headsUpDisplay = new HUD(context, GLwidth, GLheight,glText);
		headsUpDisplay.initialBoxTexture(gl);
		
		mm = new MainMenu(context,GLwidth,GLheight);
		mm.loadMenuTexture(gl);
		
		gov = new GameOver(context,GLwidth,GLheight);
		gov.loadGameOVerTexture(gl);
		
		network = new NetworkInterface(context,GLwidth,GLheight);
		network.loadNetWorkingTexture(gl);
		
		MaterialLibrary.init(gl, context);
		
		RendererAccessor.init(gl,context);
						
		RendererAccessor.map.init(mapData.NumberOfColumns(), mapData.NumberOfRows());
		RendererAccessor.map.loadModels();
		RendererAccessor.map.defineMap(mapData);
				
		distance = 12;
		viewAngle = 2f;
	}	
		
	
	/*
	 * ONDRAWFRAME(non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 * 
	 * Main drawing loop, chooses which screen to draw (menu, game, etc..)
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
			drawNetworkMenu(gl);			
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
		
		RendererAccessor.map.floatingPass();
	}
	
	public void drawNetworkMenu(GL10 gl)
	{
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);			
		gl.glDisable(GL10.GL_DEPTH_TEST);		
		headsUpDisplay.SwithToOrtho(gl);
		//headsUpDisplay.drawHUD(gl);
		network.DrawNetWorking(gl);
		headsUpDisplay.SwitchToPerspective(gl);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		RendererAccessor.map.floatingPass();
	}
	public void drawGameOver(GL10 gl){
		drawGameScreen(gl);
		//gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);			
		gl.glDisable(GL10.GL_DEPTH_TEST);
		headsUpDisplay.SwithToOrtho(gl);
		//headsUpDisplay.drawHUD(gl);
		gov.DrawGameOver(gl);
		headsUpDisplay.SwitchToPerspective(gl);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
	}
	
	/* 	DRAWGAMESCREEN - Executes main rendering of the 3D scene through the
	 *  MapRenderer class instantiated in RendererAccessor.
	 *  
	 *  Also draws the HUD overlay
	 * 
	 */
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
		
		// adjust camera position, to move it closer to a distance of 8 from the view point
		if (distance > 8)
			distance -= 0.05f * (distance - 6)*0.3f;
		
		// bring angle of camera closer to zero
		if (viewAngle > 0)
			viewAngle -= 0.025f* (distance - 6)*0.3f;
				
		Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, viewX, viewY, viewZ, 0f, 1f, 0f);
				
		// Renders all the game related objects (units, map, etc...)
		// Main work is done in this method
		RendererAccessor.map.render(viewMatrix, projectionMatrix, viewX, viewY, viewZ);		
		
		gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);		
		headsUpDisplay.SwithToOrtho(gl);
		headsUpDisplay.drawHUD(gl);
				
		headsUpDisplay.SwitchToPerspective(gl);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		
		
		
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
		
		network = new NetworkInterface(context,GLwidth,GLheight);
		network.loadNetWorkingTexture(gl);
	} 
	
	// processes a scaling request ( which is interpreted as moving the camera closer
	// or farther from the map)
	// Amount > 1 moves closer
	// Amount < 1 moves out
	// I've disabled it for now, actually
	public void scaleRequest(float amount)
	{
		/*
		if (amount < 1)
			distance += 0.1f;
		if (amount > 1)
			distance -= 0.1f;
		
		if (distance < 6)
			distance = 6;
		
		if (distance > 10)
			distance = 10;
			*/	
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
		
		if (viewX < 0)
			viewX = 0;
		if (viewX > ((float)RendererAccessor.map.mapWidth - 1) * 1.5f)
			viewX = ((float)RendererAccessor.map.mapWidth - 1) * 1.5f;
		if (viewZ < 0)
			viewZ = 0;
		if (viewZ > ((float)RendererAccessor.map.mapHeight - 1) * 0.8660254038f * 2 + ((RendererAccessor.map.mapWidth-1) % 2) * 0.8660254038f)
			viewZ = ((float)RendererAccessor.map.mapHeight - 1) * 0.8660254038f * 2 + ((RendererAccessor.map.mapWidth-1) % 2) * 0.8660254038f;
					
	}
	
	/*	PCIK - Given two screen co-ordinates, will return
	 * 	a Point, containing the x and y map co-ordinates selected.
	 */
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
	
	/*	SETPROJECTIONMATRIX - initializes the projection matrix based
	 *  on predefined screen dimensions 
	 */
	public void SetProjectionMatrix(GL10 gl)
	{		
		Matrix.setIdentityM(projectionMatrix, 0);

		projectionMatrix[15] = 0; // 3,3

		float near_plane = 1;
		float far_plane = 1000;		

		float h_fov = (float) (30.0 * ((float)GLwidth / (float)GLheight));
		float v_fov = 30.0f;
		
		float aspect = (float)GLwidth / (float)GLheight;
		float f = (float) (1.0f / Math.tan(v_fov / 180.0f * 3.141593f * 0.5f));		

		float w = (float) (1.0f / Math.tan(h_fov / 180.0f * 3.141593f * 0.5f));
		float h = (float) (1.0f / Math.tan(v_fov / 180.0f * 3.141593f * 0.5f));
		float q = far_plane / (far_plane - near_plane);		
		
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
			//mm.selected = -1;
			return -1;
		}else{
			int result = mm.checkListItem(y);
			//mm.selected = result;
			return result;
		}
		
		
	}
	
	public void updateHUDPanel(Unit abc){
		character = abc;
		update = true;
		headsUpDisplay.updateStatPanel( abc);
	}
}

