package com.comp4903.project.gameEngine.enums;

public enum IconType {
	None(-1),
	Defense(0),	
	P1(1),
	P2(2),
	Health1(3),
	Health2(4),
	Health3(5),
	Lock(6);
	
	private int code;
	
	private IconType(int c)
	{
		code = c;
	}
	
	public int getCode() {
		return code;
	}
}

