package com.comp4903.project.graphics.animation;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Point;
import android.opengl.Matrix;

import com.comp4903.project.gameEngine.enums.UnitType;
import com.comp4903.project.graphics.RendererAccessor;
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
	public boolean remove = false;
	public boolean alt = false;
	public boolean noRepeat = false;
	public boolean active;
	
	Model3D m3d;
	
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
		noRepeat = false;
		active = true;
		
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
		model = type.getCode() % 3;
		m3d = RendererAccessor.map.models[model];
	}
	
	public void display(GL10 gl, float[] viewMatrix, Model3D m)
	{
		previousZ = lastZ;
		lastZ = m.setPose(animation, time);		
		
		YaxisRotate(m);		
				
		m.display(gl, viewMatrix, animation, time, alt);
				
		time += speed * 2;
		if (time < 0)
			time = 0;
		if ((!noRepeat) && (time >= 119))
			time = 0;
		if ((noRepeat) && time >= 118)
			time = 110;
	}
	
	public void YaxisRotate(Model3D m)
	{
		float[] yAxis = { 0, 0, 0, 0 };
		
		yAxis[0] = 0;
		yAxis[1] = 1;
		yAxis[2] = 0;	

		float[] rotation = m.matrixAngleAroundAxis(yRotate, yAxis[0], yAxis[1], yAxis[2]);
		float[] temp = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				
		Matrix.multiplyMM(temp, 0, rotation, 0, m.components[0].orientation, 0);
		
		for (int i = 0; i < 16; i++)
			m.components[0].orientation[i] = temp[i];
		
	}	
		
	public void setAnimation(String animationName)
	{
		animation = RendererAccessor.map.models[model].getAnimationIndex(animationName);
		time = 0;
	}
}