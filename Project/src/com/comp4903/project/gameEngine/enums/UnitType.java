package com.comp4903.project.gameEngine.enums;

public enum UnitType {
	None(-1),
	SwordMaster(0),
	Sniper(1),
	Medic(2),
	Scout(3), //not in use
	Assault(4), //not in use
	Specialist(5), //not in use
	StormTrooper(6); //not in use
	
	private int code;
	
	private UnitType(int c)
	{
		code = c;
	}
	
	public int getCode() {
		return code;
	}
}
