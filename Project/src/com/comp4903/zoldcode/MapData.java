package com.comp4903.zoldcode;

import com.comp4903.project.gameEngine.enums.*;

import java.util.ArrayList;
import java.util.List;

public class MapData {
	private int _numRows;				// Num of rows
	private int _numColumns;			// Num of columns
	public boolean _firstTileOnLeft;	// Is the first line of tile oh left or right
	
	public Point _tileSelected;			// Currently Selected Tile
	public List<Point> _movementBox;	// Movement box of the currently selected box
	public List<Point> _attackBox;		// Attack box of the currently selected box
	
	public UnitGroup _activeGroup; 		// group that currently making action
	
	public List<Unit> _units;
	public TileType[][] _tileTypes;
	
	public MapData(int rows, int columns){
		_movementBox = new ArrayList<Point>();
		_attackBox = new ArrayList<Point>();
		_numRows = rows;
		_numColumns = columns;
		_tileTypes = new TileType[_numRows][_numColumns];
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
