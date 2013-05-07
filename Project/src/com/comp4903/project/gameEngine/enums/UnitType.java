package com.comp4903.project.gameEngine.enums;

public enum UnitType {
	None(-1),
	Scout(0),
	Assault(1),
	Specialist(2),
	Sniper(3),
	StormTrooper(4);
	
	private int code;
	
	private UnitType(int c)
	{
		code = c;
	}
	
	public int getCode() {
		return code;
	}
}
