package com.comp4903.project.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.opengl.Matrix;

public class Hexagon {

	public int[][] screenCoords = new int[5000][14];
	public int screenCoordCount;
	
	private float[] vec = new float[4];
	private float[] resultVec = new float[4];
	
	private float vertices[];
	
	private short indices[] = {
		0, 5, 1, 1, 5, 4, 1, 4, 2, 2, 4, 3 	
	};
	
	private float texcoords1[] = {
			0.02f, 0.26f,
			0.08f, 0.04f,
			0.22f, 0.04f,
			0.28f, 0.26f,
			0.22f, 0.49f,
			0.08f, 0.49f			
	};
	
	private float texcoords2[] = {
			0.3f, 0.23f,
			0.37f, 0.01f,
			0.49f, 0.01f,
			0.54f, 0.23f,
			0.49f, 0.47f,
			0.37f, 0.47f			
	};
	
	private float texcoords3[] = {
			0.55f, 0.35f,
			0.65f, 0.01f,
			0.80f, 0.01f,
			0.91f, 0.35f,
			0.80f, 0.68f,
			0.65f, 0.68f			
	};
	
	private int textures[] = new int[1];
	
	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
	private FloatBuffer[] texBuffer =  new FloatBuffer[3];
		
	public Hexagon(GL10 gl, Context context) {
		
		float C = 1.0f;
		float A = 0.5f * C;
		float B = 0.8660254038f * C;
		
		vertices = new float[18];
		vertices[0] = 0; vertices[1] = 0; vertices[2] = B;
		vertices[3] = A; vertices[4] = 0; vertices[5] = 0;
		vertices[6] = A + C; vertices[7] = 0; vertices[8] = 0;
		vertices[9] = 2 * C; vertices[10] = 0; vertices[11] = B;
		vertices[12] = A + C; vertices[13] = 0; vertices[14] = 2 * B;
		vertices[15] = A; vertices[16] = 0; vertices[17] = 2 * B;		
		
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
		
		ByteBuffer tbb = ByteBuffer.allocateDirect(texcoords1.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		texBuffer[0] = tbb.asFloatBuffer();
		texBuffer[0].put(texcoords1);
		texBuffer[0].position(0);
		
		tbb = ByteBuffer.allocateDirect(texcoords2.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		texBuffer[1] = tbb.asFloatBuffer();
		texBuffer[1].put(texcoords2);
		texBuffer[1].position(0);
		
		tbb = ByteBuffer.allocateDirect(texcoords3.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		texBuffer[2] = tbb.asFloatBuffer();
		texBuffer[2].put(texcoords3);
		texBuffer[2].position(0);
	
		Bitmap bitmap;		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.texturetest, options);		
		
		gl.glGenTextures(1, textures, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		bitmap.recycle();		
		
	}
	
	public void draw(GL10 gl, float[] transformMatrix, float[] projectionMatrix, int typ)
	{		
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);				
		
		gl.glFrontFace(GL10.GL_CW);		
		gl.glEnable(GL10.GL_CULL_FACE); 
		gl.glCullFace(GL10.GL_BACK); 

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer[typ]);

		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, vertices.length / 3);
		
		//gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
		//				  GL10.GL_UNSIGNED_SHORT, indexBuffer);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_CULL_FACE);		
		
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
}
