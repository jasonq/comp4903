package com.comp4903.project.gameEngine.engine;

import java.util.List;

import android.graphics.Point;

import com.comp4903.AI.AIEngine;
import com.comp4903.pathfind.PathFind;
import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Status;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.ActionType;
import com.comp4903.project.gameEngine.enums.IconType;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.UnitGroup;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.gameEngine.factory.SkillStats;
import com.comp4903.project.gameEngine.factory.UnitStats;
import com.comp4903.project.gameEngine.networking.Action;
import com.comp4903.project.graphics.GLRenderer;
import com.comp4903.project.graphics.RendererAccessor;
import com.comp4903.project.network.Networking;

public class GameEngine {
	public static MapData mapData;
	public static void Initialize(MapData data) { mapData = data; }
	
	public static boolean moveUnit(Unit unit, Point point, boolean network){
		Unit u = mapData.getUnitAt(unit.position);
		if (u == null)
			return false;
		if (!mapData.inMap(point))
			return false;
		if (moveUnit(unit, point, PathFind.UnitToPoint(u, point))){
			System.out.println("Successfully moved");
			if (network){
				System.out.println("Creating network message");
				Action a = new Action();
				a.action = ActionType.Move;
				a.uIDOne = u.uID;
				a.x = point.x;
				a.y = point.y;
				Networking.send(a.getActionMessage());
				System.out.println("Network message sent");
			}
			return true;
		} else {
			System.out.println("Move Failed?");
			return false;
		}
	}
	
	public static boolean moveUnit(Unit unit, Point point, List<Point> p){
		Unit u = mapData.getUnitAt(unit.position);
		if (u == null)
			return false;
		if (!mapData.inMap(point))
			return false;
		RendererAccessor.moveAnimation(u, p);
		u.position = point;
		Status s = new Status(mapData._tileTypes[point.x][point.y]);
		u.tileStatus = s;
		u.UpdateCombatStats();
		System.out.println(u.unitType.name() + u.uID + " Move to " + point.x + ", " + point.y);
		RendererAccessor.update(mapData);
		return true;
	}
	
	public static boolean useSkill(Unit source, Unit target, SkillType skill, boolean inActive, boolean network){
		Unit unitOne = null;
		Unit unitTwo = null;
		if (source != null)
			unitOne = mapData.getUnitAt(source.position);
		if (target != null)
			unitTwo = mapData.getUnitAt(target.position);
		switch (skill){
			case Attack:
				System.out.println("Attacking");
				if (canCastSkill(unitOne, SkillType.Attack))
				if (SkillEngine.Attack(unitOne, unitTwo, network)){
					mapData.RemoveDeadUnit();
					RendererAccessor.update(mapData);
					if (inActive) source.active = false;
					return true;
				}
				break;
			case Defend:
				System.out.println("Defend");
				if (canCastSkill(unitOne, SkillType.Defend))
				if (SkillEngine.Defend(unitOne, true)){
					RendererAccessor.update(mapData);
					if (inActive) source.active = false;
					return true;
				}
				break;
			case Headshot:
				System.out.println("Headshot");
				if (canCastSkill(unitOne, SkillType.Headshot))
				if (SkillEngine.HeadShot(unitOne, unitTwo, network)){
					mapData.RemoveDeadUnit();
					RendererAccessor.update(mapData);
					if (inActive) source.active = false;
					return true;
				}
				break;
			case Heal:
				System.out.println("Heal");
				if (canCastSkill(unitOne, SkillType.Heal))
				if (SkillEngine.Heal(unitOne, unitTwo, network)){					
					RendererAccessor.update(mapData);
					if (inActive) source.active = false;
					return true;
				}
				break;
			case ExposeWeakness: //not in use
				break;
			case StimPack: //not in use
				break;
			case Cripple: //not in use
				break;
			case DrainingGrenade: //not in use
				break;
			case EnergyVoid: //not in use
				break;
			case Disable: //not in use
				break;
			case DoubleTime: //not in us
				break;
			case Flamethrower:
				System.out.println("To be implemented");
				break;
			default:
				return false;
		}
		return false;
	}
	
	public static boolean canCastSkill(Unit u, SkillType skill){
		UnitStats uStats = GameStats.getUnitStats(u.unitType);
		SkillStats sStats = GameStats.getSkillStats(skill);
		if (uStats.canUseThisSkill(skill) == false) return false;
		if (u.combatStats.currentEnergy < sStats.energyCost) return false;
		if (u.combatStats.currentHealth < sStats.healthCost) return false;
		return true;
	}
	
	public static void endTurn(boolean network){
		int index = mapData._groupList.indexOf(mapData._activeGroup) + 1;
		if (index >= mapData._groupList.size())
			index = 0;
		UnitGroup previousGroup = mapData._activeGroup;
		UnitGroup currentGroup = mapData._groupList.get(index);
		for (Unit u : mapData._units){
			if (u.unitGroup == previousGroup){
				u.active = false;
				u.resolveStatus(true); // clear buffs from unit
			} else if (u.unitGroup == currentGroup) {
				u.active = true;
				u.resolveStatus(false); // clear buffs from unit
			} else {
				u.active = false;
			}
		}
		mapData._activeGroup = currentGroup;
		mapData.RemoveDeadUnit();
		if (network){
			Action a = new Action();
			a.action = ActionType.Endturn;
			Networking.send(a.getActionMessage());
		}
		if(mapData._activeGroup == UnitGroup.PlayerOne)
			RendererAccessor.floatingIcon(GLRenderer.GLwidth/2 - 125, GLRenderer.GLheight/10, 0, 0, 100, null, IconType.P1);
		else if(mapData._activeGroup == UnitGroup.PlayerTwo)
			RendererAccessor.floatingIcon(GLRenderer.GLwidth/2 - 125, GLRenderer.GLheight/10, 0, 0, 100, null, IconType.P2);
	}
	
	/* Networking Supporting Method */
	public static boolean executeAction(Action action){
		System.out.println("GameEngine: Received Action message.");
		Unit uOne = mapData.getUnitByID(action.uIDOne);
		Unit uTwo = mapData.getUnitByID(action.uIDTwo);
		switch (action.action){
			case Move:
				return moveUnit(uOne, new Point(action.x, action.y), false);
			case Attack:
				return SkillEngine.NetVAttack(uOne, uTwo, action);
			case Defend:
				return SkillEngine.Defend(uOne, false);
			case Headshot:
				return SkillEngine.NetVHeadShot(uOne, uTwo, action);
			case Heal:
				return SkillEngine.Heal(uOne, uTwo, false);
			case Endturn:
				endTurn(false);
				return true;
			default:
				break;
		}
		return true;
	}
}
