package com.comp4903.project.gameEngine.engine;

import com.comp4903.project.gameEngine.data.Status;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.gameEngine.factory.SkillStats;
import com.comp4903.project.graphics.RendererAccessor;

public class SkillEngine {
	
	public static boolean Attack(Unit source, Unit destination){
		if (source == null || destination == null){
			System.out.println("Missing source or destination unit");
			return false;
		}
		if (source.combatStats.hasWeapon == false){
			System.out.println("No Weapon");
			return false;
		}
		SkillStats stats = GameStats.getSkillStats(SkillType.Attack);
		
		//Skill cost, used in all skills
		source.combatStats.currentHealth -= stats.healthCost;
		source.combatStats.currentEnergy -= stats.energyCost;
		source.combatStats.fixHealthAndEnergy();
		
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
		destination.combatStats.fixHealthAndEnergy();
		RendererAccessor.attackAnimation( source, destination, result);
		
		/* Log */
		System.out.print("Damage from " + source.uID + " to " + destination.uID + ":");
		for (String s : result){
			System.out.print(s + " ");
		}
		System.out.println();
		/* Log */
		
		return true;
	}
	
	public static boolean Defend(Unit source){
		if (source == null){
			System.out.println("Missing source units");
			return false;
		}
		SkillStats stats = GameStats.getSkillStats(SkillType.Defend);
		
		//Skill cost, used in all skills
		source.combatStats.currentHealth -= stats.healthCost;
		source.combatStats.currentEnergy -= stats.energyCost;
		source.combatStats.fixHealthAndEnergy();
		
		// Applying Status effect of skill
		Status s = new Status();
		s.name = SkillType.Defend;
		s.defence = stats.getModifier("Armour").intValue();
		s.duration = stats.getModifier("Duration").intValue();
		s.resolveAtEndOfTurn = false;
		source.AddStatus(s);
		return true;
	}
	
	public static boolean HeadShot(Unit source, Unit destination){
		if (source == null || destination == null){
			System.out.println("Missing units");
			return false;
		}
		SkillStats stats = GameStats.getSkillStats(SkillType.Headshot);
		
		//Skill cost, used in all skills
		source.combatStats.currentHealth -= stats.healthCost;
		source.combatStats.currentEnergy -= stats.energyCost;
		source.combatStats.fixHealthAndEnergy();
		
		if (HelperEngine.doesHit(stats.getModifier("Chance").intValue())){
			destination.combatStats.currentHealth = 0;
			destination.combatStats.fixHealthAndEnergy();
		} else {
			Status s = new Status();
			s.name = SkillType.Headshot;
			s.accuracy = -stats.getModifier("Accuracy").intValue();
			s.duration = stats.getModifier("Duration").intValue();
			s.resolveAtEndOfTurn = true;
			destination.AddStatus(s);
		}
		return true;
	}
	
	public static boolean Heal(Unit source, Unit destination){
		if (source == null || destination == null){
			System.out.println("Missing units");
			return false;
		}
		SkillStats stats = GameStats.getSkillStats(SkillType.Heal);
		
		//Skill cost, used in all skills
		source.combatStats.currentHealth -= stats.healthCost;
		source.combatStats.currentEnergy -= stats.energyCost;
		source.combatStats.fixHealthAndEnergy();
		
		destination.combatStats.currentHealth += stats.getModifier("Heal");
		destination.combatStats.fixHealthAndEnergy();
		
		//empty method
		return false;
	}
}
