//package com.comp4903.AI;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.graphics.Point;
//import android.util.Log;
//
//import com.comp4903.pathfind.AIAlgorithm;
//import com.comp4903.pathfind.Algorithms;
//import com.comp4903.pathfind.PathFind;
//import com.comp4903.project.gameEngine.data.MapData;
//import com.comp4903.project.gameEngine.data.Unit;
//import com.comp4903.project.gameEngine.engine.GameEngine;
//import com.comp4903.project.gameEngine.enums.SkillType;
//import com.comp4903.project.gameEngine.enums.TileType;
//import com.comp4903.project.gameEngine.enums.UnitType;
//import com.comp4903.project.graphics.animation.AnimationEngine;
//
//public class AIBehaviours {
//
//	private static MapData _mapdata;
//	private static final String TAG = "AIBehaviour";
//	
//	public static void Intialize(MapData md) { _mapdata = md; }
//	
//	/**
//	 * Returns a list of points that exist in both lists
//	 */
//	protected static List<Point> matchingPoints(List<Point> p1, List<Point> p2){
//		List<Point> matching = new ArrayList<Point>();
//		for(Point p1Point : p1){
//			for(Point p2Point : p2){
//				if(p1Point.equals(p2Point)){
//					matching.add(p1Point);
//				}
//			}
//		}
//		return matching;
//	}
//
//	/**
//	 * Uses a list of units and returns a unit that belongs to a type
//	 */
//	protected static Unit unitType(List<Unit> units, UnitType type){
//		if(units.size() == 0)
//			return null;
//		Unit target = null;
//		for(Unit u : units){
//			if(u.unitType == type){
//				target = u;
//			}
//		}
//		return target;
//	}
//	
//	/**
//	 * Returns the lowest hp unit from a list.
//	 */
//	protected static Unit lowestUnit(List<Unit> units){
//		if(units.size() == 0)
//			return null;
//		int lowestHP = Integer.MAX_VALUE;
//		Unit target = null;
//		for(Unit u: units){
//			if(u.combatStats.currentHealth < lowestHP){
//				lowestHP = u.combatStats.currentHealth;
//				target = u;
//			}
//		}
//		return target;
//	}
//	
//	/**
//	 * Returns the closest unit from a list to a point.
//	 */
//	protected static Unit closestUnit(List<Unit> units, Point source){
//		if(units.size() == 0)
//			return null;
//		int distance = Integer.MAX_VALUE;
//		Unit target = null;
//		for(Unit u: units){
//			int curDis = PathFind.Distance(u.position, source);
//			if(curDis < distance){
//				distance = curDis;
//				target = u;
//			}
//		}
//		return target;
//	}
//	
//	/**
//	 * Uses a list of units and returns the unit with the lowest influence
//	 */
//	protected static Unit lowestInf(List<Unit> units, InfluenceMap imap){
//		if(units.size() == 0)
//			return null;
//		int lowestInf = Integer.MAX_VALUE;
//		Unit target = null;
//		for(Unit u : units){
//			int inf = unitSupport(u, imap);
//			if(inf < lowestInf){
//				lowestInf = inf;
//				target = u;
//			}
//		}
//		return target;
//	}
//	
//	/**
//	 * Uses a list of points and returns the point with the lowest influence
//	 */
//	protected static Point lowestInfPoint(List<Point> points, InfluenceMap imap){
//		if(points.size() == 0)
//			return null;
//		int lowestInf = Integer.MAX_VALUE;
//		Point target = null;
//		for(Point p : points){
//			int inf = imap.tiles[p.x][p.y];
//			if(inf < lowestInf){
//				lowestInf = inf;
//				target = p;
//			}
//		}
//		return target;
//	}
//	
//	/**
//	 * Gets if the unit's position is supported by ally troops based on influence map
//	 */
//	protected static int unitSupport(Unit u, InfluenceMap imap){
//		int x = u.position.x;
//		int y = u.position.y;
//		return imap.tiles[x][y];
//	}
//	
//	/**
//	 * Gets units health percentage
//	 */
//	protected static float healthPercent(Unit u){
//		float percent = u.combatStats.currentHealth / u.combatStats.maxHealth;
//		return percent;
//	}
//	
//	/**
//	 * Gets a point to move to that will get the unit closest to the unit
//	 */
//	protected static Point closestPointBetweenUnits(Unit source, Unit target){
//		List<Point> movePath = AIAlgorithm.GetOpenSpaces(source.position, source.combatStats.maxMovement);
//		int distance = Integer.MAX_VALUE;
//		Point moveTo = null;
//		for(Point p : movePath){
//			int curDis = PathFind.Distance(p, target.position);
//			if(curDis < distance){
//				distance = curDis;
//				moveTo = p;
//			}
//		}
//		return moveTo;
//	}
//	
//	/**
//	 * Gets a point to a cover position within a unit's range.
//	 * Returns null if no points can be found.
//	 */
//	protected static Point coverInRange(Unit u, int range){
//		int distance = Integer.MAX_VALUE;
//		Point moveTo = null;
//		if(_mapdata._tileTypes[u.position.x][u.position.y] == TileType.Generator){
//			return u.position;
//		}
//		if(_mapdata._tileTypes[u.position.x][u.position.y] == TileType.Sandbag){
//			moveTo = u.position;
//			distance = 0;
//		}
//		List<Point> points = Algorithms.GetPointsBFS(u.position, range);
//		boolean genFound = false;
//		for(Point p : points){
//			if(_mapdata._tileTypes[p.x][p.y] == TileType.Generator){
//				int d = PathFind.Distance(p, u.position);
//				if(d < distance && genFound == true){
//					distance = d;
//					moveTo = p;
//				}
//				if(genFound == false){
//					distance = d;
//					moveTo = p;
//					genFound = true;
//				}
//			}
//			if(_mapdata._tileTypes[p.x][p.y] == TileType.Sandbag && !genFound){
//				int d = PathFind.Distance(p, u.position);
//				if(d < distance){
//					distance = d;
//					moveTo = p;
//				}
//			}
//				
//		}
//		return moveTo;
//	}
//	
//	/**
//	 * Gets a point to a cover position within a list.
//	 * Returns null if no points can be found.
//	 */
//	protected static Point coverInList(List<Point> points, Unit u){
//		int distance = Integer.MAX_VALUE;
//		Point moveTo = null;
//		if(_mapdata._tileTypes[u.position.x][u.position.y] == TileType.Generator){
//			return u.position;
//		}
//		if(_mapdata._tileTypes[u.position.x][u.position.y] == TileType.Sandbag){
//			moveTo = u.position;
//			distance = 0;
//		}
//		boolean genFound = false;
//		for(Point p : points){
//			if(_mapdata._tileTypes[p.x][p.y] == TileType.Generator){
//				int d = PathFind.Distance(p, u.position);
//				if(d < distance && genFound == true){
//					distance = d;
//					moveTo = p;
//				}
//				if(genFound == false){
//					distance = d;
//					moveTo = p;
//					genFound = true;
//				}
//			}
//			if(_mapdata._tileTypes[p.x][p.y] == TileType.Sandbag && !genFound){
//				int d = PathFind.Distance(p, u.position);
//				if(d < distance){
//					distance = d;
//					moveTo = p;
//				}
//			}
//				
//		}
//		return moveTo;
//	}
//	
//	/**
//	 * Returns a point with the lowest influence within a list of points
//	 */
//	protected static Point lowestInfList(List<Point> points, InfluenceMap imap){
//		int lowestInf = Integer.MAX_VALUE;
//		Point lowestP = null;
//		for(Point p: points){
//			int inf = imap.tiles[p.x][p.y];
//			if(inf < lowestInf){
//				lowestInf = inf;
//				lowestP = p;
//			}
//		}
//		return lowestP;
//	}
//	
//	/**
//	 * Checks if a unit is within range
//	 */
//	protected static boolean unitInRange(Unit AI, Unit target, int range){
//		return PathFind.Distance(AI.position, target.position) < range;
//	}
//	
//	/**
//	 * Checks if unit's position is in the list
//	 */
//	protected static boolean unitInRange(Unit u, List<Point> range){
//		for(Point p : range){
//			if(u.position.equals(p))
//				return true;
//		}
//		return false;
//	}
//	
//	/**
//	 * Checks if a point is in a units move range, returns false if not
//	 */
//	protected static boolean pointInList(Point p, List<Point> points){
//		for(Point point : points){
//			if(point.equals(p))
//				return true;
//		}
//		return false;
//	}
//	
//	/**
//	 * Moves and attacks a unit. If the unit is out of range, it will move to it.
//	 */
//	protected static void attack(Unit source, Unit target){
//		if(PathFind.Distance(source.position, target.position) > source.combatStats.range){
//			List<Point> pathTo = Algorithms.GetAttackPathAStar(source, target);
//			Point moveTo = pathTo.get(0);
//			if(!GameEngine.moveUnit(source, moveTo, pathTo))
//				Log.d(TAG, "Move failed");
//			while(!AnimationEngine.noForegroundAnimations()){ try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} }
//		}
//		if(PathFind.Distance(source.position, target.position) <= source.combatStats.range){
//			if(!GameEngine.useSkill(source, target, SkillType.Attack, true, false))
//				Log.d(TAG, "Attack failed");
//			while(!AnimationEngine.noForegroundAnimations()){ 
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} 
//			}
//		} else {
//			GameEngine.useSkill(source, source, SkillType.Defend, true, false);
//			while(!AnimationEngine.noForegroundAnimations()){ 
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} 
//			}
//		}
//	}
//	
//	/**
//	 * Moves and attacks a unit. If the unit is out of range, it will move to it.
//	 */
//	protected static void useSkill(Unit source, Unit target, SkillType type){
//		if(PathFind.Distance(source.position, target.position) > source.combatStats.range){
//			List<Point> pathTo = Algorithms.GetAttackPathAStar(source, target);
//			Point moveTo = pathTo.get(0);
//			if(!GameEngine.moveUnit(source, moveTo, pathTo))
//				Log.d(TAG, "Move failed");
//			while(!AnimationEngine.noForegroundAnimations()){ 
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		if(PathFind.Distance(source.position, target.position) <= source.combatStats.range){
//			if(!GameEngine.useSkill(source, target, type, true, false))
//				Log.d(TAG, "Attack failed");
//			while(!AnimationEngine.noForegroundAnimations()){ 
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} 
//			}
//		} else {
//			GameEngine.useSkill(source, source, SkillType.Defend, true, false);
//			while(!AnimationEngine.noForegroundAnimations()){ 
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} 
//			}
//		}
//	}
//	
//	/**
//	 * Moves the unit to a point.
//	 */
//	protected static void move(Unit source, Point target){
//		if(!source.position.equals(target)){
//			if(!GameEngine.moveUnit(source, target, false))
//				Log.d(TAG, "Move failed");
//			while(!AnimationEngine.noForegroundAnimations()){ try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} }
//		}
//		GameEngine.useSkill(source, source, SkillType.Defend, true, false);
//		while(!AnimationEngine.noForegroundAnimations()){ try {
//			Thread.sleep(10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} }
//	}
//	
//	protected static void defend(Unit source){
//		GameEngine.useSkill(source, source, SkillType.Defend, true, false);
//		while(!AnimationEngine.noForegroundAnimations()){ try {
//			Thread.sleep(10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} }
//	}
//	
//	/**
//	 * Move and attack.
//	 */
//	protected static void moveAttack(Unit source, Unit target, Point p){
//		List<Point> pathTo = Algorithms.GetMovePathAStar(source, p);
//		Point moveTo = pathTo.get(0);
//		if(!GameEngine.moveUnit(source, moveTo, pathTo))
//			Log.d(TAG, "Move failed");
//		while(!AnimationEngine.noForegroundAnimations()){ 
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} 
//		}
//		if(PathFind.Distance(source.position, target.position) <= source.combatStats.range){
//			if(!GameEngine.useSkill(source, target, SkillType.Attack, true, false))
//				Log.d(TAG, "Attack failed");
//			while(!AnimationEngine.noForegroundAnimations()){ 
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} 
//			}
//		} else {
//			GameEngine.useSkill(source, source, SkillType.Defend, true, false);
//			while(!AnimationEngine.noForegroundAnimations()){ 
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} 
//			}
//		}
//	}
//	
//	/**
//	 * Move and skill.
//	 */
//	protected static void moveSkill(Unit source, Unit target, Point p, SkillType skill){
//		List<Point> pathTo = Algorithms.GetMovePathAStar(source, p);
//		Point moveTo = pathTo.get(0);
//		if(!GameEngine.moveUnit(source, moveTo, pathTo))
//			Log.d(TAG, "Move failed");
//		while(!AnimationEngine.noForegroundAnimations()){ 
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} 
//		}
//		if(PathFind.Distance(source.position, target.position) <= source.combatStats.range){
//			if(!GameEngine.useSkill(source, target, skill, true, false))
//				Log.d(TAG, "Attack failed");
//			while(!AnimationEngine.noForegroundAnimations()){ 
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} 
//			}
//		} else {
//			GameEngine.useSkill(source, source, SkillType.Defend, true, false);
//			while(!AnimationEngine.noForegroundAnimations()){ 
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} 
//			}
//		}
//	}
//	
//}
