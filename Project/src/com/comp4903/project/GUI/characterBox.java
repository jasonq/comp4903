package com.comp4903.project.GUI;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.data.UnitData;
import com.comp4903.project.gameEngine.factory.GameStats;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class characterBox extends UI {



	public Square RedBar,BlueBar,Avatar;
	public String atkVal, defVal, roundVal, nameVal, classVal,hpVal, enVal;
	public int currentHp, totalHp, currentE, totalE;
	public Unit unit;
	public GLText GLT;
	public int x,y,bWidth, bHeight;
	public float scale;
	public Square[] avatar = new Square[3];
	public characterBox(Context c, int w, int h,Unit u,GLText g) {
		super(c,w,h);
		currentHp = 500;
		totalHp = 2000;
		currentE = 500;
		totalE = 1000;
		unit = u;
		RedBar = new Square();
		BlueBar = new Square();
		Avatar = new Square();

		x = width/40;
		y = height/30;
		
		bWidth = width/3;
		bHeight = height  / 4;

		box = new Square( x, y, bWidth, bHeight);
		GLT = g;
		float textHeight = GLT.getHeight();
		scale = bHeight/textHeight;


	}


	public void setText(){
		if(unit != null){
			UnitData stat = unit.combatStats;	
			atkVal = "" + stat.attack;
			defVal = "" + stat.defence;
			roundVal = "" + stat.round;
			nameVal = "" + GameStats.getUnitStats(unit.unitType).name;
			classVal = nameVal;
			hpVal = ""  + stat.currentHealth + "/" + stat.maxHealth;
			enVal = "" + stat.currentEnergy + "/" + stat.maxEnergy;
			currentHp = stat.currentHealth;
			totalHp = stat.maxHealth;
			currentE = stat.currentEnergy;
			totalE = stat.maxEnergy;

		}
	}
	public void setUnit(Unit abc){
		unit = abc;
	}
	public void loadUITexture(GL10 gl,Resources r, int id){
		super.loadUITexture(gl, r, id);


		Bitmap hp = BitmapFactory.decodeResource(context.getResources(),R.drawable.bar);

		int w = hp.getWidth();
		int h = hp.getHeight();

		Bitmap temp = Bitmap.createBitmap(hp,
				0 ,0, w, h/2);

		RedBar = new Square();
		RedBar.loadGLTexture(gl, context, temp);
		temp.recycle();

		Bitmap temp1 = Bitmap.createBitmap(hp,
				0 ,h/2, w, h/2);

		BlueBar = new Square();
		BlueBar.loadGLTexture(gl, context, temp1 );
		temp1.recycle();
		hp.recycle();
		
		loadAvatar(gl, context);
	}
	
	public void loadAvatar(GL10 gl, Context c){
		Bitmap ava = BitmapFactory.decodeResource(c.getResources(), R.drawable.avatar);
		int bmWidth = ava.getWidth();
		int bmHeight = ava.getHeight();
		int aWidth = bmWidth / 3;
		
		for(int i = 0; i < avatar.length; i++){
			avatar[i] = new Square();
			Bitmap temp = Bitmap.createBitmap(ava,
					i * aWidth , 0, aWidth, bmHeight);
			Bitmap scale = Bitmap.createScaledBitmap(temp, 256, 256, false);
			avatar[i].loadGLTexture(gl, context, scale);
			temp.recycle();
			//temp.recycle();
		}
		ava.recycle();
	}
	public void draw(GL10 gl){
		gl.glLoadIdentity();
		box.draw(gl);
		if(unit != null){
			//gl.glEnable( GL10.GL_TEXTURE_2D );
			gl.glEnable( GL10.GL_BLEND );                   // Enable Alpha Blend
			gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );  // Set Alpha Blend Function

			int xLocation = x + (bWidth * 15 / 100);
			int yLocation = y + (bHeight * 75 / 100);//((height * 3 )/ 4) + (height/40);
			
			//int gap = (int)(bHeight * 5  / 100 + GLT.getHeight());//(height/40);
			int gap = bWidth *  20 / 100;
			GLT.begin( 1.0f, 1.0f, 1.0f, 1.0f );    
			//set location and load texture  the attack value	
			//float ratioText = GLT.getHeight()/ (bHeight * 15/100);
			//GLT.setScale(1.0f, ratioText);
			
			GLT.draw(atkVal, xLocation, yLocation);
			//yLocation +=  gap;//increament y
			xLocation += gap;
			//set location and load texture the defense value
			GLT.draw(defVal, xLocation, yLocation);
			//yLocation +=  gap;
			xLocation += gap;
			//set location and load texture  number of round
			GLT.draw(roundVal, xLocation, yLocation);
			//yLocation +=   gap;
			xLocation += gap;
			//move x and y to write the name
			//xLocation = x +  (bWidth * 35 / 100);
			//yLocation = y + (bHeight * 10/ 100);
			
			xLocation = x +  (bWidth * 65 / 100);
			yLocation = y + (bHeight * 5/ 100);
			//set location and load texture  of name
			GLT.draw(nameVal, xLocation, yLocation);
			//yLocation +=    gap;

			//set location and load texture of class
			//GLT.draw(classVal, xLocation, yLocation);
			//yLocation +=   gap;
			
			
			GLT.end(); 

		
			
			float hpRatio = (float)currentHp/totalHp;
			float eRatio = (float)currentE / totalE;
			
			int eWidth = (int)((bWidth * 55 / 100) * eRatio);
			int hpWidth = (int)((bWidth * 55 / 100) * hpRatio);
			int barHeight = bHeight * 15 / 100;
			
			xLocation = x + (bWidth * 40/ 100);
			yLocation = y + (bHeight * 25 / 100);
			
			int Tx = xLocation;
			int Ty = yLocation;
			RedBar.UpdateVertices(xLocation, yLocation, hpWidth, barHeight);
			
			yLocation += ((bHeight * 20) / 100);
			BlueBar.UpdateVertices(xLocation, yLocation, eWidth, barHeight + 2);
			
			int Ty2 = yLocation;
			RedBar.draw(gl);
			BlueBar.draw(gl);
			
			xLocation = x +  (bWidth * 5 )/ 100;
			yLocation = y + (bHeight * 5 )/ 100;
			
			avatar[unit.unitType.getCode()].UpdateVertices(xLocation, yLocation, (bWidth * 30) / 100, (bHeight * 65) / 100);
			avatar[unit.unitType.getCode()].draw(gl);
			
			
			//Avatar.draw(gl);
			//HP.drawText(gl);
			//Energy.drawText(gl);
			// disable texture + alpha
			GLT.begin(1.0f, 1.0f, 1.0f,1.0f);
			GLT.draw(hpVal, Tx + 5, Ty);
			GLT.draw(enVal, Tx + 5, Ty2);
			GLT.end();
			gl.glDisable( GL10.GL_BLEND );                // Disable Alpha Blend
			//gl.glDisable( GL10.GL_TEXTURE_2D ); 
		}

	}
}
