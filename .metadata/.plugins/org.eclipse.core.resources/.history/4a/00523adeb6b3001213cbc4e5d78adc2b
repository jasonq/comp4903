package com.comp4903.project.graphics;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class MapRenderer {

	private GL10 gl;
	private Context context;
	
	private int mapWidth;
	private int mapHeight;
	
	private Hex tileMap[][];
	
	public MapRenderer(GL10 g, Context c)
	{
		gl = g;
		context = c;
	}
	
	public void init(int w, int h)
	{
		tileMap = new Hex[w][h];
	}
	
	private class Hex {
		int tile;
		int state;
	}
	
}
