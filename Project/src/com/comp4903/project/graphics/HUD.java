package com.comp4903.project.graphics;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.opengl.GLU;
import android.view.Display;
import android.view.WindowManager;
/*
 * Drawing menus, subbar, character stats, etc
 */
public class HUD {
	private Context context;
	public Square actionBox;//the box to select action for character
	public Square characterBox;//show the stats of charater
	public Square optionBox;
	public int width,height;
	public final int dis = 2;
	public HUD(Context c, int w, int h){
		actionBox = new Square();
		characterBox = new Square();
		optionBox = new Square();
		context = c;
	
		width = w;
		height = h;
	}

	public void initialBoxTexture(GL10 gl){
		int actionBoxWidth = (int)((float)width/5);
		int actionBoxHeight = (int)((float)height/2);
		
		int characterBoxWidth = (int)((float)width*3/4);
		int characterBoxHeight = (int)((float)height/3);
		
		int optionBoxWidth = (int)((float)height/5);
		int optionBoxHeight = (int)((float)width/5);
		
		/*Bitmap bitmap1 = decodeSampledBitmapFromResource(context.getResources(),R.drawable.menu_sample,
				actionBoxWidth,actionBoxHeight);
		Bitmap bitmap2 = decodeSampledBitmapFromResource(context.getResources(),R.drawable.box_sample,
				characterBoxWidth,characterBoxHeight);
		Bitmap bitmap3 = decodeSampledBitmapFromResource(context.getResources(),R.drawable.menu_sample,
				actionBoxWidth,actionBoxHeight);
		*/
		
		Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu_sample);
		Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.box_sample);
		Bitmap bitmap3 = decodeSampledBitmapFromResource(context.getResources(),R.drawable.menu_sample,
				actionBoxWidth,actionBoxHeight);
		actionBox.loadGLTexture(gl, context, bitmap1);
		characterBox.loadGLTexture(gl, context, bitmap2);
		optionBox.loadGLTexture(gl, context, bitmap3);

		bitmap1.recycle();
		bitmap2.recycle();
		bitmap3.recycle();	
	}

	public void drawHUD(GL10 gl){
		//viewOrtho(gl, width,height);
		int transxab = width/2 - dis;
		int transyab = height/2 + dis;
		gl.glLoadIdentity();
		//gl.glScalef(1.0f, 1.5f, 1.0f);
		gl.glTranslatef(-2.5f, 0.5f, -5.0f);
		actionBox.draw(gl);						// Draw the actionBox
		gl.glLoadIdentity();
		gl.glScalef(2.5f, 0.5f, 1.0f);
		gl.glTranslatef(0.5f, -2.25f, -5.0f);
		characterBox.draw(gl);
		//viewPerspective(gl);
	}
	public  int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int h = options.outHeight;
		final int w = options.outWidth;
		int inSampleSize = 1;

		if (h > reqHeight || w > reqWidth) {

			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) h / (float) reqHeight);
			final int widthRatio = Math.round((float) w / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	
	public  Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}
}