package com.comp4903.project.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import com.comp4903.project.gameEngine.data.MapData;
import com.comp4903.project.gameEngine.data.Point;
import com.comp4903.project.gameEngine.data.Unit;
import com.comp4903.project.gameEngine.enums.ArmourType;
import com.comp4903.project.gameEngine.enums.SkillType;
import com.comp4903.project.gameEngine.enums.TileType;
import com.comp4903.project.gameEngine.enums.TypeFinder;
import com.comp4903.project.gameEngine.enums.UnitGroup;
import com.comp4903.project.gameEngine.enums.UnitType;
import com.comp4903.project.gameEngine.enums.WeaponType;
import com.comp4903.project.gameEngine.factory.ArmourStats;
import com.comp4903.project.gameEngine.factory.SkillStats;
import com.comp4903.project.gameEngine.factory.UnitStats;
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
						Double.parseDouble(parser.nextText()));
			}
			else {
				parser.nextText();
			} 
		}
		return stats;
	}
	
	/* *************************************************
	 *                   Armour Parser                 *
	 *              Reading Armour Input XML           *
	 * *************************************************/
	public static Map<ArmourType, ArmourStats> readArmourInputXML(InputStream in) throws IOException, XmlPullParserException{
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return parseArmourList(parser);
		}finally{
			in.close();
		}
	}
	
	private static Map<ArmourType, ArmourStats> parseArmourList(XmlPullParser parser) throws XmlPullParserException, IOException {
		Map<ArmourType, ArmourStats> armourList = new HashMap<ArmourType, ArmourStats>();
		parser.require(XmlPullParser.START_TAG, null, "ArmourList");
		while(parser.next() != XmlPullParser.END_TAG){
			if(parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			String name = parser.getName();
			if(name.equals("Armour")){
				ArmourStats stats = parseArmour(parser);
				armourList.put(TypeFinder.findArmourType(stats.name), stats);
			} else {
				skip(parser);
			}
		}
		return armourList;
	}
	
	private static ArmourStats parseArmour(XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException{
		ArmourStats stats = new ArmourStats();  
		while (parser.nextTag() == XmlPullParser.START_TAG) {
			Log.d(TAG, "parse Item tag " + parser.getName());
			if (parser.getName().equals("Name")) {
				stats.name = parser.nextText();
			}
			else if (parser.getName().equals("Defence")) {
				stats.defence = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Health")) {
				stats.health = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Modifier")){
				stats.addModifier(parser.getAttributeValue(null, "parameter"), 
						Double.parseDouble(parser.nextText()));
			}
			else {
				parser.nextText();
			} 
		}
		return stats;
	}
	
	/* *************************************************
	 *                   Unit Parser                   *
	 *              Reading Unit Input XML             *
	 * *************************************************/
	public static Map<UnitType, UnitStats> readUnitInputXML(InputStream in) throws IOException, XmlPullParserException{
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return parseUnitList(parser);
		}finally{
			in.close();
		}
	}
	
	private static Map<UnitType, UnitStats> parseUnitList(XmlPullParser parser) throws XmlPullParserException, IOException {
		Map<UnitType, UnitStats> unitList = new HashMap<UnitType, UnitStats>();
		parser.require(XmlPullParser.START_TAG, null, "UnitList");
		while(parser.next() != XmlPullParser.END_TAG){
			if(parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			String name = parser.getName();
			if(name.equals("Unit")){
				UnitStats stats = parseUnit(parser);
				unitList.put(TypeFinder.findUnitType(stats.name), stats);
			} else {
				skip(parser);
			}
		}
		return unitList;
	}
	
	private static UnitStats parseUnit(XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException{
		UnitStats stats = new UnitStats();  
		while (parser.nextTag() == XmlPullParser.START_TAG) {
			Log.d(TAG, "parse Item tag " + parser.getName());
			if (parser.getName().equals("Name")) {
				stats.name = parser.nextText();
			}
			else if (parser.getName().equals("Defence")) {
				stats.defence = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Health")) {
				stats.health = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Attack")) {
				stats.attack = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Defence")) {
				stats.defence = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("Movement")) {
				stats.movement = Integer.parseInt(parser.nextText());
			}
			else if (parser.getName().equals("WeaponLimit")){
				parseWeaponLimit(parser, stats);
			}
			else if (parser.getName().equals("ArmourLimit")){
				parseArmourLimit(parser, stats);
			}
			else if (parser.getName().equals("SkillList")){
				parseSkillList(parser, stats);
			}
			else {
				parser.nextText();
			} 
		}
		return stats;
	}
	
	private static void parseWeaponLimit(XmlPullParser parser, UnitStats stats) throws XmlPullParserException, IOException{
		while (parser.nextTag() == XmlPullParser.START_TAG) {
			Log.d(TAG, "In-Level: parse weaponLimit tag " + parser.getName());
			if (parser.getName().equals("Weapon")){
				stats.addWeaponLimit(TypeFinder.findWeaponType(parser.nextText()));
			}
			else {
				parser.nextText();
			}
		}
	}
	
	private static void parseArmourLimit(XmlPullParser parser, UnitStats stats) throws XmlPullParserException, IOException{
		while (parser.nextTag() == XmlPullParser.START_TAG) {
			Log.d(TAG, "In-Level: parse armourLimit tag " + parser.getName());
			if (parser.getName().equals("Armour")){
				stats.addArmourLimit(TypeFinder.findArmourType(parser.nextText()));
			}
			else {
				parser.nextText();
			}
		}
	}
	
	private static void parseSkillList(XmlPullParser parser, UnitStats stats) throws XmlPullParserException, IOException{
		while (parser.nextTag() == XmlPullParser.START_TAG) {
			Log.d(TAG, "In-Level: parse skillList tag " + parser.getName());
			if (parser.getName().equals("Skill")){
				stats.addSkillLimit(TypeFinder.findSkillType(parser.nextText()));
			}
			else {
				parser.nextText();
			}
		}
	}

	/* *************************************************
	 *                   Map Parser                    *
	 *              Reading Armour Map XML             *
	 * *************************************************/
	public static MapData readMapInputXML(InputStream in) throws IOException, XmlPullParserException{
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return parseMap(parser);
		}finally{
			in.close();
		}
	}
	
	private static MapData parseMap(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "Map");
		int w = Integer.parseInt(parser.getAttributeValue(null, "width"));
		int h = Integer.parseInt(parser.getAttributeValue(null, "height"));

		Log.d(TAG, "Createing map " + w + ", " + h);
		MapData map = new MapData(w,h);
		map.defaultType(TypeFinder.findTileType(parser.getAttributeValue(null, "defaultType")));
		while(parser.next() != XmlPullParser.END_TAG){
			if(parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			String name = parser.getName();
			if(name.equals("TileGroup")){
				Log.d(TAG, "Tile Group" + parser.getName());
				map = parseTileGroup(map, parser);
			} else if (name.equals("Units")) {
				Log.d(TAG, "Units" + parser.getName());
				map = parseUnitGroup(map, parser);
			}
			else {
				skip(parser);
			}
		}
		return map;
	}
	
	private static MapData parseTileGroup(MapData data, XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException{
		while (parser.nextTag() == XmlPullParser.START_TAG) {
			Log.d(TAG, "parse Tile tag " + parser.getName());
			if (parser.getName().equals("Tile")) {
				int x = Integer.parseInt(parser.getAttributeValue(null, "x"));
				int y = Integer.parseInt(parser.getAttributeValue(null, "y"));
				TileType t = TypeFinder.findTileType(parser.nextText());
				System.out.println("Tile: " + x + ", " + y + ", " + t);
				data._tileTypes[x][y] = t;
			}
			else {
				parser.nextText();
			}
		}
		return data;
	}
	
	private static MapData parseUnitGroup(MapData data, XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException{
		UnitGroup currGroup = TypeFinder.findUnitGroup(parser.getAttributeValue(null, "group"));
		while (parser.nextTag() == XmlPullParser.START_TAG) {
			Log.d(TAG, "parse Unit tag " + parser.getName());
			if (parser.getName().equals("Tile")) {
				int x = Integer.parseInt(parser.getAttributeValue(null, "x"));
				int y = Integer.parseInt(parser.getAttributeValue(null, "y"));
				UnitType t = TypeFinder.findUnitType(parser.nextText());
				System.out.println("Unit: " + x + ", " + y + ", " + t);
				data._units.add(new Unit(t, currGroup, new Point(x,y)));
			}
			else {
				parser.nextText();
			} 
		}
		return data;
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
