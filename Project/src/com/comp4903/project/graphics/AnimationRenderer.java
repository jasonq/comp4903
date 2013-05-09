package com.comp4903.project.graphics;

import javax.microedition.khronos.opengles.GL;

import android.content.Context;

public class AnimationRenderer {

	private GL gl;
	private Context context;
	
	public AnimationRenderer(GL g, Context c)
	{
		context = c;
		gl = g;
	}
	
	
}
