package com.comp4903.project.graphics.animation;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.comp4903.project.R;
import com.comp4903.project.GUI.Square;
import com.comp4903.project.gameEngine.enums.IconType;
import com.comp4903.project.graphics.RendererAccessor;

public class FloatingIcon {

	int x, y, xMovement, yMovement, lifetime, elapsed;
	public String name;
	IconType icon;
	public boolean active;
	int delay = 20;
	boolean tied;
	float xf,yf,zf;
	
	static int[] width = new int[50];
	static int[] height = new int[50];
	
	public static Context context;
	public static GL10 gl;
	
	static Square[] images_ = new Square[50];
	
	public FloatingIcon(float xf1, float yf1, float zf1, int mx, int my, int l, String n, IconType i)
	{
		icon = i;
		name = n;
		xf = xf1;
		yf = yf1;
		zf = zf1;
		xMovement = mx;
		yMovement = my;
		lifetime = l;
		active = true;	
		tied = true;
		elapsed = 0;
	}
	
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
		tied = false;
	}
	
	public static void init(GL10 g, Context c)
	{
		gl = g;
		context = c;
		
		loadImage(IconType.Defense.getCode(), R.drawable.shield, 64, 64);
		loadImage(IconType.P1.getCode(), R.drawable.p1, 256, 64);
		loadImage(IconType.P2.getCode(), R.drawable.p2, 256, 64);
		loadImage(IconType.Health1.getCode(), R.drawable.plus1, 16, 16);
		loadImage(IconType.Health2.getCode(), R.drawable.plus2, 32, 32);
		loadImage(IconType.Health3.getCode(), R.drawable.plus3, 64, 64);
		loadImage(IconType.Lock.getCode(), R.drawable.lockicon, 64, 64);
		
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
			if (tied)
			{
				Point p = RendererAccessor.ScreenXYfromXYZ(xf, yf, zf);
				x = p.x + xMovement * elapsed;
				y = p.y + yMovement * elapsed;
				elapsed++;
			}
			
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
