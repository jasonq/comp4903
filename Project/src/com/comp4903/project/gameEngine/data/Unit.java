package com.comp4903.project.gameEngine.data;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import com.comp4903.project.gameEngine.enums.*;
import com.comp4903.project.gameEngine.factory.ArmourStats;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.gameEngine.factory.UnitStats;
import com.comp4903.project.gameEngine.factory.WeaponStats;

public class Unit {
	private static int id = 0;
	public int uID;
	public UnitGroup unitGroup;
	public UnitType unitType;
	public Point position;
	public WeaponType weapon;
	public ArmourType armour;
	private List<Status> status;
	public Status tileStatus;
	public UnitData combatStats;
	public boolean active;
	
	public Unit(UnitType type, UnitGroup group, Point pos){
		uID = id++;
		unitType = type;
		unitGroup = group;
		position = pos;
		weapon = WeaponType.None;
		armour = ArmourType.None;
		tileStatus = new Status(TileType.None);
		active = true;
		status = new ArrayList<Status>();
		InitializeCombatStats();
	}
	
	public Unit(UnitType type, UnitGroup group, Point pos, WeaponType weapon, ArmourType armour){
		uID = id++;
		unitType = type;
		unitGroup = group;
		position = pos;
		this.weapon = weapon;
		this.armour = armour;
		tileStatus = new Status(TileType.None);
		active = true;
		status = new ArrayList<Status>();
		InitializeCombatStats();
	}
	
	public UnitStats getUnitStats(){
		return GameStats.getUnitStats(unitType);
	}
	
	public void AddStatus(Status s){
		status.add(s);
		UpdateCombatStats();
	}
	
	public void resolveStatus(boolean endTurn){
		List<Status> temp = new ArrayList<Status>();
		for(Status s:status){
			if (s.resolveAtEndOfTurn == endTurn){
				s.duration --;
				combatStats.currentHealth -= s.damageHealth;
				combatStats.fixHealthAndEnergy();
			}
			if (s.duration <= 0){
				s.active = false;
			} else {
				temp.add(s);
			}
		}
		if (tileStatus.resolveAtEndOfTurn == endTurn){
			combatStats.currentHealth -= tileStatus.damageHealth;
			combatStats.currentEnergy -= tileStatus.damageEnergy;
			combatStats.fixHealthAndEnergy();
		}
		UpdateCombatStats();
		status = temp;
	}
	
	public void InitializeCombatStats(){
		UnitStats stats = GameStats.getUnitStats(unitType);
		ArmourStats armourStats = GameStats.getArmourStats(armour);
		WeaponStats weaponStats = GameStats.getWeaponStats(weapon);
		combatStats = new UnitData();
		combatStats.maxHealth = stats.health + armourStats.health;
		combatStats.maxEnergy = stats.energy;
		combatStats.maxMovement = stats.movement;
		if (weapon != WeaponType.None)
			combatStats.hasWeapon = true;
		combatStats.attack = weaponStats.damage;
		combatStats.defence = armourStats.defence;
		combatStats.accuracy = weaponStats.accuracy;
		combatStats.round = weaponStats.rounds;
		combatStats.range = weaponStats.range;
		combatStats.currentHealth = combatStats.maxHealth;
		combatStats.currentEnergy = combatStats.maxEnergy;
	}
	
	public void UpdateCombatStats(){
		UnitStats stats = GameStats.getUnitStats(unitType);
		ArmourStats armourStats = GameStats.getArmourStats(armour);
		WeaponStats weaponStats = GameStats.getWeaponStats(weapon);
		combatStats.maxHealth = stats.health + armourStats.health;
		combatStats.maxEnergy = stats.energy;
		combatStats.maxMovement = stats.movement;
		combatStats.attack = weaponStats.damage;
		combatStats.defence = armourStats.defence;
		combatStats.accuracy = weaponStats.accuracy;
		combatStats.round = weaponStats.rounds;
		combatStats.range = weaponStats.range;
		for (Status s: status){
			if (s.active){
				combatStats.maxHealth += s.maxHealth;
				combatStats.maxEnergy += s.maxEnergy;
				combatStats.maxMovement += s.movement;
				combatStats.attack += s.attack;
				combatStats.round += s.round;
				combatStats.range += s.range;
				combatStats.defence += s.defence;
				combatStats.accuracy += s.accuracy;
			}
		}
		if (tileStatus.active){
			combatStats.maxHealth += tileStatus.maxHealth;
			combatStats.maxEnergy += tileStatus.maxEnergy;
			combatStats.maxMovement += tileStatus.movement;
			combatStats.attack += tileStatus.attack;
			combatStats.round += tileStatus.round;
			combatStats.range += tileStatus.range;
			combatStats.defence += tileStatus.defence;
			combatStats.accuracy += tileStatus.accuracy;
		}
	}
	
	public void displayCombatStats(){
		System.out.println("Unit: " + this.uID);
		System.out.println("Health: " + combatStats.currentHealth);
		System.out.println("Energy: " + combatStats.currentEnergy);
		System.out.println("Attack: " + combatStats.attack);
		System.out.println("Defence: " + combatStats.defence);
		System.out.println("Movement: " + combatStats.maxMovement);
		System.out.println("Round: " + combatStats.round);
		System.out.println("Accuracy: " + combatStats.accuracy);
	}
}