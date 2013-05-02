package com.comp4903.project.graphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.GUI.HUD;
import com.comp4903.project.graphics.Hexagon;

import android.content.Context;
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
	private int[][] tileset = new int[40][40]; // this too
	
	private HUD headsUpDisplay;
	
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
		
		// random map
		for (int x = 0; x < 40; x++)
			for (int y = 0; y < 40; y++)
			{
				tileset[x][y] = 2;
				if ((x * y) % 16 > 10)
					tileset[x][y] = 1;
				if ((x > 18) && (x < 22))
					tileset[x][y] = 0;
			}
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
		gl.glEnable(GL10.GL_TEXTURE_2D);		
		
		hex = new Hexagon(gl, context);
		headsUpDisplay = new HUD(context, 1200, 300);
		headsUpDisplay.initialBoxTexture(gl);
		
		viewX = 0;
		viewY = 0;
		viewZ = 0;
		distance = 5;
		viewAngle = 1.57f / 2f;
		
	}	
	
	/*
	 * ONDRAWFRAME(non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 * 
	 * Main drawing loop
	 */	
	public void onDrawFrame(GL10 gl) {
		
		// clear the buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);		
				
		// compute camera position based on the target view point and
		// the distance
		eyeX = (float) ((Math.cos(viewAngle) - Math.sin(viewAngle)) * distance);		 
		eyeZ = (float) ((Math.sin(viewAngle) + Math.cos(viewAngle)) * distance);
		eyeX += viewX;
		eyeZ += viewZ;
		eyeY = distance;
		
		//GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, viewX, viewY, viewZ, 0f, 1.0f, 0f);
		
		Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, viewX, viewY, viewZ, 0f, 1f, 0f);
				
		draw(gl);	
		//headsUpDisplay.SwithToOrtho(gl);
		//headsUpDisplay.drawHUD(gl);
		//headsUpDisplay.SwitchToPerspective(gl);
		selectedTile = pick(640,360);
	}
	
	// Test routine to draw the map
	public void draw(GL10 gl)
	{
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		hex.screenCoordCount = 0;
		
		for (int x = 0; x < 40; x++)
			for (int y = 0; y < 40; y++)
			{
				float dx, dy, dz;
				dx = ((float)x) * 3.05f + (y % 2) * 1.525f;
				dz = ((float)y)* .9f;
				dy = 0;
				
				if ((x == selectedTile.x) && (y == selectedTile.y))
					dy = .1f;
				
				if ((dx > viewX - 16) &&
					(dz > viewZ - 16) &&
					(dx < viewX + 16) &&
					(dz < viewZ + 8))
				{
					hex.screenCoords[hex.screenCoordCount][12] = x;
					hex.screenCoords[hex.screenCoordCount][13] = y;
				
					// set up the model-view matrix for this hexagon
					Matrix.setIdentityM(modelMatrix, 0);				
					Matrix.multiplyMM(modelViewMatrix, 0, modelMatrix, 0, viewMatrix, 0);
					Matrix.translateM(modelViewMatrix, 0, dx, dy, dz);
					gl.glMatrixMode(GL10.GL_MODELVIEW);
					gl.glLoadMatrixf(modelViewMatrix, 0);
				
					hex.draw(gl, modelViewMatrix, projectionMatrix, tileset[x][y]);
				}
			}
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
		// set window to screen size
		gl.glViewport(0, 0, width, height);
		
		GLwidth = width;
		GLheight = height;
		
		// set up projection transformation		
		float ratio = (float) width / height;
		Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 200);		
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
		
		if (distance < 1)
			distance = 1;
		
		if (distance > 10)
			distance = 10;	
	}
	
	// moves the view camera by an arbitrary amount
	public void cameraMoveRequest(float dx, float dy)
	{
		viewX -= dx * 0.03f;
		viewZ -= dy * 0.03f;
	}
	
	// a crazy way of doing picking.  Don't look too closely at this code.
	// Given two screen co-ordinates, will return
	// a Point, containing the x and y map co-ordinates selected.
	public Point pick(float x, float y)
	{
		Point r = new Point(-1, -1);
		int v = 5;
		boolean p = true;
		
		r.x = -1;
		r.y = -1;
		
		for (int m = 0; m < hex.screenCoordCount; m++)
		{
			p = false;
			for (int t = 0; t < 5; t++)
			{
				if (  (((float)hex.screenCoords[m][t * 2 + 1] > y) != ((float)hex.screenCoords[m][v * 2 + 1] > y))
					  &&
					  ((float)x < ((float)hex.screenCoords[m][v * 2] - (float)hex.screenCoords[m][t * 2]) *
							  ((float)y - (float)hex.screenCoords[m][t * 2 + 1]) / 
							  ((float)hex.screenCoords[m][v * 2 + 1] - (float)hex.screenCoords[m][t * 2 + 1]) + (float)hex.screenCoords[m][t * 2])
					  
					)
				{
					p = !p;
				}
				v = t;
			}		
			if (p)
			{
				r.x = (int)(hex.screenCoords[m][12]);
				r.y = (int)(hex.screenCoords[m][13]);
			}
		}
		
		return r;
	}
	
	// Makes tile x,y the selected tile
	public void selectTile(int x, int y)
	{
		selectedTile.x = x;
		selectedTile.y = y;
	}
	
	// Makes tile x,y the selected tile
	// can be used directly with pick (ex...   selectTile(pick(screenX, screenY)); )
	public void selectTile(Point s)
	{
		selectedTile = s;		
	}
	
	
	
}
