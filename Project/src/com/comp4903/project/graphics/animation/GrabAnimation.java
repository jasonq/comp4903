package com.comp4903.project.graphics.animation;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.graphics.RendererAccessor;

public class GrabAnimation extends AnimationProcessor {

	int actorID;
	Actor actor_;
	Actor victim_;
	
	float x, y, z, stepLength;
	float xStep, yStep, zStep;
	float stepPosition;
	float tx, ty, tz;
	int count;
	
	public void init(Unit attacker, Unit attackee, Point target)
	{
		actorID = attacker.uID;
		actor_ = RendererAccessor.map.getActor(actorID);
		
		victim_ = RendererAccessor.map.getActor(attackee.uID);
		
		float angle = angleFromPoints(actor_.getX(), actor_.getZ(),
									  victim_.getX(), victim_.getZ());
		
		actor_.setYrotate(-angle + 3.141593f / 2f);
		
		float x2 = (float)target.x * 1.5f;
		float z2 = (float)target.y * 0.8660254038f * 2f + (target.x % 2) * 0.8660254038f;
		float y2 = 0;
		
		angle = angleFromPoints(victim_.getX(), victim_.getZ(),
				  x2, z2);
		victim_.setYrotate(-angle + 3.141593f / 2f);
		//victim_.setYrotate(-angle + 3.141593f / 2f + 3.141593f);
		
		x = victim_.getX();
		z = victim_.getZ();
		y = 0;
		victim_.setPosition(x, y, z);		
		
		stepLength = (float)Math.sqrt((x2-x) * (x2-x) + (y2-y)*(y2-y) + (z2-z)*(z2-z));
		if (stepLength == 0)
			stepLength = 0.000001f;
		xStep = (x2 - x) / stepLength; // * RendererAccessor.map.models[actor_.model].scale[0];
		yStep = (y2 - y) / stepLength; // * RendererAccessor.map.models[actor_.model].scale[1];;
		zStep = (z2 - z) / stepLength; // * RendererAccessor.map.models[actor_.model].scale[2];;
		stepPosition = 0f;
		
		count = 0;
		
	}
	
	@Override
	public boolean iteration() {
		
		x += xStep / stepLength;
		y += yStep / stepLength;
		z += zStep / stepLength;
		
		victim_.setPosition(x,y,z);
		count++;
		if (count > stepLength * stepLength)
			ended = true;
		
		return false;
	}

	@Override
	public boolean signal(int value) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
