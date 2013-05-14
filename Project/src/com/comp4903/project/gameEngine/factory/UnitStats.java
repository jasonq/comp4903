package com.comp4903.project.gameEngine.factory;

import java.util.ArrayList;
import java.util.List;

import com.comp4903.project.gameEngine.enums.ArmourType;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.WeaponType;

public class UnitStats {
	public String name;
	public int health;
	public int energy;
	public int attack;
	public int defence;
	public int movement;
	private List<WeaponType> weaponList;
	private List<ArmourType> armourList;
	private List<SkillType> skillList;
	
	public UnitStats(){
		name = "";
		health = -1;
		energy = -1;
		attack = -1;
		defence = -1;
		movement = -1;
		weaponList = new ArrayList<WeaponType>();
		armourList = new ArrayList<ArmourType>();
		skillList = new ArrayList<SkillType>();
	}
	
	public boolean addWeaponLimit(WeaponType weapon){
		if (weaponList.contains(weapon))
			return false;
		else
			weaponList.add(weapon);
		return true;
	}
	
	public boolean addArmourLimit(ArmourType armour){
		if (armourList.contains(armour))
			return false;
		else
			armourList.add(armour);
		return true;
	}
	
	public boolean addSkillLimit(SkillType skill){
		if (skillList.contains(skill))
			return false;
		else
			skillList.add(skill);
		return true;
	}
	
	public boolean isWeaponListEmpty(){
		return weaponList.isEmpty();
	}
	
	public boolean isArmourListEmpty(){
		return armourList.isEmpty();
	}
	
	public boolean isSkillListEmpty(){
		return skillList.isEmpty();
	}
	
	public boolean canUseThisWeapon(WeaponType weapon){
		return weaponList.contains(weapon);
	}
	
	public boolean canUseThisArmour(ArmourType armour){
		return armourList.contains(armour);
	}
	
	public boolean canUseThisSkill(SkillType skill){
		return skillList.contains(skill);
	}
	
	public List<SkillType> getAvailableSkills(){
		return skillList;
	}
}
