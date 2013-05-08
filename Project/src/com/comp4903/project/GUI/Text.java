package com.comp4903.project.GUI;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;

public class Text {

	private Square boxtexture;
	private String text;
	private Glyphs glyph;
	public int textHeight;
	public int textWidth;
	public Text(int x, int y, int screenwidth, int screenheight, Glyphs g,String s){
		text = s;
		textHeight = (screenheight * 3 ) / 80;
		textWidth = (screenwidth/80)* text.length(); 
		boxtexture = new Square(x,y,textWidth,textHeight);
		glyph = g;

	}

	public void loadTextture(GL10 gl, Context context){
		Bitmap bmp = glyph.getString(text);
		Bitmap newbmp = Bitmap.createScaledBitmap(bmp, 256, 128, false);
		boxtexture.loadGLTexture(gl, context, newbmp);
		
	}
	public void drawText(GL10 gl){
		boxtexture.draw(gl);
	}
}
