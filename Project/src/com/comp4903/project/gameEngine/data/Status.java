package com.comp4903.project.gameEngine.data;

import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.TileType;

public class Status {
	public SkillType name;
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
	public boolean resolveAtEndOfTurn;
	public boolean active;
	
	public Status(){
		duration = 0;
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
		resolveAtEndOfTurn = true;
	}
	
	public Status(TileType tile){
		duration = 0;
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
		resolveAtEndOfTurn = true;
		switch(tile){
		case Plain:
			break;
		case Sandbag:
			defence = 2;
			break;
		case Generator:
			damageHealth = -10;
			break;
		case Building:
			break;
		default:
			break;
		}
	}
}
