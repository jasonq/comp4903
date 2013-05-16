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
	private static List<AIUnitData> AIUnits;
	private static InfluenceMap inMap;
	
	public static void Initialize(MapData md){
		_mapdata = md;
		inMap = new InfluenceMap(md.NumberOfColumns(), md.NumberOfRows());
	}
	
	//run in MyGLSurfaceView line 184
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
		inMap.intializeInfluenceMap(PlayerUnits, AIUnits);
		for(AIUnitData ai : AIUnits){
			attack(ai.unit, PlayerUnits.get(0));
		}
		GameEngine.endTurn();
	}
	
	private static void getUnitData(){
		PlayerUnits = new ArrayList<Unit>();
		AIUnits = new ArrayList<AIUnitData>();
		for(Unit u : _mapdata._units){
			if(u.unitGroup == UnitGroup.PlayerOne)
				PlayerUnits.add(u);
			if(u.unitGroup == UnitGroup.PlayerTwo)
				AIUnits.add(new AIUnitData(u));
		}
		Collections.sort(AIUnits);
	}
	
	private static void attack(Unit source, Unit target){
		if(PathFind.Distance(source.position, target.position) > source.combatStats.range){
			List<Point> pathTo = Algorithms.GetAttackPathAStar(source, target);
			Point moveTo = pathTo.get(0);
			GameEngine.moveUnit(source, moveTo, pathTo);
			while(!AnimationEngine.noForegroundAnimations()){ try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} }
		}
		if(PathFind.Distance(source.position, target.position) <= source.combatStats.range){
			GameEngine.useSkill(source, target, SkillType.Attack, true);
			while(!AnimationEngine.noForegroundAnimations()){ try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} }
		}
	}
}
