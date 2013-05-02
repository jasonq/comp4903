package com.comp4903.project.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.TypeFinder;
import com.comp4903.project.gameEngine.enums.WeaponType;
import com.comp4903.project.gameEngine.factory.SkillStats;
import com.comp4903.project.gameEngine.factory.WeaponStats;

public class XMLParser {
	/* *************************************************
	 *                   Weapon Parser                 *
	 *              Reading Weapon Input XML           *
	 * *************************************************/
	
	// We don't use namespace
	private static final String TAG = "";
	
	public static Map<WeaponType, WeaponStats> readWeaponInputXML(InputStream in) throws IOException, XmlPullParserException{
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return parseWeaponList(parser);
		}finally{
			in.close();
		}
	}
	
	private static Map<WeaponType, WeaponStats> parseWeaponList(XmlPullParser parser) throws XmlPullParserException, IOException {
		Map<WeaponType, WeaponStats> weaponList = new HashMap<WeaponType, WeaponStats>();
		parser.require(XmlPullParser.START_TAG, null, "WeaponList");
		while(parser.next() != XmlPullParser.END_TAG){
			if(parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			String name = parser.getName();
			if(name.equals("Weapon")){
				WeaponStats stats = parseWeapon(parser);
				weaponList.put(TypeFinder.findWeaponType(stats.name), stats);
			} else {
				skip(parser);
			}
		}
		return weaponList;
	}
	
	private static WeaponStats parseWeapon(XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException{
		WeaponStats stats = new WeaponStats();  
		while (parser.nextTag() == XmlPullParser.START_TAG) {
			Log.d(TAG, "parse Item tag " + parser.getName());
			if (parser.getName().equals("Name")) {
				stats.name = parser.nextText();
			}
			else if (parser.getName().equals("Description")) {
				stats.description = parser.nextText();
			}
			else if (parser.getName().equals("Damage")) {
				stats.damage = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Accuracy")){
				stats.accuracy = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Range")){
				stats.range = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Rounds")){
				stats.rounds = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Modifier")){
				stats.addModifier(parser.getAttributeValue(null, "parameter"), 
						Integer.parseInt(parser.nextText()));
			}
			else {
				parser.nextText();
			} 
		}
		return stats;
	}
	
	/* *************************************************
	 *                   Skill Parser                  *
	 *              Reading Skill Input XML            *
	 * *************************************************/
	public static Map<SkillType, SkillStats> readSkillInputXML(InputStream in) throws IOException, XmlPullParserException{
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return parseSkillList(parser);
		}finally{
			in.close();
		}
	}
	
	private static Map<SkillType, SkillStats> parseSkillList(XmlPullParser parser) throws XmlPullParserException, IOException {
		Map<SkillType, SkillStats> skillList = new HashMap<SkillType, SkillStats>();
		parser.require(XmlPullParser.START_TAG, null, "SkillList");
		while(parser.next() != XmlPullParser.END_TAG){
			if(parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			String name = parser.getName();
			if(name.equals("Ability")){
				SkillStats stats = parseSkill(parser);
				System.out.println("Find Ability");
				skillList.put(TypeFinder.findSkillType(stats.name), stats);
			} else {
				skip(parser);
			}
		}
		return skillList;
	}
	
	private static SkillStats parseSkill(XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException{
		SkillStats stats = new SkillStats();  
		while (parser.nextTag() == XmlPullParser.START_TAG) {
			Log.d(TAG, "parse Item tag " + parser.getName());
			if (parser.getName().equals("Name")) {
				stats.name = parser.nextText();
			}
			else if (parser.getName().equals("Description")) {
				stats.description = parser.nextText();
			}
			else if (parser.getName().equals("Health")) {
				stats.healthCost = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Energy")){
				stats.energyCost = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Range")){
				stats.range = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Target")){
				stats.target = TypeFinder.findTargetType(parser.nextText());
			}
			else if (parser.getName().equals("Modifier")){
				stats.addModifier(parser.getAttributeValue(null, "parameter"), 
						Integer.parseInt(parser.nextText()));
			}
			else {
				parser.nextText();
			} 
		}
		return stats;
	}
	/* *************************************************
	 *                  Helper Parser                  *
	 *  Stolen Online (Source, Official Android Site)  *
	 * *************************************************/
    
	private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
