package com.comp4903.project.gameEngine.enums;

public enum ColorType {
	
		None(-1),
		Red(0x0FFFF0000),
		Green(0x0FF00FF00),
		Blue(0x0FF0000FF),
		White(0x0FFFFFFFF);
		
		private int code;
		
		private ColorType(int c)
		{
			code = c;
		}
		
		public int getCode() {
			return code;
		}
		
		public float[] getAsFloats()
		{
			float[] r = new float[4];
			r[0] = (code >> 16) & 0x0FF;
			r[1] = (code >> 8) & 0x0FF;
			r[2] = (code) & 0x0FF;
			r[3] = 1.0f;
			
			r[0] = r[0] / 255.0f;
			r[1] = r[1] / 255.0f;
			r[2] = r[2] / 255.0f;
			
			return r;
			
		}
	
}
