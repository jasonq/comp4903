package com.comp4903.project.gameEngine.engine;

import android.graphics.Point;

import com.comp4903.pathfind.PathFind;
import com.comp4903.project.gameEngine.data.Status;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.ActionType;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.gameEngine.factory.SkillStats;
import com.comp4903.project.gameEngine.networking.Action;
import com.comp4903.project.graphics.RendererAccessor;
import com.comp4903.project.network.Networking;

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
		int[] intResult = new int[round];
		String[] result = new String[round];
		for (int i = 0; i < round; i++){
			if (HelperEngine.doesHit(source.combatStats.accuracy)){
				int damage = source.combatStats.attack - destination.combatStats.defence;
				if (damage <= 0){
					result[i] = "Blocked";
					intResult[i] = 0;
					damage = 0;
				} else {
					result[i] = "" + damage;
					intResult[i] = damage;
				}
				destination.combatStats.currentHealth -= damage;
			} else {
				result[i] = "Miss";
				intResult[i] = -1;
			}
		}
		destination.combatStats.fixHealthAndEnergy();
		RendererAccessor.attackAnimation( source, destination, result);
		
		if (network){
			Action a = new Action();
			a.action = ActionType.Attack;
			a.uIDOne = source.uID;
			a.uIDTwo = destination.uID;
			a.numOfAttacks = round;
			a.attack = intResult;
			Networking.send(a.getActionMessage());
		}
		
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
		
		RendererAccessor.defendAnimation(source);
		
		if (network){
			Action a = new Action();
			a.action = ActionType.Defend;
			a.uIDOne = source.uID;
			Networking.send(a.getActionMessage());
		}
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
		
		int choice = -1;
		if (HelperEngine.doesHit(stats.getModifier("Chance").intValue())){
			destination.combatStats.currentHealth = 0;
			destination.combatStats.fixHealthAndEnergy();
			choice = 0;
		} else {
			Status s = new Status();
			s.name = SkillType.Headshot;
			s.accuracy = -stats.getModifier("Accuracy").intValue();
			s.duration = stats.getModifier("Duration").intValue();
			s.resolveAtEndOfTurn = true;
			destination.AddStatus(s);
			choice = 1;
		}

		String[] s = new String[1];
		if (choice == 0)
			s[0] = "HeadShot";
		else
			s[0] = "Blinded";
		RendererAccessor.headShotAnimation(source, destination, s);
		
		if (network){
			Action a = new Action();
			a.action = ActionType.Headshot;
			a.uIDOne = source.uID;
			a.uIDTwo = destination.uID;
			a.decisionNum = choice;
			Networking.send(a.getActionMessage());
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
		

		String[] s = new String[1];
		if (action.decisionNum == 0)
			s[0] = "HeadShot";
		else
			s[0] = "Blinded";
		RendererAccessor.headShotAnimation(source, destination, s);
		
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
		
		if (network){
			Action a = new Action();
			a.action = ActionType.Heal;
			a.uIDOne = source.uID;
			a.uIDTwo = destination.uID;
			Networking.send(a.getActionMessage());
		}
		
		RendererAccessor.healthAnimation(destination ,""+ heal);
		return false;
	}
	
	public static boolean Grab(Unit source, Unit destination, boolean network){
		System.out.println("In Grab Function.");
		if (source == null || destination == null){
			System.out.println("Missing units");
			return false;
		}
		System.out.println("Has Unit");
		SkillStats stats = GameStats.getSkillStats(SkillType.Grab);
		
		//Skill cost, used in all skills
		source.combatStats.currentHealth -= stats.healthCost;
		source.combatStats.currentEnergy -= stats.energyCost;
		source.combatStats.fixHealthAndEnergy();

		System.out.println("Finding Point");
		Point p = PathFind.TractorBeam(source, destination);
		System.out.println("Destination Unit move to:" + p.x + ", " + p.y);
		destination.position = p;
		
		//********* Code Copied from Attack **************//
		String[] result = new String[0];
		if (PathFind.Distance(source.position, destination.position) <= 1){
			stats = GameStats.getSkillStats(SkillType.Attack);
			
			//Skill cost, used in all skills
			source.combatStats.currentHealth -= stats.healthCost;
			source.combatStats.currentEnergy -= stats.energyCost;
			source.combatStats.fixHealthAndEnergy();
			
			int round = source.combatStats.round;
			int[] intResult = new int[round];
			result = new String[round];
			for (int i = 0; i < round; i++){
				if (HelperEngine.doesHit(source.combatStats.accuracy)){
					int damage = source.combatStats.attack - destination.combatStats.defence;
					if (damage <= 0){
						result[i] = "Blocked";
						intResult[i] = 0;
						damage = 0;
					} else {
						result[i] = "" + damage;
						intResult[i] = damage;
					}
					destination.combatStats.currentHealth -= damage;
				} else {
					result[i] = "Miss";
					intResult[i] = -1;
				}
			}
			destination.combatStats.fixHealthAndEnergy();					
		}
		
		RendererAccessor.grabAnimation(source, destination, p, result);
		
		if (network){
			Action a = new Action();
			a.action = ActionType.Grab;
			a.uIDOne = source.uID;
			a.uIDTwo = destination.uID;
			Networking.send(a.getActionMessage());
		}
		return true;
	}
}
