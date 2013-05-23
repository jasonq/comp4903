package com.comp4903.project.graphics.animation;

import java.util.Random;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.ColorType;
import com.comp4903.project.gameEngine.enums.IconType;
import com.comp4903.project.graphics.RendererAccessor;

/*	DEFENDANIMATION - Not really an animation; it displays a small
 *  shield icon that slowly drifts up the screen.
 * 
 */
public class DefendAnimation extends AnimationProcessor {

	int actorID;
	Actor actor_;
	Point p;	
	
	public void init(Unit u)
	{
		foreground = true;
		actorID = u.uID;
		actor_ = RendererAccessor.map.getActor(actorID);
		p = RendererAccessor.ScreenXYfromXYZ(actor_.getX(), actor_.getY() + 2, actor_.getZ());
		p.x -= 20;
		if (p == null)
			p = new Point(0,0);
		
				
	}
	
	@Override
	public boolean iteration() {
		
		//RendererAccessor.floatingIcon(p.x, p.y, 0, -1, 50, "defense" + actorID, IconType.Defense);
		RendererAccessor.floatingIcon(actor_.getX(),
									  actor_.getY(),
									  actor_.getZ(),
									  0, -1, 50, "defense" + actorID, IconType.Defense);
		ended = true;
		
		return false;
	}

	@Override
	public boolean signal(int value) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
