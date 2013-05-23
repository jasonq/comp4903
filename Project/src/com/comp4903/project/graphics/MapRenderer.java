package com.comp4903.project.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.GUI.HUD;
import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.ColorType;
import com.comp4903.project.gameEngine.enums.IconType;
import com.comp4903.project.gameEngine.enums.UnitType;
import com.comp4903.project.graphics.animation.Actor;
import com.comp4903.project.graphics.animation.AnimationEngine;
import com.comp4903.project.graphics.animation.DeathAnimation;
import com.comp4903.project.graphics.animation.DefendAnimation;
import com.comp4903.project.graphics.animation.FloatingIcon;
import com.comp4903.project.graphics.animation.FloatingText;
import com.comp4903.project.graphics.animation.GenericAttack;
import com.comp4903.project.graphics.animation.GrabAnimation;
import com.comp4903.project.graphics.animation.HealthAnimation;
import com.comp4903.project.graphics.animation.MoveAnimate;
import com.comp4903.project.graphics.animation.ReceiveAttack;
import com.comp4903.project.graphics.model.Model3D;
import com.comp4903.project.graphics.model.ModelLoader;
import com.comp4903.project.graphics.tile.Hexagon;
import com.comp4903.project.network.Networking;
import com.comp4903.project.sound.SFX;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.opengl.Matrix;

/*	MAPRENDERER - Handles display of the map, models and animations
 * 
 */
public class MapRenderer {

	// device and resource access
	public GL10 gl;
	private Context context;
	
	// map statistics
	public int mapWidth;
	public int mapHeight;
	
	// map data
	private Hex tileMap[][];
	
	// vectors to be used throughout
	private float[] vec = new float[4];
	private float[] resultVec = new float[4];
	
	// standard 3d matrices
	public float[] viewMatrix;
	public float[] projectionMatrix;
	private float[] modelMatrix = new float[16];
	private float[] modelViewMatrix = new float[16];
	private static float eyeX;
	private float eyeY;
	private static float eyeZ;
	
	private Point selectedTile = new Point();	
	
	// handles rendering of Hexagon tiles
	private Hexagon hex;
	
	// keeps track of what the units (actors) are doing
	private HashMap<Integer, Actor> actors;
	
	// 3D model data
	public Model3D[] models;
		
