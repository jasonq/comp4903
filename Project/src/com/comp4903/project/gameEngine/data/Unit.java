package com.comp4903.project.gameEngine.data;
import android.graphics.Point;

import com.comp4903.project.gameEngine.enums.*;

public class Unit {
	public UnitGroup unitGroup;
	public UnitType unitType;
	public Point position;
	public WeaponType weapon;
	public ArmourType armour;
	public UnitData combatStats;
	
	public Unit(UnitType type, UnitGroup group, Point pos){
		unitType = type;
		unitGroup = group;
		position = pos;
		weapon = WeaponType.None;
		armour = ArmourType.None;
	}
	
	public void InitializeCombatStats(){
		
	}
	
	public void UpdateCombatStats(){
		
	}
}