
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
	public Square cancel,pressed_cancel;
	public int joinX,joinY,hostX,hostY,cancelX,cancelY;
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
		pressed_jbutton = new Square(x + 10,y + 10,bWidth-20,bHeight-20);

		hbutton = new Square(width/2 + x,y,bWidth,bHeight);
		pressed_hbutton =  new Square(width/2  + x + 10,y + 10,bWidth - 20,bHeight-20);

		joinX = x;
		joinY = y;

		hostX = width/2 + x;
		hostY = y;
		
		cancelX = width - bWidth;
		cancelY = height - bHeight;
		cancel = new Square(cancelX,cancelY,bWidth,bHeight);
		pressed_cancel = new Square(cancelX + 10,cancelY + 10,bWidth - 20,bHeight - 20);
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
		
		Bitmap cbmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.cancel);
		
		int cancelW = cbmp.getWidth();
		int cancelH = cbmp.getHeight();
		
		Bitmap can = Bitmap.createBitmap(cbmp,0,0,cancelW,cancelH/2);
		Bitmap scan = Bitmap.createScaledBitmap(can, 512, 512, false);
		cancel.loadGLTexture(gl, context, scan);
		can.recycle();
		
		Bitmap pcan = Bitmap.createBitmap(cbmp,0,cancelH/2,cancelW,cancelH/2);
		Bitmap pscan = Bitmap.createScaledBitmap(pcan, 512, 512, false);
		pressed_cancel.loadGLTexture(gl, context, pscan);
		pcan.recycle();
	}

	public void DrawNetWorking(GL10 gl){
		gl.glLoadIdentity();
		gl.glEnable( GL10.GL_BLEND );                   // Enable Alpha Blend
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );  // Set Alpha Blend Function
		if(selected == -1){
			jbutton.draw(gl);
			hbutton.draw(gl);
			cancel.draw(gl);
		}else{
			if(selected == 1){
				pressed_jbutton.draw(gl);
				hbutton.draw(gl);
				cancel.draw(gl);
			}else if(selected == 2){
				jbutton.draw(gl);
				pressed_hbutton.draw(gl);
				cancel.draw(gl);
			}else if(selected == 3){
				jbutton.draw(gl);
				hbutton.draw(gl);
				pressed_cancel.draw(gl);
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
		}else if  (x >= cancelX && x <= (cancelX + bWidth) && y >= cancelY && y <= (cancelY + bHeight)){
			return 3;
			
		}else{
			//selected = -1;
			return -1;
		}
	}
}
