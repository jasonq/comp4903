package com.comp4903.project.graphics.animation;

import java.util.Random;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.ColorType;
import com.comp4903.project.gameEngine.enums.IconType;
import com.comp4903.project.graphics.RendererAccessor;

public class HealthAnimation extends AnimationProcessor {

	int actorID;
	Actor actor_;
	Point p;
	Random r;
	int time;
	
	public void init(Unit u, String val)
	{
		foreground = true;
		actorID = u.uID;
		actor_ = RendererAccessor.map.getActor(actorID);
		p = RendererAccessor.ScreenXYfromXYZ(actor_.getX(), actor_.getY() + 2, actor_.getZ());
		if (p == null)
			p = new Point(0,0);
		//RendererAccessor.floatingText(p.x, p.y, 0, -1, 150, ColorType.Green, "n", val);
		RendererAccessor.floatingText(actor_.getX(),
									  actor_.getY() + 2,
									  actor_.getZ(),
									  0, -1, 150, ColorType.Green, "n", val);
		r = new Random();
		time = 0;
	}
	
	@Override
	public boolean iteration() {
		
		if ((time % 5) == 0)
		{
			int c = r.nextInt(3);
			IconType ic = IconType.Health1;
			if (c == 0)
				ic = IconType.Health1;
			if (c == 1)
				ic = IconType.Health2;
			if (c == 2)
				ic = IconType.Health3;
			int spd = r.nextInt(2) * -1 -1;
			int x = r.nextInt(120) -60;
			//RendererAccessor.floatingIcon(p.x + x - 30, p.y, 0, spd, 50, "h", ic);
			RendererAccessor.floatingIcon(actor_.getX() + (r.nextFloat() * 2f) - 1f, 
										  actor_.getY() + 2,
										  actor_.getZ(),
										  0, spd, 50, "h" + time, ic);
			
		}
		time++;
		if (time == 100)
			ended = true;
		
		return false;
	}

	@Override
	public boolean signal(int value) {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
