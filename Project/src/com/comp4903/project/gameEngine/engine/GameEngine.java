package com.comp4903.project.gameEngine.engine;

import android.graphics.Point;

import com.comp4903.pathfind.PathFind;
import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.SkillType;
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
	
	public static void endTurn(){
		int index = mapData._groupList.indexOf(mapData._activeGroup);
		if (index >= (mapData._groupList.size() - 1))
			index = 0;
		mapData._activeGroup = mapData._groupList.get(index);
		for (Unit u : mapData._units){
			if (u.unitGroup == mapData._activeGroup)
				u.active = true;
			else
				u.active = false;
		}
	}
	
	public static boolean useSkill(Unit source, Unit target, SkillType skill){
		Unit unitOne = mapData.getUnitAt(source.position);
		Unit unitTwo = mapData.getUnitAt(target.position);
		switch (skill){
			case Attack:
				System.out.println("Attacked");
				SkillEngine.Attack(unitOne, unitTwo);
				break;
			case Defend:
				System.out.println("Defend");
				//SkillEngine.Defend(unitOne);
				break;
			case Headshot:
				System.out.println("Headshot");
				break;
			case Heal:
				System.out.println("Heal");
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
				break;
			default:
				return false;
		}
		return true;
	}
}
