package com.comp4903.project.graphics.tile;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.comp4903.project.graphics.model.MaterialLibrary;
import com.comp4903.project.graphics.model.Model3D;
import com.comp4903.project.graphics.model.ModelLoader;
import com.comp4903.project.graphics.model.Texture;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Xml;

public class TileSetDefinition {
	
	String name;
	String fileName;
	
	String texFileName;
	int textureMap = -1;
	
	int numberOfTiles;
	Tile[] tiles = new Tile[100];
	
	String ns = "";
	
	Context context;
	
	public TileSetDefinition(String f, Context c)
	{
		fileName = f;
		AssetManager am = c.getAssets();
		context = c;
		numberOfTiles = 0;
		
		try {
			InputStream buf = null;
			buf = am.open(fileName);
			XMLparseTileSet(buf);
			buf.close();
			
		} catch (IOException e)
		{ }		
		finally {
			Texture t = new Texture(name, "tiles/" + texFileName, 0);
			textureMap = MaterialLibrary.addTexture(t);
			MaterialLibrary.loadTexture(textureMap);
		}
		
	}
	
	public void XMLparseTileSet(InputStream buf) throws IOException
	{
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(buf, null);
            //parser.nextTag();
            String nmsp = parser.getNamespace();
            processXML(parser);
			
		} catch (XmlPullParserException e)
		{}		
		finally {
			
		}
	}
	
	public void processXML(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		//parser.require(XmlPullParser.START_TAG, ns, "def:tileset");
		
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			
			String name = parser.getName();
			
			if (name.equals("def:tileset"))
				processTileSetTag(parser);
			
			if (name.equals("def:tile"))
				processTileTag(parser);
		}
	}
	
	public void processTileSetTag(XmlPullParser parser) throws XmlPullParserException, IOException
	{		
		name = parser.getAttributeValue(ns, "name");
		texFileName = parser.getAttributeValue(ns, "map");
	}
	
	public void processTileTag(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		tiles[numberOfTiles] = new Tile(parser.getAttributeValue(ns, "name"));
		String elevation = parser.getAttributeValue(ns, "elevation");
		if (elevation != null)
			tiles[numberOfTiles].elevation = Float.parseFloat(elevation);
		tiles[numberOfTiles].model = null;
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			
			String name = parser.getName();
			
			if (name.equals("def:base"))
				processBaseTag(parser);		
			
			if (name.equals("def:model"))
				processModelTag(parser);
			
		}
		numberOfTiles++;
		
	}
	
	public void processBaseTag(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		boolean degenerated = true;
		
		while ((parser.next() != XmlPullParser.END_TAG) || degenerated)
		{
			degenerated = false;
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;
			
			String name = parser.getName();
			
			if (name.equals("def:point1"))
			{
				degenerated = true;
				readUV(parser, 0);
			}
			
			if (name.equals("def:point2"))
			{
				degenerated = true;
				readUV(parser, 1);
			}
			
			if (name.equals("def:point3"))
			{
				degenerated = true;
				readUV(parser, 2);
			}
			
			if (name.equals("def:point4"))
			{
				degenerated = true;
				readUV(parser, 3);
			}
			
			if (name.equals("def:point5"))
			{
				degenerated = true;
				readUV(parser, 4);
			}
			
			if (name.equals("def:point6"))
			{
				degenerated = true;
				readUV(parser, 5);
			}
		}
	}
	
	public void processModelTag(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		tiles[numberOfTiles].model = new Model3D();
		
		AssetManager am = context.getAssets();
		
		//while (parser.next() != XmlPullParser.END_TAG)
		//{
			//if (parser.getEventType() != XmlPullParser.START_TAG)
			//	continue;
			
			String filename = "models/" + parser.nextText().replaceAll("\\s", "");
			
			InputStream buf = null;
			buf = am.open(filename);			
			ModelLoader.load(buf, tiles[numberOfTiles].model, false);
		//}
	}
	
	public void readUV(XmlPullParser parser, int p) throws XmlPullParserException, IOException
	{
		tiles[numberOfTiles].texcoords[p * 2] = Float.parseFloat(parser.getAttributeValue(ns, "u"));
		tiles[numberOfTiles].texcoords[p * 2 + 1] = Float.parseFloat(parser.getAttributeValue(ns, "v"));
	}
}
