package com.comp4903.project.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.microedition.khronos.opengles.GL10;

import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.UnitType;
import com.comp4903.project.graphics.animation.AnimationEngine;
import com.comp4903.project.graphics.animation.MoveAnimate;
import com.comp4903.project.graphics.model.Model3D;
import com.comp4903.project.graphics.model.ModelLoader;
import com.comp4903.project.graphics.tile.Hexagon;

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
	private int mapWidth;
	private int mapHeight;
	
	// map data
	private Hex tileMap[][];
	
	// vectors to be used throughout
	private float[] vec = new float[4];
	private float[] resultVec = new float[4];
	
	// standard 3d matrices
	private float[] viewMatrix;
	private float[] projectionMatrix;
	private float[] modelMatrix = new float[16];
	private float[] modelViewMatrix = new float[16];
	private float eyeX, eyeY, eyeZ;
	
	private Point selectedTile = new Point();	
	
	// handles rendering of Hexagon tiles
	private Hexagon hex;
	
	// keeps track of what the units (actors) are doing
	private HashMap<Integer, Actor> actors;
	
	// 3D model data
	private Model3D[] models;
		
	private float[] ambientLight = { 0.4f, 0.4f, 0.4f, 1 };
	private float[] diffuseLight = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] lightPosition = { 10.0f, 10.0f, 10.0f, 10.0f };
	
	AnimationEngine animator;
	
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
		animator = new AnimationEngine(gl, context);
	}
	
	/*	LOADMODELS - loads and initializes the 3D model data
	 * 
	 */
	public void loadModels()
	{
		AssetManager am = context.getAssets();
		models = new Model3D[2];
		
		for (int t = 0; t < 2; t++)
		{
			models[t] = new Model3D();
			try {
				InputStream buf = null;
				if (t == 0)
					buf = am.open("models/testmodel.mdl");
				else
					buf = am.open("models/soldier.gmodel");
				ModelLoader.load(buf, models[t]);
				models[t].SetScale(.08f, .08f, .08f);
				models[t].SetPosition(1, 1, 1);
				buf.close();
				
			} catch (IOException e)
			{ }			
		}
		models[0].SetPosition(21,1,21);		
		//models[1].YRotate(1.6f);
		//models[1].XRotate(1.57f);
		models[1].SetScale(1, 1, 1);
		models[1].SetPosition(26, 0, 23);
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
		animator.execute();
		
		// execute the rendering passes
		basePass();				// flat tiles
		selectionPass();		// selection effects
		actorPass();			// 3D units
		tileModelPass();		// 3D tiles
	}
	
	/* BASEPASS - clears the tile states and draws the base hexagon shaped floor tiles
	 * 
	 */
	private void basePass()
	{
		hex.screenCoordCount = 0;
				
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
					hex.screenCoords[hex.screenCoordCount][6].x = x;
					hex.screenCoords[hex.screenCoordCount][6].y = y;
				
					// set up the model-view matrix for this hexagon
					Matrix.setIdentityM(modelMatrix, 0);				
					Matrix.multiplyMM(modelViewMatrix, 0, modelMatrix, 0, viewMatrix, 0);
					Matrix.translateM(modelViewMatrix, 0, dx, dy, dz);
					gl.glMatrixMode(GL10.GL_MODELVIEW);
					gl.glLoadMatrixf(modelViewMatrix, 0);
				
					hex.draw(gl, modelViewMatrix, projectionMatrix, 1, tileMap[x][y].tile, true);
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
						hex.draw(gl, modelViewMatrix, projectionMatrix, 0, tileMap[x][y].state, false);
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
						
			float x = a.x;
			float y = a.y;
			float z = a.z;
			
			// cheat for now; raise Marvin so his feet are on the ground
			if (a.model == 0)
			{
				y = 0.6f;
			}
			
			gl.glEnable(GL10.GL_LIGHTING);
			gl.glEnable(GL10.GL_LIGHT0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLight, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseLight, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosition, 0);
			
			models[a.model].SetPosition(x, y, z);
			models[a.model].ResetOrientation();
			models[a.model].YRotate(a.yRotate);
			models[a.model].display(gl, viewMatrix);
			
			gl.glDisable(GL10.GL_LIGHTING);
			
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
		
		tileMap[4][4].state = 1;
		
		actors.clear();
		for (int i = 0; i < m._units.size(); i++)
		{
			Actor a = new Actor();
			a.tileX = m._units.get(i).position.x;
			a.tileY = m._units.get(i).position.y;
			a.type = m._units.get(i).unitType;
			a.model = a.type.getCode();
			a.model = a.model % 2;
			a.uID = m._units.get(i).uID;
			a.x = (float)a.tileX * 1.5f;
			a.z = (float)a.tileY * 0.8660254038f * 2f + (a.tileX % 2) * 0.8660254038f;
			a.y = 0;
				
			actors.put(i, a);
		}
	}
	
	/*	UPDATE - updates the renderer with new unit positions, current user selections,
	 *  and other pertinent information which may affect what is being displayed
	 * 
	 * 		m		MapData structure with essentially the current state of the game
	 */
	public void update(MapData m)
	{
		GLRenderer.pauseRender = true;
		while (GLRenderer.isRenderingNow);
		
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
				a.tileX = m._units.get(i).position.x;
				a.tileY = m._units.get(i).position.y;
			} 
			else
			{
				Actor a = new Actor();
				a.uID = m._units.get(i).uID;
				a.tileX = m._units.get(i).position.x;
				a.tileY = m._units.get(i).position.y;
				a.type = m._units.get(i).unitType;
				a.model = a.type.getCode();
			
				a.model = a.model % 2;
				a.x = (float)a.tileX * 1.5f;
				a.z = (float)a.tileY * 0.8660254038f * 2f + (a.tileX % 2) * 0.8660254038f;
				a.y = 0;
				actors.put(a.uID, a);
			}
		}
		
		GLRenderer.pauseRender = false;
	}
	
	public void moveAnimation(Unit u, List<Point> steps)
	{
		MoveAnimate m = new MoveAnimate();
		
		m.init(u, steps);
		
		animator.add("move", m);
		animator.start("move");
	}
	
	/*	SETACTORROTATION - used to update a unit's direction.  Used for
	 *  animation
	 *  
	 *  	actorID		The unit's id
	 *  	r			the new rotation in radians
	 * 
	 */
	public void setActorRotation(int actorID, float r)
	{
		actors.get(actorID).yRotate = r;
	}
	
	/*	SETACTORPOSITION - used to update a unit's position.  Used for
	 *  animation.  This has nothing to do with which tile the unit is on,
	 *  and is used primarly to depict the unit moving between tiles
	 *  
	 *  	actorID		The unit's id
	 *  	x, y, z		the new position in 3D world space
	 * 
	 */
	public void setActorPosition(int actorID, float x, float y, float z)
	{
		actors.get(actorID).x = x;
		actors.get(actorID).y = y;
		actors.get(actorID).z = z;
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

	/*	ACTOR - used to store the graphical aspects of a unit, such as 3D orientation,
	 *  position and other data as required by the renderer.
	 * 
	 */
	private class Actor {
		int model;
		UnitType type;
		int uID;
		int tileX;
		int tileY;
		float x, y, z;
		float xRotate, yRotate, zRotate;
		
		public Actor()
		{
			xRotate = 0f; yRotate = 0f; zRotate = 0f;
		}
	}
}
