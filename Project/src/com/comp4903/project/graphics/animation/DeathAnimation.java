package com.comp4903.project.graphics.animation;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.IconType;
import com.comp4903.project.graphics.RendererAccessor;

public class DeathAnimation extends AnimationProcessor {

	Actor actor_;
	int actorID;
	int time = 0;
	
	public void init(Unit u)
	{
		actorID = u.uID;
		actor_ = RendererAccessor.map.getActor(actorID);
		foreground = true;
	}
	
	@Override
	public boolean iteration() {
		
		float r = actor_.getYrotate();
		r += 0.1;
		actor_.setYrotate(r);
		time++;
		
		if (time > 50)
		{
			actor_.remove = true;
			ended = true;
		}
		return false;
	}

	@Override
	public boolean signal(int value) {
				
		return false;
	}

}

