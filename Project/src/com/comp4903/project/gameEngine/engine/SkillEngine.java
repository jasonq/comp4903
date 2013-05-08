package com.comp4903.project.gameEngine.engine;

import com.comp4903.project.gameEngine.data.Unit;

public class SkillEngine {
	
	public static boolean Attack(Unit source, Unit destination){
		if (source.combatStats.hasWeapon == true && source.combatStats.attack >= 0){
			int round = source.combatStats.round;
			int damage = source.combatStats.attack - destination.combatStats.defence;
			if (damage < 0) damage = 0;
			destination.combatStats.currentHealth -= damage;
			return true;
		} else {
			return false;
		}
	}
}
