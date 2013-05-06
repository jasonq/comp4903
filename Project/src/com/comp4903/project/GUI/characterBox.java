package com.comp4903.project.GUI;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class characterBox extends UI {

	
	public Square Atk,Def,Round,Name,HP,Energy,Class;
	public String atkVal, defVal, roundVal, nameVal, classVal;
	public Glyphs glyph;
	public characterBox(Context c, int w, int h) {
		super(c,w,h);
		Atk = new Square();
		Def = new Square();
		Round = new Square();
		Name  = new Square();
		HP  = new Square();
		Energy  = new Square();
		Class  = new Square();
		box = new Square( width  / 3, (height * 3) / 4, (width * 2)  / 3, height/4 );
		float sx = width  / 3;
		float sy = height / 8;
		float x = width * 2  / 3 ;
		float y = height * 7 / 8;
		this.setTransformation(sx, sy, x, y);
		glyph = new Glyphs(BitmapFactory.decodeResource(context.getResources(), R.drawable.glyphs_white));
	}
	
	
	public void setText(){
		atkVal = "123";
		defVal = "456";
		roundVal = "2";
		nameVal = "Anonymous";
		classVal = "Archer";
		
	}
	public void loadUITexture(GL10 gl,Resources r, int id){
		super.loadUITexture(gl, r, id);
		this.setText();
		
		
		Bitmap bmp = glyph.getString(atkVal);
		Atk = new Square(100,50,100,50);
		Atk.loadGLTexture(gl, context, bmp);
		bmp.recycle();
	}
	
	public void draw(GL10 gl){
		gl.glLoadIdentity();
		box.draw(gl);
		
		gl.glEnable( GL10.GL_BLEND );                   // Enable Alpha Blend
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );  // Set Alpha Blend Function
		
		gl.glLoadIdentity();
		Atk.draw(gl);
		
		
		// disable texture + alpha
		gl.glDisable( GL10.GL_BLEND );                  // Disable Alpha Blend
		
	}
}
