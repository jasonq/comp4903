package com.comp4903.AI;

import java.util.List;

import android.graphics.Point;
import android.util.Log;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.UnitType;
import com.comp4903.project.gameEngine.factory.GameStats;
/**
 * Sniper Behaviour
 * @author Jason Quan
 *
 */
public class SPBehaviour {
	private static String TAG = "SniperBehaviour";
	
	public static void think(Unit u, InfluenceMap aiMap, InfluenceMap playerMap, List<Unit> allyUnits, List<Unit> enemyUnits){
		switch (u.aiData.getState()){
		case Aggressive:
			aggressiveSP(u, aiMap, playerMap, allyUnits, enemyUnits);
			break;
		case Defensive:
			defensiveSP(u, aiMap, playerMap, allyUnits, enemyUnits);
			break;
		case Retreat:
			retreatSP(u, aiMap, playerMap, allyUnits, enemyUnits);
			break;
		}
	}
	
	private static void aggressiveSP(Unit aiUnit, InfluenceMap aiMap, InfluenceMap playerMap, List<Unit> allyUnits, List<Unit> enemyUnits){
		int atkMov = aiUnit.combatStats.range + aiUnit.combatStats.maxMovement;
		
		SkillType snipe = SkillType.Headshot;
		int snipeCost = GameStats.getSkillStats(snipe).energyCost;
		
		List<Point> movePath = AI.Pathfind.GetMovePath(aiUnit);
		List<Unit> unitsInAtkRng = AI.Pathfind.GetAttackUnitPrediction(aiUnit, aiUnit.position);
		List<Point> atkMovRange = AI.Pathfind.GetOpenSpaces(aiUnit.position, atkMov);
		
		if(unitsInAtkRng.size() > 0){ //enemy in atk range
			Log.d(TAG, "enemy in atk range");
			List<Unit> medics = AI.GetUnits.fromType(unitsInAtkRng, UnitType.Medic);
			if(medics.size() > 0){ //medic in atk range
				Log.d(TAG, "medic in atk range");
				Unit medic = AI.GetUnit.closestPoint(medics, aiUnit.position);
				AI.Actions.attack(aiUnit, medic);
			} else {
				List<Unit> swordmasters = AI.GetUnits.fromType(unitsInAtkRng, UnitType.SwordMaster);
				if(swordmasters.size() > 0){ //sword master in atk range
					Log.d(TAG, "sword master in atk range");
					if(aiUnit.combatStats.currentEnergy > snipeCost){ //has enough energy to snipe
						Log.d(TAG, "has enough energy to snipe");
						Unit swordMaster = AI.GetUnit.highestHP(swordmasters);
						AI.Actions.useSkill(aiUnit, swordMaster, snipe);
					} else {
						Unit swordMaster = AI.GetUnit.lowestHP(swordmasters);
						AI.Actions.attack(aiUnit, swordMaster);
					}
				} else {
					Unit lowestEnemy = AI.GetUnit.lowestHP(unitsInAtkRng);
					AI.Actions.attack(aiUnit, lowestEnemy);
				}
			}
		} else {
			List<Unit> enInRange = AI.GetUnits.fromPoints(enemyUnits, atkMovRange);
			if(enInRange.size() > 0){ //enemy in atk + move range
				Log.d(TAG, "enemy in atk + move range");
				List<Unit> medics = AI.GetUnits.fromType(enInRange, UnitType.Medic);
				if(medics.size() > 0){ //medic in range
					Log.d(TAG, "medic in atk + mov range");
					Unit medic = AI.GetUnit.closestPoint(medics, aiUnit.position);
					List<Point> medicAtkRange = AI.Pathfind.GetOpenSpaces(medic.position, aiUnit.combatStats.range);
					List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, medicAtkRange);
					if(validMoves.size() > 0){ //valid moves
						Point bestMove = AI.GetPoint.lowestInf(validMoves, playerMap);
						AI.Actions.moveAttack(aiUnit, medic, bestMove);
					} else {
						AI.Actions.attack(aiUnit, medic);
					}
				} else {
					List<Unit> swordmasters = AI.GetUnits.fromType(enInRange, UnitType.SwordMaster);
					if(swordmasters.size() > 0){ //sword master in atk range
						Log.d(TAG, "sword master in atk + mov range");
						Unit swordmaster = AI.GetUnit.highestHP(swordmasters);
						List<Point> smAtkRange = AI.Pathfind.GetOpenSpaces(swordmaster.position, aiUnit.combatStats.range);
						List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, smAtkRange);
						if(validMoves.size() > 0){ //valid move between sniper and swordmaster
							Log.d(TAG, "valid move between sniper and swordmaster");
							Point bestMove = AI.GetPoint.lowestInf(validMoves, playerMap);
							if(aiUnit.combatStats.currentEnergy > snipeCost){ //has enough energy to snipe
								Log.d(TAG, "has enough energy to snipe");
								AI.Actions.moveSkill(aiUnit, swordmaster, bestMove, snipe);
							} else {
								AI.Actions.moveAttack(aiUnit, swordmaster, bestMove);
							}
						} else {
							AI.Actions.attack(aiUnit, swordmaster);
						}
					} else {
						List<Unit> closeEnemies = AI.GetUnits.fromPoints(enemyUnits, atkMovRange);
						Unit closestEnemy = AI.GetUnit.lowestHP(closeEnemies);
						AI.Actions.attack(aiUnit, closestEnemy);
					}
				}
			} else {
				Unit closestUnit = AI.GetUnit.closestPoint(enemyUnits, aiUnit.position);
				AI.Actions.attack(aiUnit, closestUnit);
			}
		}
	}
	
	private static void defensiveSP(Unit aiUnit, InfluenceMap aiMap, InfluenceMap playerMap, List<Unit> allyUnits, List<Unit> enemyUnits){		
		List<Point> movePath = AI.Pathfind.GetMovePath(aiUnit);
		List<Unit> unitsInAtkRng = AI.Pathfind.GetAttackUnitPrediction(aiUnit, aiUnit.position);

		if(unitsInAtkRng.size() > 0){ //units in atk range
			Log.d(TAG, "units in atk range");
			List<Unit> medics = AI.GetUnits.fromType(unitsInAtkRng, UnitType.Medic);
			if(medics.size() > 0){ //medic in range
				Log.d(TAG, "medic in range");
				Unit medic = AI.GetUnit.lowestHP(medics);
				List<Point> medicAtkRange = AI.Pathfind.GetOpenSpaces(medic.position, aiUnit.combatStats.range);
				List<Point> validMoves = AI.GetPoints.matchingPoints(medicAtkRange, movePath);
				if(validMoves.size() > 0){
					Point cover = AI.GetPoint.closestCover(validMoves, aiUnit.position);
					if(cover != null){ // cover in Range
						Log.d(TAG, "Cover in range");
						AI.Actions.moveAttack(aiUnit, medic, cover);
					} else {
						Point lowestInf = AI.GetPoint.lowestInf(validMoves, playerMap);
						AI.Actions.moveAttack(aiUnit, medic, lowestInf);
					}
				} else {
					Point lowestInf = AI.GetPoint.lowestInf(movePath, playerMap);
					AI.Actions.move(aiUnit, lowestInf);
				}
			} else {
				List<Unit> swordmasters = AI.GetUnits.fromType(unitsInAtkRng, UnitType.SwordMaster);
				if(swordmasters.size() > 0){// swordmaster in range
					Log.d(TAG, "swordmaster in range");
					Unit swordmaster = AI.GetUnit.highestHP(swordmasters);
					List<Point> smAtkRange = AI.Pathfind.GetOpenSpaces(swordmaster.position, aiUnit.combatStats.range);
					List<Point> validMoves = AI.GetPoints.matchingPoints(smAtkRange, movePath);
					if(validMoves.size() > 0){
						Point cover = AI.GetPoint.closestCover(validMoves, aiUnit.position);
						if(cover != null){ //cover in range
							Log.d(TAG, "Cover in range");
							AI.Actions.moveAttack(aiUnit, swordmaster, cover);
						} else {
							Point furthestPoint = AI.GetPoint.furthestPoint(validMoves, swordmaster.position);
							AI.Actions.moveAttack(aiUnit, swordmaster, furthestPoint);
						}
					} else {
						Point lowestInf = AI.GetPoint.lowestInf(movePath, playerMap);
						AI.Actions.move(aiUnit, lowestInf);
					}
				}
			}
		} else {
			Unit closestUnit = AI.GetUnit.closestPoint(enemyUnits, aiUnit.position);
			List<Point> clsAtkRange = AI.Pathfind.GetOpenSpaces(closestUnit.position, aiUnit.combatStats.range);
			List<Point> validMoves = AI.GetPoints.matchingPoints(clsAtkRange, movePath);
			if(validMoves.size() > 0){ //valid move to closest unit
				Log.d(TAG, "valid move to closest unit");
				Point cover = AI.GetPoint.closestCover(validMoves, aiUnit.position);
				if(cover != null){ //cover in range
					Log.d(TAG, "cover in range");
					AI.Actions.moveAttack(aiUnit, closestUnit, cover);
				} else {
					Point lowestInf = AI.GetPoint.lowestInf(validMoves, playerMap);
					AI.Actions.moveAttack(aiUnit, closestUnit, lowestInf);
				}
			} else {
				Point cover = AI.GetPoint.closestCover(movePath, aiUnit.position);
				if(cover != null){ //cover in range
					Log.d(TAG, "cover in range");
					AI.Actions.move(aiUnit, cover);
				} else {
					Point lowestInf = AI.GetPoint.lowestInf(movePath, playerMap);
					AI.Actions.move(aiUnit, lowestInf);
				}
			}
			
		}
	}
	
	private static void retreatSP(Unit aiUnit, InfluenceMap aiMap, InfluenceMap playerMap, List<Unit> allyUnits, List<Unit> enemyUnits){
		SkillType snipe = SkillType.Headshot;
		int snipeCost = GameStats.getSkillStats(snipe).energyCost;
		
		List<Point> movePath = AI.Pathfind.GetMovePath(aiUnit);
		List<Unit> unitsInAtkRng = AI.Pathfind.GetAttackUnitPrediction(aiUnit, aiUnit.position);
		
		if(unitsInAtkRng.size() > 0){ //enemy in range
			Log.d(TAG, "Enemy in range");
			List<Unit> swordmasters = AI.GetUnits.fromType(unitsInAtkRng, UnitType.SwordMaster);
			if(swordmasters.size() > 0){ //swordmaster in range
				Log.d(TAG, "swordmaster in range");
				Unit swordmaster = AI.GetUnit.closestPoint(swordmasters, aiUnit.position);
				List<Point> smAtkRng = AI.Pathfind.GetOpenSpaces(swordmaster.position, aiUnit.combatStats.range);
				List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, smAtkRng);
				if(validMoves.size() > 0){ // valid to move and attack swordmaster
					Log.d(TAG,"valid to move and attack swordmaster");
					Point cover = AI.GetPoint.closestCover(validMoves, aiUnit.position);
					if(cover != null){ //cover is nearby
						Log.d(TAG, "cover is nearby");
						if(aiUnit.combatStats.currentEnergy > snipeCost){ //has enough energy to snipe
							Log.d(TAG, "has enough energy to snipe");
							AI.Actions.moveSkill(aiUnit, swordmaster, cover, snipe);
						} else {
							AI.Actions.moveAttack(aiUnit, swordmaster, cover);
						}
					} else {
						Point lowestInf = AI.GetPoint.lowestInf(validMoves, playerMap);
						AI.Actions.moveAttack(aiUnit, swordmaster, lowestInf);
					}
				} else {
					Point cover = AI.GetPoint.closestCover(movePath, aiUnit.position);
					if(cover != null){ //cover is available
						AI.Actions.move(aiUnit, cover);
					} else {
						Point lowestInf = AI.GetPoint.lowestInf(movePath, playerMap);
						AI.Actions.move(aiUnit, lowestInf);
					}
				}
			} else {
				List<Unit> medics = AI.GetUnits.fromType(allyUnits, UnitType.Medic);
				if(medics.size() > 0){ //friendly medic on field
					Log.d(TAG, "friendly medic on field");
					Unit medic = AI.GetUnit.closestPoint(medics, aiUnit.position);
					List<Point> mdHealRng = AI.Pathfind.GetOpenSpaces(medic.position, GameStats.getSkillStats(SkillType.Heal).range);
					List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, mdHealRng);
					if(validMoves.size() > 0){ // valid move to medic heal range
						Log.d(TAG, "valid move to medic heal range");
						Point highInf = AI.GetPoint.highestInf(validMoves, aiMap);
						AI.Actions.move(aiUnit, highInf);
					} else {
						List<Point> bestPath = AI.Pathfind.GetMovePath(aiUnit);
						AI.GetPoint.closestPoint(bestPath, medic.position);
					}
				} else {
					Unit lowestEnemy = AI.GetUnit.lowestHP(unitsInAtkRng);
					AI.Actions.attack(aiUnit, lowestEnemy);
				}
			}
		} else {
			List<Unit> medics = AI.GetUnits.fromType(allyUnits, UnitType.Medic);
			if(medics.size() > 0){ //friendly medic on field
				Log.d(TAG, "friendly medic on field");
				Unit medic = AI.GetUnit.closestPoint(medics, aiUnit.position);
				List<Point> mdHealRng = AI.Pathfind.GetOpenSpaces(medic.position, GameStats.getSkillStats(SkillType.Heal).range);
				List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, mdHealRng);
				if(validMoves.size() > 0){ // valid move to medic heal range
					Log.d(TAG, "valid move to medic heal range");
					Point highInf = AI.GetPoint.highestInf(validMoves, aiMap);
					AI.Actions.move(aiUnit, highInf);
				} else {
					List<Point> bestPath = AI.Pathfind.GetMovePath(aiUnit);
					AI.GetPoint.closestPoint(bestPath, medic.position);
				}
			} else {
				Point cover = AI.GetPoint.closestCover(movePath, aiUnit.position);
				if(cover != null) { // cover in range
					Log.d(TAG, "cover in range");
					AI.Actions.move(aiUnit, cover);
				} else {
					Point lowestInf = AI.GetPoint.lowestInf(movePath, playerMap);
					AI.Actions.move(aiUnit, lowestInf);
				}
			}
		}
	}
}
