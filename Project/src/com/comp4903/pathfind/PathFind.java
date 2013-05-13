package com.comp4903.pathfind;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.graphics.RendererAccessor;

public class PathFind {

	private static MapData _map;
	public static void initialize(MapData map) { _map = map; Algorithms.initialize(map); }
	
	public static void DisplayUnitMoveBox(Unit u){
		List<Point> l = Algorithms.GetUnitPointsBFS(u);
		List<Point> toRemove = new ArrayList<Point>();
		for(Unit unit : _map._units){
			for(Point p : l){
				if(unit.position.equals(p))
					toRemove.add(p);
			}
		}
		for(Point p : toRemove){
			if(l.contains(p))
				l.remove(p);
		}
		_map.clearBoxes();
		
		_map._movementBox = l;
		RendererAccessor.update(_map);
	}
	
	public static void DisplayUnitAttackBox(Unit u){
		List<Point> units = Algorithms.GetUnitAttackBFS(u);
		_map.clearBoxes();
		_map._attackBox = units;
		RendererAccessor.update(_map);
	}
	
	public static List<Point> UnitToPoint(Unit u, Point p){
		return Algorithms.GetMovePathAStar(u, p);
	}
	
	public static Point TractorBeam(Unit atkUnit, Unit defUnit){
		return Algorithms.GetTractorPoint(atkUnit, defUnit);
	}
}
