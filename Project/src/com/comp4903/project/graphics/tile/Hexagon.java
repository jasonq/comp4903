package com.comp4903.project.graphics.tile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.graphics.GLRenderer;
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

	public int[][] screenCoords = new int[5000][14];
	public int screenCoordCount;
	
	private float[] vec = new float[4];
	private float[] resultVec = new float[4];
	
	private float vertices[];
	
	private TileSetDefinition[] tileDefinitions = new TileSetDefinition[10];
	
	private short indices[] = {
		0, 5, 1, 1, 5, 4, 1, 4, 2, 2, 4, 3 	
	};		
	
	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;	
	
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
		
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);		
		
	}
	
	/*	READTILEDATA - reads the display specifications for the tile sets used
	 *  by the application, including texture coordinates and associated models
	 * 
	 */
	public void readTileData()
	{
		tileDefinitions[0] = new TileSetDefinition("textures/tiles/selectiontiles.xml", context);
		tileDefinitions[1] = new TileSetDefinition("textures/tiles/metals.xml", context);
				
		/*Texture t = new Texture(tileDefinitions[0].name, "tiles/" + tileDefinitions[0].texFileName, 0);
		int index = MaterialLibrary.addTexture(t);
		MaterialLibrary.loadTexture(index);*/
		
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
	public void draw(GL10 gl, float[] transformMatrix, float[] projectionMatrix, int set, int typ)
	{		
				
		int tex = tileDefinitions[set].textureMap;
		gl.glBindTexture(GL10.GL_TEXTURE_2D, MaterialLibrary.texturenames[tex]);
	
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);		
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tileDefinitions[set].tiles[typ].UV);

		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, vertices.length / 3);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);		
		
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
			screenCoords[screenCoordCount][m * 2] = (int)(w + vec[0] / vec[3] * w);
			screenCoords[screenCoordCount][m * 2 + 1] = (int)(h - vec[1] / vec[3] * h);		
		}	
		
		screenCoordCount++;		
	
		
	}
	
	// a crazy way of doing picking.  Don't look too closely at this code.
	// Given two screen co-ordinates, will return
	// a Point, containing the x and y map co-ordinates selected.
	public Point pick(float x, float y)
	{
		
		Point r = new Point(-1, -1);
		int v = 5;
		boolean p = true;
				
		r.x = -1;
		r.y = -1;		
		
		for (int m = 0; m < screenCoordCount; m++)
		{
			p = false;
			for (int t = 0; t < 5; t++)
			{
				if (  (((float)screenCoords[m][t * 2 + 1] > y) != ((float)screenCoords[m][v * 2 + 1] > y))
					  &&
					  ((float)x < ((float)screenCoords[m][v * 2] - (float)screenCoords[m][t * 2]) *
							  ((float)y - (float)screenCoords[m][t * 2 + 1]) / 
							  ((float)screenCoords[m][v * 2 + 1] - (float)screenCoords[m][t * 2 + 1]) + (float)screenCoords[m][t * 2])
					  
					)
				{
					p = !p;
				}
				v = t;
			}		
			if (p)
			{
				r.x = (int)(screenCoords[m][12]);
				r.y = (int)(screenCoords[m][13]);
			}
		}
			
		return r;
	}
		
}
