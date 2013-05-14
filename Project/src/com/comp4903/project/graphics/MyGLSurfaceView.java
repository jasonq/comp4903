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
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.UnitGroup;
import com.comp4903.project.gameEngine.engine.GameEngine;
import com.comp4903.project.graphics.GLRenderer;

public class MyGLSurfaceView extends GLSurfaceView {

	private final GLRenderer mRenderer;
	private ScaleGestureDetector mScaleDetector;	


	private float pickx,picky;
	private boolean pickControlledUnit = false;
	private boolean pickEnemyUnit = false;
	private boolean pickEmpty = false;
	private boolean finishMoving = false;
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
			Point pickPoint = mRenderer.pick(x, y);
			boolean pressEnd = mRenderer.headsUpDisplay.checkPressingEndTurn(x, y);
			//check if out of bounce
			if(pickPoint.x == -1 && pickPoint.y == -1 && touchMenu== -1 && !pressCancel && !pressEnd){
				ResetGUI();
				return;
			}		
			if(pressEnd){//might put condition if this is player turn
				//end turn code goes here
				System.out.println("End turn pressed");
				GameEngine.endTurn();
				ResetGUI();
			}
			Unit pickUnit = mapData.getUnitAt(pickPoint);
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
				handlePickEmpty(x,y,pickPoint,touchMenu);
			}			
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
					finishMoving = true;//finish moving for the currentunit
					mapData.clearBoxes();//clear the movement box
					RendererAccessor.update(mapData);//update mapdata
					mRenderer.headsUpDisplay.updateHUD(true, true, false, false);//update hud disable cancel button
					return;
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
				//update mapdata
				mRenderer.updateHUDPanel(pickUnit);
				mRenderer.headsUpDisplay.updateHUD(false, true, false, false);
			}else if(currentUnit != null && pickControlledUnit && finishMoving && (decision == 1 || decision == 2)){
				if(decision == 1){
					GameEngine.useSkill(currentUnit, pickUnit, SkillType.Attack, true);
					RendererAccessor.update(mapData);
					Log.d("Debug", "I ATTACK YOUUUU");
					ResetGUI();
				} else if(decision == 2){
					GameEngine.useSkill(currentUnit, null, SkillType.Defend, true);
					Log.d("Debug", "I Defend");
					ResetGUI();
				} else if(decision == 3){
					GameEngine.useSkill(currentUnit, pickUnit, SkillType.None, true);
					Log.d("Debug", "I Use My Skill");
					ResetGUI();
				}

			}
		}
		
		
		/*
		 * handle when picking empty tilte
		 * if we are controlling unit
		 * 		-if the unit hasnt moved
		 * 			if the user press cancel on top right
		 * 				cancel the current selected unit and return, below logic will be ignored
		 * 			-moving the unit
		 * 			-disable and enable some of the hud
		 * 			
		 * 		 else if the unit has moved
		 * 			-if we press anything inside the menu
		 * 				- if the decision is attack
		 * 					 showing attack boxes
		 * 				- if the decision is ability
		 * 					 showing skills boxes
		 * 				- if the decision is wait
		 * 					 yield the turn
		 * 	
		 */
		public void handlePickEmpty(int x, int y,Point p,int touchMenu){
			boolean pressCancel = mRenderer.headsUpDisplay.action.checkPressingCancel(x, y);
			if(pickControlledUnit){
				if(finishMoving){
					if(touchMenu != -1){
						//select either attack, ability,
						decision = mRenderer.setSelectedHUD(x,y);
						if(decision == 1){//if attack we show the attack range
							mRenderer.headsUpDisplay.updateHUD(true, true, false, false);//maintain the HUD
							PathFind.DisplayUnitAttackBox(currentUnit);//show the attack range
						}else if(decision == 4){//if cancel or wait we disable the HUD
							//more on this later
							//currentUnit.active = false;
							ResetGUI();
						}			
					}
				}else{
					//if cancel the movement action
					if(pressCancel){
						ResetGUI();
						return;
					}
					if(mapData._movementBox.contains(p)){
						GameEngine.moveUnit(currentUnit, p);
						mRenderer.headsUpDisplay.updateHUD(true, true, false, false);
						//PathFind.DisplayUnitMoveBox(currentUnit);
						mapData.clearBoxes();
						RendererAccessor.update(mapData);
						finishMoving = true;

					}
				}
			}else{
				ResetGUI();
			}
		}
		/*
		 * handle when pick a new unit, new controlled unit
		 * enable hud
		 * showing the path of movement
		 */
		public void handlePickUnit(Unit p){
			if(p.active){
				currentUnit = p;
				pickControlledUnit = true;
				PathFind.DisplayUnitMoveBox(currentUnit);
				mRenderer.updateHUDPanel(currentUnit);
				mRenderer.headsUpDisplay.updateHUD(true, false, true, false);
				finishMoving = false;
			}

		}
		//reset the GUI, boolean value etc
		public void ResetGUI(){
			pickControlledUnit = false;
			decision = -1;
			mRenderer.headsUpDisplay.action.menuSelected = -1;
			mapData.clearBoxes();
			RendererAccessor.update(mapData);
			finishMoving = false;
			currentUnit = null;
			mRenderer.headsUpDisplay.updateHUD(false, false, false, true);

		}

	}
}
