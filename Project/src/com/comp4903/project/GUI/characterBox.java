package com.comp4903.project.GUI;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class characterBox extends UI {

	
	public Text Atk,Def,Round,Name,HP,Energy,Class;
	public Square RedBar,BlueBar;
	public String atkVal, defVal, roundVal, nameVal, classVal,hpVal, enVal;
	public int currentHp, totalHp, currentE, totalE;
	public Glyphs glyph;
	public characterBox(Context c, int w, int h) {
		super(c,w,h);
		currentHp = 500;
		totalHp = 2000;
		currentE = 500;
		totalE = 1000;
		RedBar = new Square();
		BlueBar = new Square();
		box = new Square( width  / 3, (height * 3) / 4, (width * 2)  / 3, height/4 );
		glyph = new Glyphs(BitmapFactory.decodeResource(context.getResources(), R.drawable.glyphs_white));
	}
	
	
	public void setText(){
		atkVal = "123";
		defVal = "4567";
		roundVal = "2";
		nameVal = "Anonymous";
		classVal = "Archer";
		hpVal = "" + currentHp;
		enVal = "" + currentE;
		
	}
	
	public void loadUITexture(GL10 gl,Resources r, int id){
		super.loadUITexture(gl, r, id);
		this.setText();
		
		int xLocation = width/3 + width/10;
		int yLocation = ((height * 3 )/ 4) + (height/40);
		int gap = height / 80;//(height/40);
		
		//set location and load texture  the attack value	
		Atk = new Text(xLocation,yLocation,width,height,glyph,atkVal);
		Atk.loadTextture(gl, context);
		yLocation += (Atk.textHeight + gap);//increament y
		
		//set location and load texture the defense value
		Def = new Text(xLocation,yLocation,width,height,glyph,defVal);
		Def.loadTextture(gl, context);
		yLocation +=  (Atk.textHeight + gap);
		
		//set location and load texture  number of round
		Round = new Text(xLocation,yLocation,width,height,glyph,roundVal);
		Round.loadTextture(gl, context);
		yLocation +=   (Atk.textHeight + gap);
		
		//move x and y to write the name
		yLocation = ((height * 3 )/ 4) + (height/40);
		xLocation = width/3 + ((width * 7 )/ 30);
		
		//set location and load texture  of name
		Name = new Text(xLocation,yLocation,width,height,glyph,nameVal);
		Name.loadTextture(gl, context);
		yLocation +=   (Name.textHeight + gap);
		
		//set location and load texture of class
		Class = new Text(xLocation,yLocation,width,height,glyph,classVal);
		Class.loadTextture(gl, context);
		yLocation +=   (Name.textHeight + gap);
		
		
		//DRAWING HP AND ENERGY BAR
		float hpRatio = (float)currentHp/totalHp;
		float eRatio = (float)currentE / totalE;
		
		Bitmap hp = BitmapFactory.decodeResource(context.getResources(),R.drawable.bar);
		int bWidth = hp.getWidth();
		int bHeight = hp.getHeight();
		Bitmap temp = Bitmap.createBitmap(hp,
				0 ,0, bWidth, bHeight/2);
		
		int hpWidth = (int)((width * 3 / 10) * hpRatio);
		RedBar = new Square(xLocation,yLocation, hpWidth, Class.textHeight);
		RedBar.loadGLTexture(gl, context, temp);
		temp.recycle();
		
		Bitmap temp1 = Bitmap.createBitmap(hp,
				0 ,bHeight/2, bWidth, bHeight/2);
		
		yLocation +=  (Name.textHeight + gap);
		int eWidth = (int)((width * 3 / 10) * eRatio);
		BlueBar = new Square(xLocation,yLocation, eWidth, Class.textHeight);
		BlueBar.loadGLTexture(gl, context, temp1 );
		temp1.recycle();
		hp.recycle();
		
		//set location and draw value of hp
		xLocation =  width/3 + ((width * 8 )/15);
		yLocation -= (Name.textHeight + gap);
		
		HP = new Text(xLocation,yLocation,width,height,glyph,hpVal);
		HP.loadTextture(gl, context);
		
		yLocation += (Name.textHeight + gap);
		Energy = new Text(xLocation,yLocation,width,height,glyph,enVal);
		Energy.loadTextture(gl, context);
	}
	
	public void draw(GL10 gl){
		gl.glLoadIdentity();
		box.draw(gl);
		
		gl.glEnable( GL10.GL_BLEND );                   // Enable Alpha Blend
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );  // Set Alpha Blend Function
		
		gl.glLoadIdentity();
		Atk.drawText(gl);
		Def.drawText(gl);
		Round.drawText(gl);
		Name.drawText(gl);
		Class.drawText(gl);
		RedBar.draw(gl);
		BlueBar.draw(gl);
		HP.drawText(gl);
		Energy.drawText(gl);
		// disable texture + alpha
		gl.glDisable( GL10.GL_BLEND );                  // Disable Alpha Blend
		
	}
}
