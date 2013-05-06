package com.comp4903.project.gameEngine.enums;

public enum TileType {
	None(-1),
	Plain(0),
	Sandbag(1),
	Generator(2),
	Building(3);
	
	private int code;
	
	private TileType(int c)
	{
		code = c;
	}
	
	public int getCode() {
		return code;
	}
}
