package com.comp4903.project.graphics.model;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import android.opengl.Matrix;

/*	MODELLOADER - Provides methods to load 3D models (.gmodel files) from
 *  a resource.
 *  
 *  load(InputStream f, Model3D m)
 *		f	InputStream representing the .gmodel file resource 
 *		m	Model3D object to contain the model
 */
public class ModelLoader {
	
	public static final int GMDL = 0x4C444D47;
	public static final int MTRL = 0x4C52544D;
	public static final int CPNT = 0x544E5043;
	public static final int ANIM = 0x4d494e41;
	
	private static Model3D model;
	private static DataInputStream fs;
	
	private static int[] mat_cross_ref = new int[100];
	
	/* LOAD - Loads a model from a provided FileInputStream
	 *	f	InputStream representing the .gmodel file resource 
	 *	m	Model3D object to contain the model
	 */
	public static void load(InputStream f, Model3D m)
	{
		
		model = m;
		boolean done = false;
		
		fs = new DataInputStream(f);
		
		readHeader();
						
		while (!done)
		{
			int chunk_id = readInt();
			
			switch (chunk_id)
			{
			case -1:
				done = true;
				break;
			case MTRL:
				loadMaterial();
				break;
			case CPNT:
				loadComponents();
				break;
			case ANIM:
				loadAnimations();
			default:
				done = true;
			}
			
		}
		
		model.recallNeutralPose();
		
	}
	
	// reads the file header
	private static void readHeader()
	{
		int id = readInt();
		model.name = readString();			
	}
	
	/* READINT - reads an integer from the buffer.  As .gmodel files
	 * store the integer bytes in reverse order, this method re-orders them
	 *
	 * @returns	int	
	 */
	private static int readInt()
	{
		int result = 0;		
		try {
			result += fs.read();			
			result += (fs.read() << 8);			
			result += (fs.read() << 16);			
			result += (fs.read() << 24);			
		} 
		catch (IOException e)
		{
			result = -1;
		}		
		return result;
	}
	
	/* READSHORT - same as above, for 2-byte integers */
	private static short readShort()
	{
		short result = 0;
		
		try {	
			result += fs.read();			
			result += (fs.read() << 8);		
			//result = fs.readShort();
		} 
		catch (IOException e)
		{
			result = -1;
		}
		
		return result;
	}
	
	/* READFLOAT - same as above, for floats */
	private static float readFloat()
	{
		float result = 0;
		
		result = Float.intBitsToFloat(readInt());		
		
		return result;
	}
	
	/* READSTRING - reads a string from the buffer */
	private static String readString()
	{
		String result = new String();
		int sz = readInt();
		
		try {
			for(int i = 0; i < sz; i++)
			{
				char c;
				c = (char)fs.read();
				result += c;
			}
		} 
		catch (IOException e)
		{
			
		}
		return result;
	}
	
	/* READFLOATARRAY - reads a sequence of floats from the buffer
	 * and stores them in an array
	 * 
	 * @param	sz		size; number of floats to read
	 * @returns float[]	resulting array of floats
	 */
	private static float[] readFloatArray(int sz)
	{
		float[] result = new float[sz];
		
			for (int i = 0; i < sz; i++)			
				result[i] = readFloat();			
		
		
		return result;
	}
	
	/* READVECTOR - reads a vector (3 floats) from the buffer, and
	 * adds a 4th float with a value of 1.0, returning an array of 
	 * 4 floats 
	 */
	private static float[] readVector()
	{
		float[] result = new float[4];
		
		for (int i = 0; i < 3; i++)			
			result[i] = readFloat();			
		
		result[3] = 1.0f;
		return result;	
	}
	
	/* READMATRIX - reads a matrix (16 floats) returning the results
	 * in an array
	 */
	private static float[] readMatrix()
	{		
		return readFloatArray(16);
	}
	
	/* READCONSTRAINTS - reads a <i>Constraints structure</i> from the
	 * buffer
	 */
	private static Constraint readConstraints()
	{
		Constraint result = new Constraint();
		
		result.minY = readFloat();
		result.maxY = readFloat();
		result.minX = readFloat();
		result.maxX = readFloat();
		result.minZ = readFloat();
		result.maxZ = readFloat();		
		
		return result;
	}
	
	/* LOADCOMPONENTS - processes a MCPT block, reading in all the
	 * components of the 3D model.
	 */
	private static void loadComponents()
	{
		int numberOfComponents = readInt();
		Component[] components = new Component[numberOfComponents];
		
		for (int i = 0; i < numberOfComponents; i++)
		{
			components[i] = new Component();
			Matrix.setIdentityM(components[i].orientation, 0);
			loadComponent(components[i]);
			
			for (int u = 0; u < components[i].numTriangleLists; u++)
			{
				if (components[i].materialIndex[u] >= 0)
					components[i].materialIndex[u] = mat_cross_ref[components[i].materialIndex[u]]; 
			}
		}
		
		model.setComponents(components);
	}
	
	/* LOADCOMPONENT - loads a single component from the input buffer */
	private static void loadComponent(Component c)
	{
		c.name = readString();
		c.parent = readInt();
		c.numVertices = readInt();
		c.numTriangleLists = readInt();
		
		c.materialIndex = new int[c.numTriangleLists];
		c.numTriangles = new int[c.numTriangleLists];
		
		c.translation = readVector();
		c.initialTranslation = readVector();
		c.neutralOrientation = readMatrix();
		c.constraints = readConstraints();
		
		readVertexBuffer(c);
		readIndexBuffers(c);		
	}
	
