package com.comp4903.project.gameEngine.factory;

import java.util.HashMap;
import java.util.Map;

public class ArmourStats {
	public String name;
	public int defence;
	public int health;
	private Map<String, Double> modifier;
	
	public ArmourStats(){
		name = "";
		defence = -1;
		health = -1;
		modifier = new HashMap<String, Double>();
	}

	public boolean addModifier(String key, double d){
		if (modifier.containsKey(key))
			return false;
		else
			modifier.put(key, d);
		return true;
	}
	
	public boolean hasModifier(){
		return (modifier.size() != 0);
	}

	public Double getModifier(String key) {
		return modifier.get(key);
	}
}
