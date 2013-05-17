package com.comp4903.project.gameEngine.enums;

public enum GameState {
	Main_Menu(0),
	Network_Menu(1),
	Game_Screen(2), 
	Game_Over(3);
	
	private int code;
	
	private GameState(int c)
	{
		code = c;
	}
	
	public int getCode() {
		return code;
	}
}
