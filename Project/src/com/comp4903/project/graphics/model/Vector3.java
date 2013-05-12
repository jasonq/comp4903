package com.comp4903.project.graphics.model;

public class Vector3 {

	float x;
	float y;
	float z;
	float w;
	
	public Vector3(float xx, float yy, float zz)
	{
		x = xx; y = yy; z = zz; w = 1.0f;
	}
	
	public Vector3(float[] v)
	{
		x = v[0];
		y = v[1];
		z = v[2];
		w = 1.0f;
	}
}
