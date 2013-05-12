package com.comp4903.project.gameEngine.engine;

import com.comp4903.project.gameEngine.data.Status;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.gameEngine.factory.SkillStats;

public class SkillEngine {
	
	public static boolean Attack(Unit source, Unit destination){
		if (source == null || destination == null){
			System.out.println("Missing units");
			return false;
		} else if (source.combatStats.hasWeapon == true){
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
			System.out.print("Damage from " + source.uID + " to " + destination.uID + ":");
			for (String s : result){
				System.out.print(s + " ");
			}
			System.out.println();
			return true;
		} else {
			System.out.println("No Weapon");
			return false;
		}
	}
	
	public static boolean Defend(Unit source){
		if (source == null)
			return false;
		SkillStats stats = GameStats.getSkillStats(SkillType.Defence);
		Status s = new Status();
		s.name = SkillType.Defence;
		s.defence = stats.getModifier("Armour").intValue();
		s.duration = stats.getModifier("Duration").intValue();
		s.clearAtEndOfTurn = false;
		source.AddStatus(s);
		return true;
	}
}
