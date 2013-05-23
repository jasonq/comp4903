package com.comp4903.AI;

import java.util.List;

import android.graphics.Point;
import android.util.Log;

import com.comp4903.pathfind.Algorithms;
import com.comp4903.pathfind.BFSNode;
import com.comp4903.project.gameEngine.data.Unit;

public class InfluenceMap {
	private int tiles[][];
	private int col;
	private int row;
	
	public InfluenceMap(int columns, int rows){
		col = columns;
		row = rows;
	}
	
	public int getValue(int x, int y){
		return tiles[x][y];
	}
	
	/**
	 * Gets influence map based on units passed
	 * @param units list of units (should belong to only one unit group, player/ai)
	 * @return influence map
	 */
	public void getInfluenceMap(List<Unit> units){
		int infMap[][] = new int[col][row];
		for(Unit u : units){
			Point pos = u.position;
			List<BFSNode> nodes = Algorithms.GetNodesBFS(u.position, 4);
			if(infMap[pos.x][pos.y] == 0){
				infMap[pos.x][pos.y] = 10;
			} else {
				infMap[pos.x][pos.y] += 10;
			}
			for(BFSNode n : nodes){
				int influence = getInfluenceValue(n);
				int x = n.p.x;
				int y = n.p.y;
				if(infMap[x][y] == 0){
					infMap[x][y] = influence;
				} else {
					infMap[x][y] += influence;
				}
					
			}
		}
		tiles = infMap;
	}
	
	/**
	 * Get influence value based on number of steps taken.
	 * @param node BFSnode with step value
	 * @return influence value
	 */
	private int getInfluenceValue(BFSNode node){
		switch(node.step){
		case 1:
			return 8;
		case 2:
			return 6;
		case 3:
			return 4;
		case 4:
			return 2;
		default:
			return 0;
		}
	}
}
