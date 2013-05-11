package com.comp4903.project.graphics.animation;

import android.graphics.Point;

import com.comp4903.project.gameEngine.enums.UnitType;

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
	
	public Actor(int i)
	{
		uID = i;
		xRotate = 0f; yRotate = 0f; zRotate = 0f;
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
}