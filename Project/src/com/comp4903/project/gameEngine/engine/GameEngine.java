package com.comp4903.project.gameEngine.engine;

import android.graphics.Point;

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
		u.position = point;
		RendererAccessor.map.update(mapData);
		return true;
	}
	
	public static boolean useSkill(Unit source, Unit target, SkillType skill){
		Unit unitOne = mapData.getUnitAt(source.position);
		Unit unitTwo = mapData.getUnitAt(target.position);
		switch (skill){
			case Attack:
				break;
			case Defence:
				break;
			case ExposeWeakness:
				break;
			case StimPack:
				break;
			case Cripple:
				break;
			case DrainingGrenade:
				break;
			case EnergyVoid:
				break;
			case Headshot:
				break;
			case Disable:
				break;
			case DoubleTime:
				break;
			case Flamethrower:
				break;
			default:
				return false;
		}
		return true;
	}
}
