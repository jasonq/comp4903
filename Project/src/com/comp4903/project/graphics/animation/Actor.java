package com.comp4903.project.graphics.animation;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Point;
import android.opengl.Matrix;

import com.comp4903.project.gameEngine.enums.UnitType;
import com.comp4903.project.graphics.model.Model3D;

/*	ACTOR - used to store the graphical aspects of a unit, such as 3D orientation,
 *  position and other data as required by the renderer and animation system
 * 
 */
public class Actor {
	
	int model;
	UnitType type;
	int uID;
	int tileX;
	int tileY;
	float x, y, z;
	float xRotate, yRotate, zRotate;
	float[] orientation = new float[16];
	public int animation;
	public float time;
	public float speed;
	public float previousZ, lastZ;
	
	public Actor(int i)
	{
		uID = i;
		xRotate = 0f; yRotate = 0f; zRotate = 0f;
		Matrix.setIdentityM(orientation, 0);
		animation = -1;
		time = 0;
		speed = 0;
		previousZ = 0;
		lastZ = 0;
	}
	
	public void setPosition(float xp, float yp, float zp)
	{
		x = xp; y = yp; z = zp;
	}
	
	public void setRotation(float xr, float yr, float zr)
	{
		xRotate = xr; yRotate = yr; zRotate = zr;
	}
	
	public int getID() { return uID; }
	public int getModel() { return model; }
	
	public float getX()	{ return x;	}	
	public float getY()	{ return y;	}	
	public float getZ()	{ return z;	}	
	
	public void setX(float v) {	x = v; }	
	public void setY(float v) {	y = v; }	
	public void setZ(float v) {	z = v; }
	
	public float getXrotate()	{ return xRotate; }	
	public float getYrotate()	{ return yRotate; }	
	public float getZrotate()	{ return zRotate; }	
	
	public void setXrotate(float v) { xRotate = v; }	
	public void setYrotate(float v) { yRotate = v; }	
	public void setZrotate(float v) { zRotate = v; }
	
	public void setTilePosition(int x, int y) { tileX = x; tileY = y; }
	public void setTilePosition(Point p) { tileX = p.x; tileY = p.y; }
	public Point getTilePosition() { Point p = new Point(tileX, tileY); return p; } 
	
	public UnitType getType() { return type; }
	public void setType(UnitType t) 
	{ 
		type = t; 
		model = type.getCode() % 2; 
	}
	
	public void display(GL10 gl, float[] viewMatrix, Model3D m)
	{
		previousZ = lastZ;
		lastZ = m.setPose(animation, time);		
		
		m.YRotateComponent(0, yRotate);
		m.display(gl, viewMatrix, animation, time);
				
		time += speed;
		if (time > 120)
			time = 10;
		
	}
}