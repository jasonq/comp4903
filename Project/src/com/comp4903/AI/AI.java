package com.comp4903.AI;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.util.Log;

import com.comp4903.pathfind.Algorithms;
import com.comp4903.pathfind.BFSNode;
import com.comp4903.pathfind.PathFind;
import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.engine.GameEngine;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.TileType;
import com.comp4903.project.gameEngine.enums.UnitGroup;
import com.comp4903.project.gameEngine.enums.UnitType;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.graphics.animation.AnimationEngine;

public class AI {
	private static MapData _mapdata;
	private static final String TAG = "AI";
	
	public static void Intialize(MapData md) { _mapdata = md; }
	
	
	static class Pathfind{
		private static final String TAG = "AI.Pathfind";
		/**
		 * Gets a list of open spaces with a given range.
		 * @param p
		 * @param maxSteps
		 * @return
		 */
		protected static List<Point> GetOpenSpaces(Point p, int maxSteps)
	    {
	        List<BFSNode> queue = new ArrayList<BFSNode>();
	        List<BFSNode> marked = new ArrayList<BFSNode>();
	        List<Point> steps = new ArrayList<Point>();
	        queue.add(new BFSNode(p, 0));
	        marked.add(queue.get(0));
	        while (queue.size() > 0)
	        {
	            BFSNode t = queue.get(0);
	            queue.remove(0);
	            if (t.step > maxSteps)
	                continue;
            	steps.add(t.p);
	            List<BFSNode> adjNodes;
	            if(t.p.x % 2 == 0)
	            	adjNodes = evenNodes(t);
	            else
	            	adjNodes = oddNodes(t);
	            
	            for(BFSNode node : adjNodes){
	            	if(!ListHasNode(marked, node) && _mapdata.isOpen(node.p)){
	            		queue.add(node);
	            		marked.add(node);
	            	}
	            }
	        }
	        return steps;
	    }
		
		/**
		 * Takes a unit and returns its move path.
		 */
		protected static List<Point> GetMovePath(Unit u)
	    {
	    	int maxSteps = u.combatStats.maxMovement;
	        List<BFSNode> queue = new ArrayList<BFSNode>();
	        List<BFSNode> marked = new ArrayList<BFSNode>();
	        List<Point> steps = new ArrayList<Point>();
	        queue.add(new BFSNode(u.position, 0));
	        marked.add(queue.get(0));
	        while (queue.size() > 0)
	        {
	            BFSNode t = queue.get(0);
	            queue.remove(0);
	            if (t.step > maxSteps)
	                continue;
	            steps.add(t.p);
	            List<BFSNode> adjNodes;
	            if(t.p.x % 2 == 0)
	            	adjNodes = evenNodes(t);
	            else
	            	adjNodes = oddNodes(t);
	            
	            for(BFSNode node : adjNodes){
	            	if(!ListHasNode(marked, node) && _mapdata.isOpen(node.p)){
	            		Unit un = _mapdata.getUnitAt(node.p);
	            		if(un == null){
	            			queue.add(node);
	            			marked.add(node);
	            		}
	            	}
	            }
	        }
	        return steps;
	    }
		
