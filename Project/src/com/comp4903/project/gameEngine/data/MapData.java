package com.comp4903.project.gameEngine.data;

import android.graphics.Point;

import com.comp4903.project.gameEngine.enums.*;
import com.comp4903.project.graphics.RendererAccessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapData {
	private int _numRows;				// Num of rows
	private int _numColumns;			// Num of columns
	public boolean _firstTileOnLeft;	// Is the first line of tile oh left or right
	
	public Point _tileSelected;			// Currently Selected Tile
	public List<Point> _movementBox;	// Movement box of the currently selected box
	public List<Point> _attackBox;		// Attack box of the currently selected box

	public ColorType _attackBoxColor;
	public ColorType _movementBoxColor;
	
	public List<UnitGroup> _groupList;  // List of group on the map
	public UnitGroup _activeGroup; 		// group that currently making action
	
	public List<Unit> _units;
	public TileType[][] _tileTypes;
	
	public MapData(int rows, int columns){
		_movementBox = new ArrayList<Point>();
		_attackBox = new ArrayList<Point>();
		_units = new ArrayList<Unit>();
		_groupList = new ArrayList<UnitGroup>();
		_activeGroup = UnitGroup.PlayerOne;
		_numRows = rows;
		_numColumns = columns;
		_tileTypes = new TileType[_numColumns][_numRows];
		
		_attackBoxColor = ColorType.Red;
		_movementBoxColor = ColorType.Blue;
	}
	
	public void defaultType(TileType type){
		for (int i = 0 ; i < _numRows; i++){
			for (int j = 0; j < _numColumns; j++){
				_tileTypes[j][i] = type;
			}
		}
	}
	
	public void InitializeTileStatus(){
		for (Unit u: _units){
			u.tileStatus = new Status(_tileTypes[u.position.x][u.position.y]);
		}
	}
	
	public boolean inMap(Point p){
		return (p.x < _numColumns && p.y < _numRows
				&& p.x >= 0 && p.y >= 0);
	}
	
	public boolean isOpen(Point p){
		if (!inMap(p))
			return false;
		switch(_tileTypes[p.x][p.y]){
			case Building:
				return false;
			default:
				return true;
		}
	}
	
	public Unit getUnitAt(Point p){
		if (p == null)
			return null;
		for (Unit u : _units){
			if (u.position.equals(p))
				return u;
		}
		return null;
	}
	
	public Unit getUnitByID(int id){
		for (Unit u: _units){
			if (u.uID == id)
				return u;
		}
		return null;
	}
	
	public void RemoveDeadUnit(){
		List<Unit> temp = new ArrayList<Unit>();
		for (Unit u: _units){
			if (u.combatStats.currentHealth > 0)
				temp.add(u);
			else
				RendererAccessor.map.deathAnimation(u);
		}
		_units.clear();
		_units = temp;
	}
	
	public int NumberOfRows(){
		return _numRows;
	}
	
	public int NumberOfColumns(){
		return _numColumns;
	}
	
	public void clearBoxes(){
		_movementBox.clear();
		_attackBox.clear();
	}
	
	
}
