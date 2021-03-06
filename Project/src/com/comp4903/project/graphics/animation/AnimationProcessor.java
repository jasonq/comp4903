package com.comp4903.project.graphics.animation;

import android.graphics.Point;

/*	ANIMATIONPROCESSOR - base class to define generic animation logic.
 *  Derived classes will implement logic of various types of animations
 *  such as walking or attacking, and must implement:
 *  
 *  iteration()		performs an iteration of the animation
 *  signal(..)		used to send signals between animations
 * 
 * 	It is also recommended to implement a method called init(...) although not
 *  required.
 */
public abstract class AnimationProcessor {

	int delay;						// can be used to delay start of an animation
	boolean started = false;
	boolean ended = false;
	boolean foreground = false;		// indicate whether the animation is to have priority over the user
	String name;
	
	public abstract boolean iteration();
	public abstract boolean signal(int value);
	
	/*	PROCESS - checks to see if the animation is active and ready, and
	 *  if so, performs an iteration of it.
	 * 
	 */
	public boolean process()
	{
		if (!started)
			return false;
		if (delay > 0)
		{
			delay--;
			return false;
		}
		iteration();	
		
		return true;
	}
	
	// Some useful functions which can be used by derived classes, or by any class
	// for that matter.
	
	/*	ANGLEFROMDIRECTION - given a direction (0 - 5, representing
	 *  the 6 sides of a hexagon starting from northwest), returns
	 *  the angle in radians.
	 * 
	 */
	public float angleFromDirection(int d)
	{
		switch (d)
		{
		case 0:
			return 4.112388898f;			
		case 1:
			return 3.326990817f;
		case 2:
			return 1.95619449f;
		case 3:
			return 1.170796327f;			
		case 4:
			return -0.1f; //0.0853981634f;
		case 5:
			return 5.197787144f;
		default:
			return 0f;
		}
	}
	
	/*	ANGLEFROMPOINTS - given two points in world space, computes the angle
	 * 
	 */
	public float angleFromPoints(float fromX, float fromY, float toX, float toY)
	{
		float result = 0;
				
		float xdiff = toX - fromX;
		float ydiff = toY - fromY;
		
		result = (float)Math.atan2((double)ydiff, (double)xdiff);
		
		return result;
	}
}
