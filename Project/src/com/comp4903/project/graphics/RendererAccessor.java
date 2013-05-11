package com.comp4903.project.graphics;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;

import android.content.Context;
import android.graphics.Point;

/*	RENDERACCESSOR - Accessor class to provide access to
 *  game screen rendering system (to send updates to it)
 */
public class RendererAccessor {

		public static MapRenderer map;
			
		public static void init(GL10 gl, Context c)
		{
			map = new MapRenderer(gl, c);
		}
		
		public static void update(MapData m)
		{
			map.update(m);
		}	
		
		public static void moveAnimation(Unit u, List<Point> steps)
		{
			map.moveAnimation(u, steps);
		}
		
		public static void attackAnimation(Unit u, Unit u2)
		{
			map.attackAnimation(u, u2);
		}
		
}
