package com.comp4903.AI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Point;

import com.comp4903.pathfind.PathFind;
import com.comp4903.project.gameEngine.data.Unit;

public class AIUnitData implements Comparable<AIUnitData>{
	public Unit unit;
	public AIState state;
	public List<Unit> allyUnits;
	public List<Unit> enemyUnits;
	
	public AIUnitData(Unit u){
		state = AIState.Aggressive;
		unit = u;
	}
	
	public void initializeUnitLists(List<Unit> mdList){
		enemyUnits = new ArrayList<Unit>();
		allyUnits = new ArrayList<Unit>();
		for(Unit u : mdList){
			if(u.unitGroup != unit.unitGroup)
				enemyUnits.add(u);
			else if(!unit.equals(u))
				allyUnits.add(u);
		}
	}


	public int compareTo(AIUnitData o) {
		//int selfPriority = unitClassPriority() + curHealthPriority();
		//int oPriority = o.unitClassPriority() + o.curHealthPriority();
		// TODO Auto-generated method stub
		return 1;//selfPriority - oPriority;
	}
	
	public AIState getState(List<Unit> mdList, InfluenceMap aiMap, InfluenceMap playerMap){
		initializeUnitLists(mdList);
		switch (unit.unitType){
		case SwordMaster:
			state = swordState();
		case Sniper:
			//state = sniperState(imap);
			return AIState.Aggressive;
		case Medic:
			//state = medicState(imap);
			return AIState.Aggressive;
		default:
			return AIState.Aggressive;
		}
	}
	
	private AIState swordState(){
		switch (state){
		case Aggressive:
			if(numberOfUnitsInRange(enemyUnits, 3) >= 3)
				return AIState.Defensive;
			else if (currentHealthPercent() < 25.0f)
				return AIState.Retreat;
			else
				return AIState.Aggressive;
		case Defensive:
			if(numberOfUnitsInRange(enemyUnits, 3) < 3)
				return AIState.Aggressive;
			else if (currentHealthPercent() < 50.0f)
				return AIState.Retreat;
			else
				return AIState.Defensive;
		case Retreat:
			if(currentHealthPercent() > 70.0f)
				return AIState.Aggressive;
			else if(currentHealthPercent() > 60.0f)
				return AIState.Defensive;
			else
				return AIState.Retreat;
		default:
			return AIState.Aggressive;
		}
	}
	
	//determines the closest unit to this unit
	private Unit closestUnit(List<Unit> units){
		int distance = Integer.MAX_VALUE;
		Unit closestUnit = null;
		for(Unit u : units){
			int newDistance = PathFind.Distance(unit.position, u.position);
			if(newDistance < distance){
				distance = newDistance;
				closestUnit = u;
			}
		}
		return closestUnit;
	}
	
	//determines if the unit is within another unit's specified range
	private boolean withinRange(Unit testUnit, int range){
		int distance = PathFind.Distance(unit.position, testUnit.position);
		return distance < range;
	}
	
	// determines how many units are within range, should give a list of
	// enemy or ally units
	private int numberOfUnitsInRange(List<Unit> units, int range){
		int unitInRange = 0;
		for(Unit enemy : units){
			if(PathFind.Distance(unit.position, enemy.position) < range)
				unitInRange++;
		}
		return unitInRange;
	}
	
	private float currentHealthPercent(){
		return unit.combatStats.currentHealth / unit.combatStats.maxHealth;
	}
	
}
