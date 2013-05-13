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
	
	public static boolean useSkill(Unit source, Unit target, SkillType skill, boolean inActive){
		Unit unitOne = mapData.getUnitAt(source.position);
		Unit unitTwo = mapData.getUnitAt(target.position);
		switch (skill){
			case Attack:
				System.out.println("Attacking");
				if (SkillEngine.Attack(unitOne, unitTwo)){
					mapData.RemoveDeadUnit();
					RendererAccessor.update(mapData);
					if (inActive) source.active = false;
					return true;
				}
				break;
			case Defence:
				System.out.println("Defend");
				if (SkillEngine.Defend(source)){
					if (inActive) source.active = false;
					return true;
				}
				break;
			case ExposeWeakness:
				System.out.println("To be implemented");
				break;
			case StimPack:
				System.out.println("To be implemented");
				break;
			case Cripple:
				System.out.println("To be implemented");
				break;
			case DrainingGrenade:
				System.out.println("To be implemented");
				break;
			case EnergyVoid:
				System.out.println("To be implemented");
				break;
			case Headshot:
				System.out.println("To be implemented");
				break;
			case Disable:
				System.out.println("To be implemented");
				break;
			case DoubleTime:
				System.out.println("To be implemented");
				break;
			case Flamethrower:
				System.out.println("To be implemented");
				break;
			default:
				return false;
		}
		return false;
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
	
	public static void clearBuffs(boolean begin){
		mapData._units.clear();
	}
}
