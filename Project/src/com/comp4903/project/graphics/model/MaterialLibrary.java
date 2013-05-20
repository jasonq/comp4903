package com.comp4903.project.graphics.model;

import java.io.IOException;

import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.comp4903.project.R;

/*	MATERIALLIBRARY - provides methods to manage the library of OpenGL materials
 *  and textures.
 *  
 *  init
 *  getMaterialIndex
 *  addMaterial
 *  getTextureIndex
 *  addTexture
 *  loadTexture
 * 
 */
public class MaterialLibrary {
	
	public static int numMaterials;
	public static int numTextures;
	
	public static ArrayList<Material> materials;
	public static ArrayList<Texture> textures;
	
	public static int[] texturenames = new int[100];
	
	public static GL10 gl;
	public static Context context;	
	
	public static void init(GL10 g, Context c)
	{
		gl = g;
		context = c;
		// sets aside 100 texturemap entries in openGL
		gl.glGenTextures(100, texturenames, 0);
		
		materials = new ArrayList<Material>();
		textures = new ArrayList<Texture>();		
		
	}
	
	public static int getMaterialIndex(String n)
	{
		int result = -1;
		
		for (int i = 0; i < numMaterials; i++)
			if (materials.get(i).name.equals(n))
				return i;
		
		return result;
	}
	
	public static int addMaterial(Material t)
	{
		int indx = -1;
		for (int i = 0; i < numMaterials; i++)
			if (t.name.equals(materials.get(i).name))
				indx = i;
		if (indx != -1)
			materials.set(indx, t);
		else
		{
			indx = numMaterials;
			materials.add(t);
			numMaterials++;
		}
		return indx;
	}
	
	public static int getTextureIndex(String n)
	{
		int result = -1;
		
		for (int i = 0; i < numTextures; i++)
			if (textures.get(i).name.equals(n))
				return i;
		
		return result;
	}
	
	public static int addTexture(Texture t)
	{
		int indx = -1;
		for (int i = 0; i < numTextures; i++)
			if (t.name.equals(textures.get(i).name))
				indx = i;
		if (indx != -1)
			textures.set(indx, t);
		else
		{
			indx = numTextures;
			textures.add(t);
			numTextures++;
		}
		return indx;
	}
	
	// loads a texture map into openGL.
	// i is the textures maps index #
	public static void loadTexture(int i)
	{
		AssetManager am = context.getAssets();
		
		String path = "textures/";
		
		String filename = textures.get(i).filename;
		
		//if (filename.startsWith("assets//"))
		//{
		//	filename = "models/" + filename;//.substring(8);
		//}
		
		path += filename; //textures.get(i).filename;
		
		Bitmap bitmap = null;	
		try {
			InputStream is = context.getAssets().open(path);
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) 
		{}
				
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturenames[i]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
	}
}
