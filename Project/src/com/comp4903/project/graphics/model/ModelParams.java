package com.comp4903.project.graphics.model;

public class ModelParams {

	public static final int INTVAL = 0;
	public static final int FLOATVAL = 1;
	public static final int STRINGVAL = 2;
	
	public int type;
	public int intVal;
	public float floatVal;
	public String stringVal;
	
	public ModelParams() {
		type = -1;
		intVal = 0;
		floatVal = 0;
		stringVal = "";
	}
	
}
