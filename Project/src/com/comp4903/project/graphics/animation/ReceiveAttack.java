package com.comp4903.project.graphics.animation;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.ColorType;
import com.comp4903.project.gameEngine.enums.IconType;
import com.comp4903.project.graphics.RendererAccessor;

public class ReceiveAttack extends AnimationProcessor {

	Actor actor_;
	int actorID;
	int state = 0;
	String[] messages_;
	int count = 2;
	int m = 0;
	
	public void init(Unit attacker, Unit attackee, String[] messages)
	{
		actorID = attackee.uID;
		actor_ = RendererAccessor.map.getActor(actorID);
		messages_ = messages;
	}
	
	@Override
	public boolean iteration() {
		
		if (state == 0)
			return false;
		
		count--;
		if (count == 0)
		{
			Point p = RendererAccessor.ScreenXYfromXYZ(actor_.getX(), actor_.getY() + 2, actor_.getZ());
			RendererAccessor.floatingText(p.x-40, p.y, 0, -2, 100, ColorType.Red, "bozo", messages_[m++]);
			count = 20;
		}
		if (m == messages_.length)
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
