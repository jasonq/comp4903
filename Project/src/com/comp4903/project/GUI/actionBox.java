package com.comp4903.project.GUI;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class actionBox extends UI{

	private Square[] command;
	private Square[] pressedCommand;
	public int menuSelected ;
	public boolean pressed = false;
	public actionBox(Context c, int w, int h) {
		super(c,w,h);
		menuSelected = -1;
		command = new Square[4];
		pressedCommand = new Square[4];
		int boxHeight = height / 2;
		int boxWidth = width  / 15 ;
		
		int x = width* 19/20;
		int y = height/10;
		//box = new Square(width/20 - 10, height/10- 10,boxWidth + 20,boxHeight + 20);
		
		int bHeight = boxHeight/command.length;
		int nHeight = height/10;
		
		int bWidth = boxWidth;
		for(int i = 0; i < command.length;i++){
			command[i] = new Square(x, nHeight,bWidth, bHeight );
			pressedCommand[i] = new Square(x, nHeight, bWidth,bHeight );
			nHeight += bHeight;
		}
		xTop = x;
		yTop = y;
		xBot = xTop + boxWidth;
		yBot = yTop + boxHeight;
	}

	public void loadUITexture(GL10 gl,Resources r, int id){
		super.loadUITexture(gl, r, id);
		Bitmap commandT = BitmapFactory.decodeResource(r, R.drawable.icons);
		int width = commandT.getWidth();
		int height = commandT.getHeight()/command.length;
		for(int i = 0; i < command.length; i++){
			Bitmap temp = Bitmap.createBitmap(commandT,
					0 , i * height, width, height);
			command[i].loadGLTexture(gl, context, temp);
			//temp.recycle();
		}
		commandT.recycle();
		
		Bitmap pCommand =  BitmapFactory.decodeResource(r, R.drawable.icons);
		width = pCommand.getWidth();
		height = pCommand.getHeight()/command.length;
		for(int i = 0; i < pressedCommand.length; i++){
			Bitmap temp = Bitmap.createBitmap(pCommand,
					0 , i * height, width, height);
			pressedCommand[i].loadGLTexture(gl, context, temp);
			//temp.recycle();
		}		
		pCommand.recycle();
	}
	public void draw(GL10 gl){
		gl.glLoadIdentity();
		gl.glEnable( GL10.GL_BLEND );                   // Enable Alpha Blend
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );  // Set Alpha Blend Function
		//box.draw(gl);
		for(int i = 0; i < command.length; i++){	
			if(menuSelected == i){
				pressedCommand[i].draw(gl);
			}else
				command[i].draw(gl);
			//gl.glTranslatef(0.0f,dis,0);
		}
		gl.glDisable( GL10.GL_BLEND );                  // Disable Alpha Blend
	}
	public int checkListItem(float y){
		int pad = (yBot - yTop)/command.length;
		return ((int)y-yTop) / pad  ;
	}
	

}
