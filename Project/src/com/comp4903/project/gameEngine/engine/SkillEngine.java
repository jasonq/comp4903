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
		RendererAccessor.attackAnimation( source, destination, result);
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
		
		// Applying Status effect of skill
		Status s = new Status();
		s.name = SkillType.Defend;
		s.defence = stats.getModifier("Armour").intValue();
		s.duration = stats.getModifier("Duration").intValue();
		s.clearAtEndOfTurn = false;
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
		
		if (HelperEngine.doesHit(stats.getModifier("Chance").intValue())){
			destination.combatStats.currentHealth = 0;
		} else {
			Status s = new Status();
			s.name = SkillType.Headshot;
			s.accuracy = -stats.getModifier("Accuracy").intValue();
			s.duration = stats.getModifier("Duration").intValue();
			s.clearAtEndOfTurn = true;
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
		
		destination.combatStats.currentHealth += stats.getModifier("Heal");
		if (destination.combatStats.currentHealth > destination.combatStats.maxHealth)
			destination.combatStats.currentHealth = destination.combatStats.maxHealth;
		//empty method
		return false;
	}
}
