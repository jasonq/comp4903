package com.comp4903.project.gameEngine.data;

import android.graphics.Point;

public class UnitData {
	public int maxHealth;
	public int currentHealth;
	public int maxEnergy;
	public int currentEnergy;
	public int maxMovement;
	public int attack;
	public int round;
	public int range;
	public int defence;
	public int accuracy;
	public boolean hasWeapon;
	
	public UnitData(){
		maxHealth = -1;
		currentHealth = -1;
		maxEnergy = -1;
		currentEnergy = -1;
		maxMovement = -1;
		attack = -1;
		defence = -1;
		accuracy = -1;
		round = -1;
		hasWeapon = false;
	}
	
	public void fixHealthAndEnergy(){
		if (currentHealth > maxHealth)
			currentHealth = maxHealth;
		if (currentEnergy > maxEnergy)
			currentEnergy = maxEnergy;
		if (currentHealth < 0)
			currentHealth = 0;
		if (currentEnergy < 0)
			currentEnergy = 0;
	}
}