		/**
		 * Takes a unit and its predicted position and returns its attack points.
		 */
		protected static List<Point> GetAttackPointPrediction(Unit u, Point pred)
	    {
	    	int atkRange = u.combatStats.range;
	    	List<BFSNode> queue = new ArrayList<BFSNode>();
	        List<BFSNode> marked = new ArrayList<BFSNode>();
	        List<Point> units = new ArrayList<Point>();
	        queue.add(new BFSNode(pred, 0));
	        marked.add(queue.get(0));
	        while (queue.size() > 0)
	        {
	            BFSNode t = queue.get(0);
	            queue.remove(0);
	            if (t.step > atkRange)
	                continue;
	            Unit un = _mapdata.getUnitAt(t.p);
	            if(un != null && un.unitGroup != u.unitGroup){
	            	units.add(t.p);
	            }
	            List<BFSNode> adjNodes;
	            if(t.p.x % 2 == 0)
	            	adjNodes = evenNodes(t);
	            else
	            	adjNodes = oddNodes(t);
	            
	            for(BFSNode node : adjNodes){
	            	if(!ListHasNode(marked, node) && _mapdata.isOpen(node.p)){
	            		queue.add(node);
	            		marked.add(node);
	            	}
	            }
	        }
	        return units;
	    }
		/**
		 * Takes a unit and its predicted position and returns its attack points.
		 */
		protected static List<Unit> GetAttackUnitPrediction(Unit u, Point pred)
	    {
	    	int atkRange = u.combatStats.range;
	    	List<BFSNode> queue = new ArrayList<BFSNode>();
	        List<BFSNode> marked = new ArrayList<BFSNode>();
	        List<Unit> units = new ArrayList<Unit>();
	        queue.add(new BFSNode(pred, 0));
	        marked.add(queue.get(0));
	        while (queue.size() > 0)
	        {
	            BFSNode t = queue.get(0);
	            queue.remove(0);
	            if (t.step > atkRange)
	                continue;
	            Unit un = _mapdata.getUnitAt(t.p);
	            if(un != null && un.unitGroup != u.unitGroup){
	            	units.add(un);
	            }
	            List<BFSNode> adjNodes;
	            if(t.p.x % 2 == 0)
	            	adjNodes = evenNodes(t);
	            else
	            	adjNodes = oddNodes(t);
	            
	            for(BFSNode node : adjNodes){
	            	if(!ListHasNode(marked, node) && _mapdata.isOpen(node.p)){
	            		queue.add(node);
	            		marked.add(node);
	            	}
	            }
	        }
	        return units;
	    }
		
		/**
	     * Checks if the list (l) has the node (n)
	     */
	    private static boolean ListHasNode(List<BFSNode> l, BFSNode n)
	    {
	        for(BFSNode ln : l)
	        {
	            if (ln.p.equals(n.p))
	                return true;
	        }
	        return false;
	    }
	    
	    /**
	     * Gets adjacent nodes if the y value is odd
	     */
		private static List<BFSNode> oddNodes(BFSNode t){
			List<BFSNode> list = new ArrayList<BFSNode>();
			
	        BFSNode north = new BFSNode(new Point(t.p.x, t.p.y - 1), (t.step + 1));
	        list.add(north);
	        BFSNode south = new BFSNode(new Point(t.p.x, t.p.y + 1), (t.step + 1));
	        list.add(south);
	        BFSNode NW = new BFSNode(new Point(t.p.x - 1, t.p.y), (t.step + 1));
	        list.add(NW);
	        BFSNode NE = new BFSNode(new Point(t.p.x + 1, t.p.y), (t.step + 1));
	        list.add(NE);
	        BFSNode SW = new BFSNode(new Point(t.p.x - 1, t.p.y + 1), (t.step + 1));
	        list.add(SW);
	        BFSNode SE = new BFSNode(new Point(t.p.x + 1, t.p.y + 1), (t.step + 1));
	        list.add(SE);
	        
	        return list;
		}
		/**
	     * Gets adjacent nodes if the y value is even
	     */
		private static List<BFSNode> evenNodes(BFSNode t){
			List<BFSNode> list = new ArrayList<BFSNode>();
			
	        BFSNode north = new BFSNode(new Point(t.p.x, t.p.y - 1), (t.step + 1));
	        list.add(north);
	        BFSNode south = new BFSNode(new Point(t.p.x, t.p.y + 1), (t.step + 1));
	        list.add(south);
	        BFSNode NW = new BFSNode(new Point(t.p.x - 1, t.p.y - 1), (t.step + 1));
	        list.add(NW);
	        BFSNode NE = new BFSNode(new Point(t.p.x + 1, t.p.y - 1), (t.step + 1));
	        list.add(NE);
	        BFSNode SW = new BFSNode(new Point(t.p.x - 1, t.p.y), (t.step + 1));
	        list.add(SW);
	        BFSNode SE = new BFSNode(new Point(t.p.x + 1, t.p.y), (t.step + 1));
	        list.add(SE);
	        
	        return list;
		}
	}

