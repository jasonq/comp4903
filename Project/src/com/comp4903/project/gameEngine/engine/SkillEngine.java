package com.comp4903.project.gameEngine.engine;

import com.comp4903.project.gameEngine.data.Unit;

public class SkillEngine {
	
	public static boolean Attack(Unit source, Unit destination){
		if (source.combatStats.hasWeapon == true){
			int round = source.combatStats.round;
			String[] result = new String[round];
			for (int i = 0; i < round; i++){
				if (HelperEngine.doesHit(source.combatStats.accuracy)){
					int damage = source.combatStats.attack - destination.combatStats.defence;
					if (damage <= 0){
						result[i] = "Blocked";
						damage = 0;
					} else {
						result[i] = "" + damage;
					}
					destination.combatStats.currentHealth -= damage;
				} else {
					result[i] = "Miss";
				}
			}
			for (String s : result){
				System.out.print(s + " ");
			}
			System.out.println();
			return true;
		} else {
			return false;
		}
	}
}
