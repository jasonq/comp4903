package com.comp4903.project.gameEngine.enums;

public enum SkillType {
	None(-1),
	Attack(0),
	Defend(1),
	Headshot(2),
	Heal(3),
	ExposeWeakness(4), //not in use
	StimPack(5), //not in use
	Cripple(6), //not in use
	DrainingGrenade(7), //not in use
	EnergyVoid(8), //not in use
	Disable(9), //not in use
	DoubleTime(10), //not in use
	Flamethrower(11); //not in use
	
	private int code;
	
	private SkillType(int c)
	{
		code = c;
	}
	
	public int getCode() {
		return code;
	}
}
