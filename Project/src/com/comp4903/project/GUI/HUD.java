package com.comp4903.project.GUI;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.graphics.GLRenderer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.view.Display;
import android.view.WindowManager;
/*
 * Drawing menus, subbar, character stats, etc
 */
public class HUD {
	private Context context;
	public int width,height;
	public actionBox action;
	public characterBox character;
	
	public Square giveUpTurn;
	public Square pgiveUpTurn;
	
	public Square check;
	
	private int Gx,Gy,Gw,Gh;
	public boolean showAction = false;
	public boolean showStat = false;
	public boolean showCancel = false;
	public boolean showEnd = true;
	
	public HUD(Context c, int w, int h,GLText glt){
		context = c;		
		width = w;
		height = h;
		action = new actionBox(context,width,height);
		Unit abc = null;
		character = new characterBox(context,width,height,abc, glt);
		Gx = width/30;
		Gy = height*8/10;
		Gw = width  / 15 ;
		Gh =  height/ 8;
		giveUpTurn = new Square(Gx,Gy,Gw,Gh);
		pgiveUpTurn = new Square(Gx,Gy,Gw,Gh);
		check = new Square(Gx,Gy,Gw,Gh);
	}
	
	public void updateHUD(boolean showAction, boolean showStat, boolean showCancel, boolean showEnd){
		this.showAction = showAction;
		this.showStat = showStat;
		this.showCancel = showCancel;
		this.showEnd = showEnd;
	}

	public void initialBoxTexture(GL10 gl){
		action.loadUITexture(gl, context.getResources(), R.drawable.bar);
		character.loadUITexture(gl, context.getResources(), R.drawable.statpanel_v2);
		
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.check);
		giveUpTurn.loadGLTexture(gl, context, bm);
		
		
		Bitmap bm2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.pressed_check);
		pgiveUpTurn.loadGLTexture(gl, context, bm2);
		
		Bitmap bm3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.check);
		check.loadGLTexture(gl, context, bm3);
	}

	public void drawHUD(GL10 gl){
		gl.glLoadIdentity();
		gl.glEnable( GL10.GL_BLEND );                   // Enable Alpha Blend
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );  // Set Alpha Blend Function
		if(showEnd)
			check.draw(gl);
		gl.glDisable(GL10.GL_BLEND);
		if(showAction)
			action.draw(gl,showCancel);		
		if(showStat)
			character.draw(gl);
	}

	public boolean checkTouchingMenu(int x, int y){
		if(x <= action.xBot && x >= action.xTop && y <= action.yBot && y >=action.yTop)
			return true;
		else
			return false;
	}

	public static void SwithToOrtho(GL10 gl){
		//gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(gl.GL_PROJECTION); // Select Projection
		gl.glPushMatrix(); // Push The Matrix
		gl.glLoadIdentity(); // Reset The Matrix
		gl.glOrthof( 0, GLRenderer.GLwidth , GLRenderer.GLheight  , 0, 1, -1 ); // Select Ortho Mode
		gl.glMatrixMode(gl.GL_MODELVIEW); // Select Modelview Matrix
		gl.glPushMatrix(); // Push The Matrix
		gl.glLoadIdentity(); // Reset The Matrix
		//gl.glOrthof( 0, width , 0  , height, 0, 0 ); // Select Ortho Mode
	}

	public static void SwitchToPerspective(GL10 gl){
		gl.glMatrixMode( gl.GL_PROJECTION ); // Select Projection
		gl.glPopMatrix();
		//gl.glLoadIdentity();
		gl.glMatrixMode( gl.GL_MODELVIEW ); // Select Modelview
		//gl.glLoadIdentity();
		gl.glPopMatrix();
	}
	
	public void updateStatPanel( Unit abc){
		//character = new characterBox(context,width,height,abc);
		character.setUnit(abc);
		character.setText();
		action.UpdateUnit(abc);
		//character.loadUITexture(gl, context.getResources(), R.drawable.statpanel);
	}
	
	public boolean checkPressingEndTurn(int x , int y){
		return (x >= Gx && x <= (Gx + Gw) && y >= Gy && y <= (Gy + Gh));
	}

}