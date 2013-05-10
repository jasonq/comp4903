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
		actorID = attacker.uID;
		actor_ = RendererAccessor.map.getActor(actorID);
		
		Actor victim = RendererAccessor.map.getActor(attackee.uID);
		
		float angle = angleFromPoints(actor_.getX(), actor_.getY(),
									  victim.getX(), victim.getY());
		
		actor_.setYrotate(angle);
		victim.setYrotate(angle + 3.141593f);
	}
	
	@Override
	public boolean process() {

		actor_.setZrotate(r.nextFloat() * 0.06f - 0.03f);
		actor_.setXrotate(r.nextFloat() * 0.06f - 0.03f);
		
		time++;
		
		if (time > 30)
		{
			actor_.setZrotate(0);
			actor_.setXrotate(0);
			ended = true;
		}
		
		return false;
	}

}