	static class GetUnit{
		/**
		 * Returns the unit with the lowest HP
		 */
		protected static Unit lowestHP(List<Unit> ul){
			int lowHP = Integer.MAX_VALUE;
			Unit target = null;
			for(Unit u: ul){
				int curHP = u.combatStats.currentHealth;
				if(curHP < lowHP){
					lowHP = curHP;
					target = u;
				}
			}
			return target;
		}
		/**
		 * Returns the unit with the highest HP
		 */
		protected static Unit highestHP(List<Unit> ul){
			int hiHP = Integer.MIN_VALUE;
			Unit target = null;
			for(Unit u: ul){
				int curHP = u.combatStats.currentHealth;
				if(curHP > hiHP){
					hiHP = curHP;
					target = u;
				}
			}
			return target;
		}
		/**
		 * Returns the unit with the lowest influence based on the influencemap passed
		 */
		protected static Unit lowestInf(List<Unit> ul, InfluenceMap imap){
			int lowInf = Integer.MAX_VALUE;
			Unit target = null;
			for(Unit u: ul){
				int curInf = imap.getValue(u.position.x, u.position.y);
				if(curInf < lowInf){
					lowInf = curInf;
					target = u;
				}
			}
			return target;
		}
		/**
		 * Gets a unit with the lowest hp and lowest influence
		 */
		protected static Unit lowHPlowInf(List<Unit> ul, InfluenceMap imap){
			int lowInf = Integer.MAX_VALUE;
			int lowHP = Integer.MAX_VALUE;
			Unit target = null;
			for(Unit u: ul){
				int curHP = u.combatStats.currentHealth;
				int curInf = imap.getValue(u.position.x, u.position.y);
				if(curHP <= lowHP && curInf <= lowInf){
					lowHP = curHP;
					lowInf = curInf;
					target = u;
				}
			}
			return target;
		}
		/**
		 * Returns the unit with the lowest influence based on the influencemap passed
		 */
		protected static Unit highestInf(List<Unit> ul, InfluenceMap imap){
			int hiInf = Integer.MIN_VALUE;
			Unit target = null;
			for(Unit u: ul){
				int curInf = imap.getValue(u.position.x, u.position.y);
				if(curInf > hiInf){
					hiInf = curInf;
					target = u;
				}
			}
			return target;
		}
		/**
		 * Returns the unit closest to a point
		 */
		protected static Unit closestPoint(List<Unit> ul, Point p){
			int clsDis = Integer.MAX_VALUE;
			Unit target = null;
			for(Unit u: ul){
				int curDis = PathFind.Distance(u.position, p);
				if(curDis < clsDis){
					clsDis = curDis;
					target = u;
				}
			}
			return target;
		}
		/**
		 * Returns the unit furthest to a point
		 */
		protected static Unit furthestPoint(List<Unit> ul, Point p){
			int farDis = Integer.MIN_VALUE;
			Unit target = null;
			for(Unit u: ul){
				int curDis = PathFind.Distance(u.position, p);
				if(curDis > farDis){
					farDis = curDis;
					target = u;
				}
			}
			return target;
		}
		
	}
	
	static class GetPoint{
		private static final String TAG = "AI.GetPoint";
		/**
		 * Gets the closest point from a list of points to a specified point.
		 */
		protected static Point closestPoint(List<Point> points, Point point){
			int clsDis = Integer.MAX_VALUE;
			Point target = null;
			for(Point p : points){
				int curDis = PathFind.Distance(p, point);
				if(curDis < clsDis){
					clsDis = curDis;
					target = p;
				}
			}
			return target;
		}
		/**
		 * Gets the closest point from a list of points to a specified point.
		 */
		protected static Point closestPointLowestInf(List<Point> points, Point point, InfluenceMap imap){
			int clsDis = Integer.MAX_VALUE;
			int inf = Integer.MAX_VALUE;
			Point target = null;
			for(Point p : points){
				int curDis = PathFind.Distance(p, point);
				int curInf = imap.getValue(p.x, p.y);
				if(curDis <= clsDis && curInf <= inf){
					clsDis = curDis;
					inf = curInf;
					target = p;
				}
			}
			Log.d(TAG, "Point: " + target.x + ", " + target.y);
			return target;
		}
		/**
		 * Gets the furthest point from a list of points to a specified point.
		 */
		protected static Point furthestPoint(List<Point> points, Point point){
			int hiDis = Integer.MIN_VALUE;
			Point target = null;
			for(Point p : points){
				int curDis = PathFind.Distance(p, point);
				if(curDis > hiDis){
					hiDis = curDis;
					target = p;
				}
			}
			return target;
		}
		/**
		 * Gets the point with the lowest influence
		 */
		protected static Point lowestInf(List<Point> points, InfluenceMap imap){
			int lowInf = Integer.MAX_VALUE;
			Point target = null;
			for(Point p : points){
				int curInf = imap.getValue(p.x, p.y);
				if(curInf < lowInf){
					lowInf = curInf;
					target = p;
				}
			}
			return target;
		}
		/**
		 * Gets the point with the highest influence
		 */
		protected static Point highestInf(List<Point> points, InfluenceMap imap){
			int hiInf = Integer.MIN_VALUE;
			Point target = null;
			for(Point p : points){
				int curInf = imap.getValue(p.x, p.y);
				if(curInf > hiInf){
					hiInf = curInf;
					target = p;
				}
			}
			return target;
		}
		/**
		 * Looks through a lists of points and determines the closest cover point from a position.
		 */
		protected static Point closestCover(List<Point> points, Point position){
			int distance = Integer.MAX_VALUE;
			Point target = null;
			boolean genFound = false;
			for(Point p : points){
				if(_mapdata._tileTypes[p.x][p.y] == TileType.Generator){
					int curDis = PathFind.Distance(p, position);
					if(curDis < distance && genFound == true){
						distance = curDis;
						target = p;
					}
					if(genFound == false){
						distance = curDis;
						target = p;
						genFound = true;
					}
				}
				if(_mapdata._tileTypes[p.x][p.y] == TileType.Sandbag && !genFound){
					int curDis = PathFind.Distance(p, position);
					if(curDis < distance){
						distance = curDis;
						target = p;
					}
				}
					
			}
			return target;
		}
	}
	
