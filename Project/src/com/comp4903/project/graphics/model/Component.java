package com.comp4903.project.graphics.model;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/* Component
 * 
 * Free section of a 3D model, such as an arm, which can be independently
 * positioned.
 * 
 */
public class Component {
	
	String name;
	int parent;
	
	public float[] orientation = new float[16];
	float[] neutralOrientation = new float[16];
	float[] translation = new float[4];
	float[] initialTranslation = new float[4];
	
	Constraint constraints;
	
	int numVertices;
	
	int numTriangleLists;
	int[]numTriangles;
	int[]materialIndex;
	
	FloatBuffer vertexBuffer;
	FloatBuffer normalBuffer;
	FloatBuffer colorBuffer;
	ShortBuffer[] indexBuffer;
	FloatBuffer texBuffer;
	
	float Xrotate;
	float Yrotate;
	float Zrotate;		
}