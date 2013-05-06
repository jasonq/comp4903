package com.comp4903.project.GUI;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class UI {

	protected Square box;
	protected int width,height;
	protected Context context;
	protected float scalex,scaley,tranx,trany;
	public int xTop,yTop,xBot,yBot;
	public UI(Context c,int w, int h){
		width = w;
		height = h;
		context = c;
		box = new Square();
	}
	
	public void loadUITexture(GL10 gl,Resources r, int id){
		Bitmap bitmap1 = BitmapFactory.decodeResource(r, id);
		box.loadGLTexture(gl, context, bitmap1);
		bitmap1.recycle();
	}
	
	public void setTransformation(float sx, float sy,float x, float y){
		scalex = sx;
		scaley= sy;
		tranx = x;
		trany = y;
	}
	
}