	static class GetUnits{
		/**
		 * Takes a list of points and returns units that are on those points.
		 */
		protected static List<Unit> fromPoints(List<Unit> ul, List<Point> points){
			List<Unit> units = new ArrayList<Unit>();
			for(Point p : points){
				for(Unit u: ul){
					if(u.position.equals(p))
						units.add(u);
				}
			}
			return units;
		}
		
		/**
		 * Takes a unit group and returns units that belong to that group.
		 */
		protected static List<Unit> fromGroup(List<Unit> ul, UnitGroup ug){
			List<Unit> units = new ArrayList<Unit>();
			for(Unit u: ul){
				if(u.unitGroup == ug)
					units.add(u);
			}
			return units;
		}
		
		/**
		 * Takes a unit type and returns units that belong to that type.
		 */
		protected static List<Unit> fromType(List<Unit> ul, UnitType ut){
			List<Unit> units = new ArrayList<Unit>();
			for(Unit u: ul){
				if(u.unitType == ut)
					units.add(u);
			}
			return units;
		}
		
		/**
		 * Gets a list units surrounding a unit at a specified range
		 */
		protected static List<Unit> surroundingPoint(List<Unit> ul, Point position, int range){
			List<Unit> units = new ArrayList<Unit>();
			for(Unit u: ul){
				if(PathFind.Distance(u.position, position) <= range){
					units.add(u);
				}
			}
			return units;
		}
		
	}
	
	static class GetPoints{
		/**
		 * Returns a list of points that units are standing on.
		 */
		protected static List<Point> unitPoints(List<Unit> units){
			List<Point> targets = new ArrayList<Point>();
			for(Unit u: units){
				targets.add(u.position);
			}
			return targets;
		}
		
		/**
		 * Returns a list of points that exist in both lists.
		 */
		protected static List<Point> matchingPoints(List<Point> pts1, List<Point> pts2){
			List<Point> targets = new ArrayList<Point>();
			for(Point p1: pts1){
				for(Point p2: pts2){
					if(p1.equals(p2)){
						targets.add(p1);
					}
				}
			}
			return targets;
		}
		
		protected static List<Point> coverPoints(List<Point> pts){
			List<Point> targets = new ArrayList<Point>();
			for(Point p: pts){
				if(_mapdata._tileTypes[p.x][p.y] == TileType.Generator || _mapdata._tileTypes[p.x][p.y] == TileType.Sandbag){
					targets.add(p);
				}
			}
			return targets;
		}
	}
	
	static class Check{
		protected static boolean checkPoint(Point target, List<Point> points){
			for(Point p : points){
				if(p.equals(target))
					return true;
			}
			return false;
		}
		
		protected static boolean checkInfluence(List<Point> points, InfluenceMap imap){
			for(Point p : points){
				if(imap.getValue(p.x, p.y) > 0)
					return true;
			}
			return false;
		}
	}
	
