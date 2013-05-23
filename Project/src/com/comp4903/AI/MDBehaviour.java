package com.comp4903.AI;

import java.util.List;

import android.graphics.Point;
import android.util.Log;

import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.factory.GameStats;
/**
 * Medic Behaviour
 * @author Jason Quan
 *
 */
public class MDBehaviour {
	private static final String TAG = "MedicBehaviour";
	
	private static final SkillType heal = SkillType.Heal;
	private static final int healCost = GameStats.getSkillStats(heal).energyCost;
	private static final int healValue = GameStats.getSkillStats(heal).getModifier("Heal").intValue();
	private static final int healRange = GameStats.getSkillStats(heal).range;
	
	public static void think(Unit u, InfluenceMap aiMap, InfluenceMap playerMap, List<Unit> allyUnits, List<Unit> enemyUnits){
		switch (u.aiData.getState()){
		case Aggressive:
			aggressiveMD(u, aiMap, playerMap, allyUnits, enemyUnits);
			break;
		case Defensive:
			defensiveMD(u, aiMap, playerMap, allyUnits, enemyUnits);
			break;
		case Retreat:
			retreatMD(u, aiMap, playerMap, allyUnits, enemyUnits);
			break;
		}
	}
	
	private static void aggressiveMD(Unit aiUnit, InfluenceMap aiMap, InfluenceMap playerMap, List<Unit> allyUnits, List<Unit> enemyUnits){
		int atkMov = aiUnit.combatStats.range + aiUnit.combatStats.maxMovement;
		int healMov = aiUnit.combatStats.maxMovement + healRange;
		List<Point> movePath = AI.Pathfind.GetMovePath(aiUnit);
		List<Point> atkMovRange = AI.Pathfind.GetOpenSpaces(aiUnit.position, atkMov);
		
		if(aiUnit.combatStats.currentEnergy > healCost){
			List<Point> healMovePath = AI.Pathfind.GetOpenSpaces(aiUnit.position, healMov);
			List<Unit> allyInRange = AI.GetUnits.fromPoints(allyUnits, healMovePath);
			if(allyInRange.size() > 0){
				Unit lowAlly = AI.GetUnit.lowestHP(allyInRange);
				if((lowAlly.combatStats.maxHealth - lowAlly.combatStats.currentHealth) > healValue){
					List<Point> healAllyRange = AI.Pathfind.GetOpenSpaces(lowAlly.position, healRange);
					List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, healAllyRange);
					if(validMoves.size() > 0){
						Point targetPoint = AI.GetPoint.lowestInf(validMoves, playerMap);
						AI.Actions.moveSkill(aiUnit, lowAlly, targetPoint, heal);
					} else {
						AI.Actions.useSkill(aiUnit, lowAlly, heal);
					}
				} else {
					List<Unit> enemyInRange = AI.GetUnits.fromPoints(enemyUnits, atkMovRange);
					if(enemyInRange.size() > 0){ //enemy in atk + move range
						Unit lowestEnemy = AI.GetUnit.lowestHP(enemyInRange);
						List<Point> enemyAtkrange = AI.Pathfind.GetOpenSpaces(lowestEnemy.position, aiUnit.combatStats.range);
						List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, enemyAtkrange);
						if(validMoves.size() > 0){ //valid move
							List<Point> validCover = AI.GetPoints.coverPoints(validMoves);
							if(validCover.size() > 0){ //cover in valid move
								Point cover = AI.GetPoint.closestCover(validCover, aiUnit.position);
								AI.Actions.moveAttack(aiUnit, lowestEnemy, cover);
							} else {
								Point targetPoint = AI.GetPoint.highestInf(validMoves, aiMap);
								AI.Actions.moveAttack(aiUnit, lowestEnemy, targetPoint);
							}
						} else {
							AI.Actions.attack(aiUnit, lowestEnemy);
						}
					} else {
						if(AI.Check.checkInfluence(movePath, aiMap)){
							Point targetPoint = AI.GetPoint.highestInf(movePath, aiMap);
							AI.Actions.move(aiUnit, targetPoint);
						} else {
							Unit closestEnemy = AI.GetUnit.closestPoint(enemyUnits, aiUnit.position);
							AI.Actions.attack(aiUnit, closestEnemy);
						}
					}
				}
			} else {
				List<Unit> enemyInRange = AI.GetUnits.fromPoints(enemyUnits, atkMovRange);
				if(enemyInRange.size() > 0){ //enemy in atk + move range
					Unit lowestEnemy = AI.GetUnit.lowestHP(enemyInRange);
					List<Point> enemyAtkrange = AI.Pathfind.GetOpenSpaces(lowestEnemy.position, aiUnit.combatStats.range);
					List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, enemyAtkrange);
					if(validMoves.size() > 0){ //valid move
						List<Point> validCover = AI.GetPoints.coverPoints(validMoves);
						if(validCover.size() > 0){ //cover in valid move
							Point cover = AI.GetPoint.closestCover(validCover, aiUnit.position);
							AI.Actions.moveAttack(aiUnit, lowestEnemy, cover);
						} else {
							Point targetPoint = AI.GetPoint.lowestInf(validMoves, playerMap);
							AI.Actions.moveAttack(aiUnit, lowestEnemy, targetPoint);
						}
					} else {
						AI.Actions.attack(aiUnit, lowestEnemy);
					}
				} else {
					if(AI.Check.checkInfluence(movePath, aiMap)){
						Point targetPoint = AI.GetPoint.highestInf(movePath, aiMap);
						AI.Actions.move(aiUnit, targetPoint);
					} else {
						Unit closestEnemy = AI.GetUnit.closestPoint(enemyUnits, aiUnit.position);
						AI.Actions.attack(aiUnit, closestEnemy);
					}
				}
			}
		} else {
			List<Unit> enemyInRange = AI.GetUnits.fromPoints(enemyUnits, atkMovRange);
			if(enemyInRange.size() > 0){ //enemy in atk + move range
				Unit lowestEnemy = AI.GetUnit.lowestHP(enemyInRange);
				List<Point> enemyAtkrange = AI.Pathfind.GetOpenSpaces(lowestEnemy.position, aiUnit.combatStats.range);
				List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, enemyAtkrange);
				if(validMoves.size() > 0){ //valid move
					List<Point> validCover = AI.GetPoints.coverPoints(validMoves);
					if(validCover.size() > 0){ //cover in valid move
						Point cover = AI.GetPoint.closestCover(validCover, aiUnit.position);
						AI.Actions.moveAttack(aiUnit, lowestEnemy, cover);
					} else {
						Point targetPoint = AI.GetPoint.highestInf(validMoves, aiMap);
						AI.Actions.moveAttack(aiUnit, lowestEnemy, targetPoint);
					}
				} else {
					AI.Actions.attack(aiUnit, lowestEnemy);
				}
			} else {
				if(AI.Check.checkInfluence(movePath, aiMap)){
					Point targetPoint = AI.GetPoint.highestInf(movePath, aiMap);
					AI.Actions.move(aiUnit, targetPoint);
				} else {
					Unit closestEnemy = AI.GetUnit.closestPoint(enemyUnits, aiUnit.position);
					AI.Actions.attack(aiUnit, closestEnemy);
				}
			}
		}
	}
	
	private static void defensiveMD(Unit aiUnit, InfluenceMap aiMap, InfluenceMap playerMap, List<Unit> allyUnits, List<Unit> enemyUnits){		
		int atkMov = aiUnit.combatStats.range + aiUnit.combatStats.maxMovement;
		int healMov = aiUnit.combatStats.maxMovement + healRange;
		List<Point> movePath = AI.Pathfind.GetMovePath(aiUnit);
		List<Point> atkMovRange = AI.Pathfind.GetOpenSpaces(aiUnit.position, atkMov);
		
		if(aiUnit.combatStats.currentEnergy > healCost){
			List<Point> healMovePath = AI.Pathfind.GetOpenSpaces(aiUnit.position, healMov);
			List<Unit> allyInRange = AI.GetUnits.fromPoints(allyUnits, healMovePath);
			if(allyInRange.size() > 0){
				Unit lowAlly = AI.GetUnit.lowestHP(allyInRange);
				if((lowAlly.combatStats.maxHealth - lowAlly.combatStats.currentHealth) > healValue){
					List<Point> healAllyRange = AI.Pathfind.GetOpenSpaces(lowAlly.position, healRange);
					List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, healAllyRange);
					if(validMoves.size() > 0){
						Point targetPoint = AI.GetPoint.lowestInf(validMoves, playerMap);
						AI.Actions.moveSkill(aiUnit, lowAlly, targetPoint, heal);
					} else {
						AI.Actions.useSkill(aiUnit, lowAlly, heal);
					}
				} else {
					List<Unit> enemyInRange = AI.GetUnits.fromPoints(enemyUnits, atkMovRange);
					if(enemyInRange.size() > 0){ //enemy in atk + move range
						Unit lowestEnemy = AI.GetUnit.lowestHP(enemyInRange);
						List<Point> enemyAtkrange = AI.Pathfind.GetOpenSpaces(lowestEnemy.position, aiUnit.combatStats.range);
						List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, enemyAtkrange);
						if(validMoves.size() > 0){ //valid move
							List<Point> validCover = AI.GetPoints.coverPoints(validMoves);
							if(validCover.size() > 0){ //cover in valid move
								Point cover = AI.GetPoint.closestCover(validCover, aiUnit.position);
								AI.Actions.moveAttack(aiUnit, lowestEnemy, cover);
							} else {
								Point targetPoint = AI.GetPoint.highestInf(validMoves, aiMap);
								AI.Actions.moveAttack(aiUnit, lowestEnemy, targetPoint);
							}
						} else {
							AI.Actions.attack(aiUnit, lowestEnemy);
						}
					} else {
						if(AI.Check.checkInfluence(movePath, aiMap)){
							Point targetPoint = AI.GetPoint.highestInf(movePath, aiMap);
							AI.Actions.move(aiUnit, targetPoint);
						} else {
							Unit closestEnemy = AI.GetUnit.closestPoint(enemyUnits, aiUnit.position);
							AI.Actions.attack(aiUnit, closestEnemy);
						}
					}
				}
			} else { //ally not in range
				if((aiUnit.combatStats.maxHealth - aiUnit.combatStats.currentHealth) > healValue){
					if(AI.Check.checkInfluence(movePath, aiMap)){
						Point infPoint = AI.GetPoint.highestInf(movePath, playerMap);
						AI.Actions.moveSkill(aiUnit, aiUnit, infPoint, heal);
					} else {
						Point cover = AI.GetPoint.closestCover(movePath, aiUnit.position);
						if(cover != null){
							AI.Actions.moveSkill(aiUnit, aiUnit, cover, heal);
						} else {
							AI.Actions.useSkill(aiUnit, aiUnit, heal);
						}
					}
				} else {
					List<Unit> enemyInRange = AI.GetUnits.fromPoints(enemyUnits, atkMovRange);
					if(enemyInRange.size() > 0){ //enemy in atk + move range
						Unit lowestEnemy = AI.GetUnit.lowestHP(enemyInRange);
						List<Point> enemyAtkrange = AI.Pathfind.GetOpenSpaces(lowestEnemy.position, aiUnit.combatStats.range);
						List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, enemyAtkrange);
						if(validMoves.size() > 0){ //valid move
							List<Point> validCover = AI.GetPoints.coverPoints(validMoves);
							if(validCover.size() > 0){ //cover in valid move
								Point cover = AI.GetPoint.closestCover(validCover, aiUnit.position);
								AI.Actions.moveAttack(aiUnit, lowestEnemy, cover);
							} else {
								Point targetPoint = AI.GetPoint.highestInf(validMoves, aiMap);
								AI.Actions.moveAttack(aiUnit, lowestEnemy, targetPoint);
							}
						} else {
							AI.Actions.attack(aiUnit, lowestEnemy);
						}
					} else {
						if(AI.Check.checkInfluence(movePath, aiMap)){
							Point targetPoint = AI.GetPoint.highestInf(movePath, aiMap);
							AI.Actions.move(aiUnit, targetPoint);
						} else {
							Unit closestEnemy = AI.GetUnit.closestPoint(enemyUnits, aiUnit.position);
							AI.Actions.attack(aiUnit, closestEnemy);
						}
					}
				}
			}
		} else {
			List<Unit> enemyInRange = AI.GetUnits.fromPoints(enemyUnits, atkMovRange);
			if(enemyInRange.size() > 0){ //enemy in atk + move range
				Unit lowestEnemy = AI.GetUnit.lowestHP(enemyInRange);
				List<Point> enemyAtkrange = AI.Pathfind.GetOpenSpaces(lowestEnemy.position, aiUnit.combatStats.range);
				List<Point> validMoves = AI.GetPoints.matchingPoints(movePath, enemyAtkrange);
				if(validMoves.size() > 0){ //valid move
					List<Point> validCover = AI.GetPoints.coverPoints(validMoves);
					if(validCover.size() > 0){ //cover in valid move
						Point cover = AI.GetPoint.closestCover(validCover, aiUnit.position);
						AI.Actions.moveAttack(aiUnit, lowestEnemy, cover);
					} else {
						Point targetPoint = AI.GetPoint.highestInf(validMoves, aiMap);
						AI.Actions.moveAttack(aiUnit, lowestEnemy, targetPoint);
					}
				} else {
					AI.Actions.attack(aiUnit, lowestEnemy);
				}
			} else {
				if(AI.Check.checkInfluence(movePath, aiMap)){
					Point targetPoint = AI.GetPoint.highestInf(movePath, aiMap);
					AI.Actions.move(aiUnit, targetPoint);
				} else {
					Unit closestEnemy = AI.GetUnit.closestPoint(enemyUnits, aiUnit.position);
					AI.Actions.attack(aiUnit, closestEnemy);
				}
			}
		}
						
	}
	
	private static void retreatMD(Unit aiUnit, InfluenceMap aiMap, InfluenceMap playerMap, List<Unit> allyUnits, List<Unit> enemyUnits){
		int atkMov = aiUnit.combatStats.range + aiUnit.combatStats.maxMovement;
		List<Point> movePath = AI.Pathfind.GetMovePath(aiUnit);
		
		if(aiUnit.combatStats.currentEnergy > healCost){ //energy to heal
			List<Point> coverPoints = AI.GetPoints.coverPoints(movePath);
			if(coverPoints.size() > 0){ //cover in range
				if(AI.Check.checkInfluence(coverPoints, playerMap)){ //enemy influence in cover points
					Point lowInf = AI.GetPoint.lowestInf(coverPoints, playerMap);
					AI.Actions.moveSkill(aiUnit, aiUnit, lowInf, heal);
				} else { //no enemy influence in cover points
					if(AI.Check.checkInfluence(coverPoints, aiMap)){ // ally influence in cover points
						Point highInf = AI.GetPoint.highestInf(coverPoints, aiMap);
						AI.Actions.moveSkill(aiUnit, aiUnit, highInf, heal);
					} else { //no ally influence in cover points
						Point closestCover = AI.GetPoint.closestCover(coverPoints, aiUnit.position);
						AI.Actions.moveSkill(aiUnit, aiUnit, closestCover, heal);
					}
				}
			} else { //cover not in range
				if(AI.Check.checkInfluence(movePath, playerMap)){ //enemy influence in cover points
					Point lowInf = AI.GetPoint.lowestInf(movePath, playerMap);
					AI.Actions.moveSkill(aiUnit, aiUnit, lowInf, heal);
				} else { //no enemy influence in cover points
					if(AI.Check.checkInfluence(movePath, aiMap)){ // ally influence in cover points
						Point highInf = AI.GetPoint.highestInf(movePath, aiMap);
						AI.Actions.moveSkill(aiUnit, aiUnit, highInf, heal);
					} else { //no ally influence in cover points
						Point closestCover = AI.GetPoint.closestCover(movePath, aiUnit.position);
						AI.Actions.moveSkill(aiUnit, aiUnit, closestCover, heal);
					}
				}
			}
			
		} else { //no energy to heal
			List<Unit> enemyInRange = AI.Pathfind.GetAttackUnitPrediction(aiUnit, aiUnit.position);
			if(enemyInRange.size() > 0) { //enemy in atk range
				Unit target = AI.GetUnit.lowHPlowInf(enemyInRange, playerMap);
				List<Point> atkRange = AI.Pathfind.GetOpenSpaces(target.position, aiUnit.combatStats.range);
				List<Point> validMoves = AI.GetPoints.matchingPoints(atkRange, movePath);
				if(validMoves.size() > 0){ //valid moves in atkrange and path
					Point cover = AI.GetPoint.closestCover(validMoves, aiUnit.position);
					if(cover != null){ //cover in valid moves
						AI.Actions.moveAttack(aiUnit, target, cover);
					} else { // no cover in valid moves
						Point highInf = AI.GetPoint.highestInf(validMoves, aiMap);
						AI.Actions.moveAttack(aiUnit, target, highInf);
					}
				} else { // no valid moves in atkrange and path
					AI.Actions.attack(aiUnit, target);
				}
			} else { //no enemy in atk range
				if(AI.Check.checkInfluence(movePath, aiMap)){ //ally inf in movePath
					Point highestInf = AI.GetPoint.highestInf(movePath, aiMap);
					List<Unit> enemiesAround = AI.Pathfind.GetAttackUnitPrediction(aiUnit, highestInf);
					if(enemiesAround.size() > 0){ //enemies around highestInf point
						Unit lowestEnemy = AI.GetUnit.lowestHP(enemiesAround);
						AI.Actions.moveAttack(aiUnit, lowestEnemy, highestInf);
					} else { //no enemies around highestInf point
						AI.Actions.move(aiUnit, highestInf);
					}
				} else { //no ally inf in movePath
					Point cover = AI.GetPoint.closestCover(movePath, aiUnit.position);
					if(cover != null){ //cover in movePath
						List<Unit> enemiesAround = AI.Pathfind.GetAttackUnitPrediction(aiUnit, cover);
						if(enemiesAround.size() > 0) { //enemies around cover
							Unit lowestEnemy = AI.GetUnit.lowestHP(enemiesAround);
							AI.Actions.moveAttack(aiUnit, lowestEnemy, cover);
						} else { //no enemies around cover
							AI.Actions.move(aiUnit, cover);
						}
					} else { // no cover in movePath
						Point lowestEnInf = AI.GetPoint.lowestInf(movePath, playerMap);
						List<Unit> enemiesAround = AI.Pathfind.GetAttackUnitPrediction(aiUnit, lowestEnInf);
						if(enemiesAround.size() > 0){ // enemies around lowest inf point
							Unit lowestEnemy = AI.GetUnit.lowestHP(enemiesAround);
							AI.Actions.moveAttack(aiUnit, lowestEnemy, lowestEnInf);
						} else { // no enemies around lowest inf point
							AI.Actions.move(aiUnit, lowestEnInf);
						}
					}
				}
			}
		} //end of tree
	} //end of retreatMD
}
