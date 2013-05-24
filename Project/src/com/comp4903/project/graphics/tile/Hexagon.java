package com.comp4903.project.graphics.tile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.graphics.GLRenderer;
import com.comp4903.project.graphics.MapRenderer;
import com.comp4903.project.graphics.RendererAccessor;
import com.comp4903.project.graphics.model.MaterialLibrary;
import com.comp4903.project.graphics.model.Texture;

import android.content.Context;
import android.graphics.Point;
import android.opengl.Matrix;

/*	HEXAGON - Generic routines to display the base hexagon tiles, and
 *  any selection effects such as highlighting * 
 */
public class Hexagon {
	
	private Context context;
	private GL10 gl;

	public Point[][] screenCoords = new Point[5000][7];
	public int screenCoordCount;
	
	private float[] vec = new float[4];
	private float[] resultVec = new float[4];
	private float[] modelMatrix = new float[16];
	private float[] modelViewMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	
	private float vertices[];	
	
	private TileSetDefinition[] tileDefinitions = new TileSetDefinition[10];
		
	private FloatBuffer vertexBuffer;	
			
	/* CONSTRUCTOR - sets up default shape for the hexagons, and
	 * initializes the vertex and index buffers	 * 
	 */
	public Hexagon(GL10 gl, Context c) {
		
		float C = 1.0f;
		float A = 0.5f * C;
		float B = 0.8660254038f * C;
		
		context = c;
		
		vertices = new float[18];
		vertices[0] = 0; vertices[1] = 0; vertices[2] = B;
		vertices[3] = A; vertices[4] = 0; vertices[5] = 0;
		vertices[6] = A + C; vertices[7] = 0; vertices[8] = 0;
		vertices[9] = 2 * C; vertices[10] = 0; vertices[11] = B;
		vertices[12] = A + C; vertices[13] = 0; vertices[14] = 2 * B;
		vertices[15] = A; vertices[16] = 0; vertices[17] = 2 * B;	
		
		for (int i = 0; i < 6; i++)
		{
			vertices[i * 3] -= C;
			vertices[i * 3 + 2] -= B;
		}
				
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);			
		