	static class Actions{
		private static final String TAG = "AI.Actions";
		/**
		 * Moves and attacks a unit. If the unit is out of range, it will move to it.
		 */
		protected static void attack(Unit source, Unit target){
			if(PathFind.Distance(source.position, target.position) > source.combatStats.range){
				List<Point> pathTo = Algorithms.GetAttackPathAStar(source, target);
				Point moveTo = pathTo.get(0);
				if(!GameEngine.moveUnit(source, moveTo, pathTo))
					Log.d(TAG, "Move failed");
				while(!AnimationEngine.noForegroundAnimations()){ try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} }
			}
			if(PathFind.Distance(source.position, target.position) <= source.combatStats.range){
				if(!GameEngine.useSkill(source, target, SkillType.Attack, true, false))
					Log.d(TAG, "Attack failed");
				while(!AnimationEngine.noForegroundAnimations()){ 
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			} else {
				GameEngine.useSkill(source, source, SkillType.Defend, true, false);
				while(!AnimationEngine.noForegroundAnimations()){ 
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			}
		}
		
		/**
		 * Moves and attacks a unit. If the unit is out of range, it will move to it.
		 */
		protected static void useSkill(Unit source, Unit target, SkillType type){
			if(PathFind.Distance(source.position, target.position) > source.combatStats.range){
				List<Point> pathTo = Algorithms.GetAttackPathAStar(source, target);
				Point moveTo = pathTo.get(0);
				if(!GameEngine.moveUnit(source, moveTo, pathTo))
					Log.d(TAG, "Move failed");
				while(!AnimationEngine.noForegroundAnimations()){ 
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if(PathFind.Distance(source.position, target.position) <= GameStats.getSkillStats(type).range){
				if(!GameEngine.useSkill(source, target, type, true, false))
					Log.d(TAG, "Attack failed");
				while(!AnimationEngine.noForegroundAnimations()){ 
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			} else {
				GameEngine.useSkill(source, source, SkillType.Defend, true, false);
				while(!AnimationEngine.noForegroundAnimations()){ 
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			}
		}
		
		/**
		 * Moves the unit to a point.
		 */
		protected static void move(Unit source, Point target){
			if(!source.position.equals(target)){
				if(!GameEngine.moveUnit(source, target, false))
					Log.d(TAG, "Move failed");
				while(!AnimationEngine.noForegroundAnimations()){ try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} }
			}
			GameEngine.useSkill(source, source, SkillType.Defend, true, false);
			while(!AnimationEngine.noForegroundAnimations()){ try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} }
		}
		
		protected void defend(Unit source){
			GameEngine.useSkill(source, source, SkillType.Defend, true, false);
			while(!AnimationEngine.noForegroundAnimations()){ try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} }
		}
		
		/**
		 * Move and attack.
		 */
		protected static void moveAttack(Unit source, Unit target, Point p){
			if(!GameEngine.moveUnit(source, p, false))
				Log.d(TAG, "Move failed");
			while(!AnimationEngine.noForegroundAnimations()){ 
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
			if(PathFind.Distance(source.position, target.position) <= source.combatStats.range){
				if(!GameEngine.useSkill(source, target, SkillType.Attack, true, false))
					Log.d(TAG, "Attack failed");
				while(!AnimationEngine.noForegroundAnimations()){ 
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			} else {
				GameEngine.useSkill(source, source, SkillType.Defend, true, false);
				while(!AnimationEngine.noForegroundAnimations()){ 
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			}
		}
		
		/**
		 * Move and skill.
		 */
		protected static void moveSkill(Unit source, Unit target, Point p, SkillType skill){
			if(!GameEngine.moveUnit(source, p, false))
				Log.d(TAG, "Move failed");
			while(!AnimationEngine.noForegroundAnimations()){ 
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
			if(PathFind.Distance(source.position, target.position) <= GameStats.getSkillStats(skill).range){
				if(!GameEngine.useSkill(source, target, skill, true, false))
					Log.d(TAG, "Attack failed");
				while(!AnimationEngine.noForegroundAnimations()){ 
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			} else {
				GameEngine.useSkill(source, source, SkillType.Defend, true, false);
				while(!AnimationEngine.noForegroundAnimations()){ 
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			}
		}
	}
}
