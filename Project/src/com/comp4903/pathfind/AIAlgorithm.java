package com.comp4903.pathfind;

import java.util.ArrayList;
import java.util.List;

import com.comp4903.project.gameEngine.data.MapData;

import android.graphics.Point;

public class AIAlgorithm {
	
	private static MapData _map;
	public static void initialize(MapData map) { _map = map; }
	
	/**
	 * Gets a list of open spaces with a given range.
	 * @param p
	 * @param maxSteps
	 * @return
	 */
	public static List<Point> GetOpenSpaces(Point p, int maxSteps)
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
            if(_map.getUnitAt(t.p) == null) //check if a unit exists at point
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
