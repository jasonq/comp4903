package com.comp4903.project.graphics.animation;

import java.util.Random;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.graphics.RendererAccessor;

public class GenericAttack extends AnimationProcessor {
	
	private int actorID;
	private Actor actor_;
	private Random r;
	
	private int time = 0;
		
	public void init(Unit attacker, Unit attackee)
	{
		r = new Random();
		actorID = attacker.uID;
		actor_ = RendererAccessor.map.getActor(actorID);
		
		Actor victim = RendererAccessor.map.getActor(attackee.uID);
		
		float angle = angleFromPoints(actor_.getX(), actor_.getZ(),
									  victim.getX(), victim.getZ());
		
		actor_.setYrotate(-angle + 3.141593f / 2f);
		victim.setYrotate(-angle + 3.141593f / 2f + 3.141593f);
	}
	
	@Override
	public boolean iteration() {
		
		if (time % 3 == 0) {

			actor_.setZrotate(r.nextFloat() * 0.10f - 0.05f);
			actor_.setXrotate(r.nextFloat() * 0.10f - 0.05f);
		}
		
		time++;
		
		if (time > 30)
		{
			actor_.setZrotate(0);
			actor_.setXrotate(0);
			ended = true;
			AnimationEngine.signal("Receiver", 1);
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
