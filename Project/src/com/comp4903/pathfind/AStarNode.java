package com.comp4903.pathfind;

import com.comp4903.project.gameEngine.data.Point;

public class AStarNode
{
    public Point p;
    public AStarNode parent;
    public int g;
    public int f;
    public AStarNode(Point p, AStarNode parent, int g, int f)
    {
        this.p = p; this.parent = parent; this.g = g; this.f = f;
    }
}