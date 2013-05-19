package com.comp4903.project.gameEngine.engine;

import com.comp4903.project.gameEngine.data.Status;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.gameEngine.factory.SkillStats;
import com.comp4903.project.gameEngine.networking.Action;
import com.comp4903.project.graphics.RendererAccessor;

public class SkillEngine {
	
	public static boolean Attack(Unit source, Unit destination, boolean network){
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
	
	public static boolean NetVAttack(Unit source, Unit destination, Action action){
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
			if (action.attack[i] < 0){
				result[i] = "Miss";
			} else if (action.attack[i] == 0){
				result[i] = "Blocked";
			} else {
				result[i] = "" + action.attack[i];
				destination.combatStats.currentHealth -= action.attack[i];
			}
		}
		destination.combatStats.fixHealthAndEnergy();
		RendererAccessor.attackAnimation( source, destination, result);
		return true;
	}
	
	public static boolean Defend(Unit source, boolean network){
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
	
	public static boolean HeadShot(Unit source, Unit destination, boolean network){
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
	
	public static boolean NetVHeadShot(Unit source, Unit destination, Action action){
		if (source == null || destination == null){
			System.out.println("Missing units");
			return false;
		}
		SkillStats stats = GameStats.getSkillStats(SkillType.Headshot);
		
		//Skill cost, used in all skills
		source.combatStats.currentHealth -= stats.healthCost;
		source.combatStats.currentEnergy -= stats.energyCost;
		source.combatStats.fixHealthAndEnergy();
		
		if (action.decisionNum == 0){
			destination.combatStats.currentHealth = 0;
			destination.combatStats.fixHealthAndEnergy();
		} else if (action.decisionNum == 1) {
			Status s = new Status();
			s.name = SkillType.Headshot;
			s.accuracy = -stats.getModifier("Accuracy").intValue();
			s.duration = stats.getModifier("Duration").intValue();
			s.resolveAtEndOfTurn = true;
			destination.AddStatus(s);
		} else {
			System.out.println("Unknown Decision for NetVHeadShot from network");
			return false;
		}
		return true;
	}
	
	public static boolean Heal(Unit source, Unit destination, boolean network){
		if (source == null || destination == null){
			System.out.println("Missing units");
			return false;
		}
		SkillStats stats = GameStats.getSkillStats(SkillType.Heal);
		
		//Skill cost, used in all skills
		source.combatStats.currentHealth -= stats.healthCost;
		source.combatStats.currentEnergy -= stats.energyCost;
		source.combatStats.fixHealthAndEnergy();
		int heal;
		if (destination.combatStats.maxHealth - destination.combatStats.currentHealth < stats.getModifier("Heal"))
			heal = destination.combatStats.maxHealth - destination.combatStats.currentHealth;
		else
			heal = stats.getModifier("Heal").intValue();
		destination.combatStats.currentHealth += stats.getModifier("Heal");
		destination.combatStats.fixHealthAndEnergy();
		RendererAccessor.healthAnimation(destination ,""+ heal);
		//empty method
		return false;
	}
}
