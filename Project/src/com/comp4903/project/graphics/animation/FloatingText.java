package com.comp4903.project.graphics.animation;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Point;

import com.comp4903.project.GUI.GLText;
import com.comp4903.project.gameEngine.enums.ColorType;
import com.comp4903.project.gameEngine.enums.IconType;
import com.comp4903.project.graphics.RendererAccessor;

public class FloatingText {

	public static GLText font;
	public static Context context;
	public static GL10 gl;
	public static int delay;
	
	boolean tied;
	public float xf,yf,zf;
	
	String content;
	public String name;
	int x, y;
	int xMovement, yMovement;
	int lifetime, elapsed;
	float[] color = { 1.0f, 1.0f, 1.0f, 1.0f };
	
	ColorType clr;
	public boolean active;
	
	public FloatingText(float xf1, float yf1, float zf1, int mx, int my, int l, ColorType col, String n, String c)
	{
		set(0,0,mx,my,l,col,n,c);
		xf = xf1; yf = yf1; zf = zf1;
		tied = true;
		
	}
	
	public FloatingText(int x1, int y1, int mx, int my, int l, ColorType col, String n, String c)
	{
		set(x1,y1,mx,my,l,col,n,c);
		tied = false;
		
	}
	
	public void set(int x1, int y1, int mx, int my, int l, ColorType col, String n, String c)
	{
		content = c;
		name = n;
		x = x1;
		y = y1;
		xMovement = mx;
		yMovement = my;
		lifetime = l;
		active = true;
		clr = col;
		col.getAsFloats(color);
		
	}
	
	public void draw()
	{
		if (active)
		{
			if (tied)
			{
				Point p = RendererAccessor.ScreenXYfromXYZ(xf, yf, zf);
				x = p.x + xMovement * elapsed;
				y = p.y + yMovement * elapsed;
				elapsed++;
			}
			
			font.begin(color[0], color[1], color[2], color[3] );         // Begin Text Rendering (Set Color WHITE)
			font.setScale(2.0f);
			
			font.draw( content, x, y );          // Draw Test String
			if ((delay % 10) == 0) {
				x += xMovement;
				y += yMovement;
				lifetime--;
				if (lifetime == 0)
					active = false;
			}
			
			font.end();
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
