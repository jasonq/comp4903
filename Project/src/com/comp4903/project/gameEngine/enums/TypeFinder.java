package com.comp4903.project.gameEngine.enums;

import com.comp4903.zoldcode.TileType;

public class TypeFinder {
	public static TileType findTileType(String in){
		in = in.toLowerCase();
		if (in.equals("grass"))
			return TileType.Grass;
		else if (in.equals("mountain"))
			return TileType.Mountain;
		else if (in.equals("forest"))
			return TileType.Forest;
		else
			return TileType.None;
	}
	
	public static UnitType findUnitType(String in){
		in = in.toLowerCase();
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
		else
			return WeaponType.None;
	}
	
	public static SkillType findSkillType(String in){
		in = in.toLowerCase();
		if (in.equals("hyper awareness"))
			return SkillType.HyperAwareness;
		else if (in.equals("expose weakness"))
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

}
