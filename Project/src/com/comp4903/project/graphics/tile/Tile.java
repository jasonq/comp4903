package com.comp4903.project.graphics.tile;

import java.nio.FloatBuffer;

import com.comp4903.project.graphics.model.Model3D;

public class Tile {
	
	float texcoords[] = new float[12];
	FloatBuffer UV;
	String name;
	int textureMap;
	Model3D model = null;
	
	public Tile(String n)
	{
		name = n;
	}
	
}
