package com.comp4903.project.graphics.model;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.Matrix;

public class Model3D {	
	
	public String name;
	
	private int numComponents;
	public Component[] components;
	
	private float[] orientation = new float[16];
	public float[] position = new float[4];
	public float[] scale = new float[4];
	private float[] tempMatrix = new float[16];
	
	private float[] colorBlack = { 0f, 0f, 0f, 0f };
	
	private Animation[] animations_;
	private int numberOfAnimations;
	
	boolean walking;
	
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
		walking = false;
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
			//for (int i = 0; i < 16; i++)
			//	components[c].orientation[i] = components[c].neutralOrientation[i];
			Matrix.setIdentityM(components[c].orientation, 0);
			
			components[c].Xrotate = 0;
			components[c].Yrotate = 0;
			components[c].Zrotate = 0;
		}
		components[0].translation[0] = 0;
		components[0].translation[1] = 0;
		components[0].translation[2] = 0;
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
	
	public void YRotateComponent(int c, float angle)
	{
		float[] yAxis = { 0, 0, 0, 0 };
		yAxis[0] = components[c].orientation[1];
		yAxis[1] = components[c].orientation[5];
		yAxis[2] = components[c].orientation[9];	

		float[] rotation = matrixAngleAroundAxis(angle, yAxis);
		float[] temp = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				
		Matrix.multiplyMM(temp, 0, rotation, 0, components[c].orientation, 0);
		
		for (int i = 0; i < 16; i++)
			components[c].orientation[i] = temp[i];
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
	public float[] computeWorldTransform(float[] adjust)
	{
		float[] w = new float[16]; //{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		float[] t = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		float[] r = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		float[] s = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				
		float multiplier = 0;
		
		if (walking)
			multiplier = 0;
		else 
			multiplier = 1f;
		
		Matrix.setIdentityM(t, 0);
		Matrix.translateM(t, 0, position[0], 
								position[1], 
								position[2] );
		
		Matrix.multiplyMM(w, 0, t, 0, components[0].orientation, 0);	
		
		Matrix.translateM(w, 0, adjust[0], 
				adjust[1], 
				adjust[2] * multiplier);
		
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
		
		
		world = computeWorldTransform(components[0].translation);
		worldtransformed = computeWorldTransform(components[0].translation);

		for (int t = count - 1; t >= 0; t--)
		{
			m = clist[t];
			Matrix.translateM(transformed, 0, worldtransformed, 0, 
					  components[m].initialTranslation[0],
					  components[m].initialTranslation[1],
					  components[m].initialTranslation[2]);

			Matrix.multiplyMM(result, 0, transformed,  0, components[m].orientation, 0);			
			
			for (int i = 0; i < 16; i++)
				worldtransformed[i] = result[i];
		}		
		
		return worldtransformed;

	}
	
	public void setComponents(Component[] c)
	{
		components = c;
		numComponents = components.length;
	}
	
	public void setNumberOfAnimations(int a) { 
		numberOfAnimations = a;
		animations_ = new Animation[a];
	}
	
	public int getNumberOfAnimations() { return numberOfAnimations; }
	
	public void setAnimationName(int a, String n) { animations_[a].name = n; }
	public String getAnimationName(int a) { return animations_[a].name; }
	
	public void setAnimationFrameTranslation(int a, int f, Vector3 v)
	{
		animations_[a].setTranslation(f, v);
	}
	
	public Vector3 getAnimationFrameTranslation(int a, int f)
	{
		return animations_[a].getTranslation(f);
	}
	
	public void setSignal(int a, int f, int s) { animations_[a].setSignal(f,  s); }
	public int getSignal(int a, int f) { return animations_[a].getSignal(f); }
	
	public int getNumberOfComponents() { return numComponents; }
	public void newAnimation(int a) { animations_[a] = new Animation(numComponents); }
	
	public void setComponentFrame(int a, int c, int f, float[] m, float[] v)
	{
		animations_[a].setComponentFrame(c, f, m, v);
	}
	
	public void display (GL10 gl, float[] viewMatrix)
	{
		display(gl, viewMatrix, -1, 0f);
	}
	
	public void display(GL10 gl, float[] viewMatrix, int animation, float time)
	{
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, colorBlack, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, colorBlack, 0);
		
		walking = false;
		if (animation != -1)
		{
			if (animations_[animation].name.startsWith("walk", 0))
				walking = true;
		}
		
		float[] world = computeWorldTransform(components[0].translation);
		float[] modelViewMatrix = new float[16];		
				
		Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, world, 0);		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadMatrixf(modelViewMatrix, 0);
			
		gl.glFrontFace(GL10.GL_CCW);		
		gl.glEnable(GL10.GL_CULL_FACE); 
		gl.glCullFace(GL10.GL_BACK);		
		
		for (int c = 0; c < numComponents; c++)		
		{
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);			
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, components[c].vertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, components[c].texBuffer);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, components[c].normalBuffer);	
			
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
					if (m.texture != -1)
					{
						gl.glBindTexture(GL10.GL_TEXTURE_2D, MaterialLibrary.texturenames[m.texture]);
					} else {
						gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
					}
				}				
				
				gl.glDrawElements(GL10.GL_TRIANGLES, components[c].numTriangles[i] * 3,
							  GL10.GL_UNSIGNED_SHORT, components[c].indexBuffer[i]);
			}
				
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		
		gl.glDisable(GL10.GL_CULL_FACE);
	}
	
	public float setPose(int animation, float time)
	{
		if (animation == -1)
		{
			recallNeutralPose();
			return 0f;
		}
		
		int frame1 = (int)Math.floor((double)time);
		int frame2 = (int)Math.ceil((double)time);
		
		float percent = time - frame1;
		
		for (int c = 0; c < numComponents; c++)
		{
			float[] matrix1 = animations_[animation].getComponentOrientation(c, frame1);
			float[] matrix2 = animations_[animation].getComponentOrientation(c, frame2);
			
			for (int i = 0; i < 16; i++) {
				components[c].orientation[i] = (matrix2[i] - matrix1[i]) * percent + matrix1[i];
			}
		}
		
		float[] trans1 = animations_[animation].getComponentTranslation(0, frame1);
		float[] trans2 = animations_[animation].getComponentTranslation(0, frame2);
		for (int i = 0; i < 3; i++) {
			components[0].translation[i] = ((trans2[i] - trans1[i]) * percent + trans1[i]) * scale[i];
		}
		
		return components[0].translation[2];
		
	}
	
	public int getAnimationIndex(String animationName)
	{
		for (int i = 0; i < numberOfAnimations; i++)
			if (animations_[i].name.compareTo(animationName)==0)
				return i;
		
		return -1;
	}
}
