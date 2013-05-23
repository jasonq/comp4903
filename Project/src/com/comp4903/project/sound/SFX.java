package com.comp4903.project.sound;


import com.comp4903.project.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SFX {

	private static SoundPool library_;
	static Context context;
	
	public static int LASER;
	public static int BOOM;
	public static int BUMP;
	public static int SWOOSH;
	
	public static void init(Context c)
	{
		context = c;
		
		library_ = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		
		LASER = library_.load(context, R.raw.fxlazer283, 1);
		BOOM = library_.load(context, R.raw.boom5, 1);
		BUMP = library_.load(context, R.raw.bump1, 1);
		SWOOSH = library_.load(context, R.raw.swoosh, 1);
	}
	
	public static void play(int s)
	{
		library_.play(s, 1.0f, 1.0f, 0, 0, 1.0f);
	}
	
}
