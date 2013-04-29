package com.comp4903.project.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;
import android.util.Xml;

//-----TO USE PLACE IN OnCreate-----//
/*
	ItemParser ip = new ItemParser();
 	AssetManager am = getAssets();
 	try{
 		List<Item> items = ip.parse(am.open("ItemTest.xml"));
 	} catch (Exception e){
		Log.e(TAG, "Error while parsing", e);
	}
*/

public class ItemParser extends Activity{
	
	// We don't use namespace
	private final String TAG = getClass().getSimpleName();
	
	public List<Item> parse(InputStream in) throws XmlPullParserException, IOException{
		try{
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(in, null);
		parser.nextTag();
		return parseItems(parser);
		}finally{
			in.close();
		}
	}
	
	private List<Item> parseItems(XmlPullParser parser) throws XmlPullParserException, IOException {
		List<Item> itemList = new ArrayList<Item>();
		
		parser.require(XmlPullParser.START_TAG, null, "Items");
		while(parser.next() != XmlPullParser.END_TAG){
			if(parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			String name = parser.getName();
			if(name.equals("Item")){
				itemList.add(parseItem(parser));
			} else {
				skip(parser);
			}
		}
		return itemList;
	}
	
	private Item parseItem(XmlPullParser parser) throws XmlPullParserException, IOException {
		Item item = new Item();
		item.Type = ItemType.valueOf(parser.getAttributeValue(null, "type"));  
		while (parser.nextTag() == XmlPullParser.START_TAG) {
			Log.d(TAG, "parse Item tag " + parser.getName());
			if (parser.getName().equals("Name")) {
				item.Name = parser.nextText();
			}
			else if (parser.getName().equals("Description")) {
				item.Desc = parser.nextText();
			}
			else if (parser.getName().equals("Attack")) {
				item.Atk = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Defence")){
				item.Def = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Range")){
				item.Range = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Accuracy")){
				item.Accuracy = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Avoidance")){
				item.Avoidance = Integer.parseInt(parser.nextText());
			}
			else {
				parser.nextText();
			} 
		}
		return item;
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