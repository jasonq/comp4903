package com.comp4903.AI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Point;

import com.comp4903.project.gameEngine.data.Unit;

public class AIUnitData implements Comparable<AIUnitData>{
	Unit unit;
	public List<EnemyData> unitPriority;
	
	public AIUnitData(Unit u){
		unit = u;
	}
	
	public void initializeUnitPriority(List<Unit> enemyUnits){
		Point unitPosition = unit.position;
		unitPriority = new ArrayList<EnemyData>();
		for(Unit enemy : enemyUnits){
			unitPriority.add(new EnemyData(enemy, unitPosition));
		}
		Collections.sort(unitPriority);
	}
	
	//gets priority based on class
	public int unitClassPriority(){
		switch(unit.unitType){
		default:
			return 0;
		case SwordMaster:
			return 1;
		case Sniper:
			return 2;
		case Medic:
			return 3;
		}		
	}
	
	//gets priority based on health
	public int curHealthPriority(){
		int curHealth = unit.combatStats.currentHealth;
		if(curHealth > 0 && curHealth < 25)
			return 4;
		else if(curHealth >= 25 && curHealth < 50)
			return 3;
		else if(curHealth >= 50 && curHealth < 75)
			return 2;
		else
			return 1;
	}

	public int compareTo(AIUnitData o) {
		int selfPriority = unitClassPriority() + curHealthPriority();
		int oPriority = o.unitClassPriority() + o.curHealthPriority();
		// TODO Auto-generated method stub
		return selfPriority - oPriority;
	}
	
	
}