	/* READVERTEXBUFFER - reads a vertex buffer from the input stream
	 * 
	 * @param	c	component # to contain the vertex buffer	 
	 */
	private static void readVertexBuffer(Component c)
	{
		
		float[] vertices = new float[c.numVertices * 3];
		float[] normals = new float[c.numVertices * 3];
		float[] colors = new float[c.numVertices * 4];
		float[] texcoords = new float[c.numVertices * 2];
		
		for (int i = 0; i < c.numVertices; i++)
		{
			vertices[i * 3] = readFloat();
			vertices[i * 3 + 1] = readFloat();
			vertices[i * 3 + 2] = readFloat();
			normals[i * 3] = readFloat();
			normals[i * 3 + 1] = readFloat();
			normals[i * 3 + 2] = readFloat();
			int col = readInt();
			colors[i * 4] = (float)(col & 0x0ff) / 255.0f;
			colors[i * 4 + 1] = (float)((col >> 8) & 0x0ff) / 255.0f;
			colors[i * 4 + 2] = (float)((col >> 16) & 0x0ff) / 255.0f;
			colors[i * 4 + 3] = (float)((col >> 24) & 0x0ff) / 255.0f;
			texcoords[i * 2] = readFloat();
			texcoords[i * 2 + 1] = readFloat();
		}
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		c.vertexBuffer = vbb.asFloatBuffer();
		c.vertexBuffer.put(vertices);
		c.vertexBuffer.position(0);
		
		vbb = ByteBuffer.allocateDirect(normals.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		c.normalBuffer = vbb.asFloatBuffer();
		c.normalBuffer.put(normals);
		c.normalBuffer.position(0);
		
		vbb = ByteBuffer.allocateDirect(colors.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		c.colorBuffer = vbb.asFloatBuffer();
		c.colorBuffer.put(colors);
		c.colorBuffer.position(0);
		
		vbb = ByteBuffer.allocateDirect(texcoords.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		c.texBuffer = vbb.asFloatBuffer();
		c.texBuffer.put(texcoords);
		c.texBuffer.position(0);
	}
	
	/* READINDEXBUFFERS - reads all the index buffers for the component
	 * 
	 * @param	c	component #
	 */
	private static void readIndexBuffers(Component c)
	{
		c.indexBuffer = new ShortBuffer[c.numTriangleLists];
		
		for (int i = 0; i < c.numTriangleLists; i++)
		{
			c.materialIndex[i] = readInt();
			c.numTriangles[i] = readInt();
			short[] indices = new short[c.numTriangles[i] * 3];
			for (int p = 0; p < c.numTriangles[i] * 3; p++)
				indices[p] = readShort();
			
			ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
			ibb.order(ByteOrder.nativeOrder());
			c.indexBuffer[i] = ibb.asShortBuffer();
			c.indexBuffer[i].put(indices);
			c.indexBuffer[i].position(0);
		}
	}
	
	/* LOADMATERIAL - reads a material description from the buffer, and
	 * adds the material to the library if necessary
	 */
	private static void loadMaterial()
	{
		int index, foundIndex;
		
		Material mat = new Material();
		Texture tex = new Texture();
		
		index = readInt();
		
		mat.name = readString();
		
		mat.Diffuse = readColor();
		mat.Ambient = readColor();
		mat.Specular = readColor();
		mat.Emissive = readColor();
		mat.Power = readFloat();
		
		mat.texture = readInt();
		if (mat.texture != -1)
		{
			tex.name = readString();
			tex.filename = readString();
		}
		
		foundIndex = MaterialLibrary.getMaterialIndex(mat.name);
		
		if (foundIndex != -1)
			mat_cross_ref[index] = foundIndex;
		else
		{
			if (mat.texture != -1)
			{
				int tex_index = MaterialLibrary.getTextureIndex(tex.name);
				if (tex_index == -1)
				{
					tex_index = MaterialLibrary.addTexture(tex);
					MaterialLibrary.loadTexture(tex_index);					
				}
				mat.texture = tex_index;
			}
			mat_cross_ref[index] = MaterialLibrary.numMaterials;
			MaterialLibrary.addMaterial(mat);
		}
		
	}
	
	/* READCOLOR - reads an OpenGL color (4 floats */
	private static float[] readColor()
	{
		float[] col = new float[4];
		
		col[0] = readFloat();
		col[1] = readFloat();
		col[2] = readFloat();
		col[3] = readFloat();		
		
		return col;
	}
	
	private static void loadAnimations()
	{
		int animCount = readInt();
		model.setNumberOfAnimations(animCount);
		
		for (int a = 0; a < animCount; a++)
		{
			model.newAnimation(a);
			String a_name = readString();
			model.setAnimationName(a, a_name);
			
			for (int p = 0; p < 120; p++)
			{
				Vector3 t = new Vector3(readFloatArray(3));
				model.setAnimationFrameTranslation(a, p, t);
				
				int s = readInt();
				model.setSignal(a, p, s);
			}
		}
		
		int numberOfComponents = model.getNumberOfComponents();
		
		for (int c = 0; c < numberOfComponents; c++)
		{
			for (int a = 0; a < animCount; a++)
			{				
				for (int f = 0; f < 120; f++)
				{
					float[] matrix = readMatrix();
					model.setComponentFrameOrientation(a, c, f, matrix);
				}
			}
		}
		
	}
}
