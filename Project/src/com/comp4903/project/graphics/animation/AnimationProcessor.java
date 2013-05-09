package com.comp4903.project.graphics.animation;

/*	ANIMATIONPROCESSOR - base class to define generic animations
 *  such as walking and attacking
 * 
 */
public abstract class AnimationProcessor {

	int delay;
	boolean started = false;
	boolean ended = false;
	String name;
	
	public abstract boolean process();
	
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
			return 0.7853981634f;
		case 1:
			return 1.570796327f;
		case 2:
			return 2.35619449f;
		case 3:
			return 3.926990817f;
		case 4:
			return 4.712388898f;
		case 5:
			return 5.497787144f;
		default:
			return 0f;
		}
	}
}
