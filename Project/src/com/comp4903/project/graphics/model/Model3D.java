package com.comp4903.project.graphics.model;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.Matrix;

public class Model3D {	
	
	public String name;
	
	private int numComponents;
	private Component[] components;
	
	private float[] orientation = new float[16];
	private float[] position = new float[4];
	private float[] scale = new float[4];
	
	private float[] colorBlack = { 0f, 0f, 0f, 0f };
	
	public Model3D()
	{
		position[0] = 0f;
		position[1] = 0f;
		position[2] = 0f;
		position[3] = 1f;
		scale[0] = 1.0f;
		scale[1] = 1.0f;
		scale[2] = 1.0f;
		scale[3] = 1.0f;
		Matrix.setIdentityM(orientation, 0);
	}
	
	public void SetOrientation(float[] vm)
	{
		for (int i = 0; i < 16; i++)
			orientation[i] = vm[i];
	}

	public float[] GetOrientation()
	{
		float[] result = new float[16];
		
		for (int i = 0; i < 16; i++)
			result[i] = orientation[i];
		
		return result;
	}

	public void SetPosition(float x, float y, float z)
	{
		position[0] = x;
		position[1] = y;
		position[2] = z;
	}

	public void SetPosition(float[] p)
	{
		position[0] = p[0];
		position[1] = p[1];
		position[2] = p[2];		
	}

	public float[] GetPosition()
	{
		float[] result = new float[3];
		
		result[0] = position[0];
		result[1] = position[1];
		result[2] = position[2];
		
		return result;
	}

	public void SetScale(float x, float y, float z)
	{
		scale[0] = x;
		scale[1] = y;
		scale[2] = z;
	}

	public void SetScale(float[] p)
	{
		scale[0] = p[0];
		scale[1] = p[1];
		scale[2] = p[2];	
	}

	public float[] GetScale()
	{
		float[] result = new float[3];
		
		result[0] = scale[0];
		result[1] = scale[1];
		result[2] = scale[2];
		
		return result;
	}

	public void ResetOrientation()
	{
		Matrix.setIdentityM(orientation, 0);
	}

	public void recallNeutralPose()
	{
		for (int c = 0; c < numComponents; c++)
		{
			for (int i = 0; i < 16; i++)
				components[c].orientation[i] = components[c].neutralOrientation[i];
			components[c].Xrotate = 0;
			components[c].Yrotate = 0;
			components[c].Zrotate = 0;
		}
	}
	
	public void YRotate(float angle)
	{
		float[] yAxis = { 0, 0, 0, 0 };
		yAxis[0] = orientation[1];
		yAxis[1] = orientation[5];
		yAxis[2] = orientation[9];	

		float[] rotation = matrixAngleAroundAxis(angle, yAxis);
		float[] temp = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				
		Matrix.multiplyMM(temp, 0, rotation, 0, orientation, 0);
		
		for (int i = 0; i < 16; i++)
			orientation[i] = temp[i];
	}

	public void XRotate(float angle)
	{
		float[] xAxis = { 0, 0, 0, 0 };
		xAxis[0] = orientation[0];
		xAxis[1] = orientation[4];
		xAxis[2] = orientation[8];	

		float[] rotation = matrixAngleAroundAxis(angle, xAxis);
		float[] temp = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				
		Matrix.multiplyMM(temp, 0, rotation, 0, orientation, 0);
		
		for (int i = 0; i < 16; i++)
			orientation[i] = temp[i];
	}

	public void ZRotate(float angle)
	{
		float[] zAxis = { 0, 0, 0, 0 };
		zAxis[0] = orientation[2];
		zAxis[1] = orientation[6];
		zAxis[2] = orientation[10];	

		float[] rotation = matrixAngleAroundAxis(angle, zAxis);
		float[] temp = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				
		Matrix.multiplyMM(temp, 0, rotation, 0, orientation, 0);
		
		for (int i = 0; i < 16; i++)
			orientation[i] = temp[i];
	}
	
	// Turns out there was a DX9 function built in that already does this.
	public float[] matrixAngleAroundAxis(float angle, float Ax, float Ay, float Az)
	{
		float[] result = new float[16];
		Matrix.setIdentityM(result, 0);

		float C = (float)Math.cos(angle);
		float S = (float)Math.sin(angle);	

		result[0] = C + Ax * Ax * (1 - C);
		result[4] = Ax * Ay * (1 - C) - Az * S;
		result[8] = Ax * Az * (1 - C) + Ay * S;
		result[1] = Ax * Ay * (1 - C) + Az * S;
		result[5] = C + Ay * Ay * (1 - C);
		result[9] = Ay * Az * (1 - C) - Ax * S;
		result[2] = Ax * Az * (1 - C) - Ay * S;
		result[6] = Ay * Az * (1 - C) + Ax * S;
		result[10] = C + Az * Az * (1 - C);

		return result;
	}

