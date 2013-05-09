package com.comp4903.project.graphics.animation;

import java.util.List;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.graphics.RendererAccessor;
import com.comp4903.project.graphics.tile.Hexagon;

/*	MOVEANIMATE - animation process for moving a unit down a 
 *  path of tiles.
 * 
 */
public class MoveAnimate extends AnimationProcessor {

	private int actorID;
	private List<Point> steps;
	
	private float stepPosition;
	private int step;
	private int nextstep;
	
	private float xStep, yStep, zStep;
	private float x, y, z;
	private Point startPosition = new Point();
	
	/*	INIT - initializes and starts move
	 * 		u		Unit to move
	 * 		steps	List of points representing each tile
	 * 				the unit is expected to visit
	 */
	public void init(Unit u, List<Point> s)
	{
		actorID = u.uID;
		step = s.size();
		nextstep = step - 1;
		
		startPosition = u.position;
		
		if (step <= 0)
		   ended = true;
		steps = s;
				
		if (nextstep >= 0)
			startNewMove();			
		else
			ended = true;		
		
	}
	
	/*	STARTNEWMOVE - For each tile, this turns the unit to face the
	 *  next tile, and computes the movement direction
	 * 
	 */
	private void startNewMove()
	{
		int d = 0;
		if (step == steps.size())
			d = Hexagon.getDirection(startPosition, steps.get(nextstep));
		else
			d = Hexagon.getDirection(steps.get(step), steps.get(nextstep));
		
		float a = angleFromDirection(d);
		
		RendererAccessor.map.setActorRotation(actorID, a);
		
		Point p;
		
		if (step == steps.size())
			p = startPosition;
		else
			p = steps.get(step);
		
		x = (float)p.x * 1.5f;
		z = (float)p.y * 0.8660254038f * 2f + (p.x % 2) * 0.8660254038f;
		y = 0;
		RendererAccessor.map.setActorPosition(actorID, x, y, z);
				
		p = steps.get(nextstep);
		
		float x2 = (float)p.x * 1.5f;
		float z2 = (float)p.y * 0.8660254038f * 2f + (p.x % 2) * 0.8660254038f;
		float y2 = 0;
		
		xStep = (x2 - x) / 40f;
		yStep = (y2 - y) / 40f;
		zStep = (z2 - z) / 40f;
		stepPosition = 0f;
	}
	
	/*	PROCESS - processes one iteration of the animation.  Returns true
	 *  when the animation is complete, false otherwise
	 */
	public boolean process() {
		
		if (!started)
			return false;
		if (delay > 0)
		{
			delay--;
			return false;
		}
		
		if (stepPosition < 0.975f)
		{
			x += xStep;
			y += yStep;
			z += zStep;
			stepPosition += 0.025f;
			RendererAccessor.map.setActorPosition(actorID, x, y, z);
		} else {
			step--;
			nextstep = step - 1;
			
			if (nextstep >= 0)
				startNewMove();			
			else
				ended = true;	
		}
		
		return ended;
	}
	
	

}
