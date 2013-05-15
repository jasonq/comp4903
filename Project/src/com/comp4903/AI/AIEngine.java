package com.comp4903.AI;

import java.util.ArrayList;
import java.util.List;

import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.UnitGroup;

public class AIEngine {
	
	private static MapData _mapdata;
	private static List<Unit> PlayerUnits;
	private static List<Unit> AIUnits;
	private static InfluenceMap inMap;
	
	public static void Initialize(MapData md){
		_mapdata = md;
		inMap = new InfluenceMap(md.NumberOfColumns(), md.NumberOfRows());
	}
	
	public static void startTurn(){
		getUnitData();
		inMap.intializeInfluenceMap(PlayerUnits, AIUnits);
		//function to start turn
	}
	
	private static void getUnitData(){
		PlayerUnits = new ArrayList<Unit>();
		AIUnits = new ArrayList<Unit>();
		for(Unit u : _mapdata._units){
			if(u.unitGroup == UnitGroup.PlayerOne)
				PlayerUnits.add(u);
			if(u.unitGroup == UnitGroup.PlayerTwo)
				AIUnits.add(u);
		}
	}
}
