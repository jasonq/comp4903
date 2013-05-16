package com.comp4903.project.graphics;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.ColorType;
import com.comp4903.project.gameEngine.enums.IconType;

import android.content.Context;
import android.graphics.Point;
import android.opengl.Matrix;

/*	RENDERACCESSOR - Accessor class to provide access to
 *  game screen rendering system (to send updates to it)
 */
public class RendererAccessor {
	
		
	
		public static MapRenderer map;
		
		
		private static float[] vec = new float[4];
		private static float[] resultVec = new float[4];		 
		
		public static void init(GL10 gl, Context c)
		{
			map = new MapRenderer(gl, c);
		}
		
		public static void update(MapData m)
		{
			map.update(m);
		}	
		
		public static void moveAnimation(Unit u, List<Point> steps)
		{
			map.moveAnimation(u, steps);
		}
		
		public static void attackAnimation(Unit u, Unit u2, String[] whatever)
		{
			map.attackAnimation(u, u2, whatever);
		}
		
		public static void deathAnimation(Unit u)
		{
			map.deathAnimation(u);
		}
		
		public static void healthAnimation(Unit u, String val)
		{
			map.healthAnimation(u, val);
		}
		
		/*	FLOATINGTEXT - will summon floating text to appear over the game screen
		 *  at any time.  Parameters:
		 * 
		 * 	x		x - coordinate in screen coordinates
		 *  y		y 
		 *  mx		Movement in the x direction (0 for stationary text)
		 *  my		Movement in y 
		 *  l		lifetime of text (-1 = unlimited lifetime)
		 *  col		text color, must be of ColorType
		 *  n		name of text (this does not get displayed, can be null)
		 *  c		content - the text message to display
		 */
		public static void floatingText(int x, int y, int mx, int my, int l, ColorType col, String n, String c)
		{
			map.addFloatingText(x,y,mx,my,l,col,n,c);
		}
		public static void floatingIcon(int x, int y, int mx, int my, int l, String n, IconType i)
		{
			map.addFloatingIcon(x,y,mx,my,l,n,i);
		}
		
		/*	SCREENXYFROMXYZ - takes a 3D world co-ordinate (xyz) and returns the 
		 *  xy location on the screen where it would appear if rendered
		 * 
		 */
		public static Point ScreenXYfromXYZ(float x, float y, float z)
		{
			float w = (float)GLRenderer.GLwidth / 2f;
			float h = (float)GLRenderer.GLheight / 2f;
			
			Point p = new Point(0,0);			
			
			// get the vertex
			vec[0] = x;
			vec[1] = y;
			vec[2] = z;
			vec[3] = 1.0f;
					
			Matrix.multiplyMV(resultVec, 0, map.viewMatrix, 0, vec, 0);
			Matrix.multiplyMV(vec, 0, map.projectionMatrix, 0, resultVec, 0);
			if (vec[3] == 0)
				vec[3] = 0.000001f;
			p.x = (int)(w + vec[0] / vec[3] * w);
			p.y = (int)(h - vec[1] / vec[3] * h);	
				
				
			
			return p;
		}
		
}
