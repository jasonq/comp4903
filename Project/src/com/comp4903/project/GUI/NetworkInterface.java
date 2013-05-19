package com.comp4903.project.GUI;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;
import com.comp4903.project.gameEngine.enums.UnitGroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class NetworkInterface {

	public Square jbutton,hbutton;
	public Square pressed_jbutton, pressed_hbutton;

	public int joinX,joinY,hostX,hostY;
	public int bWidth,bHeight;
	public int width,height;
	public Context context;

	public int selected = -1;
	public  NetworkInterface(Context c,int w , int h){
		width = w;
		height = h;
		bWidth = width/4;
		bHeight = height/4;
		this.context = c;
		int x = width/8;
		int y = height/8;

		jbutton = new Square(x,y,bWidth,bHeight);
		pressed_jbutton = new Square(x,y,bWidth,bHeight);

		hbutton = new Square(width/2 + x,y,bWidth,bHeight);
		pressed_hbutton =  new Square(width/2 + x,y,bWidth,bHeight);

		joinX = x;
		joinY = y;

		hostX = width/2 + x;
		hostY = y;

	}

	public void loadNetWorkingTexture(GL10 gl){
		Bitmap network = BitmapFactory.decodeResource(context.getResources(), R.drawable.net_working);
		int w = network.getWidth();
		int h = network.getHeight();


		Bitmap join = Bitmap.createBitmap(network,0,0,w/2,h/2);
		Bitmap sjoin = Bitmap.createScaledBitmap(join, 512, 512, false);
		jbutton.loadGLTexture(gl, context, sjoin);
		join.recycle();

		Bitmap pjoin = Bitmap.createBitmap(network,0,h/2,w/2,h/2);
		Bitmap spjoin = Bitmap.createScaledBitmap(pjoin, 512, 512, false);
		pressed_jbutton.loadGLTexture(gl, context, spjoin);
		pjoin.recycle();

		Bitmap host = Bitmap.createBitmap(network,w/2,0,w/2,h/2);
		Bitmap shost = Bitmap.createScaledBitmap(host, 512, 512, false);
		hbutton.loadGLTexture(gl, context, shost);
		host.recycle();

		Bitmap phost = Bitmap.createBitmap(network,w/2,h/2,w/2,h/2);

		Bitmap sphost = Bitmap.createScaledBitmap(phost, 512, 512, false);
		pressed_hbutton.loadGLTexture(gl, context, sphost);
		sphost.recycle();
	}

	public void DrawNetWorking(GL10 gl){
		gl.glLoadIdentity();
		gl.glEnable( GL10.GL_BLEND );                   // Enable Alpha Blend
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );  // Set Alpha Blend Function
		if(selected == -1){
			jbutton.draw(gl);
			hbutton.draw(gl);
		}else{
			if(selected == 1){
				pressed_jbutton.draw(gl);
				hbutton.draw(gl);
			}else if(selected == 2){
				jbutton.draw(gl);
				pressed_hbutton.draw(gl);
			}

		}
		gl.glDisable( GL10.GL_BLEND );
	}


	/*
	 * return 1 if pressing join, return 2 if pressing host
	 * return -1 if pressing none of them
	 */
	public int checkButton(int x, int y){
		if (x >= joinX && x <= (joinX + bWidth) && y >= joinY && y <= (joinY + bHeight)){
			//selected = 1;
			return 1;
		}else if  (x >= hostX && x <= (hostX + bWidth) && y >= hostY && y <= (hostY + bHeight)){
			//selected = 2;
			return 2;
		}else{
			//selected = -1;
			return -1;
		}
	}
}
