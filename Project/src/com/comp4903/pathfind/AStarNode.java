package com.comp4903.pathfind;

import com.comp4903.project.gameEngine.data.Point;

public class AStarNode
{
    private Point p;
    private AStarNode parent;
    private int g;
    private int f;
    
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
    
    /************************/
    /**   Getter Methods   **/
    /************************/
    public Point getP() { return p; }
    public AStarNode getParent() { return parent; }
    public int getG() { return g; }
    public int getF() { return f; }
    /************************/
    /**   Setter Methods   **/
    /************************/
    public void setP(Point p) { this.p = p; }
    public void setParent(AStarNode p) { parent = p; }
    public void setG(int i) { g = i; }
    public void setF(int i) { f = i; }
}