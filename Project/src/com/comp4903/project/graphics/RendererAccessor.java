package com.comp4903.project.graphics;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class RendererAccessor {

		public static MapRenderer map;
			
		public static void init(GL10 gl, Context c)
		{
			map = new MapRenderer(gl, c);
		}
}
