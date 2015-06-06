package com.mandr.level;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mandr.util.AABB;
import com.mandr.util.Directions;

// A tile on the FOREGROUND layer (i.e. something that interacts with other entities
public class Tile {
	private TiledMapTileLayer m_Layer;
	private TileType m_TileType;
	private final int m_X;
	private final int m_Y;
	private final AABB m_BoundingBox;
	
	private Level m_Level;
	
	// Slopes
	private int m_SlopeLeft;
	private int m_SlopeRight;
	private Directions m_SlopeDirection;
	
	public Tile(Level level, TiledMapTileLayer foregroundLayer, int x, int y) {
		m_Level = level;
		m_Layer = foregroundLayer;
		m_X = x;
		m_Y = y;
		m_TileType = TileType.TILE_EMPTY;
		
		m_BoundingBox = new AABB(new Vector2(x,y), new Vector2(1,1));
		
		setTileType();
	}
	
	// Set the tile type of the tile based on its properties
	private void setTileType() {
		Cell cell = getCell();
		if(cell == null) return;
		TiledMapTile tile = cell.getTile();
		
		if(tile == null) return;
		
		// Solid tile?
		Object property = tile.getProperties().get("solid");
		if(property != null) {
			m_TileType = TileType.TILE_SOLID;
			return;
		}
		
		// One way tile?
		property = tile.getProperties().get("oneway");
		if(property != null) {
			property = tile.getProperties().get("strict");
			if(property != null) {
				//System.out.println(property);
				if(property.equals("false")) {
					m_TileType = TileType.TILE_ONE_WAY;
				}
				else {
					m_TileType = TileType.TILE_ONE_WAY_STRICT;
				}
			}
			else  {
				m_TileType = TileType.TILE_ONE_WAY_STRICT;
			}
			return;
		}
		
		// Ladder tile?
		property = tile.getProperties().get("ladder");
		if(property != null) {
			m_TileType = TileType.TILE_LADDER;
			return;
		}
		
		// Is this a slope tile?
		property = tile.getProperties().get("slope");
		if(property != null) {
			m_TileType = TileType.TILE_SLOPE;
			property = tile.getProperties().get("slope_left");
			if(property != null) m_SlopeLeft = (int) Integer.valueOf((String) property);
			property = tile.getProperties().get("slope_right");
			if(property != null) m_SlopeRight = (int) Integer.valueOf((String) property);
			
			// Direction of slope = the side which has the larger slope
			if(m_SlopeLeft > m_SlopeRight) {
				m_SlopeDirection = Directions.DIRECTION_LEFT;
			}
			else if(m_SlopeLeft < m_SlopeRight ){
				m_SlopeDirection = Directions.DIRECTION_RIGHT;
			}
			else {
				m_SlopeDirection = Directions.NO_DIRECTION;
				m_TileType = TileType.TILE_EMPTY;
				m_SlopeLeft = 0;
				m_SlopeRight = 0;
				System.out.println("Warning! Slope tile at " + this + "has invalid slope!");
			}
			return;
		}
		
		System.out.println(this + " " + getTileType());
	}
	
	// Calculate the y-position of the floor
	public float floorY(float t) {
		t = MathUtils.clamp(t, 0.0f, 1.0f);
		
		float floorY = 0.0f;
		if(m_TileType != TileType.TILE_SLOPE) {
			return (float) getY()+1;
		}
		// Slope tiles
		else {
			// Slope goes up-left
			if(getSlopeDirection() == Directions.DIRECTION_LEFT) {
				int height = getSlopeLeft() - getSlopeRight();
				floorY = (float) height * (1.0f-t);
				floorY += getSlopeRight();
				floorY *= (1.0f / 16.0f);
				floorY += getY();
			}
			// Slope goes up-right
			else if(getSlopeDirection() == Directions.DIRECTION_RIGHT){
				int height = getSlopeRight() - getSlopeLeft();
				floorY = (float) height * t;
				floorY += getSlopeLeft();
				floorY *= (1.0f / 16.0f);
				floorY += getY();
			}
			
			return floorY;
		}
	}
	
	public float getSlope() {
		if(getTileType() != TileType.TILE_SLOPE) return 0;
		if(getSlopeDirection() == Directions.DIRECTION_RIGHT)
			return 1.0f / (16.0f / (getSlopeRight() - getSlopeLeft() + 1));
		else
			return 1.0f / (16.0f / (getSlopeLeft() - getSlopeRight() + 1));
	}
	
	// Direction of the slope tile's slope (right = right > left, left = left > right)
	public Directions getSlopeDirection() {
		return m_SlopeDirection;
	}
	
	// TODO: Test function to draw tile overlay
	public void draw(ShapeRenderer shapeRender) {		
		// Outline
		shapeRender.setColor(0, 0.5f, 0.85f, 0.5f);
		shapeRender.rect(m_X, m_Y, 1, 1);
	}
	
	public AABB getBoundingBox() { return m_BoundingBox; }
	public TileType getTileType() { return m_TileType; }
	public Cell getCell() { return m_Layer.getCell(m_X, m_Y); }
	public int getX() { return m_X; }
	public int getY() { return m_Y; }
	public int getSlopeLeft() { return m_SlopeLeft; }
	public int getSlopeRight() { return m_SlopeRight; }
	
	// Is this the top of a ladder?
	public boolean isLadderTop() {
		if(m_TileType != TileType.TILE_LADDER) return false;
		Tile aboveTile = m_Level.getTile(getX(), getY()+1);
		if(aboveTile == null) return true;
		
		return aboveTile.getTileType() != TileType.TILE_LADDER;
	}
	
	@Override
	public String toString() { return new String("[" + m_X + "," + m_Y + "]"); }
}
