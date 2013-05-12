package com.comp4903.project.graphics.animation;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.comp4903.project.GUI.GLText;

public class FloatingText {

	public static GLText font;
	public static Context context;
	public static GL10 gl;
	public static int delay;
	
	String content;
	String name;
	int x, y;
	int xMovement, yMovement;
	int lifetime;
	public boolean active;
	
	public FloatingText(int x1, int y1, int mx, int my, int l, String n, String c)
	{
		content = c;
		name = n;
		x = x1;
		y = y1;
		xMovement = mx;
		yMovement = my;
		lifetime = l;
		active = true;
		
	}
	
	public void draw()
	{
		if (active)
		{
			font.draw( content, x, y );          // Draw Test String
			if ((delay % 10) == 0) {
				x += xMovement;
				y += yMovement;
				lifetime--;
				if (lifetime == 0)
					active = false;
			}
		}
	}
	
	public static void init(GL10 g, Context c)
	{
		gl = g;
		context = c;
		
		font = new GLText( gl, context.getAssets() );
		font.load( "Roboto-Regular.ttf", 14, 2, 2 );
	}
	
	
}
