package com.comp4903.project.GUI;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;
import com.comp4903.project.gameEngine.data.Unit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class actionBox extends UI{

	private Square[] command;
	private Square[] pressedCommand;
	private Square[] skillBox;
	private Square[] pressedskillBox;
	//private Square cancelBox;
	private Unit unit;
	//private int cX,cY;
	private int bHeight, bWidth;
	public int menuSelected ;
	public boolean pressed = false;
	public actionBox(Context c, int w, int h) {
		super(c,w,h);
		menuSelected = -1;
		command = new Square[4];
		pressedCommand = new Square[4];
		
		skillBox = new Square[3];
		pressedskillBox = new Square[3];
		
		int boxHeight = height * 3 / 4;
		int boxWidth = width  / 10 ;
		
		unit = null;
		
		int x = width* 18/20;
		int y = height/10;
		
		
		//box = new Square(width/20 - 10, height/10- 10,boxWidth + 20,boxHeight + 20);
		
		 bHeight = ((boxHeight/command.length ) * 4 ) /6;
		 bWidth = boxWidth * 4 / 6;
		
		int padX = boxWidth/ 6;
		int padY = (boxHeight/command.length) / 6;
		
		int nHeight = height/10 + padY;
		
		//cancelBox = new Square(x + padX, y + padY, bWidth, bHeight);
		//nHeight += (bHeight + padY);
		//int bWidth = boxWidth;
		int ty = 0;
		for(int i = 0; i < command.length;i++){
			if(i == 3){
				ty = nHeight;
			}
			command[i] = new Square(x + padX, nHeight,bWidth, bHeight );
			pressedCommand[i] = new Square(x + padX, nHeight, bWidth,bHeight );
			nHeight += (bHeight + (2 * padY));
		}
		
		for(int i = 0; i< skillBox.length;i++){
			skillBox[i] = new Square(x + padX, ty ,bWidth,bHeight);
			pressedskillBox[i] = new Square(x + padX, ty,bWidth,bHeight);
		}
		xTop = x;
		yTop = y;
		xBot = xTop + boxWidth;
		yBot = yTop + boxHeight;
		box = new Square(xTop, yTop,boxWidth,boxHeight);
		
	}

	public void UpdateUnit(Unit u){
		unit = u;
		command[3] = skillBox[u.unitType.getCode()];
		pressedCommand[3] = pressedskillBox[u.unitType.getCode()];
	}
	
	
	public void loadUITexture(GL10 gl,Resources r, int id){
		super.loadUITexture(gl, r, id);
		
		Bitmap bTex = BitmapFactory.decodeResource(r,R.drawable.menu);
		box.loadGLTexture(gl, context, bTex);
		Bitmap commandT = BitmapFactory.decodeResource(r, R.drawable.better2);
		//Bitmap pCommand =  BitmapFactory.decodeResource(r, R.drawable.unpresseded_new_icon);
		//initialize cancel box
		Bitmap cBox =  BitmapFactory.decodeResource(r, R.drawable.cancelbutton);
		
		command[0].loadGLTexture(gl, context, Bitmap.createScaledBitmap(cBox, 128, 128, false));
		//pressedCommand[0].loadGLTexture(gl, context, Bitmap.createScaledBitmap(cBox, 128, 128, false));
		
		//Bitmap unpressed = Bitmap.createBitmap(commandT,0,0,commandT.getWidth()/2, commandT.getHeight()/2);
		int width = commandT.getWidth()/6;
		int height = commandT.getHeight()/2;
		
		for(int i = 1; i < command.length; i++){
			int index = i;
			if(i== 3)
				continue;
			if(i == 4){
				index = 3;
			}
			Bitmap temp = Bitmap.createBitmap(commandT,
					(index -1) * width , 0 , width, height);
			Bitmap scale = Bitmap.createScaledBitmap(temp, 128, 128, false);
			pressedCommand[i].loadGLTexture(gl, context, scale);
			temp.recycle();
			
			//load pressed texture
			Bitmap temp2 = Bitmap.createBitmap(commandT,
					(index -1 ) * width , height , width, height);
			Bitmap scale2 = Bitmap.createScaledBitmap(temp2, 128, 128, false);
			command[i].loadGLTexture(gl, context, scale2);
			temp2.recycle();
			//temp.recycle();
		}
		
		for(int i = 0; i < skillBox.length; i++){
			
			Bitmap temp = Bitmap.createBitmap(commandT,(commandT.getWidth()/2) + (i * width ) , 0 , width, height);
			Bitmap scale = Bitmap.createScaledBitmap(temp, 128, 128, false);
			pressedskillBox[i].loadGLTexture(gl, context, scale);
			temp.recycle();
			
			Bitmap temp2 = Bitmap.createBitmap(commandT, (commandT.getWidth()/2) + (i  * width ), commandT.getHeight()/2, width, height);	
			Bitmap scale2 = Bitmap.createScaledBitmap(temp2, 128, 128, false);
			skillBox[i].loadGLTexture(gl, context, scale2);
			temp2.recycle();
		}
		commandT.recycle();
	}
	public void draw(GL10 gl,boolean showCancel){
		gl.glLoadIdentity();
		gl.glEnable( GL10.GL_BLEND );                   // Enable Alpha Blend
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );  // Set Alpha Blend Function
		//box.draw(gl);
		
		if(showCancel){
			if(menuSelected == 0)
				pressedCommand[0].draw(gl);
			else
				command[0].draw(gl);
		}else{
			for(int i = 1; i < command.length; i++){	
				if(menuSelected == i){
					pressedCommand[i].draw(gl);
				}else
					command[i].draw(gl);
			//gl.glTranslatef(0.0f,dis,0);
			}
		}
		
		/*skillBox[0].UpdateVertices(150	, 150, 50, 50);
		pressedskillBox[0].UpdateVertices(200, 150, 50, 50);
		
		skillBox[1].UpdateVertices(150	, 200, 50, 50);
		pressedskillBox[1].UpdateVertices(200, 200, 50, 50);
		
		skillBox[2].UpdateVertices(150, 250, 50, 50);
		pressedskillBox[2].UpdateVertices(200, 250, 50, 50);
		skillBox[0].draw(gl);
		skillBox[1].draw(gl);
		skillBox[2].draw(gl);
		
		pressedskillBox[0].draw(gl);
		pressedskillBox[1].draw(gl);
		pressedskillBox[2].draw(gl);*/
		gl.glDisable( GL10.GL_BLEND );                  // Disable Alpha Blend
	}
	public int checkPressingBox(int x, int y){
		int pad = (yBot - yTop)/(command.length);
		int newYTop = yTop + pad;
		if( x >= xTop && x <= xBot && y >= newYTop  && y <= yBot)
			return (((int)y -newYTop) / pad) + 1  ;
		else
			return -1;
	}
	
	public boolean checkPressingCancel(int X, int Y){
		return (X >= xTop && X <= xBot && Y >= yTop && Y <= (yTop + bHeight));
	}
	
	
}
