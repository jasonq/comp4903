package com.comp4903.project.GUI;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;

public class characterBox extends UI {

	public characterBox(Context c, int w, int h) {
		super(c,w,h);

		float sx = width  / 3;
		float sy = height / 8;
		float x = width * 2  / 3 ;
		float y = height * 7 / 8;
		this.setTransformation(sx, sy, x, y);
	}
	public void loadUITexture(GL10 gl,Resources r, int id){
		super.loadUITexture(gl, r, id);
	}
	
	public void draw(GL10 gl){
		gl.glLoadIdentity();
		gl.glTranslatef(tranx,trany,0.0f);
		gl.glScalef(scalex, scaley, 1.0f);
		//gl.glTranslatef(tranx,trany,0);
		this.box.draw(gl);
	}
}
