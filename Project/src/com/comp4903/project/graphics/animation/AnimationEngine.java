package com.comp4903.project.graphics.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

/*	ANIMATIONENGINE - static class which manages the various ongoing animations.
 *  Animations are kept track of in objects of classes derived from AnimationProcessor.
 *  
 *  The engine does not handle the display or looping of animations, or deal directly with
 *  the individual frames (see the Actor and Model3D class), but handles the overall
 *  scheduling and intercommunication of the animations.  
 *  
 *  For example, an attack animation may be initiated, but at a certain point during the
 *  attack another animation is started depicting the reaction of the recipient.  It is
 *  these interactions that are handled here.
 * 
 */
public class AnimationEngine {
	
	private static GL10 gl;
	private static Context context;
	
	private static Semaphore AnimationListAccess;
	
	private static HashMap<String, AnimationProcessor> animations_;
	
	/*	INIT - set up the class members
	 * 
	 */
	public static void init(GL10 g, Context c)
	{
		gl = g;
		context = c;
		
		animations_ = new HashMap<String, AnimationProcessor>();
		
		AnimationListAccess = new Semaphore(1);
	}
	
	private static void acquireAccess()
	{
		try {
			AnimationListAccess.acquire();			
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}	
	}
	
	/*	ADD - adds a new animation to the system
	 * 	
	 * 	name		animation name, as a string.  Should be unique, although not required
	 *  a			AnimationProcessor object
	 */
	public static void add(String name, AnimationProcessor a)
	{
		acquireAccess();
		
		a.name = name;
		animations_.put(name, a);
		
		AnimationListAccess.release();
	}
	
	/*	This version is not safe from a synchronization standpoint.
	 *  Use only if the semaphore AnimationListAccess has already been
	 *  acquired, such as in an AnimationProcessor's iteration() method  
	 * 
	 */
	public static void unsafeAdd(String name, AnimationProcessor a)
	{		
		a.name = name;
		animations_.put(name, a);	
	}
	
	/*	START - begin executing the animation's processor
	 *
	 * 	name	animation name
	 */
	public static void start(String name)
	{
		acquireAccess();
		
		animations_.get(name).started = true;
		
		AnimationListAccess.release();
	}
	
	// see unsafeAdd(..)
	public static void unsafeStart(String name)
	{		
		
		animations_.get(name).started = true;
		
	}
	
	/*	END - stop processing the animation's processor.  Note that the associated
	 *  Actor's animation frame cycle may still continue to execute.
	 * 
	 */
	public static void end(String name)
	{
		acquireAccess();
		animations_.get(name).ended = true;
		AnimationListAccess.release();
	}
	
	/*	EXECUTE - execute one iteration of each active AnimationProcessor 
	 * 
	 */
	public static void execute()
	{
		
		acquireAccess();
		
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
		
		AnimationListAccess.release();
			
	}
	
	/*	SIGNAL - sends a signal to a certain animation, not to be
	 *  confused with multi-threaded synchronization
	 * 
	 * 	animationName	name of the recipient animation (or "all")
	 *  value			numeric value of the signal
	 */
	public static void signal(String animationName, int value)
	{
		AnimationProcessor a;	
		if (animationName.equals("all"))
		{					
		
			Iterator<Entry<String, AnimationProcessor>> i = animations_.entrySet().iterator();
			while (i.hasNext())
			{
				a = i.next().getValue();
				if ((a.started) && (!a.ended))
					a.signal(value);			
			}
		}
		else
		{
			a = animations_.get(animationName);
			if ((a.started) && (!a.ended))
				a.signal(value);		
		}
	}
	
	/*	NOFOREGROUNDANIMATIONS - checks to see if there are any executing
	 *  animations that are meant to run in the foreground.  Foreground animations
	 *  generally have a priority indicating there is to be no user interaction
	 *  until it is complete.
	 */
	public static boolean noForegroundAnimations()
	{
		Iterator<Entry<String, AnimationProcessor>> i = animations_.entrySet().iterator();
		while (i.hasNext())
		{
			AnimationProcessor a = i.next().getValue();
			if ((!a.ended) && (a.foreground))
				return false;
		}
		
		return true;
	}
}
