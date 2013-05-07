package com.comp4903.pathfind;

import java.util.ArrayList;
import java.util.List;

import com.comp4903.project.gameEngine.data.Point;

public class Pathfinding {
	
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
                break;
            steps.add(t.p);

            BFSNode west = new BFSNode(new Point(t.p.getX() - 1, t.p.getY()), (t.step + 1));
            if (!ListHasNode(marked, west) )//&& _map.InMap(west.p))
            {
                queue.add(west);
                marked.add(west);
            }
            BFSNode east = new BFSNode(new Point(t.p.getX() + 1, t.p.getY()), (t.step + 1));
            if (!ListHasNode(marked, east) )//&& _map.InMap(right.p))
            {
                queue.add(east);
                marked.add(east);
            }
            BFSNode NW = new BFSNode(new Point(t.p.getX(), t.p.getY() - 1), (t.step + 1));
            if (!ListHasNode(marked, NW) ) //&& _map.InMap(up.p))
            {
                queue.add(NW);
                marked.add(NW);
            }
            BFSNode NE = new BFSNode(new Point(t.p.getX() + 1, t.p.getY() - 1), (t.step + 1));
            if (!ListHasNode(marked, NE) ) //&& _map.InMap(up.p))
            {
                queue.add(NE);
                marked.add(NE);
            }
            BFSNode SW = new BFSNode(new Point(t.p.getX(), t.p.getY() + 1), (t.step + 1));
            if (!ListHasNode(marked, SW) )// && _map.InMap(down.p))
            {
                queue.add(SW);
                marked.add(SW);
            }
            BFSNode SE = new BFSNode(new Point(t.p.getX() + 1, t.p.getY() + 1), (t.step + 1));
            if (!ListHasNode(marked, SE) )// && _map.InMap(down.p))
            {
                queue.add(SE);
                marked.add(SE);
            }
        }
        return steps;
    }
    
    /**
     * Checks if the list (l) has the node (n)
     */
    public static boolean ListHasNode(List<BFSNode> l, BFSNode n)
    {
        for(BFSNode ln : l)
        {
            if (ln.p.getX() == n.p.getX() && ln.p.getY() == n.p.getY())
                return true;
        }
        return false;
    }
	
	//--------------AStar Algorithm--------------//
	/**
     * Determines the best movement path based on the a* from start to end points.
     */
    private static List<Point> GetPathAStar(Point start, Point end)
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
            if (cur.getP().getX() == end.getX() && cur.getP().getY() == end.getY())
            {
                endFound = true;
                break;
            }
            if (cur == null)
                return new ArrayList<Point>();
            openList.remove(cur);
            closedList.add(cur);
            List<AStarNode> neighbor = connections(cur, end);
            for (AStarNode con : neighbor)
            {
            	AStarNode check = ListHasAStarNode(closedList, con);
                //if (!_map.IsOpen(con.p) || check != null) //need map function
                 //   continue;
                if (check == null)
                    openList.add(con);
                else if (check != null && con.getG() < check.getG())
                {
                    check.setParent(cur);
                    check.setG(con.getG());
                    check.setF(con.getF());
                }

            }
        }
        if (endFound)
        {
            AStarNode endNode = cur;
            while (cur.getParent() != null)
            {
                result.add(cur.getP());
                cur = cur.getParent();
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
            if (ln.getP().getX() == cur.getP().getX() && ln.getP().getY() == cur.getP().getY())
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
            if (ln.getF() < lowestF)
            {
                lowestF = ln.getG();
                lowest = ln;
            }
        }
        return lowest;
    }

    /**
     * Gets the nodes to the left, right, up and down of the current node
     */
    private static List<AStarNode> connections(AStarNode cur, Point end)
    {
        List<AStarNode> connect = new ArrayList<AStarNode>();
        Point cp = cur.getP();
        Point westP = new Point(cp.getX() - 1, cp.getY());
        AStarNode west = new AStarNode(westP, cur, cur.getG() + 1, cur.getG() + heuristic(westP, end));
        connect.add(west);

        Point eastP = new Point(cp.getX() + 1, cp.getY());
        AStarNode east = new AStarNode(eastP, cur, cur.getG() + 1, cur.getG() + heuristic(eastP, end));
        connect.add(east);

        Point NWP = new Point(cp.getX(), cp.getY() - 1);
        AStarNode NW = new AStarNode(NWP, cur, cur.getG() + 1, cur.getG() + heuristic(NWP, end));
        connect.add(NW);

        Point NEP = new Point(cp.getX() + 1, cp.getY() - 1);
        AStarNode NE = new AStarNode(NEP, cur, cur.getG() + 1, cur.getG() + heuristic(NEP, end));
        connect.add(NE);
        
        Point SWP = new Point(cp.getX(), cp.getY() + 1);
        AStarNode SW = new AStarNode(SWP, cur, cur.getG() + 1, cur.getG() + heuristic(SWP, end));
        connect.add(SW);
        
        Point SEP = new Point(cp.getX() + 1, cp.getY() + 1);
        AStarNode SE = new AStarNode(SEP, cur, cur.getG() + 1, cur.getG() + heuristic(SEP, end));
        connect.add(SE);
        return connect;
    }

    /**
     * Used to determine the f value of a node
     */
    private static int heuristic(Point cur, Point end)
    {
        return Math.abs(cur.getX() - end.getX()) + Math.abs(cur.getY() - end.getY());
    }
    
}
