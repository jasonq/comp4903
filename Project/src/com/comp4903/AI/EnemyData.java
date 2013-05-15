package com.comp4903.AI;

import com.comp4903.pathfind.PathFind;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.UnitType;

import android.graphics.Point;

public class EnemyData implements Comparable<EnemyData> {
	private Unit unit;
	private int distance;
	
	public EnemyData(Unit u, Point AIPoint){
		unit = u;
		distance = PathFind.Distance(u.position, AIPoint);
	}
	
	public int compareTo(EnemyData e1, EnemyData e2) {
        if(e1.distance > e2.distance)
        	return -1;
        else if(e1.distance == e2.distance)
        	return 0;
        else
        	return 1;
    }

	public int compareTo(EnemyData e) {
		return distance - e.distance;
	}
	
}