	private float[] ambientLight = { 0.6f, 0.6f, 0.6f, 1 };
	private float[] grayedOutLight = { 0.3f, 0.3f, 0.3f, 1 };
	private float[] tileColor = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] diffuseLight = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] lightPosition = { 10.0f, 10.0f, 10.0f, 0.0f };	
	
	// to keep track of floating text and icons
	private ArrayList<FloatingText> floatingText_;
	private ArrayList<FloatingIcon> floatingIcons_;
	
	private ColorType _attackBoxColor;
	private ColorType _movementBoxColor;
	
	/*	CONSTRUCTOR - sets up data structures
	 * 
	 * 		g		GL10 openGL object
	 * 		c		Application context
	 */
	public MapRenderer(GL10 g, Context c)
	{
		gl = g;
		
		context = c;
		hex = new Hexagon(gl, context);
		hex.readTileData();
		actors = new HashMap<Integer, Actor>();
		AnimationEngine.init(gl, context);
		floatingText_ = new ArrayList<FloatingText>();
		floatingIcons_ = new ArrayList<FloatingIcon>();
		FloatingText.init(gl, context);
		FloatingIcon.init(gl, context);
		SFX.init(context);
				
	}	
	
	/*	LOADMODELS - loads and initializes the 3D model data
	 * 
	 */
	public void loadModels()
	{
		AssetManager am = context.getAssets();
		models = new Model3D[3];
		
		for (int t = 0; t < 3; t++)
		{
			models[t] = new Model3D();
			try {
				InputStream buf = null;
				if (t == 2)
					buf = am.open("models/marvin.gmodel");
				if (t == 1)
					buf = am.open("models/sniper.gmodel");
				if (t == 0)
					buf = am.open("models/swordmaster.gmodel");
				ModelLoader.load(buf, models[t], true);
				//models[t].SetScale(.08f, .08f, .08f);
				models[t].SetScale(0.07f, 0.07f, 0.07f);
				models[t].SetPosition(1, 1, 1);
				buf.close();
				
			} catch (IOException e)
			{ }			
		}		
	}
	
	/*	INIT - Used to initialize, or re-initialize the map
	 * 		w		Width
	 * 		h		Height
	 */
	public void init(int w, int h)
	{
		mapWidth = w;
		mapHeight = h;
		tileMap = new Hex[w][h];			
	}
	
	/*	RENDER - draw the map
	 * 
	 * 		viewM		view matrix of the camera
	 * 		projM		projection matrix of the camera
	 * 		eX, eY, eZ	eye position in map co-ordinates
	 */
	public void render(float[] viewM, 
					   float[] projM,
					   float eX,
					   float eY,
					   float eZ)
	{
		viewMatrix = viewM;
		projectionMatrix = projM;
		
		eyeX = eX;
		eyeY = eY;
		eyeZ = eZ;
				
		// execute one iteration of all active animations
		AnimationEngine.execute();	
		removeUnusedActors();
		
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLight, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseLight, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosition, 0);
		
		// execute the rendering passes
		backgroundPass();		// extra background tiles
		basePass();				// flat tiles
		selectionPass();		// selection effects
		actorPass();			// 3D units
		tileModelPass();		// 3D tiles
		
		gl.glDisable(GL10.GL_LIGHTING);
				
		floatingPass(); 	// floating text and icons, do last so it is overlayed over map
		
	}
	
	/*	FLOATINGPASS - displays the floating text and icons
	 * 
	 */
	public void floatingPass()
	{
		HUD.SwithToOrtho(gl);
		
		gl.glDisable(GL10.GL_DEPTH_TEST);
		
		gl.glEnable( GL10.GL_BLEND );                   // Enable Alpha Blend
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );  // Set Alpha Blend Function
				
		for (int i = 0; i < floatingText_.size(); i++)
			floatingText_.get(i).draw();		
		
		for (int i = 0; i < floatingIcons_.size(); i++)
			floatingIcons_.get(i).draw();
		
		gl.glDisable( GL10.GL_BLEND );                  // Disable Alpha Blend*/
		
		HUD.SwitchToPerspective(gl);
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}
	
	/*	ADDFLOATINGTEXT - adds a new line of text to appear on the screen as 
	 *  feedback.  Will also remove any inactive text that is currently being stored.
	 *  
	 *  x, y	screen co-ordinates
	 *  mx, my	motion vector for text (to make it rise up on the screen for example)
	 *  l		lifetime of the text (in no particular standard time measurement system)
	 *  col		text color (use ColorType enum)
	 *  n		name of the text (in case it needs to be identified)
	 *  c		content - what will be displayed
	 */
	public void addFloatingText(int x, int y, int mx, int my, int l, ColorType col, String n, String c)
	{		
		for (int i = 0; i < floatingText_.size(); i++)
		{
			if (!floatingText_.get(i).active)
				floatingText_.remove(i);
		}
		for (int i = 0; i < floatingText_.size(); i++)
		{
			if (floatingText_.get(i).name.equals(n))
			{
				floatingText_.get(i).set(x,y,mx,my,l,col,n,c);
				return;
			}
		}
		
		FloatingText f = new FloatingText(x,y,mx,my,l,col, n,c);
		floatingText_.add(f);
	}
	
	/*	same as above, but accepts 3 floats for x,y, and z.  This text will be fixed
	 *  to a point in 3D space (but will still move according to the motion vector)
	 */
	public void addFloatingText(float x, float y, float z, int mx, int my, int l, ColorType col, String n, String c)
	{		
		for (int i = 0; i < floatingText_.size(); i++)
		{
			if (!floatingText_.get(i).active)
				floatingText_.remove(i);
		}
		for (int i = 0; i < floatingText_.size(); i++)
		{
			if (floatingText_.get(i).name.equals(n))
			{
				//floatingText_.get(i).set(x,y,mx,my,l,col,n,c);
				return;
			}
		}
		
		FloatingText f = new FloatingText(x,y,z,mx,my,l,col, n,c);
		floatingText_.add(f);
	}
	
	// same as above, but for icons.  Use the IconType enum to specify which icon
	public void addFloatingIcon(int x, int y, int mx, int my, int l, String n, IconType i)
	{
		FloatingIcon f = new FloatingIcon(x,y,mx,my,l,n,i);
		
		for (int p = 0; p < floatingIcons_.size(); p++)
		{
			if (!floatingIcons_.get(p).active)
				floatingIcons_.remove(p);
		}
		floatingIcons_.add(f);
	}
	
	// another version for 3D points in space
	public void addFloatingIcon(float x, float y, float z, int mx, int my, int l, String n, IconType i)
	{
		FloatingIcon f = new FloatingIcon(x,y,z,mx,my,l,n,i);
		
		for (int p = 0; p < floatingIcons_.size(); p++)
		{
			if (!floatingIcons_.get(p).active)
				floatingIcons_.remove(p);
		}
		floatingIcons_.add(f);
	}
	
	/*	CLEARFLOATINGICONS - will de-activate icons with the specified name
	 * 	s	name of icon, or "all" for all icons
	 */
	public void clearFloatingIcons(String n)
	{		
		if (n.equals("all"))
		{
			floatingIcons_.clear();
		}
		else
		{
			for (int p = 0; p < floatingIcons_.size(); p++)
			{
				if (floatingIcons_.get(p).name != null)
				{
					if (floatingIcons_.get(p).name.equals(n))
					{
						floatingIcons_.get(p).active = false;
					}
				}
			}
		}
	}
	
	
	/* BASEPASS - clears the tile states and draws the base hexagon shaped floor tiles
	 * 
	 */
	private void basePass()
	{		
				
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{				
				float dx = (float)x * 1.5f;
				float dy = 0;
				float dz = (float)y * 0.8660254038f * 2 + (x % 2) * 0.8660254038f;											
				
				if (tileOnScreen(dx, dy, dz))
				{		
					if ((x == selectedTile.x) && (y == selectedTile.y))
						dy -= .1;
					
					// set up the model-view matrix for this hexagon
					Matrix.setIdentityM(modelMatrix, 0);				
					Matrix.multiplyMM(modelViewMatrix, 0, modelMatrix, 0, viewMatrix, 0);
					Matrix.translateM(modelViewMatrix, 0, dx, dy, dz);
					gl.glMatrixMode(GL10.GL_MODELVIEW);
					gl.glLoadMatrixf(modelViewMatrix, 0);
				
					hex.draw(gl, modelViewMatrix, projectionMatrix, 1, tileMap[x][y].tile, false);
				}
			}
		}
	}
	
	/* BACKGROUNDPASS - draws the background hexagons
	 * 
	 */
	private void backgroundPass()
	{		
				
		for (int x = -20; x < mapWidth + 20; x++)
		{
			for (int y = -20; y < mapHeight + 20; y++)
			{
				if ((x < 0) || (y < 0) || (x >= mapWidth) || (y >= mapHeight))
				{
					float dx = (float)x * 1.5f;
					float dy = 0;
					float dz = (float)y * 0.8660254038f * 2 + (x % 2) * 0.8660254038f;											
					
					if (tileOnScreen(dx, dy, dz))
					{								
						
						// set up the model-view matrix for this hexagon
						Matrix.setIdentityM(modelMatrix, 0);				
						Matrix.multiplyMM(modelViewMatrix, 0, modelMatrix, 0, viewMatrix, 0);
						Matrix.translateM(modelViewMatrix, 0, dx, dy, dz);
						gl.glMatrixMode(GL10.GL_MODELVIEW);
						gl.glLoadMatrixf(modelViewMatrix, 0);
						
						int t = (int)(0.567576f * (x * x) * (y*y)) % 6;
						if (t == 3)
							t = 4;
						t = 4;
						if ( ((x == -1) && (y >= -1) && (y <= mapHeight)) ||
						     ((x == mapWidth) && (y >= -1) && (y <= mapHeight)) ||
						     ((y == -1) && (x >= -1) && (x <= mapWidth)) ||
						     ((y == mapHeight) && (x >= -1) && (x <= mapWidth)))
							t = 5;
												
						hex.draw(gl, modelViewMatrix, projectionMatrix, 1, t, false);
					}
				}
			}
		}
	}
	
	/*	TILEONSCREEN - given x,y,z of center of tile, determines if it is
	 *  visible
	 * 
	 * 	dx,dy,dz	the center of the tile
	 * 
	 *  returns true or false
	 */
	public static boolean tileOnScreen(float dx, float dy, float dz)
	{
		/*
		Point p = RendererAccessor.ScreenXYfromXYZ(dx, dy, dz);
		
		if ((p.x == 10000) && (p.y < -1000))
			return false;
		else {
			if (p.y > GLRenderer.GLheight + 100)
				return false;
			if (p.y < -20)
				return false;
			if (p.x > GLRenderer.GLwidth + 120)
				return  false;
			if (p.x < -120)
				return false;
		}
		
		return true;*/
		
		if ((dx > eyeX - 16) &&
			(dz > eyeZ - 16) &&
			(dx < eyeX + 16) &&
			(dz < eyeZ + 8))
			return true;
		return false;
	}
	
	
		
	/*	SELECTIONPASS - Draws selection effects transparently over
	 *  floor tiles.  Some animation is built into this method, to
	 *  make the highlights shrink or expand. 
	 */
	private void selectionPass()
	{
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);					
		
		for (int x = 0; x < mapWidth; x++)
			for (int y = 0; y < mapHeight; y++)
			{
				float dx = (float)x * 1.5f;
				float dy = 0.01f;
				float dz = (float)y * 0.8660254038f * 2 + (x % 2) * 0.8660254038f;
				
				if (tileMap[x][y].state == 0)
				{
					if (tileMap[x][y].size < 1.0f)
						tileMap[x][y].size += 0.15f;
					else
						tileMap[x][y].size = 1.0f;
				}
												
				if (tileOnScreen(dx, dy, dz))
				{
					if ((x == selectedTile.x) && (y == selectedTile.y))
						dy -= .1;
					// set up the model-view matrix for this hexagon
					Matrix.setIdentityM(modelMatrix, 0);					
					Matrix.multiplyMM(modelViewMatrix, 0, modelMatrix, 0, viewMatrix, 0);
					Matrix.translateM(modelViewMatrix, 0, dx, dy, dz);
					
					if (tileMap[x][y].state == 0)
						Matrix.scaleM(modelViewMatrix, 0, tileMap[x][y].size, tileMap[x][y].size, tileMap[x][y].size);
					
					if (tileMap[x][y].state == 1)
					{
						tileMap[x][y].size += 0.02f;
						float sz = tileMap[x][y].size + 0.8f;
						if (sz > 1.2f)
							sz = 1.2f - (sz- 1.2f);
						if (tileMap[x][y].size > 0.8f)
							tileMap[x][y].size = 0;
						
						Matrix.scaleM(modelViewMatrix, 0, sz, sz, sz);
					}
					gl.glMatrixMode(GL10.GL_MODELVIEW);
					gl.glLoadMatrixf(modelViewMatrix, 0);
				
					if (tileMap[x][y].state != -1)
					{
						if (tileMap[x][y].state == 0)
							_movementBoxColor.getAsFloats(tileColor);
						if (tileMap[x][y].state == 1)
							_attackBoxColor.getAsFloats(tileColor);
						hex.setColor(tileColor);
						hex.draw(gl, modelViewMatrix, projectionMatrix, 0, tileMap[x][y].state, true);
					}
				}
			}
		gl.glDisable(GL10.GL_BLEND);			
	}
	
	/*	ACTORPASS - displays the 3D models associated with the units
	 * 
	 */
	private void actorPass()
	{
		Actor a;
		Iterator<Entry<Integer, Actor>> i = actors.entrySet().iterator();
		while (i.hasNext())
		{
			a = i.next().getValue();
			int mdl = a.getModel();
						
			float x = a.getX();
			float y = a.getY();
			float z = a.getZ();									
			
			models[mdl].SetPosition(x, y, z);
			models[mdl].ResetOrientation();	
			
			/*if ((!a.active) && (!a.displayInactive))
			{
				RendererAccessor.clearFloatingIcons("inactive" + a.getID());
				RendererAccessor.floatingIcon(a.getX(),
											  a.getY(),
											  a.getZ(),
											  0, 0, -1, "inactive" + a.getID(),
											  IconType.Lock);
				a.displayInactive = true;
			}*/
			
			a.display(gl, viewMatrix, models[mdl]);			
			
			
		}
	}
	
	// Some tile types (like buildings or generators) has 3D models that
	// display with them.  This pass handles that.
	private void tileModelPass()
	{				
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{					
				float dx = (float)x * 1.5f;
				float dy = 0;
				float dz = (float)y * 0.8660254038f * 2 + (x % 2) * 0.8660254038f;
				
				if ((x == selectedTile.x) && (y == selectedTile.y))
					dy -= .1;
				
				if (tileOnScreen(dx, dy, dz))
					hex.drawModel(gl, viewMatrix, dx, dy, dz, 1, tileMap[x][y].tile);				
			}
		}
	}
	
	// Tile picking, handled by Hexagon class
	public Point pick(float x, float y)
	{
		return hex.pick(x, y);
	}
	
	// Makes tile x,y the selected tile
	public void selectTile(int x, int y)
	{
		selectedTile.x = x;
		selectedTile.y = y;
		
		if (x == -1)
			return;		
		
		if (tileMap[x][y].state == -1)
		{
			tileMap[x][y].state = 0;
			tileMap[x][y].size = 0f;
		}
		else { tileMap[x][y].state = -1; }
		
	}
	
	// Makes tile x,y the selected tile as well
	public void highlight(int x, int y)
	{
		selectedTile.x = x;
		selectedTile.y = y;
					
	}
	
	/*	DEFINEMAP - reads in the map data, unit start positions, and anything
	 *  else required to initialize the map.
	 *  
	 *  	m		MapData structure containing the map specifics
	 * 
	 */
	public void defineMap(MapData m)
	{
		for (int x = 0; x < mapWidth; x++)
			for (int y = 0; y < mapHeight; y++)
			{
				tileMap[x][y] = new Hex();
				tileMap[x][y].tile = m._tileTypes[x][y].getCode();
				tileMap[x][y].state = -1;
			}
				
		/*tileMap[6][6].tile = 4;
		tileMap[6][7].tile = 4;
		tileMap[6][5].tile = 4;
		tileMap[5][6].tile = 4;
		tileMap[5][5].tile = 4;
		tileMap[7][7].tile = 5;*/
		//tileMap[8][8].tile = 6;
		//tileMap[9][9].tile = 7;
		
		
		 int mw = mapWidth / 2;
		 int mh = mapHeight / 2;
			
		GLRenderer.viewX = (float)mw * 1.5f;
		GLRenderer.viewY = 0;
		GLRenderer.viewZ = (float)mh * 0.8660254038f * 2 + (mw % 2) * 0.8660254038f;
		
		actors.clear();
		for (int i = 0; i < m._units.size(); i++)
		{
			Actor a = new Actor(m._units.get(i).uID);
			

			if (m._units.get(i).unitGroup.getCode() == 0)
				a.alt = true;
			else
				a.alt = false;
			
			a.setTilePosition(m._units.get(i).position);				
			a.setType(m._units.get(i).unitType);
			
			a.setPosition( (float)m._units.get(i).position.x * 1.5f,
					       0,
					       (float)m._units.get(i).position.y * 0.8660254038f * 2f 
					       + (m._units.get(i).position.x % 2) * 0.8660254038f);
			a.setAnimation("idle1");
			a.time = ((m._units.get(i).uID * 30) % 120); a.speed = 0.03f;
			actors.put(a.getID(), a);
		}
	}
	
	/*	UPDATE - updates the renderer with new unit positions, current user selections,
	 *  and other pertinent information which may affect what is being displayed
	 * 
	 * 		m		MapData structure with essentially the current state of the game
	 */
	public void update(MapData m)
	{
		
		// Block the renderer during the update.  Renderer executes
		// on a different thread.
		GLRenderer.pauseRender = true;				
		while(GLRenderer.isRenderingNow) 
		 {
			 try {
				Thread.sleep(10);
			} catch (InterruptedException e) {				
			}
		 }		
		
		_attackBoxColor = m._attackBoxColor;
		_movementBoxColor = m._movementBoxColor;
		
		for (int x = 0; x < mapWidth; x++)
			for (int y = 0; y < mapHeight; y++)
				tileMap[x][y].state = -1;
		
		// Identify which tiles need to be highlighted white
		for (int i = 0; i < m._movementBox.size(); i++)
		{
			int dx = m._movementBox.get(i).x;
			int dy = m._movementBox.get(i).y;
			tileMap[dx][dy].state = 0;
			tileMap[dx][dy].size = 0;
		}
		
		// Identify which tiles need to be highlighted red
		for (int i = 0; i < m._attackBox.size(); i++)
		{
			int dx = m._attackBox.get(i).x;
			int dy = m._attackBox.get(i).y;
			tileMap[dx][dy].state = 1;
			tileMap[dx][dy].size = 0;
		}		
		
		// Update the Actors, which are entities which handle the
		// state of the animations of the units they are associated with
		for (int i = 0; i < m._units.size(); i++)
		{
			if (actors.containsKey(m._units.get(i).uID))
			{
				Actor a = actors.get(m._units.get(i).uID);
				a.active = m._units.get(i).active;
				a.setTilePosition(m._units.get(i).position);				
			} 
			else
			{
				Actor a = new Actor(m._units.get(i).uID);
				a.active = m._units.get(i).active;
				
				if (m._units.get(i).unitGroup.getCode() == 0)
					a.alt = true;
				else
					a.alt = false;
				a.setTilePosition(m._units.get(i).position);				
				a.setType(m._units.get(i).unitType);
				
				a.setPosition( (float)m._units.get(i).position.x * 1.5f,
						       0,
						       (float)m._units.get(i).position.y * 0.8660254038f * 2f 
						       + (m._units.get(i).position.x % 2) * 0.8660254038f);
				a.setAnimation("idle1");
				a.time = 0; a.speed = 0.01f;
				actors.put(a.getID(), a);
			}
		}
		
		GLRenderer.pauseRender = false;
	}
	
	/*	REMOVEUNUSEDACTORS - clean up actor entities which are no longer being used,
	 *  usually used after the final 'death' animation of the unit. 
	 */
	public void removeUnusedActors()
	{
		Actor a;
		Iterator<Entry<Integer, Actor>> i = actors.entrySet().iterator();
		
		List<Integer> removeList = new ArrayList<Integer>();
		
		while (i.hasNext())
		{
			a = i.next().getValue();
			if (a.remove)
			{
				
				removeList.add(a.getID());
			}
		}
		
		for (int q = 0; q < removeList.size(); q++)
			actors.remove(removeList.get(q));
	}
	
	/*	MOVEANIMATION - initiates a unit moving animation through the
	 *  animation engine.
	 *  
	 *  u		Unit to move
	 *  steps	List of map points, in order, that the units move to
	 */
	public void moveAnimation(Unit u, List<Point> steps)
	{
		MoveAnimate m = new MoveAnimate();
		
		m.init(u, steps);
		
		AnimationEngine.add("move", m);
		AnimationEngine.start("move");
	}
	
	/*	ATTACKANIMATION - initiates an attack animation through the
	 *  animation engine.
	 *  
	 *  u			unit who initiates the attack
	 *  u2			unit who receives the attack
	 *  messages	text to display above the recipient unit (array of string)
	 */
	public void attackAnimation(Unit u, Unit u2, String[] messages)
	{
		GenericAttack m = new GenericAttack();
		ReceiveAttack r = new ReceiveAttack();
		
		m.init(u, u2);
		r.init(u,  u2, messages);
		
		AnimationEngine.add("Attack", m);
		AnimationEngine.add("Receiver", r);
		AnimationEngine.start("Attack");
		AnimationEngine.start("Receiver");
	}
	
	/*	DEATHANIMATION - submits a death animation to the 
	 *  animation engine
	 * 
	 * 	u	applicable unit
	 */
	public void deathAnimation(Unit u) {
		DeathAnimation d = new DeathAnimation();
		d.init(u);
		AnimationEngine.add("Death" + u.uID, d);
		AnimationEngine.start("Death" + u.uID);
		
	}
	
	/*	HEALTHANIMATION - submits a healing animation to the 
	 *  animation engine
	 * 
	 * 	u		applicable unit
	 *  val		text to appear above unit (usually indicates healing amount)
	 */
	public static void healthAnimation(Unit u, String val)
	{
		HealthAnimation h = new HealthAnimation();
		h.init(u,  val);
		AnimationEngine.add("Health" + u.uID, h);
		AnimationEngine.start("Health" + u.uID);
	}
	
	/*	DEFENDANIMATION - submits a defend animation to the 
	 *  animation engine (Usually just an icon appearing, not much of
	 *  an animation)
	 * 
	 * 	u		applicable unit	
	 */
	public static void defendAnimation(Unit u)
	{
		DefendAnimation d = new DefendAnimation();
		d.init(u);
		AnimationEngine.add("Defend" + u.uID, d);
		AnimationEngine.start("Defend" + u.uID);
	}
	
	/*	GRABANIMATION - initiates the grab animation in which one unit
	 *  uses telekinesis to levitate another unit to bring it into
	 *  better position to receive an attack
	 *  
	 *  u			Unit who initiates the action
	 *  u2			Unit upon whom the action is performed
	 *  p			destination point on map where u2 will be transported
	 *  damages		array of text messages which appear above u2
	 * 
	 */
	public static void grabAnimation(Unit u, Unit u2, Point p, String[] damages)
	{
		GrabAnimation d = new GrabAnimation();
		d.init(u, u2, p, damages);
		AnimationEngine.add("Grab" + u.uID, d);
		AnimationEngine.start("Grab" + u.uID);		
		
	}
	
	/*	HEADSHOTANIMATION - same as attack animation, but specific to a
	 *  certain unit type 
	 */
	public void headShotAnimation(Unit u, Unit u2, String[] messages)
	{
		GenericAttack m = new GenericAttack();
		ReceiveAttack r = new ReceiveAttack();
		
		m.init(u, u2);
		m.setSpeed(0.08f);
		r.init(u,  u2, messages);
		
		AnimationEngine.add("Attack", m);
		AnimationEngine.add("Receiver", r);
		AnimationEngine.start("Attack");
		AnimationEngine.start("Receiver");
	}
	
	/*	GETACTOR - given an ID, returns the actor associated with it
	 * 
	 */
	public Actor getActor(int id)
	{
		return actors.get(id);
	}
		
	/*	HEX - represents a single tile on the map, storing information about
	 *  texture, current highlighted status and other info.
	 * 
	 */
	private class Hex {
		int tile;
		int state;
		float size = 0f;
	}

	

	
}
