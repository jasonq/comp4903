package com.comp4903.project.gameEngine.enums;

public enum IconType {
	None(-1),
	Defense(0);	
	
	private int code;
	
	private IconType(int c)
	{
		code = c;
	}
	
	public int getCode() {
		return code;
	}
}

