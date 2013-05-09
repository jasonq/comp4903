package com.comp4903.project.graphics.animation;

public abstract class AnimationProcessor {

	int delay;
	boolean started;
	boolean ended;
	
	public abstract boolean process();
}
