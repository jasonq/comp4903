package com.comp4903.project.GUI;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;
import com.comp4903.project.gameEngine.enums.UnitGroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GameOver {
	public Square background;
	public Square player1, player2;
	public Square back, pressback;
	public int xTop,yTop,xBot,yBot;
	public int bWidth,bHeight;
	public int width,height;
	public Context context;
	public UnitGroup winner;
	public boolean flag = false;
	public GameOver(Context c,int w , int h){
		width = w;
		height = h;
		bWidth = width/ 2;
		bHeight = height / 2;
		int x = width/2 - (bWidth/2);
		int y = height/10;
		context = c;
		background = new Square(x,y,bWidth,bHeight);
		player1 = new Square(width/2, y + (bHeight* 90/100), bWidth /3, bHeight/10);
		player2 = new Square(width/2, y + (bHeight* 90/100), bWidth /3, bHeight/10);
		
		xTop = x ;
		xBot = xTop + bWidth;
		yTop =  y + bHeight;
		yBot = yTop + height/4;
		
		back = new Square(xTop, yTop, bWidth, height/4);
		pressback =  new Square(xTop, yTop, bWidth, height/4);
		winner = null;
	}
	
	public void loadGameOVerTexture(GL10 gl){

		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover);
		int h = bmp.getHeight();
		int w = bmp.getWidth();
		
		Bitmap bg = Bitmap.createBitmap(bmp,0,0,w,h/2);
		Bitmap sbg = Bitmap.createScaledBitmap(bg, 512, 512, false);
		background.loadGLTexture(gl, context, sbg);
		bg.recycle();
		
		Bitmap p1 = Bitmap.createBitmap(bmp,0,h/2,w/2,h/6);
		Bitmap sp1 = Bitmap.createScaledBitmap(p1, 512, 512, false);
		player1.loadGLTexture(gl, context, sp1);
		p1.recycle();
		
		Bitmap p2 = Bitmap.createBitmap(bmp,w/2,h/2,w/2,h/6);
		Bitmap sp2 = Bitmap.createScaledBitmap(p2, 512, 512, false);
		//player1.loadGLTexture(gl, context, Bitmap.createBitmap(bmp,0,h/2,w/2,h/6));
		player2.loadGLTexture(gl, context, sp2);
		
		Bitmap bb = Bitmap.createBitmap(bmp,0,h * 4/6,w,h/6);
		Bitmap sbb = Bitmap.createScaledBitmap(bb, 512, 512, false);
		back.loadGLTexture(gl, context, sbb);
		bb.recycle();
		
		Bitmap bw = Bitmap.createBitmap(bmp,0, h * 5 /6,w,h/6);
		Bitmap sbw = Bitmap.createScaledBitmap(bw, 512, 512, false);
		pressback.loadGLTexture(gl, context, sbw);
		bw.recycle();
	}
	public void UpdateWinner(UnitGroup g){
		winner = g;
	}
	public void DrawGameOver(GL10 gl){
		gl.glLoadIdentity();
		gl.glEnable( GL10.GL_BLEND );                   // Enable Alpha Blend
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );  // Set Alpha Blend Function
		background.draw(gl);
		if(winner != null){
			if(winner == UnitGroup.PlayerOne)
				player1.draw(gl);
			else if(winner == UnitGroup.PlayerTwo)
				player2.draw(gl);
		}
		if(!flag)
			back.draw(gl);
		else
			pressback.draw(gl);
		gl.glDisable( GL10.GL_BLEND );
	}
	public boolean checkPressingMeu(int x, int y){
		return( x >= xTop && x <= xBot && y <= yBot && y >= yTop);
	}
}
