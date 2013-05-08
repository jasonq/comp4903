package com.comp4903.project.graphics;

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
import com.comp4903.project.graphics.GLRenderer;

public class MyGLSurfaceView extends GLSurfaceView {

	private final GLRenderer mRenderer;
	private ScaleGestureDetector mScaleDetector;	


	private float pickx,picky;

	private boolean pickControlledUnit = false;
	private boolean pickEnemyUnit = false;
	private boolean pickEmpty = false;

	private int decision = -1;
	private GestureDetector gDetect;

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

			requestRender();
			break;
			//PICKING
			//case MotionEvent.ACTION_DOWN:

			/* return an int to decide which action taking
			 * 0- Move
			 * 1- Attack
			 * 2- Items
			 * 3- Ability
			 * 4- Wait
			 * 5- Cancel
			 */
			//if(touchMenu && pickControlledUnit)
			//decision = this.mRenderer.setSelectedHUD((int)e.getY(), touchMenu);

			//requestRender();
			//break;
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
		
		public void handle_Game_Screen(int x, int y){
			Log.d("TAG", "Single Tap Detected ...");
			boolean touchMenu = mRenderer.checkHUD(x, y);
			gl = RendererAccessor.map.gl;
			Point pickPoint = mRenderer.pick(x, y);

			if(pickPoint.x == -1 & pickPoint.y == -1){

				mRenderer.headsUpDisplay.updateHUD(false, false, false, false);
				pickControlledUnit = false;
				mRenderer.setSelectedHUD(y, false);
				return;
				//return false;
			}

			mapData._tileSelected = pickPoint;
			Unit pickUnit = mapData.getUnitAt(pickPoint);
			if(pickUnit != null){

				pickControlledUnit = true;
				mRenderer.headsUpDisplay.updateHUD(true, true, false, false);
				PathFind.getMovePoints(mapData.getUnitAt(pickPoint));
				mRenderer.updateHUDPanel(pickUnit);
				//Log.d("TAG", "Change unit ...");
			}else{
				if(pickControlledUnit && touchMenu){
					mRenderer.headsUpDisplay.updateHUD(true, true, false, false);
					decision = mRenderer.setSelectedHUD(y, touchMenu);

				}else{
					//mRenderer.headsUpDisplay.updateHUD(false, false, false, false);
					//pickControlledUnit = false;
					//mRenderer.setSelectedHUD((int)e.getY(), touchMenu);	
					//HANDLE DECISIONS
					handleTouchEvent(x,y);
				}
			}			

			requestRender();
		}
		
		
		public void handle_Main_Menu(int x , int y){
			int result = mRenderer.setSelectMainMenu(x, y);
			int a = 2;
			if(result != -1){
				if(result == 0)
					GLRenderer.state = GameState.Game_Screen;
				
			}
		}
	}

	public void handleTouchEvent(int x , int y){
		switch(decision){
		case 0:
			//moving to the selected tilte
			//maintain the menu showing
			mRenderer.headsUpDisplay.updateHUD(true, true, false, false);
			/*
			 * if pick the wrong tilte 
			 * 
			 */
			break;
		case 1:
			//Attacking enemy
			//maintain the menu showing
			mRenderer.headsUpDisplay.updateHUD(true, true, false, false);
			break;
		case 2:
			//Using items
			mRenderer.headsUpDisplay.updateHUD(true, true, false, false);
			break;
		case 3:
			//Using abilities
			mRenderer.headsUpDisplay.updateHUD(true, true, false, false);
			break;
		case 4:
		case 5:
		case -1:
			mRenderer.headsUpDisplay.updateHUD(false, false, false, false);
			pickControlledUnit = false;
			mRenderer.setSelectedHUD((int)y, false);	
			break;
		}
	}


}
