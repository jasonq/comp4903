package com.comp4903.AI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Point;
import android.util.Log;

import com.comp4903.pathfind.Algorithms;
import com.comp4903.pathfind.PathFind;
import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.engine.GameEngine;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.UnitGroup;
import com.comp4903.project.graphics.animation.AnimationEngine;
import com.comp4903.project.network.Networking;

public class AIEngine {
	
	private static MapData _mapdata;
	private static List<Unit> PlayerUnits;
	private static List<Unit> AIUnits;
	private static InfluenceMap AIinMap;
	private static InfluenceMap PLAYERinMap;
	
	public static void Initialize(MapData md){
		_mapdata = md;
		AI.Intialize(md);
		AIinMap = new InfluenceMap(md.NumberOfColumns(), md.NumberOfRows());
		PLAYERinMap = new InfluenceMap(md.NumberOfColumns(), md.NumberOfRows());
		getUnitData();
	}
	
	//run in TouchGesture line 175
	public static void startTurn(){
		Thread aiThread = new Thread()
		{			
			public void run(){
				processTurn();
			}			
		};
		aiThread.start();
	}
	public static void processTurn(){
		getUnitData();
		for(Unit ai : AIUnits){
			AIinMap.getInfluenceMap(AIUnits);
			PLAYERinMap.getInfluenceMap(PlayerUnits);
			ai.aiData.intializeState(ai, ai.unitType, PlayerUnits);
			switch(ai.unitType){
			case SwordMaster:
				SMBehaviour.think(ai, AIinMap, PLAYERinMap, AIUnits, PlayerUnits);
				break;
			case Sniper:
				SPBehaviour.think(ai, AIinMap, PLAYERinMap, AIUnits, PlayerUnits);
				break;
			case Medic:
				MDBehaviour.think(ai, AIinMap, PLAYERinMap, AIUnits, PlayerUnits);
				break;
			}
		}
		Log.d("AIEngine", "End Turn");
		GameEngine.endTurn(false);
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
		//Collections.sort(AIUnits);
	}
}
