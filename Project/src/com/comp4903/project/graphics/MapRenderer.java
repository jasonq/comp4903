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
import com.comp4903.project.graphics.animation.FloatingIcon;
import com.comp4903.project.graphics.animation.FloatingText;
import com.comp4903.project.graphics.animation.GenericAttack;
import com.comp4903.project.graphics.animation.HealthAnimation;
import com.comp4903.project.graphics.animation.MoveAnimate;
import com.comp4903.project.graphics.animation.ReceiveAttack;
import com.comp4903.project.graphics.model.Model3D;
import com.comp4903.project.graphics.model.ModelLoader;
import com.comp4903.project.graphics.tile.Hexagon;
import com.comp4903.project.network.NetworkAccessor;
import com.comp4903.project.network.Networking;

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
	private float eyeX, eyeY, eyeZ;
	
	private Point selectedTile = new Point();	
	
	// handles rendering of Hexagon tiles
	private Hexagon hex;
	
	// keeps track of what the units (actors) are doing
	private HashMap<Integer, Actor> actors;
	
	// 3D model data
	public Model3D[] models;
		
	private float[] ambientLight = { 0.6f, 0.6f, 0.6f, 1 };
	private float[] tileColor = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] diffuseLight = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] lightPosition = { 10.0f, 10.0f, 10.0f, 0.0f };	
	
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
				
	}
	
	public void networkTest()
	{
		if (Networking.IP != null)
			RendererAccessor.floatingText(20, 20, 0, 0, -1, ColorType.White, "bozo", Networking.IP);
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
				ModelLoader.load(buf, models[t]);
				models[t].SetScale(.08f, .08f, .08f);
				models[t].SetPosition(1, 1, 1);
				buf.close();
				
			} catch (IOException e)
			{ }			
		}
		
		models[0].SetScale(.75f, .75f, .75f);
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
		networkTest();
			
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
		basePass();				// flat tiles
		selectionPass();		// selection effects
		actorPass();			// 3D units
		tileModelPass();		// 3D tiles
		
		gl.glDisable(GL10.GL_LIGHTING);
				
		floatingPass(); 	// floating text, do last so it is overlayed over map
		
	}
	
	private void floatingPass()
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
	
	public void addFloatingText(int x, int y, int mx, int my, int l, ColorType col, String n, String c)
	{
		FloatingText f = new FloatingText(x,y,mx,my,l,col, n,c);
		
		for (int i = 0; i < floatingText_.size(); i++)
		{
			if (!floatingText_.get(i).active)
				floatingText_.remove(i);
		}
		floatingText_.add(f);
	}
	
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
	
	
	/* BASEPASS - clears the tile states and draws the base hexagon shaped floor tiles
	 * 
	 */
	private void basePass()
	{
		//hex.screenCoordCount = 0;
				
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				//tileMap[x][y].state = -1;
				float dx = (float)x * 1.5f;
				float dy = 0;
				float dz = (float)y * 0.8660254038f * 2 + (x % 2) * 0.8660254038f;
				
				//if ((x == selectedTile.x) && (y == selectedTile.y))
				//	dy = .1f;
				
				if ((dx > eyeX - 16) &&
					(dz > eyeZ - 16) &&
					(dx < eyeX + 16) &&
					(dz < eyeZ + 8))
				{
					//hex.screenCoords[hex.screenCoordCount][6].x = x;
					//hex.screenCoords[hex.screenCoordCount][6].y = y;
				
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
		
	/*	SELECTIONPASS - Draws selection effects transparently over
	 *  floor tiles 
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
												
				if ((dx > eyeX - 16) &&
						(dz > eyeZ - 16) &&
						(dx < eyeX + 16) &&
						(dz < eyeZ + 8))
				{
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
								
			/*gl.glEnable(GL10.GL_LIGHTING);
			gl.glEnable(GL10.GL_LIGHT0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLight, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseLight, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosition, 0);
			*/
			models[mdl].SetPosition(x, y, z);
			models[mdl].ResetOrientation();
			//models[mdl].XRotate(a.getXrotate());
			//models[mdl].YRotateComponent(0, a.getYrotate());			
			//models[mdl].ZRotate(a.getZrotate());
			a.display(gl, viewMatrix, models[mdl]);
			
			//gl.glDisable(GL10.GL_LIGHTING);
			
		}
	}
	
	private void tileModelPass()
	{
				
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				//tileMap[x][y].state = -1;
				float dx = (float)x * 1.5f;
				float dy = 0;
				float dz = (float)y * 0.8660254038f * 2 + (x % 2) * 0.8660254038f;
				
				//if ((x == selectedTile.x) && (y == selectedTile.y))
				//	dy = .1f;
				
				if ((dx > eyeX - 16) &&
					(dz > eyeZ - 16) &&
					(dx < eyeX + 16) &&
					(dz < eyeZ + 8))
				{
									
					// set up the model-view matrix for this hexagon
					/*Matrix.setIdentityM(modelMatrix, 0);				
					Matrix.multiplyMM(modelViewMatrix, 0, modelMatrix, 0, viewMatrix, 0);
					Matrix.translateM(modelViewMatrix, 0, dx, dy, dz);
					gl.glMatrixMode(GL10.GL_MODELVIEW);
					gl.glLoadMatrixf(modelViewMatrix, 0);*/
					
					hex.drawModel(gl, viewMatrix, dx, dy, dz, 1, tileMap[x][y].tile);
				}
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
				
		actors.clear();
		for (int i = 0; i < m._units.size(); i++)
		{
			Actor a = new Actor(m._units.get(i).uID);
			
			a.setTilePosition(m._units.get(i).position);				
			a.setType(m._units.get(i).unitType);
			
			a.setPosition( (float)m._units.get(i).position.x * 1.5f,
					       0,
					       (float)m._units.get(i).position.y * 0.8660254038f * 2f 
					       + (m._units.get(i).position.x % 2) * 0.8660254038f);
			a.setAnimation("idle1");
			a.time = 0; a.speed = 0.03f;
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
		//GLRenderer.pauseRender = true;
		//while (GLRenderer.isRenderingNow);
		
		_attackBoxColor = m._attackBoxColor;
		_movementBoxColor = m._movementBoxColor;
		
		for (int x = 0; x < mapWidth; x++)
			for (int y = 0; y < mapHeight; y++)
				tileMap[x][y].state = -1;
		
		for (int i = 0; i < m._movementBox.size(); i++)
		{
			int dx = m._movementBox.get(i).x;
			int dy = m._movementBox.get(i).y;
			tileMap[dx][dy].state = 0;
			tileMap[dx][dy].size = 0;
		}
		
		for (int i = 0; i < m._attackBox.size(); i++)
		{
			int dx = m._attackBox.get(i).x;
			int dy = m._attackBox.get(i).y;
			tileMap[dx][dy].state = 1;
			tileMap[dx][dy].size = 0;
		}		
		
		//actors.clear();
		for (int i = 0; i < m._units.size(); i++)
		{
			if (actors.containsKey(m._units.get(i).uID))
			{
				Actor a = actors.get(m._units.get(i).uID);
				a.setTilePosition(m._units.get(i).position);				
			} 
			else
			{
				Actor a = new Actor(m._units.get(i).uID);
				
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
		
		//GLRenderer.pauseRender = false;
	}
	
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
	
	public void moveAnimation(Unit u, List<Point> steps)
	{
		MoveAnimate m = new MoveAnimate();
		
		m.init(u, steps);
		
		AnimationEngine.add("move", m);
		AnimationEngine.start("move");
	}
	
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
	
	public void deathAnimation(Unit u) {
		DeathAnimation d = new DeathAnimation();
		d.init(u);
		AnimationEngine.add("Death" + u.uID, d);
		AnimationEngine.start("Death" + u.uID);
		
	}
	
	public static void healthAnimation(Unit u, String val)
	{
		HealthAnimation h = new HealthAnimation();
		h.init(u,  val);
		AnimationEngine.add("Health" + u.uID, h);
		AnimationEngine.start("Health" + u.uID);
	}
	
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
