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
	boolean dying;
	
	public void init(Unit attacker, Unit attackee, String[] messages)
	{
		foreground = true;
		actorID = attackee.uID;
		actor_ = RendererAccessor.map.getActor(actorID);
		messages_ = messages;
		if (attackee.combatStats.currentHealth <= 0)
			dying = true;
		else
			dying = false;
		//if ((messages.length > 0) && (messages[0].equals("")))
		//	RendererAccessor.floatingText(500, 30,0, 0, -1, ColorType.Red, "error", "messages blank");
		
	}
	
	@Override
	public boolean iteration() {
		
		if (state == 0)
			return false;
				
		count--;
		if (count == 0)
		{
			//Point p = RendererAccessor.ScreenXYfromXYZ(actor_.getX(), actor_.getY() + 2, actor_.getZ());
			//if (p == null)
			//	p = new Point(0,0);
			//RendererAccessor.floatingText(p.x-40, p.y, 0, -2, 100, ColorType.Red, "m"+ m, messages_[m++]);
			RendererAccessor.floatingText(actor_.getX(),
										actor_.getY() + 2,
										actor_.getZ(),
										0, -2, 100, ColorType.Red, "m"+ m + "u" + actorID, messages_[m++]);
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
		
		actor_.noRepeat = true;
		
		if (!dying)
		{
			actor_.setAnimation("attack.recoil");
			actor_.speed = 0.2f;
		}
		else
		{
			actor_.setAnimation("death.standard");
			actor_.speed = 0.2f;
			//if (actor_.m3d.parameters.containsKey("death.speed"))
			//{
			//	actor_.speed = actor_.m3d.parameters.get("death.speed").floatVal;
			//}
		}
		
		actor_.time = 0;	
		
		return false;
	}

}
