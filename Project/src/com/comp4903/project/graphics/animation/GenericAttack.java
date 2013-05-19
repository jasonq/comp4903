package com.comp4903.project.graphics.animation;

import java.util.Random;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.graphics.RendererAccessor;

public class GenericAttack extends AnimationProcessor {
	
	private int actorID;
	private Actor actor_;
	private Random r;
	private boolean signalled;
	
	int hitTime, endTime;
	
	private int time = 0;
		
	public void init(Unit attacker, Unit attackee)
	{
		r = new Random();
		
		foreground = true;
		
		actorID = attacker.uID;
		actor_ = RendererAccessor.map.getActor(actorID);
		
		Actor victim = RendererAccessor.map.getActor(attackee.uID);
		
		float angle = angleFromPoints(actor_.getX(), actor_.getZ(),
									  victim.getX(), victim.getZ());
		
		actor_.setYrotate(-angle + 3.141593f / 2f);
		victim.setYrotate(-angle + 3.141593f / 2f + 3.141593f);
		
		actor_.setAnimation("attack.basic");		
		actor_.speed = 0.3f; //0.08f;
		actor_.time = 3;
		
		if (actor_.animation != -1)
		{
			hitTime = actor_.m3d.parameters.get("hit.frame").intVal;
			endTime = actor_.m3d.parameters.get("end.frame").intVal;
		} else {
			hitTime = 0;
			endTime = 0;
		}
		
		signalled = false;
	}
	
	@Override
	public boolean iteration() {
			
		if (signalled)
		{
			if ((actor_.time > endTime) || (actor_.animation == -1))
			{
				actor_.setAnimation("idle1");
				ended = true;
				actor_.time = 0;
				actor_.speed = 0.03f;
			}
			return false;
		}
		
		if ((actor_.time > hitTime) || (actor_.animation == -1))
		{
			actor_.setZrotate(0);
			actor_.setXrotate(0);			
			AnimationEngine.signal("Receiver", 1);
			signalled = true;
			return true;
		}
		
		return false;
	}

	@Override
	public boolean signal(int value) {
		// TODO Auto-generated method stub
		return false;
	}

}
