package com.comp4903.AI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Point;
import android.util.Log;

import com.comp4903.pathfind.PathFind;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.UnitType;

public class AIData{
	private AIState state;
	private static final String TAG = "AiUnitData"; 
	
	public AIData(){
		state = AIState.Aggressive;
	}
	
	public AIState getState(){
		return state;
	}
	
	public void intializeState(Unit unit, UnitType type, List<Unit> enemyUnits){
		AIState prevState = state;
		Log.d(TAG, "previous state: " + prevState);
		switch (type){
		case SwordMaster:
			state = swordState(unit, prevState, enemyUnits);
			break;
		case Sniper:
			state = sniperState(unit, prevState, enemyUnits);
			break;
		case Medic:
			//state = medicState(imap);
			state = AIState.Aggressive;
			break;
		default:
			state = AIState.Aggressive;
			break;
		}
	}
	
	private AIState swordState(Unit unit, AIState prevState, List<Unit> enemyUnits){
		switch (prevState){
		case Aggressive:
			if (currentHealthPercent(unit) < 25.0f)
				return AIState.Retreat;
			else if(numberOfEnemiesInRange(unit, enemyUnits, 3) >= 3)
				return AIState.Defensive;
			else
				return AIState.Aggressive;
		case Defensive:
			if(numberOfEnemiesInRange(unit, enemyUnits, 3) < 3)
				return AIState.Aggressive;
			else if (currentHealthPercent(unit) < 50.0f)
				return AIState.Retreat;
			else
				return AIState.Defensive;
		case Retreat:
			if(currentHealthPercent(unit) > 70.0f)
				return AIState.Aggressive;
			else if(currentHealthPercent(unit) > 60.0f)
				return AIState.Defensive;
			else
				return AIState.Retreat;
		default:
			return AIState.Aggressive;
		}
	}
	
	private AIState sniperState(Unit unit, AIState prevState, List<Unit> enemyUnits){
		switch (prevState){
		case Aggressive:
			if (currentHealthPercent(unit) < 20.0f){
				Log.d(TAG, "Was Aggresive, now Retreat");
				return AIState.Retreat;
			}
			else if(numberOfEnemiesInRange(unit, enemyUnits, 3) >= 3){
				Log.d(TAG, "Was Aggresive, now Defensive");
				return AIState.Defensive;
			}
			
			else{
				Log.d(TAG, "Was Aggresive, now Aggressive");
				return AIState.Aggressive;
			}
		case Defensive:
			
			if (currentHealthPercent(unit) < 40.0f){
				Log.d(TAG, "Was Defensive, now Retreat");
				return AIState.Retreat;
			}
			else if(numberOfEnemiesInRange(unit, enemyUnits, 3) < 3){
				Log.d(TAG, "Was Defensive, now Aggressive");
				return AIState.Aggressive;
			}
			else{
				Log.d(TAG, "Was Defensive, now Defensive");
				return AIState.Defensive;
			}
		case Retreat:
			if(currentHealthPercent(unit) > 60.0f){
				Log.d(TAG, "Was Retreat, now Aggressive");
				return AIState.Aggressive;
			}
			else if(currentHealthPercent(unit) > 50.0f){
				Log.d(TAG, "Was Retreat, now Defensive");
				return AIState.Defensive;
			}
			else{
				Log.d(TAG, "Was Retreat, now Retreat");
				return AIState.Retreat;
			}
		default:
			Log.d(TAG, "Default to aggressive");
			return AIState.Aggressive;
		}
	}
	
	// determines how many units are within range, should give a list of
	// enemy or ally units
	private int numberOfEnemiesInRange(Unit unit, List<Unit> enemies, int range){
		int unitInRange = 0;
		for(Unit enemy : enemies){
			if(PathFind.Distance(unit.position, enemy.position) < range)
				unitInRange++;
		}
		Log.d(TAG, "Number of units in range: " + unitInRange);
		return unitInRange;
	}
	
	private float currentHealthPercent(Unit unit){
		float currentHP = ((float)unit.combatStats.currentHealth / (float)unit.combatStats.maxHealth) * 100;
		Log.d(TAG, "Current HP: " + unit.combatStats.currentHealth);
		Log.d(TAG, "Max HP: " + unit.combatStats.maxHealth);
		Log.d(TAG, "Current health percent: " + currentHP);
		return currentHP;
	}
	
}
