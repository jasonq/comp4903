package com.comp4903.project.gameEngine.data;
import com.comp4903.project.gameEngine.enums.*;

public class Unit {
	private UnitGroup unitGroup;
	private UnitType unitType;
	private Point position;
	
	public Unit(UnitType type, UnitGroup group, Point pos){
		unitType = type;
		unitGroup = group;
		position = pos;
	}
}