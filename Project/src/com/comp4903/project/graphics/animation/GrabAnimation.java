package com.comp4903.project.graphics.animation;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.graphics.RendererAccessor;

public class GrabAnimation extends AnimationProcessor {

	int actorID;
	Actor actor_;
	Actor victim_;
	Unit attacker, attackee;
	
	float x, y, z, stepLength;
	float xStep, yStep, zStep;
	float stepPosition;
	float tx, ty, tz;
	int count;
	boolean animateVictim, finishing;
	String[] damages;
	
	public void init(Unit a1, Unit a2, Point target, String[] d)
	{
		attacker = a1;
		attackee = a2;
		damages = d;
	
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
		stepLength *= 15f;
		if (stepLength == 0)
			stepLength = 0.000001f;
		xStep = (x2 - x) / stepLength; // * RendererAccessor.map.models[actor_.model].scale[0];
		yStep = (y2 - y) / stepLength; // * RendererAccessor.map.models[actor_.model].scale[1];;
		zStep = (z2 - z) / stepLength; // * RendererAccessor.map.models[actor_.model].scale[2];;
		stepPosition = 0f;
		
		count = 0;
		animateVictim = false;
		finishing = false;
		
		actor_.setAnimation("attack.grab");
		actor_.time =0;
		actor_.speed = 0.2f;
		
	}
	
	@Override
	public boolean iteration() {
				
		if (finishing)
		{
			if (actor_.time == 0)
			{
				ended = true;
				spawnAttack();
				
			}
		}
		
		if (actor_.time < 13)
			return false;
		
		if (!animateVictim)
		{
			animateVictim = true;
			victim_.setAnimation("drag.standard");
			victim_.time = 0;
			victim_.speed = 0.2f;
		}
		
		x += xStep;
		y += yStep;
		z += zStep;
		
		victim_.setPosition(x,y,z);
		
		count++;
		if (count > stepLength)
		{
			finishing = true;
			victim_.time = 6;
			victim_.speed = -0.2f;
			actor_.time = 13;
			actor_.speed = -0.25f;			
		}
		
		return false;
	}

	@Override
	public boolean signal(int value) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void spawnAttack()
	{
		if (damages.length == 0)
		{   
			victim_.setAnimation("idle1");
			victim_.time = 0;
			victim_.speed = 0.03f;
			actor_.setAnimation("idle1");
			actor_.time = 0;
			actor_.speed = 0.03f;
		} else {
			GenericAttack m = new GenericAttack();
			ReceiveAttack r = new ReceiveAttack();
			
			m.init(attacker, attackee);
			r.init(attacker,  attackee, damages);
			
			AnimationEngine.unsafeAdd("Attack", m);
			AnimationEngine.unsafeAdd("Receiver", r);
			AnimationEngine.unsafeStart("Attack");
			AnimationEngine.unsafeStart("Receiver");
		}
	}

	
	
}
