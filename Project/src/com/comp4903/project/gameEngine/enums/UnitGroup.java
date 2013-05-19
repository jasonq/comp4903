package com.comp4903.project.gameEngine.enums;

public enum UnitGroup {
	None(-1),
	PlayerOne(0),
	PlayerTwo(1);
	
private int code;
	
	private UnitGroup(int c)
	{
		code = c;
	}
	
	public int getCode() {
		return code;
	}
}