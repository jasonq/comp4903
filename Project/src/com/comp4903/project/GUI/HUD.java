package com.comp4903.project.GUI;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;
import com.comp4903.project.gameEngine.data.Unit;

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
	public Glyphs glyph;
	public String[] actionCommand = {"Attack","ability","items","view Stats","cancel"};
	public Square[] squareCommand;
	
	public boolean showAction = false;
	public boolean showStat = false;
	public boolean showAbility = false;
	public boolean showAttack = false;
	
	public HUD(Context c, int w, int h){
		context = c;		
		width = w;
		height = h;
		glyph = new Glyphs(BitmapFactory.decodeResource(context.getResources(), R.drawable.glyphs_black));
		action = new actionBox(context,width,height);
		Unit abc = null;
		character = new characterBox(context,width,height,abc);
	}
	
	public void updateHUD(boolean showAction, boolean showStat, boolean showAbility, boolean showAttack){
		this.showAction = showAction;
		this.showStat = showStat;
		this.showAbility = showAbility;
		this.showAttack = showAttack;
	}
	public void getCommandSquare(GL10 gl){
		//these 2 lines are important
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		squareCommand = new Square[5];
		for(int i = 0; i < 5; i++){

			String s = actionCommand[i];
			Bitmap bb = glyph.getString(this.actionCommand[i]);
			squareCommand[i] = new Square();
			squareCommand[i].loadGLTexture(gl, context, bb);
			bb.recycle();

		}
	}
	public void initialBoxTexture(GL10 gl){
		action.loadUITexture(gl, context.getResources(), R.drawable.menu);
		character.loadUITexture(gl, context.getResources(), R.drawable.statpanel);
	}

	public void drawHUD(GL10 gl){
		if(showAction)
			action.draw(gl);		
		if(showStat)
			character.draw(gl);
	}

	public boolean checkTouchingMenu(int x, int y){
		if(x <= action.xBot && x >= action.xTop && y <= action.yBot && y >=action.yTop)
			return true;
		else
			return false;
	}

	public void SwithToOrtho(GL10 gl){
		//gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(gl.GL_PROJECTION); // Select Projection
		gl.glPushMatrix(); // Push The Matrix
		gl.glLoadIdentity(); // Reset The Matrix
		gl.glOrthof( 0, width , height  , 0, -1, 1 ); // Select Ortho Mode
		gl.glMatrixMode(gl.GL_MODELVIEW); // Select Modelview Matrix
		gl.glPushMatrix(); // Push The Matrix
		gl.glLoadIdentity(); // Reset The Matrix
		//gl.glOrthof( 0, width , 0  , height, 0, 0 ); // Select Ortho Mode
	}

	public void SwitchToPerspective(GL10 gl){
		gl.glMatrixMode( gl.GL_PROJECTION ); // Select Projection
		gl.glPopMatrix();
		//gl.glLoadIdentity();
		gl.glMatrixMode( gl.GL_MODELVIEW ); // Select Modelview
		//gl.glLoadIdentity();
		gl.glPopMatrix();
	}
	
	public void updateStatPanel(GL10 gl, Unit abc){
		//character = new characterBox(context,width,height,abc);
		character.setUnit(abc);
		character.loadUITexture(gl, context.getResources(), R.drawable.statpanel);
	}


}