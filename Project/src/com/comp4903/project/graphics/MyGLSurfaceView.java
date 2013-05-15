package com.comp4903.project.graphics;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.GestureDetector;
import com.comp4903.pathfind.PathFind;
import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.GameState;
import com.comp4903.project.gameEngine.enums.IconType;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.UnitGroup;
import com.comp4903.project.gameEngine.engine.GameEngine;
import com.comp4903.project.gameEngine.factory.GameStats;
import com.comp4903.project.gameEngine.factory.SkillStats;
import com.comp4903.project.graphics.GLRenderer;

public class MyGLSurfaceView extends GLSurfaceView {

	private final GLRenderer mRenderer;
	private ScaleGestureDetector mScaleDetector;	


	private float pickx,picky;
	private boolean pickControlledUnit = false;
	private boolean pickEnemyUnit = false;
	private boolean pickEmpty = false;
	private boolean finishMoving = false;
	private boolean chooseAction = false;
	private UnitGroup controlGroup = UnitGroup.PlayerOne;
	private UnitGroup enemyGroup = UnitGroup.PlayerTwo;

	private Unit currentUnit = null;
	private int decision = -1;
	private GestureDetector gDetect;
	public int tx,ty;
	private MapData mapData = null;

	public MyGLSurfaceView(Context context, MapData md) {

		super(context);

		// Set the Renderer for drawing on the GLSurfaceView
		mRenderer = new GLRenderer(context, md);
		setRenderer(mRenderer);
		gDetect = new GestureDetector(context, new GestureDetection()); 
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		// Render the view only when there is a change in the drawing data
		//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		mapData = md;
		//gl = RendererAccessor.map.gl;
	}


	private float mPreviousX;
	private float mPreviousY;
	private int sens = 5;
	private GL10 gl;
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		mScaleDetector.onTouchEvent(e);
		gDetect.onTouchEvent(e);
		boolean touchMenu = this.mRenderer.checkHUD((int)e.getX(), (int)e.getY());
		float x = e.getX();
		float y = e.getY();

		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:

			float dx = x - mPreviousX;
			float dy = y - mPreviousY;
			if (((Math.abs(dx) > sens ) || (Math.abs(dy) > sens)) && !mScaleDetector.isInProgress() && !touchMenu){
				mRenderer.cameraMoveRequest(dx, dy);
				//invalidate();

			}
			break;
		case MotionEvent.ACTION_DOWN:
			
			if(GLRenderer.state == GameState.Game_Screen){
				mRenderer.setSelectedHUD((int)x, (int)y);
			}
			break;
		case MotionEvent.ACTION_UP:
			if(GLRenderer.state == GameState.Game_Screen){
				mRenderer.setSelectedHUD(10000, 10000);
			}
			break;
		}
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}

	class ScaleListener 
	extends ScaleGestureDetector.SimpleOnScaleGestureListener {	
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			//scale += detector.getScaleFactor() * 0.01f;
			float s = detector.getScaleFactor();

			if (s != 0)
				mRenderer.scaleRequest(s);

			//invalidate();
			return true;
		}
	}

	public class GestureDetection extends GestureDetector.SimpleOnGestureListener {
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
			}
			return true;
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

			}
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
				System.out.println("End turn pressed");
				GameEngine.endTurn();
				RendererAccessor.floatingIcon(GLRenderer.GLwidth/2 - 125, GLRenderer.GLheight/10, 0, 0, 100, null, IconType.EndTurn);
				ResetGUI();			
			}
			
			Unit pickUnit = mapData.getUnitAt(pickPoint);
			
			if(handleClickBox(touchMenu,pressCancel))
				return;
			
			if(pickUnit != null){
				System.out.println("Active Group:" + mapData._activeGroup);
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
						stats = GameStats.getSkillStats(SkillType.Headshot);
						PathFind.DisplayUnitEnemyBox(currentUnit, stats.range);
						System.out.println("Find HeadShot");
					} else if (currentUnit.getUnitStats().canUseThisSkill(SkillType.Heal)){
						stats = GameStats.getSkillStats(SkillType.Heal);
						PathFind.DisplayUnitFriendBox(currentUnit, stats.range);
						System.out.println("Find Heal");
					} else {
						System.out.println("Find Nothing");
					}//show the attack range
					
					chooseAction = true;
				}else if(decision == 4){		
					currentUnit.active = false;
					ResetGUI();
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
				mRenderer.headsUpDisplay.updateHUD(true, false, true, false);
				finishMoving = false;
			}else{
				mRenderer.updateHUDPanel(p);
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

	}
}
