package com.comp4903.project.graphics;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.GUI.HUD;
import com.comp4903.project.gameEngine.data.MapData;
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
	
	public boolean showmenu = false;
	
	private float viewX, viewY, viewZ; // co-ordinate of the location we are looking at
	private float eyeX, eyeY, eyeZ; // location of camera
	private float distance; // distance of camera
	private float viewAngle; // angle of camera
	
	// this is how we are supposed to do matrices in OpenGL ES
	// (as far as I can tell)
	private float[] modelMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	private float[] modelViewMatrix = new float[16]; // combined model+view, needed for openGL
	private float[] projectionMatrix = new float[16];
	
	private Context context;
	
	private Hexagon hex; // get rid of this
	private Model3D[] testmodels;
	private int[][] tileset = new int[40][40]; // this too
	
	private float[] ambientLight = { 0.4f, 0.4f, 0.4f, 1 };
	private float[] diffuseLight = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] lightPosition = { 10.0f, 10.0f, 10.0f, 10.0f };
	
	public HUD headsUpDisplay;
	
	private MapRenderer map;
	
	
	/*	GLRENDERER
	 * 
	 * Constructor, need to provide app context for access to
	 * resource files such as textures	 * 
	 */
	public GLRenderer(Context c)
	{
		context = c;
		selectedTile.x = 0;
		selectedTile.y = 0;		
		
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
		
		//hex = new Hexagon(gl, context);
		headsUpDisplay = new HUD(context, GLwidth, GLheight);
		headsUpDisplay.initialBoxTexture(gl);
		
		MaterialLibrary.init(gl, context);
		
		map = new MapRenderer(gl, context);
		
		InputStream in;
		MapData data = null;
		try {
			in = context.getResources().getAssets().open("MapTwo.xml");
			data = MapFactory.generateMapData(in);
		} catch (IOException e) {}
		
		map.init(data.NumberOfColumns(), data.NumberOfRows());
		map.defineMap(data);
		
		viewX = 20;
		viewY = 0;
		viewZ = 20;
		distance = 7;
		viewAngle = 0; //1.57f / 2f;
		
		modeltest(gl);
		
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
		
		// clear the buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);		
				
		// compute camera position based on the target view point and
		// the distance
		eyeX = (float) ((Math.cos(viewAngle) - Math.sin(viewAngle)) * distance);		 
		eyeZ = (float) ((Math.sin(viewAngle) + Math.cos(viewAngle)) * distance);
		eyeX += viewX;
		eyeZ += viewZ;
		eyeY = distance*(distance / 5f);		
		
		Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, viewX, viewY, viewZ, 0f, 1f, 0f);
				
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		map.render(viewMatrix, projectionMatrix, viewX, viewY, viewZ);
		
		draw(gl);
		
		gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		headsUpDisplay.SwithToOrtho(gl);
		headsUpDisplay.drawHUD(gl);
		headsUpDisplay.SwitchToPerspective(gl);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		isRenderingNow = false;
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
		
		headsUpDisplay = new HUD(context, width, height);
		headsUpDisplay.initialBoxTexture(gl);
	
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
		return map.pick(x, y);
	}
	
	// Makes tile x,y the selected tile
	public void selectTile(int x, int y)
	{
		map.selectTile(x, y);
		//selectedTile.x = x;
		//selectedTile.y = y;
	}
	
	// Makes tile x,y the selected tile
	// can be used directly with pick (ex...   selectTile(pick(screenX, screenY)); )
	public void selectTile(Point s)
	{
		map.selectTile(s.x, s.y);
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
	
	public int setSelectedHUD(int y,boolean flag){
		if(flag){
			this.headsUpDisplay.action.pressed = true; 
			int result = this.headsUpDisplay.action.checkListItem(y);
			this.headsUpDisplay.action.menuSelected =  result;
			return result;
		}else{
			this.headsUpDisplay.action.pressed = false;
			this.headsUpDisplay.action.menuSelected = -1;
			return -1;
		}
	}
	
	
}
