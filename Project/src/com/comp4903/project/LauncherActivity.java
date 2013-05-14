package com.comp4903.project;

import java.io.IOException;
import java.io.InputStream;

import com.comp4903.project.graphics.GLRenderer;
import com.comp4903.project.graphics.MyGLSurfaceView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;

import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.engine.GameEngine;
import com.comp4903.project.gameEngine.factory.*;
import com.comp4903.project.network.NetworkAccessor;
import com.comp4903.project.network.Networking;

import com.comp4903.pathfind.PathFind;
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

	MapData mapData = null;
	Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
				
		boolean loaded = loadContent();
		InputStream in;
		context = this;
		
		//Networking.staticInitializer();		
		startNetworking();
		
		try {
			in = getResources().getAssets().open("MapTwo.xml");
			mapData = MapFactory.generateMapData(in);
		} catch (IOException e) {}

		PathFind.initialize(mapData);
		GameEngine.Initialize(mapData);
		
		GLSurfaceView view = new MyGLSurfaceView(this, mapData);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(view);
		
		//Networking.staticInitializer(context);
		
		//startNetworking();
		
	}

	private boolean loadContent(){
		InputStream in;
		try {
			in = getResources().getAssets().open("Weapons2.xml");
			GameStats.InitializeWeaponData(in);
			in = getResources().getAssets().open("Skills2.xml");
			GameStats.InitializeSkillData(in);
			in = getResources().getAssets().open("Armour.xml");
			GameStats.InitializeArmourData(in);
			in = getResources().getAssets().open("Units2.xml");
			GameStats.InitializeUnitData(in);
		} catch (IOException e) {
			return false;
		}
		return true;
	}	
	
	private void startNetworking()
	{
		
		Thread netThread = new Thread()
		{			
			public void run(){
				//NetworkAccessor.net = new Networking(context);
				Networking.staticInitializer(context);
			}			
		};
		
		
		netThread.start();
	}
	
	
	
}
