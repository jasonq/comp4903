package com.comp4903.project.gameEngine.factory;

import java.util.HashMap;
import java.util.Map;

import com.comp4903.project.gameEngine.enums.TargetType;

public class SkillStats {
	public String name;
	public String description;
	public int healthCost;
	public int energyCost;
	public TargetType target;
	public int range;
	private Map<String, Integer> modifier;
	
	public SkillStats(){
		name = "";
		description = "";
		healthCost = -1;
		energyCost = -1;
		target = TargetType.None;
		range = -1;
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
