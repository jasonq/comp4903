

package com.comp4903.project.graphics;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.GestureDetector;

import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.graphics.GLRenderer;

import com.comp4903.project.network.Networking;
import com.comp4903.project.sound.SFX;


public class MyGLSurfaceView extends GLSurfaceView {

	private final GLRenderer mRenderer;
	private ScaleGestureDetector mScaleDetector;	
	private Context context;


	private boolean pickControlledUnit = false;
	private boolean finishMoving = false;
	private boolean chooseAction = false;


	private Unit currentUnit = null;
	private int decision = -1;
	private GestureDetector gDetect;
	public int tx,ty;
	private MapData mapData = null;

	public MyGLSurfaceView(Context context, MapData md) {

		super(context);
		this.context = context;
		// Set the Renderer for drawing on the GLSurfaceView
		mRenderer = new GLRenderer(context, md);

		setRenderer(mRenderer);
		TouchGesture newT = new TouchGesture(mRenderer, md);
		//gDetect = new GestureDetector(context, new GestureDetection()); 
		gDetect = new GestureDetector(context, newT); 
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
			switch(GLRenderer.state){
			case Game_Screen:
				int abc = mRenderer.setSelectedHUD((int)x, (int)y);
				if(abc != -1)
					SFX.play(SFX.PRESS2);
				if( mRenderer.headsUpDisplay.checkPressingEndTurn((int)x,(int)y)){
					mRenderer.headsUpDisplay.check =mRenderer.headsUpDisplay.pgiveUpTurn;
				}
				Point tx = mRenderer.pick(x, y);
				RendererAccessor.map.highlight(tx.x, tx.y);
				break;
			case Game_Over:
				if(mRenderer.gov.checkPressingMeu((int)x, (int)y))
					mRenderer.gov.flag = true;
				break;
			case Network_Menu:
				int nw = mRenderer.network.checkButton((int)x, (int)y);
				if(nw != -1){
					SFX.play(SFX.PRESS2);
				}
				mRenderer.network.selected = nw;
				break;
			case Main_Menu:
				int result = mRenderer.setSelectMainMenu((int)x, (int)y);
				mRenderer.mm.selected = result;
				if(result != -1)
					SFX.play(SFX.PRESS2);
				break;
			}
			break;
		case MotionEvent.ACTION_UP:
			switch(GLRenderer.state){
			case Game_Screen:
				mRenderer.setSelectedHUD(10000, 10000);
				if( mRenderer.headsUpDisplay.checkPressingEndTurn((int)x,(int)y)){
					mRenderer.headsUpDisplay.check =mRenderer.headsUpDisplay.giveUpTurn;
				}
				RendererAccessor.map.highlight(-1,-1);
				break;
			case Game_Over:
				if(mRenderer.gov.checkPressingMeu((int)x, (int)y))
					mRenderer.gov.flag = false;
				break;
			case Network_Menu:
				mRenderer.network.selected = -1;
				break;
			case Main_Menu:
				mRenderer.mm.selected = -1;
				break;
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
}
