package com.comp4903.project.gameEngine.engine;

import java.util.Random;

public class HelperEngine {
	private static Random randomGenerator;
	private static void InitializeRandomGenerator(){
		if (randomGenerator == null)
			randomGenerator = new Random();
	}
	/*
	 * Generate a random damage modifier based on the percentage of the real damage
	 * To generate a modifier for 17 damage with +/- 20%, input damage = 17, mod = 0.2
	 * will generate a minimum  of +1/-1 mod as long as damage > 0
	 * @post returns 0 if mod > 1 or mod < 0 or damage <= 0, else returns the result
	 */
	public static int getDamageMod(int damage, double mod){
		InitializeRandomGenerator();
		if (mod > 1 || mod < 0 || damage <= 0)
			return 0;
		int offSet = (int)(damage * mod);
		if (offSet < 1)
			offSet = 1;
		return randomGenerator.nextInt(offSet * 2 + 1) - offSet;
	}
	
	public static boolean doesHit(int chance){
		InitializeRandomGenerator();
		if (chance >= 100){
			return true;
		} else if (chance < 0){
			return false;
		} else {
			if (randomGenerator.nextInt(100) < chance)
				return true;
			else
				return false;
		}
	}
}
