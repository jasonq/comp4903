package com.comp4903.project.graphics;

import android.graphics.Point;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.comp4903.pathfind.PathFind;
import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.engine.GameEngine;
import com.comp4903.project.gameEngine.enums.ColorType;
import com.comp4903.project.gameEngine.enums.GameState;
import com.comp4903.project.gameEngine.enums.IconType;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.UnitGroup;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.gameEngine.factory.SkillStats;
import com.comp4903.project.network.Networking;

public class TouchGesture extends GestureDetector.SimpleOnGestureListener {
	
	public GLRenderer mRenderer;

	private boolean pickControlledUnit = false;
	private boolean finishMoving = false;
	private boolean chooseAction = false;
	private  MapData  mapData = null;
	private Unit currentUnit = null;
	private int decision = -1;
	
	public TouchGesture(GLRenderer mgl,MapData md){
		super();
		mRenderer = mgl;
		mapData = md;
	}
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Log.d("TAG", "Double Tap Detected ...");
		return true;
	}
	@Override
	public boolean onSingleTapConfirmed (MotionEvent e){
		switch(GLRenderer.state){
		case Game_Screen:
			handle_Game_Screen((int)e.getX(),(int)e.getY());
			break;
		case Main_Menu:
			handle_Main_Menu((int)e.getX(),(int)e.getY());
			break;
		case Network_Menu:
			handle_NetWork((int)e.getX(),(int)e.getY());
			break;
		case Game_Over:
			Log.d("Debug","-----------");
			Log.d("Debug","Game Over state");
			Log.d("Debug", "xTop = " + mRenderer.gov.xTop + " xBot = " + mRenderer.gov.xBot 
					+ "yTop = " + mRenderer.gov.yTop + "yBot = " + mRenderer.gov.yBot  );
			
			Log.d("Debug", "x = " + e.getX() + " y = " + e.getY()   );
			handle_Game_Over((int)e.getX(),(int)e.getY());
			break;
		}
		return true;
	}
	
	
	public void handle_NetWork(int x, int y){
		int r = mRenderer.network.checkButton((int)x, (int)y);
		if(r != -1){
			if(r == 1)
				//handle join\
				;
			else if(r == 2)
				//handle host
				;
		}
		
	}
	public void handle_Game_Over(int x, int y){
		if(mRenderer.gov.checkPressingMeu(x, y))
			GLRenderer.state = GameState.Main_Menu;
	}
	/*
	 * Hanlde touch event when we are in main menu state
	 */
	public void handle_Main_Menu(int x , int y){
		int result = mRenderer.setSelectMainMenu(x, y);
		int a = 2;
		if(result != -1){
			if(result == 0)
				GLRenderer.state = GameState.Game_Screen;
			else if(result == 1){
				GLRenderer.state = GameState.Network_Menu;
				startNetworking();
			}else if(result == 2){
				GLRenderer.state = GameState.Game_Over;
				mRenderer.gov.UpdateWinner(UnitGroup.PlayerOne);
			}
			
		}
		mRenderer.mm.selected = -1;
	}
	/*
	 * Handle touch event when we are in game screen state
	 */
	public void handle_Game_Screen(int x, int y){

		Log.d("TAG", "Single Tap Detected ...");
		int touchMenu = mRenderer.setSelectedHUD(x, y);
		boolean pressCancel = mRenderer.headsUpDisplay.action.checkPressingCancel(x, y);
		
		if(pressCancel)
			Log.d("Debug","Cancel Pressed");	
		Point pickPoint = mRenderer.pick(x, y);
		boolean pressEnd = mRenderer.headsUpDisplay.checkPressingEndTurn(x, y);


		//check if out of bounce

		//if(pickPoint.x == -1 && pickPoint.y == -1 && touchMenu == -1 && !pressCancel && !pressEnd){
		//	ResetGUI();
		//	return;
		//}		
		if(pressEnd && !pickControlledUnit){//might put condition if this is player turn
			//end turn code goes here
			//Log.d("MyGLSurfaceView", "End turn pressed");
			GameEngine.endTurn();
			if(mapData._activeGroup == UnitGroup.PlayerTwo){ //need check for if singleplayer or multiplayer
				//AIEngine.startTurn();
			}
			if(mapData._activeGroup == UnitGroup.PlayerOne)
				RendererAccessor.floatingIcon(GLRenderer.GLwidth/2 - 125, GLRenderer.GLheight/10, 0, 0, 100, null, IconType.P1);
			else if(mapData._activeGroup == UnitGroup.PlayerTwo)
				RendererAccessor.floatingIcon(GLRenderer.GLwidth/2 - 125, GLRenderer.GLheight/10, 0, 0, 100, null, IconType.P2);

			ResetGUI();			
		}

		Unit pickUnit = mapData.getUnitAt(pickPoint);
		
		if(handleClickBox(touchMenu,pressCancel))
			return;
		
		if(pickUnit != null){
			//Log.d("MyGLSurfaceView", "Active Group:" + mapData._activeGroup);
			if(pickUnit.unitGroup == mapData._activeGroup){
				handleControlledUnit(pickUnit);
			}
			//handle if we pick enemy unit
			else {

				handleEnemyUnint(pickUnit);
			}

		}else{
			handlePickEmpty(x,y,pickPoint,touchMenu,pressCancel);
		}			
	}
	/*
	 * Handle clicking on the menu
	 * only happens when pressing inside the action box
	 */
	public boolean handleClickBox(int touchMenu, boolean pressCancel){
		if(!pressCancel && touchMenu == -1)
			return false;
		if(pickControlledUnit && finishMoving && !chooseAction && touchMenu != -1 && !pressCancel){
			decision = touchMenu;
			if(decision == 1){
				mRenderer.headsUpDisplay.updateHUD(true, true, true, false);//maintain the HUD
				PathFind.DisplayUnitAttackBox(currentUnit);//show the attack range
				chooseAction = true;
			}else if(decision == 2){//defending
				mRenderer.headsUpDisplay.updateHUD(true, true, true, false);//maintain the HUD
				SkillStats stats = GameStats.getSkillStats(SkillType.Defend);
				PathFind.DisplayUnitFriendBox(currentUnit, stats.range);
				chooseAction = true;
			}else if(decision == 3){//call skills
				
				mRenderer.headsUpDisplay.updateHUD(true, true, true, false);//maintain the HUD
				SkillStats stats = new SkillStats();
				if (currentUnit.getUnitStats().canUseThisSkill(SkillType.Headshot)){
					if(GameEngine.canCastSkill(currentUnit, SkillType.Headshot)){
						stats = GameStats.getSkillStats(SkillType.Headshot);
						PathFind.DisplayUnitEnemyBox(currentUnit, stats.range);
						//Log.d("MyGLSurfaceView","Find HeadShot");
					} else {
						RendererAccessor.floatingText(300, 170, 0, -1, 100, ColorType.Blue, "n", "Not Enough Energy");
					}
				} else if (currentUnit.getUnitStats().canUseThisSkill(SkillType.Heal)){
					if (GameEngine.canCastSkill(currentUnit, SkillType.Heal)){
						stats = GameStats.getSkillStats(SkillType.Heal);
						PathFind.DisplayUnitFriendBox(currentUnit, stats.range);
						//Log.d("MyGLSurfaceView", "Find Heal");
					} else {
						RendererAccessor.floatingText(300, 170, 0, -1, 100, ColorType.Blue, "n", "Not Enough Energy");
					}
				} else {
					//Log.d("MyGLSurfaceView", "Find Nothing");
				}//show the attack range
				
				chooseAction = true;
			}
			return true;
		}else if(pickControlledUnit && finishMoving && chooseAction && touchMenu == -1 && pressCancel){
			mRenderer.headsUpDisplay.updateHUD(true, true, false, false);//show cancel is enable, hide the rest of actionbox
			chooseAction = false;
			decision = -1;
			mRenderer.headsUpDisplay.action.menuSelected = -1;
			mapData.clearBoxes();
			RendererAccessor.update(mapData);
			return true;
		}else if (pickControlledUnit && pressCancel && !finishMoving){
			ResetGUI();
			return true;
		}else
			return false;

	}
	/*
	 * handle when picking our own unit
	 * IF current unit is not null which we have picked 
	 * 		IF the unit is the same as current unit Hence we are tapping on the same location
	 * 			set the finish moving is true which is stay at the same location
	 * 			enable and disable hud
	 * 			clear the movement box transfer to choosing action phase
	 * 
	 * ELSE IF current is null, havent picked yet
	 * 		handle picking new unit
	 */
	public void handleControlledUnit(Unit pickUnit){
		if(currentUnit != null){
			if(currentUnit.uID == pickUnit.uID){
				if(finishMoving &&  chooseAction && decision == 2){
					GameEngine.useSkill(currentUnit, null, SkillType.Defend, true);
					Log.d("Debug", "I Defend");
					ResetGUI();
					return;
				}else if(finishMoving && chooseAction && decision == 3 &&
						 currentUnit.getUnitStats().canUseThisSkill(SkillType.Heal)){
					GameEngine.useSkill(currentUnit, currentUnit, SkillType.Heal, true);
					Log.d("Debug", "I heal myself");
					ResetGUI();
					return;
				}
				finishMoving = true;//finish moving for the currentunit
				mapData.clearBoxes();//clear the movement box
				RendererAccessor.update(mapData);//update mapdata
				mRenderer.headsUpDisplay.updateHUD(true, true, false, false);//update hud disable cancel button
				return;
			}else{
				if(decision == 3 &&  currentUnit.getUnitStats().canUseThisSkill(SkillType.Heal)){
					GameEngine.useSkill(currentUnit, pickUnit, SkillType.Heal, true);
					Log.d("Debug", "I Heal my comrades");
					ResetGUI();
					//return;
				}
					
				
			}
			
		}else{
			handlePickUnit(pickUnit);
		}
	}


	/*
	 * if we arent controlling any
	 * 		- show enemy stats and movement box
	 * if we are controlling any unit AND  our unit has move AND  the decision is either ATTACK or ABILITY
	 * 		- we EXECUTE THEM!!!!
	 */
	public void handleEnemyUnint(Unit pickUnit){
		if(currentUnit == null && !pickControlledUnit){
			//display data of enemy unit
			PathFind.DisplayUnitMoveBox(pickUnit);
			String unitType ="" + pickUnit.unitType;
			Log.d("Debug", "pick unit is" + unitType);
			//update mapdata
			mRenderer.updateHUDPanel(pickUnit);
			mRenderer.headsUpDisplay.updateHUD(false, true, false, false);
		}else if(currentUnit != null && pickControlledUnit && finishMoving && decision != -1){
			if(decision == 1 && mapData._attackBox.contains(pickUnit.position)){
				GameEngine.useSkill(currentUnit, pickUnit, SkillType.Attack, true);
				//currentUnit.active = false;
				RendererAccessor.update(mapData);
				Log.d("Debug", "I ATTACK YOUUUU");
				ResetGUI();
			} else if(decision == 3 && mapData._attackBox.contains(pickUnit.position) && !currentUnit.getUnitStats().canUseThisSkill(SkillType.Heal)){
				
				GameEngine.useSkill(currentUnit, pickUnit, SkillType.Headshot, true);
				Log.d("Debug", "I Use My Skill");
				ResetGUI();
			}

		}
	}


	/*
		handle when clicking on the tilte to move
	 */
	public void handlePickEmpty(int x, int y,Point p,int touchMenu,boolean pressCancel){
		if(pickControlledUnit && !finishMoving){
			if(mapData._movementBox.contains(p)){
				GameEngine.moveUnit(currentUnit, p);
				mRenderer.updateHUDPanel(currentUnit);
				mRenderer.headsUpDisplay.updateHUD(true, true, false, false);
				//PathFind.DisplayUnitMoveBox(currentUnit);
				mapData.clearBoxes();
				RendererAccessor.update(mapData);
				finishMoving = true;
				
			}else
				ResetGUI();
		}else if(!pickControlledUnit){
			ResetGUI();
		}
	}
	/*
	 * handle when pick a new unit, new controlled unit
	 * enable hud
	 * showing the path of movement
	 */
	public void handlePickUnit(Unit p){
		PrintStat(1,true,p);
		if(p.active){
			String unitType ="" + p.unitType;
			Log.d("Debug", "pick unit is" + unitType);
			currentUnit = p;
			pickControlledUnit = true;
			PathFind.DisplayUnitMoveBox(currentUnit);
			mRenderer.updateHUDPanel(currentUnit);
			mRenderer.headsUpDisplay.updateHUD(true, true, true, false);
			finishMoving = false;
		}else{
			mRenderer.updateHUDPanel(p);
			mapData.clearBoxes();
			RendererAccessor.update(mapData);
			mRenderer.headsUpDisplay.updateHUD(false, true, false, false);
		}

	}
	//reset the GUI, boolean value etc
	public void ResetGUI(){
		pickControlledUnit = false;
		decision = -1;
		mRenderer.headsUpDisplay.action.menuSelected = -1;	
		finishMoving = false;
		currentUnit = null;
		mRenderer.headsUpDisplay.updateHUD(false, false, false, true);
		chooseAction = false;
		mapData.clearBoxes();
		RendererAccessor.update(mapData);
	}
	
	public void PrintStat(int touchMenu, boolean pressCancel,Unit u){
		
		Log.d("Debug", "----------------------------");
		//Log.d("Debug", "pickControlledUnit is: " + pickControlledUnit);
		//Log.d("Debug", "decision is: " + decision);
		//Log.d("Debug", "chooseAction is: " + chooseAction);
		//Log.d("Debug", "finishMoving is: " + finishMoving);
		//Log.d("Debug", "is it touching menu?: " + touchMenu);
		//Log.d("Debug", "is it pressing cancel?: " + pressCancel);
		Log.d("Debug", "is Unit active?: " + u.active);
		
	}
	
	private void startNetworking()
	{
		
		Thread netThread = new Thread()
		{			
			public void run(){
				//NetworkAccessor.net = new Networking(context);
				Networking.staticInitializer(mRenderer.context);
			}			
		};
		
		
		netThread.start();
	}

}
