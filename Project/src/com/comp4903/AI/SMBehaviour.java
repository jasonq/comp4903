package com.comp4903.AI;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.util.Log;

import com.comp4903.pathfind.AIAlgorithm;
import com.comp4903.pathfind.Algorithms;
import com.comp4903.pathfind.PathFind;
import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.engine.GameEngine;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.TileType;
import com.comp4903.project.gameEngine.enums.UnitGroup;
import com.comp4903.project.gameEngine.enums.UnitType;
import com.comp4903.project.graphics.animation.AnimationEngine;

public class SMBehaviour {
	
	
	public static void think(AIUnitData u, InfluenceMap aiMap, InfluenceMap playerMap){
		switch (u.state){
		case Aggressive:
			aggressiveSM(u, aiMap, playerMap);
			break;
		case Defensive:
			defensiveSM(u, aiMap, playerMap);
			break;
		case Retreat:
			retreatSM(u, aiMap, playerMap);
			break;
		}
	}
	
	private static void aggressiveSM(AIUnitData u, InfluenceMap aiMap, InfluenceMap playerMap){
		Unit aiUnit = u.unit;
		int range = u.unit.combatStats.maxMovement + u.unit.combatStats.range;
		List<Unit> enemies = AIBehaviours.enemiesInRange(u, range);
		Unit enemy = AIBehaviours.closestUnit(enemies, aiUnit.position);
		if(enemy != null){ //enemy in range
			if(AIBehaviours.unitInRange(u.unit, enemy, u.unit.combatStats.range)){ //unit in melee range
				AIBehaviours.attack(aiUnit, enemy);
			}
			else{
				Unit lowestHP = AIBehaviours.lowestUnit(enemies);
				if(AIBehaviours.unitSupport(lowestHP, playerMap) < 25){ //lowest hp unit inf < 25
					AIBehaviours.attack(aiUnit, lowestHP);
				}
				else{
					Unit closestUnit = AIBehaviours.closestUnit(enemies, aiUnit.position);
					if(AIBehaviours.unitSupport(closestUnit, playerMap) < 30){//closest unit inf < 30
						AIBehaviours.attack(aiUnit, closestUnit);
					}
					else{
						AIBehaviours.attack(aiUnit, lowestHP);
					}
				}
			}
		}
		else{
			Point cover = AIBehaviours.coverInRange(u.unit, u.unit.combatStats.maxMovement);
			if(cover != null){ //cover in range
				AIBehaviours.move(u.unit, cover);
			}
			else{
				enemy = AIBehaviours.closestEnemy(u);
				AIBehaviours.attack(u.unit, enemy);
			}
		}
		
	}
	
	private static void defensiveSM(AIUnitData u, InfluenceMap  aiMap, InfluenceMap playerMap){
		Unit aiUnit = u.unit;
		List<Unit> enemies = AIBehaviours.enemiesInRange(u, u.unit.combatStats.range);
		Unit enemy = AIBehaviours.closestUnit(enemies, aiUnit.position);
		if(enemy != null){ //enemy in attack range
			if(AIBehaviours.unitSupport(enemy, playerMap) < 25){ //enemy inf < 25
				AIBehaviours.attack(aiUnit, enemy);
			}
			else{
				enemies = AIBehaviours.enemiesInRange(u, 3);
				Unit medic = AIBehaviours.unitType(enemies, UnitType.Medic);
				if(medic != null){ //enemy medic in range
					AIBehaviours.attack(aiUnit, medic);
				}
				else{
					Unit lowestInf = AIBehaviours.lowestInfEnemy(u, playerMap);
					if(AIBehaviours.unitSupport(lowestInf, playerMap) < 20){ //enemy lowest inf < 20
						AIBehaviours.attack(aiUnit, lowestInf);
					}
					else{
						Point cover = AIBehaviours.coverInRange(aiUnit, aiUnit.combatStats.maxMovement);
						if(cover != null){ //cover is in range
							AIBehaviours.move(aiUnit, cover);
						}
						else {
							AIBehaviours.defend(aiUnit);
						}
					}
				}
			}
		}
		else{
			List<Unit> enemiesInRange = AIBehaviours.enemiesInRange(u, u.unit.combatStats.maxMovement);
			Unit lowestHP = AIBehaviours.lowestUnit(enemiesInRange);
			if(AIBehaviours.unitSupport(lowestHP, playerMap) < 25){ //lowest HP enemy inf < 25
				AIBehaviours.attack(aiUnit, lowestHP);
			}
			else{
				Unit closestEnemy = AIBehaviours.closestUnit(enemiesInRange, aiUnit.position);
				if(AIBehaviours.unitSupport(closestEnemy, playerMap) < 25){ //closest enemy inf < 25
					AIBehaviours.attack(aiUnit, closestEnemy);
				}
				else{
					Point cover = AIBehaviours.coverInRange(aiUnit, aiUnit.combatStats.maxMovement);
					if(cover != null){ //cover is in range
						AIBehaviours.move(aiUnit, cover);
					}
					else {
						AIBehaviours.defend(aiUnit);
					}
				}
			}
		}
	}
	
	private static void retreatSM(AIUnitData u, InfluenceMap  aiMap, InfluenceMap playerMap){
		Unit aiUnit = u.unit;
		if(AIBehaviours.unitSupport(aiUnit, playerMap) < 10){ //is aiUnit in player Influence
			Unit closestUnit =AIBehaviours.closestEnemy(u);
			if(AIBehaviours.healthPercent(aiUnit) < 10.0f){ //is aiUnit below 10% hp
				AIBehaviours.attack(aiUnit, closestUnit);
			} else {
				if(AIBehaviours.healthPercent(closestUnit) < 30.0f){ //closest unit below 30%
					AIBehaviours.attack(aiUnit, closestUnit);
				} else{
					Unit closestAlly = AIBehaviours.closestUnit(u.allyUnits, aiUnit.position);
					Point toAlly = AIBehaviours.closestPointBetweenUnits(aiUnit, closestAlly);
					AIBehaviours.move(aiUnit, toAlly);
				}
			}
		} else{
			List<Unit> closeAllies = AIBehaviours.alliesInRange(u, aiUnit.combatStats.maxMovement);
			if(closeAllies.size() < 0){ //there are allies nearby
				Unit medic = AIBehaviours.unitType(closeAllies, UnitType.Medic);
				if(medic != null){ //if close unit is medic
					Point toAlly = AIBehaviours.closestPointBetweenUnits(aiUnit, medic);
					AIBehaviours.move(aiUnit, toAlly);
				} else {
					Point cover = AIBehaviours.coverInRange(aiUnit, aiUnit.combatStats.maxMovement);
					if(cover != null){ //cover in range
						AIBehaviours.move(aiUnit, cover);
					}
					else{
						Unit closestAlly = AIBehaviours.closestUnit(u.allyUnits, aiUnit.position);
						Point toAlly = AIBehaviours.closestPointBetweenUnits(aiUnit, closestAlly);
						AIBehaviours.move(aiUnit, toAlly);
					}
				}
			}
			else{
				Point cover = AIBehaviours.coverInRange(aiUnit, aiUnit.combatStats.maxMovement);
				if(cover != null){ //cover in range
					AIBehaviours.move(aiUnit, cover);
				}
				else{
					Unit closestAlly = AIBehaviours.closestUnit(u.allyUnits, aiUnit.position);
					Point toAlly = AIBehaviours.closestPointBetweenUnits(aiUnit, closestAlly);
					AIBehaviours.move(aiUnit, toAlly);
				}
			}
		}
	}
}
