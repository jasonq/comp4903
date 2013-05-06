/**
 * 
 */
package com.comp4903.project.GUI;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

/**
 * @author impaler
 *
 */
public class Glyphs {

	private static final String TAG = Glyphs.class.getSimpleName();
	private Bitmap bitmap;	// bitmap containing the character map/sheet

	// Map to associate a bitmap to each character
	public Map<Character, Bitmap> glyphs = new HashMap<Character, Bitmap>(62);

	private int width;	// width in pixels of one character
	private int height;	// height in pixels of one character

	// the characters in the English alphabet
	private char[] charactersL = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z' };
	private char[] charactersU = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z' };
	private char[] numbers = new char[] { '1', '2', '3', '4', '5', '6', '7',
			'8', '9', '0' };

	public Glyphs(Bitmap bitmap) {
		super();
		this.bitmap = bitmap;
		this.width = bitmap.getWidth()/26;
		this.height = bitmap.getHeight()/4;
		// Cutting up the glyphs
		// Starting with the first row - lower cases
		for (int i = 0; i < 26; i++) {
			glyphs.put(charactersL[i], Bitmap.createBitmap(bitmap,
					0 + (i * width), 0, width, height));
		}
		Log.d(TAG, "Lowercases initialised");

		// Continuing with the second row - upper cases 
		// Note that the row starts at 15px - hardcoded
		for (int i = 0; i < 26; i++) {
			glyphs.put(charactersU[i], Bitmap.createBitmap(bitmap,
					0 + (i * width), bitmap.getHeight()/4, width, height));
		}
		// row 3 for numbers
		Log.d(TAG, "Uppercases initialised");
		for (int i = 0; i < 10; i++) {
			glyphs.put(numbers[i], Bitmap.createBitmap(bitmap,
					0 + (i * width), bitmap.getHeight()/2, width, height));
		}
		Log.d(TAG, "Numbers initialised");

		// TODO - 4th row for punctuation
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	/**
	 * Draws the string onto the canvas at <code>x</code> and <code>y</code>
	 * @param text
	 */
	public void drawString(Canvas canvas, String text, int x, int y) {
		if (canvas == null) {
			Log.d(TAG, "Canvas is null");
		}
		for (int i = 0; i < text.length(); i++) {
			Character ch = text.charAt(i);
			if (glyphs.get(ch) != null) {
				canvas.drawBitmap(glyphs.get(ch), x + (i * width), y, null);
			}
		}
	}

	public Bitmap mergeBitmap(Bitmap fr, Bitmap sc) 
	{ 

		Bitmap comboBitmap; 

		int width, height; 

		width = fr.getWidth() + sc.getWidth(); 
		height = fr.getHeight(); 

		comboBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888 ); 

		Canvas comboImage = new Canvas(comboBitmap); 
		Paint transPainter = new Paint();
		transPainter.setColor(Color.WHITE);

		comboImage.drawBitmap(fr, 0f, 0f, transPainter); 
		comboImage.drawBitmap(sc, fr.getWidth() , 0, transPainter); 
		//comboBitmap.eraseColor(0);
		return comboBitmap;

	}

	public Bitmap getString(String s){
		Bitmap c =  glyphs.get(s.charAt(0));
		for (int i = 1; i < s.length(); i++) {

			Character ch = s.charAt(i);
			if (glyphs.get(ch) != null) {
				Bitmap temp = glyphs.get(ch);
				//Bitmap tempc = c;
				Bitmap tempc = mergeBitmap(c, temp);
				c = Bitmap.createBitmap(tempc);
				//c = temp;
			}
		}
		return c;
	}


}
