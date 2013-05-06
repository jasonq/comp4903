package com.comp4903.project.gameEngine.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.comp4903.project.gameEngine.enums.*;
import com.comp4903.project.parser.*;

public class GameStats {
	static Map<WeaponType, WeaponStats> weaponList;
	static Map<SkillType, SkillStats> skillList;
	static Map<ArmourType, ArmourStats> armourList;
	static Map<UnitType, UnitStats> unitList;
	
	public static void InitializeWeaponData(InputStream weapon){
		try {
			weaponList = XMLParser.readWeaponInputXML(weapon);
		} catch (IOException e) {
			System.out.println("IO Exception while parsing weapon List");
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			System.out.println("Parser Exception while parsing weapon List");
			e.printStackTrace();
		}
		System.out.println("Weapon Stats Initialized");
	}
	
	public static void InitializeSkillData(InputStream skill){
		try {
			skillList = XMLParser.readSkillInputXML(skill);
		} catch (IOException e) {
			System.out.println("IO Exception while parsing skill List");
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			System.out.println("Parser Exception while parsing skill List");
			e.printStackTrace();
		}
		System.out.println("Skill Stats Initialized");
	}
	
	public static void InitializeArmourData(InputStream armour){
		try {
			armourList = XMLParser.readArmourInputXML(armour);
		} catch (IOException e) {
			System.out.println("IO Exception while parsing armour List");
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			System.out.println("Parser Exception while parsing armour List");
			e.printStackTrace();
		}
		System.out.println("Armour Stats Initialized");
	}
	
	public static void InitializeUnitData(InputStream unit){
		try {
			unitList = XMLParser.readUnitInputXML(unit);
		} catch (IOException e) {
			System.out.println("IO Exception while parsing unit List");
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			System.out.println("Parser Exception while parsing unit List");
			e.printStackTrace();
		}
		System.out.println("Unit Stats Initialized");
	}

	
	public static WeaponStats getWeaponStats(WeaponType type){
		if (type == WeaponType.None)
			return new WeaponStats();
		return weaponList.get(type);
	}
	
	public static SkillStats getSkillStats(SkillType type){
		if (type == SkillType.None)
			return new SkillStats();
		return skillList.get(type);
	}
	
	public static ArmourStats getArmourStats(ArmourType type){
		if (type == ArmourType.None)
			return new ArmourStats();
		return armourList.get(type);
	}
	
	public static UnitStats getUnitStats(UnitType type){
		if (type == UnitType.None)
			return new UnitStats();
		return unitList.get(type);
	}
	
	public static void PrintWeaponList(){
		System.out.println("Num of Weapons: " + weaponList.size());
		Iterator<WeaponType> itr = weaponList.keySet().iterator();
		while(itr.hasNext()){
			WeaponStats stats = weaponList.get(itr.next());
			System.out.println("Weapon: " + stats.name);
			System.out.println("Description: " + stats.description);
			System.out.println("Damage: " + stats.damage);
			System.out.println("Has Mod: " + stats.hasModifier());
			System.out.println();
		}
	}
	
	public static void PrintSkillList(){
		System.out.println("Num of Skills: " + skillList.size());
		Iterator<SkillType> itr = skillList.keySet().iterator();
		while(itr.hasNext()){
			SkillStats stats = skillList.get(itr.next());
			System.out.println("Skill: " + stats.name);
			System.out.println("Description: " + stats.description);
			System.out.println("Energy: " + stats.energyCost);
			System.out.println("Has Mod: " + stats.hasModifier());
			System.out.println();
		}
	}
	
	public static void PrintArmourList(){
		System.out.println("Num of Armours: " + armourList.size());
		Iterator<ArmourType> itr = armourList.keySet().iterator();
		while(itr.hasNext()){
			ArmourStats stats = armourList.get(itr.next());
			System.out.println("Armour: " + stats.name);
			System.out.println("Defence: " + stats.defence);
			System.out.println("Health: " + stats.health);
			System.out.println("Has Mod: " + stats.hasModifier());
			System.out.println();
		}
	}
	
	public static void PrintUnitList(){
		System.out.println("Num of Units: " + unitList.size());
		Iterator<UnitType> itr = unitList.keySet().iterator();
		while(itr.hasNext()){
			UnitStats stats = unitList.get(itr.next());
			System.out.println("Unit: " + stats.name);
			System.out.println("Defence: " + stats.defence);
			System.out.println("Health: " + stats.health);
			System.out.println("Rifle Use: " + stats.canUseThisWeapon(WeaponType.Rifle));
			System.out.println("Medium Armour Use: " + stats.canUseThisArmour(ArmourType.MediumArmour));
			System.out.println("SkillList: " + stats.getAvailableSkills().size());
			System.out.println();
		}
	}
}
