package com.comp4903.project.graphics.animation;

import java.util.List;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.graphics.RendererAccessor;
import com.comp4903.project.graphics.tile.Hexagon;
import com.comp4903.project.network.Networking;

/*	MOVEANIMATE - animation process for moving a unit down a 
 *  path of tiles.
 * 
 */
public class MoveAnimate extends AnimationProcessor {

	private int actorID;
	private Actor actor_;
	private List<Point> steps;
	
	private float stepPosition;
	private int step;
	private int nextstep;
	
	private float xStep, yStep, zStep;
	private float x, y, z;
	private Point startPosition = new Point();
	private float stepLength, distTravelled;
	
	/*	INIT - initializes and starts move
	 * 		u		Unit to move
	 * 		steps	List of points representing each tile
	 * 				the unit is expected to visit
	 */
	public void init(Unit u, List<Point> s)
	{
		foreground = true;
		
		actorID = u.uID;
		actor_ = RendererAccessor.map.getActor(actorID);
		step = s.size();
		nextstep = step - 1;
		
		startPosition = u.position;
		
		actor_.setAnimation("walk.cycle");		
		actor_.speed = 0;
		actor_.time = 0;
		
		
		if (step <= 0)
		   ended = true;
		steps = s;
				
		if (nextstep >= 0)
			startNewMove();			
		else
			ended = true;	
		
		//Networking.sendBuffer.reset();
		//Networking.sendBuffer.append("Unit: " + u.uID + " moves to: " + s.get(0).x + ", " + s.get(0).y);
		//Networking.timetosend = true;
		
		
	}
	
	/*	STARTNEWMOVE - For each tile, this turns the unit to face the
	 *  next tile, and computes the movement direction
	 * 
	 */
	private void startNewMove()
	{
		distTravelled = 0;
		int d = 0;
		if (step == steps.size())
			d = Hexagon.getDirection(startPosition, steps.get(nextstep));
		else
			d = Hexagon.getDirection(steps.get(step), steps.get(nextstep));
		
		float a = angleFromDirection(d);
		
		actor_.setYrotate(a);
		
		Point p;
		
		if (step == steps.size())
			p = startPosition;
		else
			p = steps.get(step);
		
		x = (float)p.x * 1.5f;
		z = (float)p.y * 0.8660254038f * 2f + (p.x % 2) * 0.8660254038f;
		y = 0;
		actor_.setPosition(x, y, z);
				
		p = steps.get(nextstep);
		
		float x2 = (float)p.x * 1.5f;
		float z2 = (float)p.y * 0.8660254038f * 2f + (p.x % 2) * 0.8660254038f;
		float y2 = 0;
		
		stepLength = (float)Math.sqrt((x2-x) * (x2-x) + (y2-y)*(y2-y) + (z2-z)*(z2-z));
		if (stepLength == 0)
			stepLength = 0.000001f;
		xStep = (x2 - x) / stepLength; // * RendererAccessor.map.models[actor_.model].scale[0];
		yStep = (y2 - y) / stepLength; // * RendererAccessor.map.models[actor_.model].scale[1];;
		zStep = (z2 - z) / stepLength; // * RendererAccessor.map.models[actor_.model].scale[2];;
		stepPosition = 0f;
	}
	
	/*	PROCESS - processes one iteration of the animation.  Returns true
	 *  when the animation is complete, false otherwise
	 */
	public boolean iteration() {		
		
		actor_.speed = 0.3f;
		
		if (distTravelled < stepLength)
		{
			float lasttime = actor_.time - actor_.speed;
			if (lasttime < 0)
				lasttime = 0;
			float travel = actor_.lastZ - actor_.previousZ;
			if (actor_.animation == -1)
				travel = 1f / stepLength;
			x += xStep * travel;
			y += yStep * travel;
			z += zStep * travel;
			
			distTravelled += travel;
			
			actor_.setPosition(x, y, z);
		} 
		else 
		{					
			step--;
			nextstep = step - 1;
			
			if (nextstep >= 0)
				startNewMove();			
			else
			{
				ended = true;
				actor_.setAnimation("idle1");
				actor_.speed = 0.03f;
				actor_.time = 0f;
				
			}
		}
		
		return false;
	}

	@Override
	public boolean signal(int value) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
