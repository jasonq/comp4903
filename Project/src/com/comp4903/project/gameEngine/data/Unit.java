package com.comp4903.project.gameEngine.data;
import com.comp4903.project.gameEngine.enums.*;

public class Unit {
	public UnitGroup unitGroup;
	public UnitType unitType;
	public Point position;
	
	public Unit(UnitType type, UnitGroup group, Point pos){
		unitType = type;
		unitGroup = group;
		position = pos;
	}
}