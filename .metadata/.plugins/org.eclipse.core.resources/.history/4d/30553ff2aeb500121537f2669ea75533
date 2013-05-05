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
		command = new Square[6];
		pressedCommand = new Square[6];
		float sx = (width / 8);
		float sy = (height*3/4) / 2;
		float x = width/20 + sx;
		float y = height/20 + sy;
		this.setTransformation(sx, sy, x, y);
		xTop = width/10;
		yTop = height/10;
		xBot = xTop + (width/4);
		yBot = yTop + (height * 3 /4);
	}

	public void loadUITexture(GL10 gl,Resources r, int id){
		super.loadUITexture(gl, r, id);
		Bitmap commandT = BitmapFactory.decodeResource(r, R.drawable.command_button);
		int width = commandT.getWidth();
		int height = commandT.getHeight()/6;
		for(int i = 0; i < command.length; i++){
			command[i] = new Square();
			//Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.unpressed_button);
			Bitmap temp = Bitmap.createBitmap(commandT,
					0 , i * height, width, height);
			command[i].loadGLTexture(gl, context, temp);
			//commandT.recycle();
			temp.recycle();
		}
		commandT.recycle();
		
		Bitmap pCommand =  BitmapFactory.decodeResource(r, R.drawable.pressed_command_button);
		width = pCommand.getWidth();
		height = pCommand.getHeight()/6;
		for(int i = 0; i < pressedCommand.length; i++){
			pressedCommand[i] = new Square();
			//Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.button);
			Bitmap temp = Bitmap.createBitmap(pCommand,
					0 , i * height, width, height);
			pressedCommand[i].loadGLTexture(gl, context, temp);
			temp.recycle();
		}		
		pCommand.recycle();
	}
	public void draw(GL10 gl){
		gl.glLoadIdentity();
		//draw the box
		gl.glTranslatef(tranx,trany,0.0f);
		gl.glScalef(scalex, scaley, 1.0f);
		this.box.draw(gl);

		gl.glLoadIdentity();
		float sx = (width / 4)/2;
		float sy = (height * 3 / 24)/2;
		float x = width/20 + sx;
		float y = height/20 + sy;
		//command[0].draw(gl);
		gl.glTranslatef(x,y,0.0f);
		gl.glScalef(sx, sy, 1.0f);
		int dis = (int)((float)(height/ sy) * 3/ 24);
		for(int i = 0; i < command.length; i++){
			
			if(menuSelected == i){
				pressedCommand[i].draw(gl);
			}else
				command[i].draw(gl);
			gl.glTranslatef(0.0f,dis,0);
		}
		
	}
	public int checkListItem(float y){
		int pad = (yBot - yTop)/command.length;
		return ((int)y-yTop) / pad  ;
	}
	

}
