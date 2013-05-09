package com.comp4903.pathfind;

import android.graphics.Point;


public class AStarNode
{
    public Point p;
    public AStarNode parent;
    public int g;
    public int f;
    
    /**
     * Constructor
     * @param p point
     * @param parent parent node
     * @param g value to determine length of path
     * @param f value to determine which path to take to get to end
     */
    public AStarNode(Point p, AStarNode parent, int g, int f)
    {
        this.p = p; this.parent = parent; this.g = g; this.f = f;
    }
}