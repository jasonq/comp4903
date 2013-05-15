package com.comp4903.AI;

import java.util.List;

import android.graphics.Point;

import com.comp4903.pathfind.Algorithms;
import com.comp4903.pathfind.BFSNode;
import com.comp4903.project.gameEngine.data.Unit;

public class InfluenceMap {
	public Influence playerTiles[][];
	public Influence AiTiles[][];
	int col;
	int row;
	
	public InfluenceMap(int columns, int rows){
		col = columns;
		row = rows;
	}
	
	public void intializeInfluenceMap(List<Unit> player, List<Unit> ai){
		playerTiles = getInfluenceMap(player);
		AiTiles = getInfluenceMap(ai);
	}
	
	/**
	 * Gets influence map based on units passed
	 * @param units list of units (should belong to only one unit group, player/ai)
	 * @return influence map
	 */
	private Influence[][] getInfluenceMap(List<Unit> units){
		Influence infMap[][] = new Influence[col][row];
		for(Unit u : units){
			Point pos = u.position;
			List<BFSNode> nodes = Algorithms.GetNodesBFS(u.position, 4);
			infMap[pos.x][pos.y] = new Influence();
			infMap[pos.x][pos.y].playerInfluence = u.unitGroup;
			infMap[pos.x][pos.y].value = 10;
			for(BFSNode n : nodes){
				int influence = getInfluenceValue(n);
				int x = n.p.x;
				int y = n.p.y;
				if(infMap[x][y] == null){
					infMap[x][y] = new Influence();
					infMap[x][y].playerInfluence = u.unitGroup;
					infMap[x][y].value = influence;
				} else {
					if(infMap[x][y].value < influence)
						infMap[x][y].value = influence;
				}
					
			}
		}
		return infMap;
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
