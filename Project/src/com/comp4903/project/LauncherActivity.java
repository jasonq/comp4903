package com.comp4903.project;

import java.io.IOException;
import java.io.InputStream;

import com.comp4903.project.graphics.GLRenderer;
import com.comp4903.project.graphics.MyGLSurfaceView;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;

import com.comp4903.project.gameEngine.factory.*;;
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
		//GLSurfaceView view = new MyGLSurfaceView(this);
		//view.setRenderer(new GLRenderer(this));
		//setContentView(view);
		//boolean loaded = loadContent();
		//System.out.println("Loaded: " + loaded);
		//GameStats.PrintSkillList();
		
		
		GLSurfaceView view = new MyGLSurfaceView(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//view.setRenderer(new GLRenderer(this));
		setContentView(view);
	}

	private boolean loadContent(){
		InputStream in;
		try {
			in = getResources().getAssets().open("Weapons.xml");
			GameStats.InitializeWeaponData(in);
			in = getResources().getAssets().open("Skills.xml");
			GameStats.InitializeSkillData(in);
			in = getResources().getAssets().open("Armours.xml");
			GameStats.InitializeArmourData(in);
			in = getResources().getAssets().open("Units.xml");
			GameStats.InitializeUnitData(in);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
