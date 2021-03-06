package com.comp4903.project.GUI;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MainMenu {

	public Square[] menu_Item;
	public int selected= -1;
	public Square[] pressed_menu_Item;
	public Square background;
	public Context context;
	public int width,height;
	public int xTop,yTop,xBot,yBot;
	
	public MainMenu(Context c,int w , int h){
		menu_Item = new Square[3];
		
		pressed_menu_Item = new Square[3];
		width = w;
		height = h;
		context = c;
		background = new Square(0,0,width,height);
		
		int totalHeight = height * 3/  5;
		
		int x = (width*  4) / 10; 
		int menu_width = (width * 1)/5;
		int menu_height =  totalHeight / menu_Item.length;
		int y = height / 5;
		
		xTop = x;
		yTop = y;
		xBot = x + menu_width;
		yBot = y + (menu_height * menu_Item.length);
		
		for(int i = 0; i < menu_Item.length; i++){
			menu_Item[i] =  new Square(x - 10,y - 10, menu_width - 20, menu_height -20 );
			pressed_menu_Item[i] = new Square(x- 10,y - 10,menu_width -20,menu_height - 20);
			y += menu_height;
		}
	}
	
	public void loadMenuTexture(GL10 gl){
		
		Bitmap button = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu_items);
		Bitmap button2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.pressed_menu_items);
		Bitmap bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.robot);
		
		background.loadGLTexture(gl, context, Bitmap.createScaledBitmap(bg, 512, 512, false));
		int width = button.getWidth();
		int height = button.getHeight()/3;
		for(int i = 0; i < menu_Item.length; i++){
			//command[i] = new Square();
			//Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.unpressed_button);
			Bitmap temp = Bitmap.createBitmap(button,
					0 , i * height, width, height);
			Bitmap scale = Bitmap.createScaledBitmap(temp, 512, 512, false);
			menu_Item[i].loadGLTexture(gl, context, scale);
			//commandT.recycle();
			temp.recycle();
			
			Bitmap temp2 = Bitmap.createBitmap(button2,
					0 , i * height, width, height);
			Bitmap scale2 = Bitmap.createScaledBitmap(temp2, 512, 512, false);
			pressed_menu_Item[i].loadGLTexture(gl, context, scale2);
			temp2.recycle();
			
		}
		button.recycle();
		button2.recycle();
	}
	
	public void drawMainMenu(GL10 gl){
		gl.glLoadIdentity();
		gl.glEnable( GL10.GL_BLEND );                   // Enable Alpha Blend
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );  // Set Alpha Blend Function
		//box.draw(gl);
		background.draw(gl);
		for(int i = 0; i < menu_Item.length; i++){
			
			if(selected == i){
				pressed_menu_Item[i].draw(gl);
			}else
				menu_Item[i].draw(gl);
			//gl.glTranslatef(0.0f,dis,0);
		}
		gl.glDisable( GL10.GL_BLEND );                  // Disable Alpha Blend
		
	}
	public boolean checkPressingMenu(int x, int y){
		return( x >= xTop && x <= xBot && y <= yBot && y >= yTop);
	}
	
	public int checkListItem(int y){
		int pad = (yBot - yTop)/menu_Item.length;
		return ((int)y-yTop) / pad  ;
	}
}
