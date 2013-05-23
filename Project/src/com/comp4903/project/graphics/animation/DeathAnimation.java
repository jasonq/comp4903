package com.comp4903.project.graphics.animation;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.IconType;
import com.comp4903.project.graphics.RendererAccessor;

/*	DEATHANIMATION - slightly misleading name as the actual death animation
 *  frame sequence is initiated by the ReceiveAttack class.  However, this
 *  class will 'clean up' the map after a unit is killed, by having it sink
 *  into the ground. 
 */
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
		//actor_.setYrotate(r);
		time++;
		
		if (time > 150)
		{
			actor_.setY(actor_.getY() - 0.02f);			
		}
		if (time > 200)
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

