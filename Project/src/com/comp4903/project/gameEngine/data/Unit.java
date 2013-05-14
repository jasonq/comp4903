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
	public UnitData combatStats;
	public boolean active;
	
	public Unit(UnitType type, UnitGroup group, Point pos){
		uID = id++;
		unitType = type;
		unitGroup = group;
		position = pos;
		weapon = WeaponType.None;
		armour = ArmourType.None;
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
		active = true;
		status = new ArrayList<Status>();
		InitializeCombatStats();
	}
	
	public void AddStatus(Status s){
		status.add(s);
		UpdateCombatStats();
	}
	
	public void decreaseStatusCounter(boolean endTurn){
		List<Status> temp = new ArrayList<Status>();
		for(Status s:status){
			if (s.clearAtEndOfTurn == endTurn){
				s.duration --;
			}
			if (s.duration <= 0){
				s.active = false;
			} else {
				temp.add(s);
			}
		}
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
		for (Status s: status){
			if (s.active){
				combatStats.maxHealth += s.maxHealth;
				combatStats.maxEnergy += s.maxEnergy;
				combatStats.currentHealth -= s.damageHealth;
				combatStats.currentEnergy -= s.damageEnergy;
				combatStats.maxMovement += s.movement;
				combatStats.attack += s.attack;
				combatStats.round += s.round;
				combatStats.range += s.range;
				combatStats.defence += s.defence;
				combatStats.accuracy += s.accuracy;
			}
		}
	}
}