package com.comp4903.project.gameEngine.enums;

public class TypeFinder {
	public static TileType findTileType(String in){
		in = in.toLowerCase();
		if (in.equals("plain"))
			return TileType.Plain;
		else if (in.equals("sandbag"))
			return TileType.Sandbag;
		else if (in.equals("generator"))
			return TileType.Generator;
		else if (in.equals("building"))
			return TileType.Building;
		else
			return TileType.None;
	}
	
	public static UnitType findUnitType(String in){
		in = in.toLowerCase();
		if (in.equals("scout"))
			return UnitType.Scout;
		else if (in.equals("assault"))
			return UnitType.Assault;
		else if (in.equals("specialist"))
			return UnitType.Specialist;
		else if (in.equals("sniper"))
			return UnitType.Sniper;
		else if (in.equals("storm trooper"))
			return UnitType.StormTrooper;
		else if (in.equals("sword master"))
			return UnitType.SwordMaster;
		else if (in.equals("medic"))
			return UnitType.Medic;
		else
			return UnitType.None;
	}
	
	public static WeaponType findWeaponType(String in){
		in = in.toLowerCase();
		if (in.equals("rifle"))
			return WeaponType.Rifle;
		else if (in.equals("sub machine gun"))
			return WeaponType.SubMachineGun;
		else if (in.equals("automatic rifle"))
			return WeaponType.AutomaticRifle;
		else if (in.equals("sniper rifle"))
			return WeaponType.SniperRifle;
		else if (in.equals("shotgun"))
			return WeaponType.Shotgun;
		else if (in.equals("sword"))
			return WeaponType.Sword;
		else
			return WeaponType.None;
	}
	
	public static SkillType findSkillType(String in){
		in = in.toLowerCase();
		if (in.equals("expose weakness"))
			return SkillType.ExposeWeakness;
		else if (in.equals("stim pack"))
			return SkillType.StimPack;
		else if (in.equals("cripple"))
			return SkillType.Cripple;
		else if (in.equals("draining grenade"))
			return SkillType.DrainingGrenade;
		else if (in.equals("energy void"))
			return SkillType.EnergyVoid;
		else if (in.equals("headshot"))
			return SkillType.Headshot;
		else if (in.equals("disable"))
			return SkillType.Disable;
		else if (in.equals("double time"))
			return SkillType.DoubleTime;
		else if (in.equals("flamethrower"))
			return SkillType.Flamethrower;
		else if (in.equals("attack"))
			return SkillType.Attack;
		else if (in.equals("defend"))
			return SkillType.Defend;
		else if (in.equals("heal"))
			return SkillType.Heal;
		else if (in.equals("grab"))
			return SkillType.Grab;
		else
			return SkillType.None;
	}
	
	public static TargetType findTargetType(String in){
		in = in.toLowerCase();
		if (in.equals("self"))
			return TargetType.Self;
		else if (in.equals("enemy"))
			return TargetType.Enemy;
		else if (in.equals("all"))
			return TargetType.All;
		else if (in.equals("friendly"))
			return TargetType.Friendly;
		else
			return TargetType.None;
	}

	public static ArmourType findArmourType(String in){
		in = in.toLowerCase();
		if (in.equals("light armour"))
			return ArmourType.LightArmour;
		else if (in.equals("medium armour"))
			return ArmourType.MediumArmour;
		else if (in.equals("heavy armour"))
			return ArmourType.HeavyArmour;
		else
			return ArmourType.None;
	}
	
	public static UnitGroup findUnitGroup(String in){
		in = in.toLowerCase();
		if (in.equals("player one"))
			return UnitGroup.PlayerOne;
		else if (in.equals("player two"))
			return UnitGroup.PlayerTwo;
		else
			return UnitGroup.None;
	}
	
	public static ActionType findActionType(String in){
		in = in.toLowerCase();
		if (in.equals("attack")){
			return ActionType.Attack;
		} else if (in.equals("defend")){
			return ActionType.Defend;
		} else
			return ActionType.None;
	}
}
