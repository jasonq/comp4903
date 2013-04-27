package com.comp4903.project;

import com.comp4903.project.graphics.GLRenderer;
import com.comp4903.project.graphics.MyGLSurfaceView;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/* LAUNCHERACTIVITY
 * 
 * Main startup class for the comp 4903 Project
 * 
 * Gordon Koch A00792087
 * Tony Han
 * Jason Quan
 * Tin Huang
 */

public class LauncherActivity extends Activity {
   
	// Main entry point beyond the constructor
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
        
		//setContentView(R.layout.main);
        
		// Start up OpenGL ES 1.0, by creating a
		// GLSurfaceView, and setting it as the main renderer
        GLSurfaceView view = new MyGLSurfaceView(this);
        //view.setRenderer(new GLRenderer(this));
        setContentView(view);
        
    }
}
