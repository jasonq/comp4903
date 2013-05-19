package com.comp4903.project.gameEngine.enums;

public enum ActionType {
	None(-1),
	Attack(0),
	Defend(1),
	Grab(2),
	Headshot(3),
	Heal(4),
	Move(5);
	
	private int code;
	
	private ActionType(int c)
	{
		code = c;
	}
	
	public int getCode() {
		return code;
	}
}