	public float[] matrixAngleAroundAxis(float angle, float[] axis)
	{	
		return matrixAngleAroundAxis(angle, axis[0], axis[1], axis[2]);
	}

	// computes the objects world transform
	public float[] computeWorldTransform()
	{
		float[] w = new float[16]; //{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		float[] t = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		float[] r = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		float[] s = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		
		//Matrix.setIdentityM(s, 0);
		//Matrix.scaleM(s, 0, scale[0], scale[1], scale[2]);
		
		
		
		Matrix.setIdentityM(t, 0);
		Matrix.translateM(t, 0, position[0], position[1], position[2]);
		Matrix.multiplyMM(w, 0, t, 0, orientation, 0);		
		Matrix.scaleM(w, 0, scale[0], scale[1], scale[2]);		
		
		//Matrix.translateM(r, 0, orientation, 0, position[0], position[1], position[2]);
		//Matrix.scaleM(w, 0, r, 0, scale[0], scale[1], scale[2]);
		
		
		//Matrix.multiplyMM(w, 0, r, 0, t, 0);
		
		return w;
	}
	
	// gets the world transform for a specific component of the model,
	// example: the arm's current position
	public float[] getComponentWorldTransform(int c)
	{		
		float[] worldtransformed = new float[16];
		float[] world;
		float[] transformed = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		float[] translation = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		float[] result = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		
		int m = c;
		
		int[] clist = new int[100];
		int count = 0;
		while (components[m].parent != -1)
		{
			clist[count] = m;
			count++;
			m = components[m].parent;
		}
		
		
		world = computeWorldTransform();
		worldtransformed = computeWorldTransform();

		for (int t = count - 1; t >= 0; t--)
		{
			m = clist[t];
			Matrix.translateM(transformed, 0, worldtransformed, 0, 
					  components[m].translation[0],
					  components[m].translation[1],
					  components[m].translation[2]);

			Matrix.multiplyMM(result, 0, transformed,  0, components[m].orientation, 0);

			for (int i = 0; i < 16; i++)
				worldtransformed[i] = result[i];
		}
		
		/*Matrix.setIdentityM(worldtransformed, 0);
		
		while (components[m].parent != -1)
		{	
			Matrix.translateM(transformed, 0, components[m].orientation, 0, 
											  components[m].translation[0],
											  components[m].translation[1],
											  components[m].translation[2]);
			
			Matrix.multiplyMM(result, 0, worldtransformed,  0, world, 0);
			
			for (int i = 0; i < 16; i++)
				worldtransformed[i] = result[i];
			m = components[m].parent;
		}
		
		Matrix.multiplyMM(result, 0, worldtransformed, 0, world, 0);

		for (int i = 0; i < 16; i++)
			worldtransformed[i] = result[i];*/

		return worldtransformed;

	}
	
	public void setComponents(Component[] c)
	{
		components = c;
		numComponents = components.length;
	}
	
	public void display(GL10 gl, float[] viewMatrix)
	{
		float[] world = computeWorldTransform();
		float[] modelViewMatrix = new float[16];
		
		Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, world, 0);		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadMatrixf(modelViewMatrix, 0);
			
		gl.glFrontFace(GL10.GL_CCW);		
		gl.glEnable(GL10.GL_CULL_FACE); 
		gl.glCullFace(GL10.GL_BACK);
		
		//gl.glEnable(GL10.GL_COLOR_MATERIAL);
		
		for (int c = 0; c < numComponents; c++)		
		{
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			//gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, components[c].vertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, components[c].texBuffer);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, components[c].normalBuffer);
			//gl.glColorPointer(4, GL10.GL_FLOAT, 0, components[c].colorBuffer);
			
			world = getComponentWorldTransform(c);
			Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, world, 0);		
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadMatrixf(modelViewMatrix, 0);
						
			for (int i = 0; i < components[c].numTriangleLists; i++)
			{
				int materialIndex = components[c].materialIndex[i];
				if (materialIndex != -1)
				{
					Material m = MaterialLibrary.materials.get(materialIndex);
					gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, m.Ambient, 0);
					gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, m.Diffuse, 0);
					gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, m.Specular, 0);
					gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, colorBlack, 0);
					if (m.texture != -1)
					{
						gl.glBindTexture(GL10.GL_TEXTURE_2D, MaterialLibrary.texturenames[m.texture]);
					} else {
						gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
					}
				}
				//gl.glDrawArrays(GL10.GL_TRIANGLES, 0, components[c].numVertices / 3);
				gl.glDrawElements(GL10.GL_TRIANGLES, components[c].numTriangles[i] * 3,
							  GL10.GL_UNSIGNED_SHORT, components[c].indexBuffer[i]);
			}
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
			
			gl.glDisable(GL10.GL_CULL_FACE);	
		}
	}
	
}
