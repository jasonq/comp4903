package com.comp4903.project.gameEngine.enums;

public enum WeaponType {
	None(-1),
	Sword(0),
	SniperRifle(2),
	AutomaticRifle(3),
	Rifle(4), //not in use
	SubMachineGun(5), //not in use
	Shotgun(6); //not in use
	
	private int code;
	
	private WeaponType(int c)
	{
		code = c;
	}
	
	public int getCode() {
		return code;
	}
}
