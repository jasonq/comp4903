package com.comp4903.pathfind;

import com.comp4903.project.gameEngine.data.Point;

public class BFSNode
{
    public Point p;
    public int step;
    
    public BFSNode(){ }
    public BFSNode(Point p, int step){
    	this.p = p;
    	this.step = step;
    }
    
}