package com.comp4903.project.gameEngine.engine;

import android.graphics.Point;

import com.comp4903.pathfind.PathFind;
import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.UnitGroup;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.gameEngine.factory.SkillStats;
import com.comp4903.project.gameEngine.factory.UnitStats;
import com.comp4903.project.graphics.RendererAccessor;

public class GameEngine {
	public static MapData mapData;
	public static void Initialize(MapData data) { mapData = data; }
	
	public static boolean moveUnit(Unit unit, Point point){
		Unit u = mapData.getUnitAt(unit.position);
		if (u == null)
			return false;
		if (!mapData.inMap(point))
			return false;
		RendererAccessor.moveAnimation(u, PathFind.UnitToPoint(u, point));
		u.position = point;
		System.out.println(u.unitType.name() + u.uID + " Move to " + point.x + ", " + point.y);	
		RendererAccessor.update(mapData);
		return true;
	}
	
	public static boolean useSkill(Unit source, Unit target, SkillType skill, boolean inActive){
		Unit unitOne = mapData.getUnitAt(source.position);
		Unit unitTwo = mapData.getUnitAt(target.position);
		switch (skill){
			case Attack:
				System.out.println("Attacking");
				if (SkillEngine.Attack(unitOne, unitTwo)){
					mapData.RemoveDeadUnit();
					RendererAccessor.update(mapData);
					//if (inActive) source.active = false;
					return true;
				}
				break;
			case Defend:
				System.out.println("Defend");
				if (SkillEngine.Defend(unitOne)){
					RendererAccessor.update(mapData);
					//if (inActive) source.active = false;
					return true;
				}
				break;
			case Headshot:
				System.out.println("Headshot");
				if (SkillEngine.HeadShot(unitOne, unitTwo)){
					mapData.RemoveDeadUnit();
					RendererAccessor.update(mapData);
					//if (inActive) source.active = false;
					return true;
				}
				break;
			case Heal:
				System.out.println("Heal");
				if (SkillEngine.Heal(unitOne, unitTwo)){
					RendererAccessor.update(mapData);
					//if (inActive) source.active = false;
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
	
	public static void endTurn(){
		int index = mapData._groupList.indexOf(mapData._activeGroup) + 1;
		if (index >= mapData._groupList.size())
			index = 0;
		UnitGroup previousGroup = mapData._activeGroup;
		UnitGroup currentGroup = mapData._groupList.get(index);
		for (Unit u : mapData._units){
			if (u.unitGroup == previousGroup){
				u.active = false;
				u.decreaseStatusCounter(true); // clear buffs from unit
			} else if (u.unitGroup == currentGroup) {
				u.active = true;
				u.decreaseStatusCounter(false); // clear buffs from unit
			} else {
				u.active = false;
			}
		}
		mapData._activeGroup = currentGroup;
	}
}
