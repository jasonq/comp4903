package com.comp4903.project.gameEngine.factory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class WeaponStats {
	public String name;
	public String description;
	public int damage;
	public int accuracy;
	public int rounds;
	public int range;
	private Map<String, Integer> modifier;
	
	public WeaponStats(){
		name = "No Weapon";
		description = "";
		damage = 0;
		accuracy = 0;
		rounds = 0;
		range = 0;
		modifier = new HashMap<String, Integer>();
	}

	public boolean addModifier(String key, int value){
		if (modifier.containsKey(key))
			return false;
		else
			modifier.put(key, value);
		return true;
	}
	
	public boolean hasModifier(){
		return (modifier.size() != 0);
	}

	public int getModifier(String key) {
		return modifier.get(key);
	}
}
