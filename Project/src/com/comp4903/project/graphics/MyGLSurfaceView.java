package com.comp4903.project.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.comp4903.project.graphics.GLRenderer;

public class MyGLSurfaceView extends GLSurfaceView {
	
	private final GLRenderer mRenderer;
	private ScaleGestureDetector mScaleDetector;
	private boolean down = false;
	private float downx,downy;
	private float pickx,picky;
	private boolean menupressed = false;
	public MyGLSurfaceView(Context context) {
		
		super(context);
		
		// Set the Renderer for drawing on the GLSurfaceView
		mRenderer = new GLRenderer(context);
		setRenderer(mRenderer);
		
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		// Render the view only when there is a change in the drawing data
		//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private float mPreviousX;
	private float mPreviousY;
	
	private int sens = 5;
	
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.
				
		mScaleDetector.onTouchEvent(e);
				
		float x = e.getX();
		float y = e.getY();
		
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:

			float dx = x - mPreviousX;
			float dy = y - mPreviousY;
			
			/*if (dx > sens)
				mRenderer.dx = dx * 0.05f; // * TOUCH_SCALE_FACTOR;
			else if (dx < -sens)
				mRenderer.dx = dx * 0.05f; //* TOUCH_SCALE_FACTOR;

			if(dy > sens)
				mRenderer.dy = dy * 0.05f; // * TOUCH_SCALE_FACTOR;
			else if(dy < -sens)
				mRenderer.dy = dy * 0.05f; // * TOUCH_SCALE_FACTOR;*/
			
			if ((Math.abs(dx) > sens ) || (Math.abs(dy) > sens))
				mRenderer.cameraMoveRequest(dx, dy);
			
			requestRender();
			
		//PICKING
		case MotionEvent.ACTION_DOWN:
			down = true;
			downx = e.getX();
			downy = e.getY();
			/*if(this.mRenderer.checkHUD((int)downx, (int)downy)){
				menupressed = true;
				this.mRenderer.setSelectedHUD((int)e.getY(), menupressed);
			
			}else{
				menupressed = false;
				this.mRenderer.setSelectedHUD((int)e.getY(), menupressed);
			}*/

			requestRender();
		case MotionEvent.ACTION_UP:
			//this.mRenderer.setSelectedHUD(1, false);
			//menupressed = false;
			if(down){
				pickx = e.getX();
				picky = e.getY();
				down = false;
				mRenderer.selectTile(mRenderer.pick(pickx,picky));
				requestRender();
			}
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
