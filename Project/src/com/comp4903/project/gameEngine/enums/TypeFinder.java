package com.comp4903.project.gameEngine.enums;

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
		if (in.equals("soldier"))
			return UnitType.Soldier;
		else if (in.equals("knight"))
			return UnitType.Knight;
		else if (in.equals("mage"))
			return UnitType.Mage;
		else if (in.equals("archer"))
			return UnitType.Archer;
		else
			return UnitType.None;
	}

}
