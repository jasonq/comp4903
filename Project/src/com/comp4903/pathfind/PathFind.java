package com.comp4903.pathfind;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Point;
import android.util.Log;

import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.graphics.RendererAccessor;

public class PathFind {

	private static MapData _map;
	public static void initialize(MapData map) { _map = map; Algorithms.initialize(map);  AIAlgorithm.initialize(map);}
	
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
	
	//display list of points to atk
	public static void DisplayUnitAttackBox(Unit u){
		List<Point> units = Algorithms.GetUnitAttackBFS(u);
		_map.clearBoxes();
		_map._attackBox = units;
		//_map.attackBoxColor = red;
		RendererAccessor.update(_map);
	}
	
	//display list of points to cast damaging spells
	public static void DisplayUnitEnemyBox(Unit u, int range){
		List<Point> units = Algorithms.GetUnitEnemyBFS(u, range);
		_map.clearBoxes();
		_map._attackBox = units;
		//_map._attackBoxColor = red;
		RendererAccessor.update(_map);
	}
	
	//display list of points to cast friendly spells
	public static void DisplayUnitFriendBox(Unit u, int range){
		List<Point> units = Algorithms.GetUnitFriendBFS(u, range);
		_map.clearBoxes();
		_map._attackBox = units;
		//_map.attackBoxColor = green;
		RendererAccessor.update(_map);
	}
	
	//returns list of points between unit & point, starting at unit point and ending at end point
	public static List<Point> UnitToPoint(Unit u, Point p){
		return Algorithms.GetMovePathAStar(u, p);
	}
	
	//returns closest open point to atking unit between atkingUnit and defUnit 
	public static Point TractorBeam(Unit atkUnit, Unit defUnit){
		return Algorithms.GetTractorPoint(atkUnit, defUnit);
	}
	
	//returns distance between points
	public static int Distance(Point p1, Point p2){
		int x1 = p1.x;
		int x2 = p2.x;
		int y1 = p1.y - (p1.x/2);
		int y2 = p2.y - (p2.x/2);
		return (Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(x1 + y1 - x2 - y2)) / 2;
	}
}
