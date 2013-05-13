package com.comp4903.project.graphics.model;

public class Animation {
	
	public String name;
	
	private Vector3[] translations = new Vector3[120];
	private int[] signals = new int[120];
	private FrameSequence[] components;
	
	public Animation(int c)
	{
		components = new FrameSequence[c];
		for (int i = 0; i < c; i++)
			components[i] = new FrameSequence();
	}
	
	public Vector3 getTranslation(int f)
	{
		return translations[f];
	}
	
	public void setTranslation(int f, Vector3 v)
	{
		translations[f] = v;
		/*translations[f].x = v.x;
		translations[f].y = v.y;
		translations[f].z = v.z;
		translations[f].w = v.w;*/
	}
	
	public int getSignal(int f)	{ return signals[f]; }
	
	public void setSignal(int f, int s) { signals[f] = s; }
	
	public float[] getComponentOrientation(int c, int f)
	{
		return components[c].frames[f].orientation;
	}
	
	public float[] getComponentTranslation(int c, int f)
	{
		return components[c].frames[f].translation;
	}
	
	public void setComponentFrame(int c, int f, float[] m, float[] v)
	{
		//for (int i = 0; i < 16; i++)
		//	components[c].frames[f].orientation[i] = m[i];
		
		components[c].frames[f].orientation = m;
		components[c].frames[f].translation = v;
	}
	
	public class FrameSequence
	{
		Frame[] frames = new Frame[120];
		
		public FrameSequence()
		{
			for (int i = 0; i < 120; i++)
				frames[i] = new Frame();
		}
	}
	
	public class Frame 
	{
		float[] orientation = new float[16];
		float[] translation = new float[4];
		
	}
}
