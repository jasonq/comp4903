package com.comp4903.project.graphics.model;

public class Texture {
	
	String name;
	String filename;
	int texturename; // an openGL identifier for the texture, actually just an int
	int alternate;
	
	public Texture()
	{
		alternate = -1;
	}
	
	public Texture(String n, String fn, int tex)
	{
		name = n;
		filename = fn;
		texturename = tex;
		alternate = -1;
	}
}
