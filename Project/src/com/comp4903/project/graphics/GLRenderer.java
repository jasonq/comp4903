package com.comp4903.project.graphics;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.graphics.Hexagon;
import com.comp4903.project.graphics.model.MaterialLibrary;
import com.comp4903.project.graphics.model.Model3D;
import com.comp4903.project.graphics.model.ModelLoader;

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
				tileset[x][y] = 0;
				if ((x * y) % 16 > 10)
					tileset[x][y] = 1;
				if ((x * y) % 16 > 14)
					tileset[x][y] = 2;
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
		
		viewX = 20;
		viewY = 0;
		viewZ = 20;
		distance = 7;
		viewAngle = 0; //1.57f / 2f;
		
		modeltest(gl);
		
	}	
	
	public void modeltest(GL10 gl) 
	{
		MaterialLibrary.init(gl, context);
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
		testmodels[2].SetPosition(26, 0.5f, 23);
		
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
		eyeY = distance*(distance / 5f);		
		
		Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, viewX, viewY, viewZ, 0f, 1f, 0f);
				
		draw(gl);	
		
		//headsUpDisplay.drawHUD(gl);
				
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
				//dx = ((float)x) * 3.05f + (y % 2) * 1.525f;
				//dz = ((float)y)* .9f;
				
				dx = ((float)x) * 3f + (y % 2) * 1.5f;
				dz = ((float)y) * 0.8660254038f;
				
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
		
		// set up projection transformation		
		//float ratio = (float) width / height;
		//Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2, 100);	
		SetProjectionMatrix(gl);
		gl.glMatrixMode(GL10.GL_PROJECTION);	
		gl.glLoadMatrixf(projectionMatrix, 0);
	
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
		
		if (distance < 3)
			distance = 3;
		
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
	
}
