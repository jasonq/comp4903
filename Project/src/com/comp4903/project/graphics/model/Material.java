package com.comp4903.project.graphics.model;

public class Material {
	
	public String name;
	public int texture;
	public float[] Ambient = new float[4];
	public float[] Diffuse = new float[4];
	public float[] Emissive = new float[4];
	public float[] Specular = new float[4];
	public float Power;

	public Material()
	{
		texture = -1;
		for (int i = 0; i < 4; i++)
		{
			Ambient[i] = 1.0f;
			Diffuse[i] = 1.0f;
			Emissive[i] = 0.0f;
			Specular[i] = 0.0f;
		}
		Power = 0f;
	}	
}
