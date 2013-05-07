package com.comp4903.pathfind;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.graphics.RendererAccessor;

public class PathFind {

	private static MapData _map;
	public static void initialize(MapData map) { _map = map; Algorithms.initialize(map); }
	
	public static void getMovePoints(Unit u){
		List<Point> l = Algorithms.GetPointsBFS(u.position, u.combatStats.maxMovement);
		for(Unit unit : _map._units){
			for(Point p : l){
				if(unit.position.equals(p))
					l.remove(p);
			}
		}
		_map.clearBoxes();
		
		_map._movementBox = l;
		RendererAccessor.map.update(_map);
	}
}
