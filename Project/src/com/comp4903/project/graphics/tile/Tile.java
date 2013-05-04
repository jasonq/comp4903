package com.comp4903.project.graphics.tile;

import java.nio.FloatBuffer;

public class Tile {
	
	float texcoords[] = new float[12];
	FloatBuffer UV;
	String name;
	int textureMap;
	
	public Tile(String n)
	{
		name = n;
	}
	
}
