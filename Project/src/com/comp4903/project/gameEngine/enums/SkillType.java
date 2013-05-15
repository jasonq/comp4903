package com.comp4903.project.gameEngine.enums;

public enum SkillType {
	None(-1),
	Attack(0),
	Defend(1),
	Grab(2),
	Headshot(3),
	Heal(4),
	ExposeWeakness(5), //not in use
	StimPack(6), //not in use
	Cripple(7), //not in use
	DrainingGrenade(8), //not in use
	EnergyVoid(9), //not in use
	Disable(10), //not in use
	DoubleTime(11), //not in use
	Flamethrower(12); //not in use
	
	private int code;
	
	private SkillType(int c)
	{
		code = c;
	}
	
	public int getCode() {
		return code;
	}
}