		for (int i = 0; i < 5000; i++)
			for (int p = 0; p < 7; p++)
				screenCoords[i][p] = new Point();
	}
	
	/*	READTILEDATA - reads the display specifications for the tile sets used
	 *  by the application, including texture coordinates and associated models
	 * 
	 */
	public void readTileData()
	{
		tileDefinitions[0] = new TileSetDefinition("textures/tiles/selectiontiles.xml", context);
		tileDefinitions[1] = new TileSetDefinition("textures/tiles/metals.xml", context);		
		
		for (int d = 0; d < 2; d++)
		{
			for (int i = 0; i < tileDefinitions[d].numberOfTiles; i++)
			{
				ByteBuffer tbb = ByteBuffer.allocateDirect(12 * 4);
				tbb.order(ByteOrder.nativeOrder());
				tileDefinitions[d].tiles[i].UV = tbb.asFloatBuffer();
				tileDefinitions[d].tiles[i].UV.put(tileDefinitions[d].tiles[i].texcoords);
				tileDefinitions[d].tiles[i].UV.position(0);
			}
		}
	}	
	
	/*	DRAW - draws a hexagon with the specified texture
	 * 
	 * 	gl					OpenGL device
	 * 	transformMatrix		model-view transformation matrix
	 * 	projectionMatrix	projection matrix
	 *  set					texture set
	 *  typ					texture index
	 * 
	 */
	public void draw(GL10 gl, float[] transformMatrix, float[] projectionMatrix, int set, int typ, boolean usecolor)
	{		
		int tex = tileDefinitions[set].textureMap;
		gl.glBindTexture(GL10.GL_TEXTURE_2D, MaterialLibrary.texturenames[tex]);
	
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
				
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tileDefinitions[set].tiles[typ].UV);
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, vertices.length / 3);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);		
		
	}
	
	public void drawModel(GL10 gl, float[] viewMatrix, float dx, float dy, float dz, int set, int typ)
	{
		if (tileDefinitions[set].tiles[typ].model != null)
		{
			tileDefinitions[set].tiles[typ].model.SetPosition(dx, dy, dz);
			tileDefinitions[set].tiles[typ].model.display(gl, viewMatrix);
		}
	}
	
	private void computePick(float[] transformMatrix, float[]projectionMatrix)
	{
		
		float w = (float)GLRenderer.GLwidth / 2f;
		float h = (float)GLRenderer.GLheight / 2f;
		
		// compute screen co-ordinates for the picking routine
		for (int m = 0; m < 6; m++)
		{
			// get the vertex
			vec[0] = vertices[m * 3];
			vec[1] = vertices[m * 3 + 1];
			vec[2] = vertices[m * 3 + 2];
			vec[3] = 1.0f;
				
			Matrix.multiplyMV(resultVec, 0, transformMatrix, 0, vec, 0);
			Matrix.multiplyMV(vec, 0, projectionMatrix, 0, resultVec, 0);
			if (vec[3] == 0)
				vec[3] = 0.000001f;
			screenCoords[screenCoordCount][m].x = (int)(w + vec[0] / vec[3] * w);
			screenCoords[screenCoordCount][m].y = (int)(h - vec[1] / vec[3] * h);		
		}	
				
		screenCoordCount++;	
	}
		
	// Given two screen co-ordinates, will return
	// a Point, containing the x and y map co-ordinates selected.
	// Magic is used here.
	public Point pick(float x, float y)
	{	
		float w = (float)GLRenderer.GLwidth / 2f;
		float h = (float)GLRenderer.GLheight / 2f;
		
		screenCoordCount = 0;
		for (int l = 0; l < 16; l++)
			viewMatrix[l] = GLRenderer.viewMatrix[l];
		
		for (int tx = 0; tx < RendererAccessor.map.mapWidth; tx++)
		{
			for (int ty = 0; ty < RendererAccessor.map.mapHeight; ty++)
			{				
				float dx = (float)tx * 1.5f;
				float dy = 0;
				float dz = (float)ty * 0.8660254038f * 2 + (tx % 2) * 0.8660254038f;
								
				if (MapRenderer.tileOnScreen(dx, dy, dz))
				{
					screenCoords[screenCoordCount][6].x = tx;
					screenCoords[screenCoordCount][6].y = ty;
				
					// set up the model-view matrix for this hexagon
					Matrix.setIdentityM(modelMatrix, 0);				
					Matrix.multiplyMM(modelViewMatrix, 0, modelMatrix, 0, viewMatrix, 0);
					Matrix.translateM(modelViewMatrix, 0, dx, dy, dz);
					
					computePick(modelViewMatrix, GLRenderer.projectionMatrix);					
				}
			}
		}		
		
		Point r = new Point(-1, -1);
		Point[] pnts;
		int v = 5;
		boolean p = true;
				
		r.x = -1;
		r.y = -1;		
		
		// somehow these loops magically determine whether the 2D point is
		// in the 2D polygon
		for (int m = 0; m < screenCoordCount; m++)
		{
			pnts = screenCoords[m];
			
			p = false;
			for (int t = 0; t < 6; t++)
			{				
				if (  ((float)pnts[t].y > y) != ((float)pnts[v].y > y)
						  &&
						  (x < ((float)pnts[v].x - (float)pnts[t].x) *
								  (y - (float)pnts[t].y) / ((float)pnts[v].y - (float)pnts[t].y) + (float)pnts[t].x  ))						
				{
					p = !p;
				}
				v = t;
			}		
			if (p)
			{
				r.x = (int)(screenCoords[m][6].x);
				r.y = (int)(screenCoords[m][6].y);
			}
		}		
		
		return r;
	}
	
	// returns a numeric value representing the direction one tile is
	// from another.
	// 0 - NW
	// 1 - N
	// 2 - NE
	// 3 - SE
	// 4 - S
	// 5 - SW
	public static int getDirection(Point p1, Point p2)
	{
		int d = -1;
		
		if ((p1.x % 2) == 0)
		{
			if ((p2.x == p1.x - 1) && (p2.y == p1.y - 1))
					d = 0;
			if ((p2.x == p1.x) && (p2.y == p1.y - 1))
					d = 1;
			if ((p2.x == p1.x + 1) && (p2.y == p1.y - 1))
					d = 2;
			if ((p2.x == p1.x + 1) && (p2.y == p1.y))
					d = 3;
			if ((p2.x == p1.x) && (p2.y == p1.y + 1))
					d = 4;
			if ((p2.x == p1.x - 1) && (p2.y == p1.y))
					d = 5;
		} else {
			if ((p2.x == p1.x - 1) && (p2.y == p1.y))
					d = 0;
			if ((p2.x == p1.x) && (p2.y == p1.y - 1))
					d = 1;
			if ((p2.x == p1.x + 1) && (p2.y == p1.y))
					d = 2;
			if ((p2.x == p1.x + 1) && (p2.y == p1.y + 1))
					d = 3;
			if ((p2.x == p1.x) && (p2.y == p1.y + 1))
					d = 4;
			if ((p2.x == p1.x - 1) && (p2.y == p1.y + 1))
					d = 5;
		}
		return d;
	}	 
		
}
