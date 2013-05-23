package com.comp4903.AI;

import java.util.List;

import android.graphics.Point;
import android.util.Log;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.UnitType;
import com.comp4903.project.gameEngine.factory.GameStats;

/**
 * Swordmaster Behaviour
 * @author Jason Quan
 *
 */
public class SMBehaviour {
	
	private static final String TAG = "AI.SwordMasterBehaviour";
	
	public static void think(Unit u, InfluenceMap aiMap, InfluenceMap playerMap, List<Unit> allyUnits, List<Unit> enemyUnits){
		switch (u.aiData.getState()){
		case Aggressive:
			aggressiveSM(u, aiMap, playerMap, allyUnits, enemyUnits);
			break;
		case Defensive:
			defensiveSM(u, aiMap, playerMap, allyUnits, enemyUnits);
			break;
		case Retreat:
			retreatSM(u, aiMap, playerMap, allyUnits, enemyUnits);
			break;
		}
	}
	
	private static void aggressiveSM(Unit aiUnit, InfluenceMap aiMap, InfluenceMap playerMap, List<Unit> allyUnits, List<Unit> enemyUnits){
		int atkMov = aiUnit.combatStats.range + aiUnit.combatStats.maxMovement;
		
		List<Point> movePath = AI.Pathfind.GetMovePath(aiUnit);
		List<Unit> unitsInAtkRng = AI.Pathfind.GetAttackUnitPrediction(aiUnit, aiUnit.position);
		List<Point> atkMovRange = AI.Pathfind.GetOpenSpaces(aiUnit.position, atkMov);
		
		if(unitsInAtkRng.size() > 0){ //enemy in range
			Log.d(TAG, "enemy in range");
			Unit lowEnemy = AI.GetUnit.lowestHP(unitsInAtkRng);
			List<Point> spaceAroundLowEnemy = AI.Pathfind.GetOpenSpaces(lowEnemy.position, aiUnit.combatStats.range);
			Point cover = AI.GetPoint.closestCover(spaceAroundLowEnemy, lowEnemy.position);
			if(cover != null){ //cover around enemy
				Log.d(TAG, "cover around enemy");
				if(AI.Check.checkPoint(cover, movePath)){ //cover in movePath
					Log.d(TAG, "cover in movePath");
					AI.Actions.moveAttack(aiUnit, lowEnemy, cover);
				} else {
					List<Point> validMoves = AI.GetPoints.matchingPoints(spaceAroundLowEnemy, movePath);
					if(validMoves.size() > 0){ //valid move
						Log.d(TAG, "valid moves to move and attack");
						Point lowInf = AI.GetPoint.lowestInf(validMoves, playerMap);
						AI.Actions.moveAttack(aiUnit, lowEnemy, lowInf);
					} else {
						AI.Actions.attack(aiUnit, lowEnemy);
					}
				}
			} else {
				List<Point> validMoves = AI.GetPoints.matchingPoints(spaceAroundLowEnemy, movePath);
				if(validMoves.size() > 0){ //valid move
					Log.d(TAG, "valid moves to move and attack");
					Point lowInf = AI.GetPoint.lowestInf(validMoves, playerMap);
					AI.Actions.moveAttack(aiUnit, lowEnemy, lowInf);
				} else {
					AI.Actions.attack(aiUnit, lowEnemy);
				}
			}
		} else {
			List<Unit> enInAtkMovRange = AI.GetUnits.fromPoints(enemyUnits, atkMovRange);
			if(enInAtkMovRange.size() > 0){ //enemy in move + atk range
				Unit target = AI.GetUnit.lowHPlowInf(enInAtkMovRange, playerMap);
				List<Point> spaceAroundTarget = AI.Pathfind.GetOpenSpaces(target.position, aiUnit.combatStats.range);
				List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, spaceAroundTarget);
				if(validMoves.size() > 0){ //valid moves in move range
					List<Point> coverPoints = AI.GetPoints.coverPoints(validMoves);
					if(coverPoints.size() > 0){ // cover is in valid move
						Point cover = AI.GetPoint.closestCover(coverPoints, aiUnit.position);
						AI.Actions.moveAttack(aiUnit, target, cover);
					} else {
						Point lowestInf = AI.GetPoint.lowestInf(validMoves, playerMap);
						AI.Actions.moveAttack(aiUnit, target, lowestInf);
					}
				} else {
					Unit closestEnemy = AI.GetUnit.closestPoint(enemyUnits, aiUnit.position);
					AI.Actions.attack(aiUnit, closestEnemy);
				}
				
			} else {
				Unit closestEnemy = AI.GetUnit.closestPoint(enemyUnits, aiUnit.position);
				AI.Actions.attack(aiUnit, closestEnemy);
			}
		}
		
	}
	
	private static void defensiveSM(Unit aiUnit, InfluenceMap aiMap, InfluenceMap playerMap, List<Unit> allyUnits, List<Unit> enemyUnits){
		int atkMov = aiUnit.combatStats.range + aiUnit.combatStats.maxMovement;
		
		List<Point> movePath = AI.Pathfind.GetMovePath(aiUnit);
		List<Unit> unitsInAtkRng = AI.Pathfind.GetAttackUnitPrediction(aiUnit, aiUnit.position);
		List<Point> atkMovRange = AI.Pathfind.GetOpenSpaces(aiUnit.position, atkMov);
		
		if(unitsInAtkRng.size() > 0){ //units in attack range
			List<Unit> medics = AI.GetUnits.fromType(unitsInAtkRng, UnitType.Medic);
			if(medics.size() > 0){ // medic in attack range
				Unit medic = AI.GetUnit.lowestHP(medics);
				List<Point> pointsAroundMedic = AI.Pathfind.GetOpenSpaces(medic.position, aiUnit.combatStats.range);
				Point cover = AI.GetPoint.closestCover(pointsAroundMedic, medic.position);
				if(cover != null){ //cover around medic
					if(AI.Check.checkPoint(cover, movePath)){ //cover is moveable to
						AI.Actions.moveAttack(aiUnit, medic, cover);
					} else {
						AI.Actions.attack(aiUnit, medic);
					}
				} else {
					Point lowInf = AI.GetPoint.lowestInf(pointsAroundMedic, playerMap);
					if(AI.Check.checkPoint(lowInf, movePath)){ // lowestinf point is moveable to
						AI.Actions.moveAttack(aiUnit, medic, lowInf);
					} else {
						AI.Actions.attack(aiUnit, medic);
					}
				}
			} else {
				Unit target = AI.GetUnit.lowestHP(unitsInAtkRng);
				List<Point> pointsAroundTarget = AI.Pathfind.GetOpenSpaces(target.position, aiUnit.combatStats.range);
				Point cover = AI.GetPoint.closestCover(pointsAroundTarget, target.position);
				if(cover != null){ //cover around target
					if(AI.Check.checkPoint(cover, movePath)){ //cover is moveable to
						AI.Actions.moveAttack(aiUnit, target, cover);
					} else {
						AI.Actions.attack(aiUnit, target);
					}
				} else {
					Point lowInf = AI.GetPoint.lowestInf(pointsAroundTarget, playerMap);
					if(AI.Check.checkPoint(lowInf, movePath)){ // lowestinf point is moveable to
						AI.Actions.moveAttack(aiUnit, target, lowInf);
					} else {
						AI.Actions.attack(aiUnit, target);
					}
				}
			}
		} else {
			List<Unit> movAtkUnits = AI.GetUnits.fromPoints(enemyUnits, atkMovRange);
			if(movAtkUnits.size() > 0){ //there are units we can move and attack
				Unit target = AI.GetUnit.lowestHP(movAtkUnits);
				List<Point> areaAroundTarget = AI.Pathfind.GetOpenSpaces(target.position, aiUnit.combatStats.range);
				if(areaAroundTarget.size() > 0) { //there is an area around the target
					List<Point> coverPoints = AI.GetPoints.coverPoints(areaAroundTarget);
					List<Point> validPoints = AI.GetPoints.matchingPoints(movePath, coverPoints);
					if(validPoints.size() > 0){ //cover is around target
						Point cover = AI.GetPoint.closestCover(validPoints, aiUnit.position);
						AI.Actions.moveAttack(aiUnit, target, cover);
					} else {
						Point lowInf = AI.GetPoint.lowestInf(areaAroundTarget, playerMap);
						if(AI.Check.checkPoint(lowInf, movePath)){ //lowInf point is in movePath
							AI.Actions.moveAttack(aiUnit, target, lowInf);
						} else {
							AI.Actions.attack(aiUnit, target);
						}
					}
				} else {
					AI.Actions.attack(aiUnit, target);
				}
			} else {
				Point cover = AI.GetPoint.closestCover(movePath, aiUnit.position);
				if(cover != null){ //cover is in movePath
					AI.Actions.move(aiUnit, cover);
				} else {
					Point lowInf = AI.GetPoint.lowestInf(movePath, playerMap);
					AI.Actions.move(aiUnit, lowInf);
				}
			}
		}
		
	}
	
	private static void retreatSM(Unit aiUnit, InfluenceMap aiMap, InfluenceMap playerMap, List<Unit> allyUnits, List<Unit> enemyUnits){
		List<Point> movePath = AI.Pathfind.GetMovePath(aiUnit);
		List<Unit> unitsInMvRng = AI.GetUnits.fromPoints(enemyUnits, movePath);
		
		if(unitsInMvRng.size() < 0){ //is aiUnit in player Influence
			Unit target = AI.GetUnit.lowestHP(unitsInMvRng);
			if(target.combatStats.currentHealth < 25){
				List<Point> areaTarget = AI.Pathfind.GetOpenSpaces(target.position, aiUnit.combatStats.range);
				List<Point> coverPoints = AI.GetPoints.coverPoints(movePath);
				List<Point> validMoves = AI.GetPoints.matchingPoints(areaTarget, coverPoints);
				if(validMoves.size() > 0){ //cover exists
					Point cover = AI.GetPoint.closestCover(validMoves, aiUnit.position);
					AI.Actions.moveAttack(aiUnit, target, cover);
				} else {
					AI.Actions.attack(aiUnit, target);
				}
			} else {
				Point cover = AI.GetPoint.closestCover(movePath, aiUnit.position);
				if(cover != null) { //cover exists
					List<Unit> unitAround = AI.Pathfind.GetAttackUnitPrediction(aiUnit, cover);
					if(unitAround.size() > 0){ // units around cover exist
						Unit targetAround = AI.GetUnit.lowestHP(unitAround);
						AI.Actions.moveAttack(aiUnit, targetAround, cover);
					} else {
						AI.Actions.move(aiUnit, cover);
					}
				}
			}
		} else {
			List<Unit> allyMedics = AI.GetUnits.fromType(allyUnits, UnitType.Medic);
			if(allyMedics.size() > 0){
				Unit medic = AI.GetUnit.closestPoint(allyMedics, aiUnit.position);
				List<Point> mdHealRng = AI.Pathfind.GetOpenSpaces(medic.position, GameStats.getSkillStats(SkillType.Heal).range);
				List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, mdHealRng);
				if(validMoves.size() > 0){
					Point highAllyInf = AI.GetPoint.highestInf(validMoves, aiMap);
					AI.Actions.move(aiUnit, highAllyInf);
				} else {
					Point closestToMedic = AI.GetPoint.closestPoint(movePath, medic.position);
					AI.Actions.move(aiUnit, closestToMedic);
				}
			} else {
				Point highAllyInf = AI.GetPoint.highestInf(movePath, aiMap);
				AI.Actions.move(aiUnit, highAllyInf);
			}
		}
	}
}
