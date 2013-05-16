package com.comp4903.project.graphics.animation;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.comp4903.project.R;
import com.comp4903.project.GUI.Square;
import com.comp4903.project.gameEngine.enums.IconType;

public class FloatingIcon {

	int x, y, xMovement, yMovement, lifetime;
	String name;
	IconType icon;
	public boolean active;
	int delay = 20;
	
	static int[] width = new int[50];
	static int[] height = new int[50];
	
	public static Context context;
	public static GL10 gl;
	
	static Square[] images_ = new Square[50];
	
	public FloatingIcon(int x1, int y1, int mx, int my, int l, String n, IconType i)
	{
		icon = i;
		name = n;
		x = x1;
		y = y1;
		xMovement = mx;
		yMovement = my;
		lifetime = l;
		active = true;		
	}
	
	public static void init(GL10 g, Context c)
	{
		gl = g;
		context = c;
		
		loadImage(IconType.Defense.getCode(), R.drawable.shield, 64, 64);
		loadImage(IconType.EndTurn.getCode(), R.drawable.end, 256, 64);
		loadImage(IconType.StartTurn.getCode(), R.drawable.start, 256, 64);
		loadImage(IconType.Health1.getCode(), R.drawable.plus1, 16, 16);
		loadImage(IconType.Health2.getCode(), R.drawable.plus2, 32, 32);
		loadImage(IconType.Health3.getCode(), R.drawable.plus3, 64, 64);
		
	}
	
	public static void loadImage(int i, int res, int sx, int sy)
	{
		width[i] = sx; height[i] = sy;
		images_[i] = new Square();
		Bitmap temp = BitmapFactory.decodeResource(context.getResources(), res);
		Bitmap scale = Bitmap.createScaledBitmap(temp, sx, sy, false);
		images_[i].loadGLTexture(gl, context, scale);
		temp.recycle();
	}
	
	public void draw()
	{		
		if (active)
		{
			images_[icon.getCode()].UpdateVertices(x, y, width[icon.getCode()], height[icon.getCode()]);
			images_[icon.getCode()].draw(gl);
			
			if ((delay % 10) == 0) {
				x += xMovement;
				y += yMovement;
				lifetime--;
				if (lifetime == 0)
					active = false;
			}
		}		
	}
}
