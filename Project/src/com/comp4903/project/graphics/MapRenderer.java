package com.comp4903.project.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.graphics.tile.Hexagon;

import android.content.Context;
import android.graphics.Point;
import android.opengl.Matrix;

/*	MAPRENDERER - Handles display of the map
 * 
 */
public class MapRenderer {

	// device and resource access
	private GL10 gl;
	private Context context;
	
	// map statistics
	private int mapWidth;
	private int mapHeight;
	
	// map data
	private Hex tileMap[][];
	
	private float[] vec = new float[4];
	private float[] resultVec = new float[4];
	
	private float[] viewMatrix;
	private float[] projectionMatrix;
	private float[] modelMatrix = new float[16];
	private float[] modelViewMatrix = new float[16];
	private float eyeX, eyeY, eyeZ;
	
	private Point selectedTile = new Point();
	private Point reachableTiles[] = new Point[200];
	private int reachableTilesCount = 0;
	
	private Hexagon hex;
		
	/*	CONSTRUCTOR - sets up data structures
	 * 
	 */
	public MapRenderer(GL10 g, Context c)
	{
		gl = g;
		context = c;
		hex = new Hexagon(gl, context);
		hex.readTileData();
	}
	
	/*	INIT - Used to initialize, or re-initialize the map
	 * 
	 */
	public void init(int w, int h)
	{
		mapWidth = w;
		mapHeight = h;
		tileMap = new Hex[w][h];
		
		// currently using a random map
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++)
			{
				tileMap[x][y] = new Hex();
				tileMap[x][y].tile = 0;
				tileMap[x][y].size = 0f;
				tileMap[x][y].state = -1;
				if ((x * y) % 16 > 10)
					tileMap[x][y].tile = 1;
				if ((x * y) % 16 > 14)
					tileMap[x][y].tile = 2;
				if ((x > 18) && (x < 22))
					tileMap[x][y].tile = 0;
			}
	
	}
	
	/*	RENDER - draw the map
	 * 
	 * 		viewM		view matrix of the camera
	 * 		projM		projection matrix of the camera
	 * 		eX, eY, eZ	eye position in map co-ordinates
	 */
	public void render(float[] viewM, 
					   float[] projM,
					   float eX,
					   float eY,
					   float eZ)
	{
		viewMatrix = viewM;
		projectionMatrix = projM;
		
		eyeX = eX;
		eyeY = eY;
		eyeZ = eZ;
				
		basePass();
		
		setTileStates();
		selectionPass();
	}
	
	/* BASEPASS - clears the tile states and draws the base hexagon shaped floor tiles
	 * 
	 */
	private void basePass()
	{
		hex.screenCoordCount = 0;
				
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				//tileMap[x][y].state = -1;
				float dx = (float)x * 1.5f;
				float dy = 0;
				float dz = (float)y * 0.8660254038f * 2 + (x % 2) * 0.8660254038f;
				
				//if ((x == selectedTile.x) && (y == selectedTile.y))
				//	dy = .1f;
				
				if ((dx > eyeX - 16) &&
					(dz > eyeZ - 16) &&
					(dx < eyeX + 16) &&
					(dz < eyeZ + 8))
				{
					hex.screenCoords[hex.screenCoordCount][12] = x;
					hex.screenCoords[hex.screenCoordCount][13] = y;
				
					// set up the model-view matrix for this hexagon
					Matrix.setIdentityM(modelMatrix, 0);				
					Matrix.multiplyMM(modelViewMatrix, 0, modelMatrix, 0, viewMatrix, 0);
					Matrix.translateM(modelViewMatrix, 0, dx, dy, dz);
					gl.glMatrixMode(GL10.GL_MODELVIEW);
					gl.glLoadMatrixf(modelViewMatrix, 0);
				
					hex.draw(gl, modelViewMatrix, projectionMatrix, 1, tileMap[x][y].tile);
				}
			}
		}
	}
	
	/*	SELECTIONPASS - Draws selection effects transparently over
	 *  floor tiles 
	 */
	private void selectionPass()
	{
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		for (int x = 0; x < mapWidth; x++)
			for (int y = 0; y < mapHeight; y++)
			{
				float dx = (float)x * 1.5f;
				float dy = 0.01f;
				float dz = (float)y * 0.8660254038f * 2 + (x % 2) * 0.8660254038f;
				
				if (tileMap[x][y].size < 1.0f)
					tileMap[x][y].size += 0.15f;
				else
					tileMap[x][y].size = 1.0f;
				
				if ((dx > eyeX - 16) &&
						(dz > eyeZ - 16) &&
						(dx < eyeX + 16) &&
						(dz < eyeZ + 8))
				{
					// set up the model-view matrix for this hexagon
					Matrix.setIdentityM(modelMatrix, 0);					
					Matrix.multiplyMM(modelViewMatrix, 0, modelMatrix, 0, viewMatrix, 0);
					Matrix.translateM(modelViewMatrix, 0, dx, dy, dz);
					Matrix.scaleM(modelViewMatrix, 0, tileMap[x][y].size, tileMap[x][y].size, tileMap[x][y].size);
					gl.glMatrixMode(GL10.GL_MODELVIEW);
					gl.glLoadMatrixf(modelViewMatrix, 0);
				
					if (tileMap[x][y].state != -1)
					{
						hex.draw(gl, modelViewMatrix, projectionMatrix, 0, tileMap[x][y].state);
					}
				}
			}
		gl.glDisable(GL10.GL_BLEND);
	}

	/*	SETTILESTATES - sets the tile states (selected, reachable, path, etc...) prior to
	 *  drawing the UI selection highlights
	 * 
	 */
	private void setTileStates()
	{
		/*for (int i = 0; i < reachableTilesCount; i++)
		{
			if (reachableTiles[i].x != -1)
				tileMap[reachableTiles[i].x][reachableTiles[i].y].state = 0;
		}*/
	}
	
	// Tile picking, handled by Hexagon class
	public Point pick(float x, float y)
	{
		return hex.pick(x, y);
	}
	
	// Makes tile x,y the selected tile
	public void selectTile(int x, int y)
	{
		selectedTile.x = x;
		selectedTile.y = y;
		
		if (x == -1)
			return;
		
		// remove this later
		/*for (int i = 0; i < reachableTilesCount; i++)
		{
			if ((reachableTiles[i].x == x) && (reachableTiles[i].y == y))
			{
				reachableTiles[i].x = -1;
				reachableTiles[i].y = -1;
				return;
			}
		}
		
		for (int i = 0; i < reachableTilesCount; i++)
		{
			if ((reachableTiles[i].x == -1) && (reachableTiles[i].y == -1))
			{
				reachableTiles[i].x = x;
				reachableTiles[i].y = y;
				tileMap[reachableTiles[i].x][reachableTiles[i].y].size = 0;
				return;
			}
		}
		
		reachableTiles[reachableTilesCount] = new Point();
		reachableTiles[reachableTilesCount].x = x;
		reachableTiles[reachableTilesCount].y = y;		
		reachableTilesCount++;*/
		
		if (tileMap[x][y].state == -1)
		{
			tileMap[x][y].state = 0;
			tileMap[x][y].size = 0f;
		}
		else { tileMap[x][y].state = -1; }
		
	}
	
	private class Hex {
		int tile;
		int state;
		float size = 0f;
	}
	
}
