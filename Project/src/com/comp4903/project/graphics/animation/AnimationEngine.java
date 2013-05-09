package com.comp4903.project.graphics.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class AnimationEngine {
	
	private GL10 gl;
	private Context context;
	
	private HashMap<String, AnimationProcessor> animations_;
	
	public AnimationEngine(GL10 g, Context c)
	{
		gl = g;
		context = c;
		
		animations_ = new HashMap<String, AnimationProcessor>();
	}
	
	public void add(String name, AnimationProcessor a)
	{
		a.name = name;
		animations_.put(name, a);
	}
	
	public void start(String name)
	{
		animations_.get(name).started = true;
	}
	
	public void end(String name)
	{
		animations_.get(name).ended = true;
	}
	
	public void execute()
	{
		AnimationProcessor a;
		List<String> removeList = new ArrayList<String>();
	
		Iterator<Entry<String, AnimationProcessor>> i = animations_.entrySet().iterator();
		while (i.hasNext())
		{
			a = i.next().getValue();
			if ((a.started) && (!a.ended))
				a.process();
			if (a.ended)
				removeList.add(a.name);
		}
		
		for (int q = 0; q < removeList.size(); q++)
			animations_.remove(removeList.get(q));
	}
	
}