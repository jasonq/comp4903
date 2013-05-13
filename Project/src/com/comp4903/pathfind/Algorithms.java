package com.comp4903.pathfind;

import java.util.ArrayList;
import java.util.List;

import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;

import android.graphics.Point;


public class Algorithms {
	
	private static MapData _map;
	public static void initialize(MapData map) { _map = map; }
	
	
	//--------------BFS Algorithm--------------//
	/**
     * Gets all points surrounding the point within a radius (maxsteps)
     */
    public static List<Point> GetPointsBFS(Point p, int maxSteps)
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
            	if(!ListHasNode(marked, node) && _map.isOpen(node.p)){
            		queue.add(node);
            		marked.add(node);
            	}
            }
        }
        return steps;
    }
    /**
     * Gets all points surrounding the point within a radius (maxsteps)
     */
    public static List<Point> GetUnitPointsBFS(Unit u)
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
            	if(!ListHasNode(marked, node) && _map.isOpen(node.p)){
            		Unit un = _map.getUnitAt(node.p);
            		if(un == null){
            			queue.add(node);
            			marked.add(node);
            		} else if (un.unitGroup == u.unitGroup){
            			queue.add(node);
            			marked.add(node);
            		}
            	}
            }
        }
        return steps;
    }
    
    public static List<Point> GetUnitAttackBFS(Unit u)
    {
    	int atkRange = u.combatStats.range;
    	List<BFSNode> queue = new ArrayList<BFSNode>();
        List<BFSNode> marked = new ArrayList<BFSNode>();
        List<Point> units = new ArrayList<Point>();
        queue.add(new BFSNode(u.position, 0));
        marked.add(queue.get(0));
        while (queue.size() > 0)
        {
            BFSNode t = queue.get(0);
            queue.remove(0);
            if (t.step > atkRange)
                continue;
            Unit un = _map.getUnitAt(t.p);
            if(un != null && un.unitGroup != u.unitGroup){
            	units.add(t.p);
            }
            List<BFSNode> adjNodes;
            if(t.p.x % 2 == 0)
            	adjNodes = evenNodes(t);
            else
            	adjNodes = oddNodes(t);
            
            for(BFSNode node : adjNodes){
            	if(!ListHasNode(marked, node) && _map.isOpen(node.p)){
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
	
	//--------------AStar Algorithm--------------//
	/**
     * Determines the best movement path based on the a* from start to end points.
     */
    public static List<Point> GetPathAStar(Point start, Point end)
    {
        List<AStarNode> openList = new ArrayList<AStarNode>();
        List<AStarNode> closedList = new ArrayList<AStarNode>();
        List<Point> result = new ArrayList<Point>();
        AStarNode cur = null;
        AStarNode startNode = new AStarNode(start, null, 0, heuristic(start, end));
        openList.add(startNode);
        boolean endFound = false;
        while (openList.size() > 0)
        {
            cur = GetLowestFScore(openList);
            if (cur.p.equals(end))
            {
                endFound = true;
                break;
            }
            if (cur == null)
                return new ArrayList<Point>();
            openList.remove(cur);
            closedList.add(cur);
            List<AStarNode> neighbor; 
            if(cur.p.x % 2 == 0)
            	neighbor = evenConnections(cur, end);
            else
            	neighbor = oddConnections(cur, end);
            for (AStarNode con : neighbor)
            {
            	AStarNode check = ListHasAStarNode(closedList, con);
                if (!_map.isOpen(con.p) || check != null)
                    continue;
                else if (check == null)
                    openList.add(con);
                else if (check != null && con.g < check.g)
                {
                    check.parent = cur;
                    check.g = con.g;
                    check.f = con.f;
                }

            }
        }
        if (endFound)
        {
            AStarNode endNode = cur;
            while (cur.parent != null)
            {
                result.add(cur.p);
                cur = cur.parent;
            }
        }
        return result;
    }
    
    public static List<Point> GetMovePathAStar(Unit movingUnit, Point end)
    {
    	Point start = movingUnit.position;
        List<AStarNode> openList = new ArrayList<AStarNode>();
        List<AStarNode> closedList = new ArrayList<AStarNode>();
        List<Point> result = new ArrayList<Point>();
        AStarNode cur = null;
        AStarNode startNode = new AStarNode(start, null, 0, heuristic(start, end));
        openList.add(startNode);
        boolean endFound = false;
        while (openList.size() > 0)
        {
            cur = GetLowestFScore(openList);
            if (cur.p.equals(end))
            {
                endFound = true;
                break;
            }
            if (cur == null)
                return new ArrayList<Point>();
            openList.remove(cur);
            closedList.add(cur);
            List<AStarNode> neighbor; 
            if(cur.p.x % 2 == 0)
            	neighbor = evenConnections(cur, end);
            else
            	neighbor = oddConnections(cur, end);
            for (AStarNode con : neighbor)
            {
            	AStarNode check = ListHasAStarNode(closedList, con);
            	Unit un = _map.getUnitAt(con.p);
        		if(un != null && un.unitGroup != movingUnit.unitGroup){
        			continue;
        		}
                if (!_map.isOpen(con.p) || check != null)
                    continue;
                else if (check == null)
                    openList.add(con);
                else if (check != null && con.g < check.g)
                {
                    check.parent = cur;
                    check.g = con.g;
                    check.f = con.f;
                }

            }
        }
        if (endFound)
        {
            AStarNode endNode = cur;
            while (cur.parent != null)
            {
                result.add(cur.p);
                cur = cur.parent;
            }
        }
        return result;
    }
    
    /**
     * Checks if the list (l) has the node (cur) and sets the comparison node if it returns true
     */
    private static AStarNode ListHasAStarNode(List<AStarNode> l, AStarNode cur)
    {
    	
        for (AStarNode ln : l)
        {
            if (ln.p.equals(cur.p))
            {
                return ln;
            }
        }
        return null;
    }

    /**
     * Gets the node with the lowest F score
     */
    private static AStarNode GetLowestFScore(List<AStarNode> l)
    {
        int lowestF = Integer.MAX_VALUE;
        AStarNode lowest = null;
        for (AStarNode ln : l)
        {
            if (ln.f < lowestF)
            {
                lowestF = ln.g;
                lowest = ln;
            }
        }
        return lowest;
    }

    /**
     * Gets the adjacent node based on x odd value
     */
    private static List<AStarNode> oddConnections(AStarNode cur, Point end)
    {
        List<AStarNode> connect = new ArrayList<AStarNode>();
        Point cp = cur.p;
        Point northP = new Point(cp.x, cp.y - 1);
        AStarNode north = new AStarNode(northP, cur, cur.g + 1, cur.g + heuristic(northP, end));
        connect.add(north);

        Point southP = new Point(cp.x, cp.y + 1);
        AStarNode south = new AStarNode(southP, cur, cur.g + 1, cur.g + heuristic(southP, end));
        connect.add(south);

        Point NWP = new Point(cp.x - 1, cp.y);
        AStarNode NW = new AStarNode(NWP, cur, cur.g + 1, cur.g + heuristic(NWP, end));
        connect.add(NW);

        Point NEP = new Point(cp.x + 1, cp.y);
        AStarNode NE = new AStarNode(NEP, cur, cur.g + 1, cur.g + heuristic(NEP, end));
        connect.add(NE);
        
        Point SWP = new Point(cp.x - 1, cp.y + 1);
        AStarNode SW = new AStarNode(SWP, cur, cur.g + 1, cur.g + heuristic(SWP, end));
        connect.add(SW);
        
        Point SEP = new Point(cp.x + 1, cp.y + 1);
        AStarNode SE = new AStarNode(SEP, cur, cur.g + 1, cur.g + heuristic(SEP, end));
        connect.add(SE);
        return connect;
    }
    
    /**
     * Gets the adjacent node based on x even value
     */
    private static List<AStarNode> evenConnections(AStarNode cur, Point end)
    {
        List<AStarNode> connect = new ArrayList<AStarNode>();
        Point cp = cur.p;
        Point northP = new Point(cp.x, cp.y - 1);
        AStarNode north = new AStarNode(northP, cur, cur.g + 1, cur.g + heuristic(northP, end));
        connect.add(north);

        Point southP = new Point(cp.x, cp.y + 1);
        AStarNode south = new AStarNode(southP, cur, cur.g + 1, cur.g + heuristic(southP, end));
        connect.add(south);

        Point NWP = new Point(cp.x - 1, cp.y - 1);
        AStarNode NW = new AStarNode(NWP, cur, cur.g + 1, cur.g + heuristic(NWP, end));
        connect.add(NW);

        Point NEP = new Point(cp.x + 1, cp.y - 1);
        AStarNode NE = new AStarNode(NEP, cur, cur.g + 1, cur.g + heuristic(NEP, end));
        connect.add(NE);
        
        Point SWP = new Point(cp.x - 1, cp.y);
        AStarNode SW = new AStarNode(SWP, cur, cur.g + 1, cur.g + heuristic(SWP, end));
        connect.add(SW);
        
        Point SEP = new Point(cp.x + 1, cp.y);
        AStarNode SE = new AStarNode(SEP, cur, cur.g + 1, cur.g + heuristic(SEP, end));
        connect.add(SE);
        return connect;
    }

    /**
     * Used to determine the f value of a node
     */
    private static int heuristic(Point cur, Point end)
    {
        return Math.abs(cur.x - end.x) + Math.abs(cur.y - end.y);
    }
    
}
