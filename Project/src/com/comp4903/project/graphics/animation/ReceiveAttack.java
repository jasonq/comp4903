package com.comp4903.project.graphics.animation;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.IconType;
import com.comp4903.project.graphics.RendererAccessor;

public class ReceiveAttack extends AnimationProcessor {

	Actor actor_;
	int actorID;
	int state = 0;
	
	public void init(Unit attacker, Unit attackee)
	{
		actorID = attackee.uID;
		actor_ = RendererAccessor.map.getActor(actorID);
	}
	
	@Override
	public boolean iteration() {
		
		if (state == 0)
			return false;
		
		state++;
		Point p = RendererAccessor.ScreenXYfromXYZ(actor_.getX(), actor_.getY() + 2, actor_.getZ());
		RendererAccessor.floatingText(p.x-40, p.y, 0, -1, 100, "bozo", "-15 HP");
		RendererAccessor.floatingIcon(p.x - 100, p.y, 0, -1, 100, "snoz", IconType.Defense);
		
		ended = true;
			// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean signal(int value) {

		state = 1;
		
		return false;
	}

}
