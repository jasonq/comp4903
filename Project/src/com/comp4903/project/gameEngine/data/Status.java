package com.comp4903.project.gameEngine.data;

public class Status {
	public String name;
	public int duration;
	public int damageHealth;
	public int damageEnergy;
	public int maxHealth;
	public int maxEnergy;
	public int movement;
	public int attack;
	public int round;
	public int range;
	public int defence;
	public int accuracy;
	public boolean active;
	
	public Status(){
		duration = 1;
		damageHealth = 0;
		damageEnergy = 0;
		maxHealth = 0;
		maxEnergy = 0;
		movement = 0;
		attack = 0;
		round = 0;
		range = 0;
		defence = 0;
		accuracy = 0;
		active = true;
	}
}
