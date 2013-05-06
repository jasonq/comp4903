package com.comp4903.project.gameEngine.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.enums.TileType;
import com.comp4903.project.gameEngine.enums.TypeFinder;
import com.comp4903.project.parser.XMLParser;

import android.util.Xml;

public class MapFactory {
	
	private static final String ns = null;
	
	public static MapData generateMapData(InputStream in){
		MapData data = null;
		try {
			data = XMLParser.readMapInputXML(in);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		System.out.println("Num of units: " + data._units.size());
		System.out.println("Building: " + (data._tileTypes[0][0]
				== TileType.Building));
		System.out.println("Plain: " + (data._tileTypes[1][12]
				== TileType.Sandbag));
		return data;
	}
}
