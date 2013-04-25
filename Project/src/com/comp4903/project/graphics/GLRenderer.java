package com.comp4903.project.graphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.graphics.Hexagon;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;

/* GLRENDERER
 * 
 * Represents the main rendering loop of the application
 * 
 * 
 * 
 */
public class GLRenderer implements android.opengl.GLSurfaceView.Renderer {

	private float viewX, viewY, viewZ; // co-ordinate of the location we are looking at
	private float eyeX, eyeY, eyeZ; // location of camera
	private float distance; // distance of camera
	private float viewAngle; // angle of camera
	
	private Context context;
	
	private Hexagon hex; // get rid of this
	private int[][] tileset = new int[40][40]; // this too
	
	/*	GLRENDERER
	 * 
	 * Constructor, need to provide app context for access to
	 * resource files such as textures	 * 
	 */
	public GLRenderer(Context c)
	{
		context = c;
		
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
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
				
		// compute camera position based on the target view point and
		// the distance
		eyeX = (float) ((Math.cos(viewAngle) - Math.sin(viewAngle)) * distance);		 
		eyeZ = (float) ((Math.sin(viewAngle) + Math.cos(viewAngle)) * distance);
		eyeX += viewX;
		eyeZ += viewZ;
		eyeY = distance;
		
		GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, viewX, viewY, viewZ, 0f, 1.0f, 0f);
		
		draw(gl);		
				
	}
	
	// Test routine to draw the map
	public void draw(GL10 gl)
	{
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		for (int x = 0; x < 40; x++)
			for (int y = 0; y < 40; y++)
			{
				float dx, dy, dz;
				dx = ((float)x - 20) * 3.05f + (y % 2) * 1.525f;
				dz = ((float)y - 20)* .9f;
				dy = 0;
				hex.draw(gl, dx, dy, dz, tileset[x][y]);
			}
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
		gl.glViewport(0, 0, width, height);
		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);		
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 3, 200);
	} 
	
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
	
	public void cameraMoveRequest(float dx, float dy)
	{
		viewX -= dx * 0.05f;
		viewZ -= dy * 0.05f;
	}
	
}
