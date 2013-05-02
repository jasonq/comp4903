package com.comp4903.project.gameEngine.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.comp4903.project.gameEngine.enums.TypeFinder;
import com.comp4903.zoldcode.MapData;
import com.comp4903.zoldcode.Point;
import com.comp4903.zoldcode.TileType;

import android.util.Xml;

public class MapFactory {
	
	private static final String ns = null;
	
	public MapFactory(){
		
	}
	
	public MapData parse(InputStream in) throws XmlPullParserException, IOException{
		try {
	        XmlPullParser parser = Xml.newPullParser();
	        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	        parser.setInput(in, null);
	        parser.nextTag();
	        return readMapData(parser);
	    } finally {
	        in.close();
	    }
	}
	
	private MapData readMapData(XmlPullParser parser) throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, "Map");
		int w = Integer.parseInt(parser.getAttributeValue(null, "width"));
		int h = Integer.parseInt(parser.getAttributeValue(null, "height"));
		MapData result = new MapData(w, h);
		System.out.println("Width: " + w + " Height: " + h);

	    while (parser.next() != XmlPullParser.END_TAG){
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("TileGroup")) {
	        	readTileGroup(result, parser);
	        } else {
	            skip(parser);
	        }
	    }
		return result;
	}
	
	private void readTileGroup(MapData mapData, XmlPullParser parser) throws XmlPullParserException, IOException{
		if (TypeFinder.findTileType(parser.getAttributeValue(null, "type")) == TileType.None){
			System.out.println("Cant find the corrosponding tile type: " + 
					parser.getAttributeValue(null, "type"));
			return;
		}
		TileType currType = TypeFinder.findTileType(parser.getAttributeValue(null, "type"));
		while(parser.next() != XmlPullParser.END_TAG){
			
		}
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
}
