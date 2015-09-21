package com.mandr.level;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.mandr.database.DatabaseUtility;
import com.mandr.entity.Entity;
import com.mandr.enums.TileType;
import com.mandr.game.Globals;
import com.mandr.level.entity.EntityManager;
import com.mandr.util.Constants;

public class Level {
	private EntityManager m_EntityManager;
	
	private boolean m_isLoaded;
	private MapLayer m_ForegroundLayer;
	private MapLayer m_ObjectLayer;
	private TiledMap m_TiledMap;
	private int m_Width;
	private int m_Height;
	
	private Tile[] m_TileGrid;
	
	private float m_CurrentBoundaryX;
	private float m_CurrentBoundaryY;
	
	// TODO: Might want to do this in a separate class?
	private ArrayList<Vector2> m_StartPositions;
	private Checkpoint m_Checkpoint;
	
	public Level() {
		m_isLoaded = false;
		m_Width = 0;
		m_Height = 0;
		m_CurrentBoundaryX = 0;
		m_CurrentBoundaryY = 0;
		
		m_StartPositions = new ArrayList<Vector2>();
		m_EntityManager = new EntityManager();
	}
	
	//=========================================================================
	// Initialization
	//=========================================================================
	// TODO: Redo the map loading code at some point to something nicer. This is all placeholder junk.
	public void loadMap(String fileName) {
		m_TiledMap = new TmxMapLoader().load("maps\\" + fileName);
		m_ForegroundLayer = m_TiledMap.getLayers().get("foreground");
		if(!(m_ForegroundLayer instanceof TiledMapTileLayer)) {
			m_ForegroundLayer = null;
		}
		m_ObjectLayer = m_TiledMap.getLayers().get("objects");
		
		if(m_ForegroundLayer == null) {
			unloadMap();
			throw new NullPointerException("Map must have a tile layer as the first layer");
		}
		if(m_ObjectLayer == null) {
			unloadMap();
			throw new NullPointerException("Map must have a layer called 'objects'");
		}
		
		m_Width = ((TiledMapTileLayer)m_ForegroundLayer).getWidth();
		m_Height = ((TiledMapTileLayer)m_ForegroundLayer).getHeight();
		
		initGrid();
		initEntities();
		
		m_isLoaded = true;
	}
	
	public void unloadMap() {
		m_EntityManager.reset();
		for(int i=0; i<m_TileGrid.length; i++) {
			m_TileGrid[i] = null;
		}
		m_TileGrid = null;
		m_TiledMap = null;
		m_ForegroundLayer = null;
		
		m_isLoaded = false;
	}
	
	private void initEntities() {
		System.out.println("Initializing entities");
		
		MapLayer objectLayer = getObjectLayer();
		MapObjects objects = objectLayer.getObjects();
		MapObject object;
		for(int i = 0; i < objects.getCount(); i++) {
			object = objects.get(i);
			String type = (String) object.getProperties().get("type");
			if(type.isEmpty()) continue;
			
			float x = (Float) object.getProperties().get("x");
			float y = (Float) object.getProperties().get("y");
			
			x /= 16.0f;
			y /= 16.0f;
			
			System.out.println(i + ": " + type + " " + x + " " + y);
			
			if(type.equalsIgnoreCase("StartPosition")) {
				m_StartPositions.add(new Vector2(x, y));
			}
			else {
				// Objects should be in <type>_<id> format. This allows me to figure out what object to construct.
				// TODO might want to be in a function or separate class for readability sake.
				int underscore = type.indexOf("_");
				String typeName = type.substring(0, underscore);
				int id = Integer.parseInt(type.substring(underscore+1));
				if(typeName.equalsIgnoreCase("Actor")) {
					m_EntityManager.addActor(x, y, Globals.getActorInfo(id), false);
				}
				else if(typeName.equalsIgnoreCase("Item")) {
					m_EntityManager.addItem(x, y, Globals.getItemInfo(id), false);
				}
				else {
					System.out.println("Error! Unknown type name for map object. Skipping.");
					continue;
				}
			}
		}
		
		// If no player starts were initialized, spawn the player at 3,1
		if(m_StartPositions.isEmpty()) {
			m_StartPositions.add(new Vector2());
			System.out.println("WARNING! No player starts initialized! Spawning player at 0,0");
		}
		
		// The player needs to exists otherwise the game crashes.
		Entity player = m_EntityManager.addActor(m_StartPositions.get(0).x, m_StartPositions.get(0).y, Globals.getActorInfo(DatabaseUtility.getIDFromTypeName("ACTOR_PLAYER", "Actor")), true);
		m_EntityManager.setPlayer(player);
		
		saveCheckpoint();
	}
	
	private void initGrid() {
		m_TileGrid = new Tile[getWidth() * getHeight()];
		for(int i=0; i<m_TileGrid.length; i++) {
			int x = i % getWidth();
			int y = i / getWidth();
			m_TileGrid[i] = new Tile(this, (TiledMapTileLayer) m_ForegroundLayer, x, y);
		}
	}
	
	//=========================================================================
	// Updating
	//=========================================================================
	
	public void draw(float deltaTime, SpriteBatch batch) {
		m_EntityManager.draw(deltaTime, batch);
	}
	
	// Step the level
	public void update(float deltaTime) {
		m_EntityManager.update(deltaTime);
	}
	
	//=========================================================================
	// Layers and Tiles
	//=========================================================================
	
	public MapLayer getLayer(String layerName) {
		return m_TiledMap.getLayers().get(layerName);
	}
	
	public Tile getTile(int index) {
		if(index < 0 || index >= m_TileGrid.length) return null;
		return m_TileGrid[index];
	}
	
	public Tile getTile(int x, int y) { 
		if(x < 0 || x >= getWidth()) return null;
		if(y < 0 || y >= getHeight()) return null;
		return m_TileGrid[x + y * getWidth()];
	}
	
	public Tile getTileByType(int x, int y, TileType type) { 
		if(x < 0 || x >= getWidth()) return null;
		if(y < 0 || y >= getHeight()) return null;
		int index = x + y * getWidth();
		if(m_TileGrid[index].getTileType() != type) return null;
		return m_TileGrid[index];
	}
	
	public Entity getPlayer() { 
		return m_EntityManager.getPlayer();
	}
	
	//=========================================================================
	// Start Positions
	//=========================================================================
	public void saveCheckpoint() {
		PlayerState state = new PlayerState();
		state.snapshot(getPlayer());
		m_Checkpoint = new Checkpoint(getPlayer(), m_StartPositions.get(0), state);
	}
	
	// Respawn the player
	public void respawn() {
		m_Checkpoint.load();
		getPlayer().setDead(false);
		
		// TODO: Reset the level to the state it was in at the checkpoint (basically, make it smarter)
	}
	
	public ArrayList<Vector2> getStartPositions() {
		return m_StartPositions;
	}
	
	//=========================================================================
	// Level Bottom
	//=========================================================================
	
	public void setLevelBoundaryX(float leftX) {
		if(leftX < 0 || leftX >= m_Width - Constants.NUM_TILES_ON_GAME_SCREEN_WIDTH) return;
		m_CurrentBoundaryX = leftX;
	}
	
	public void changeLevelBoundaryX(float change) {
		setLevelBoundaryX(getLevelBoundaryX() + change);
	}
	
	public float getLevelBoundaryX() {
		return m_CurrentBoundaryX;
	}
	
	public void setLevelBoundaryY(float bottomY) {
		if(bottomY < 0 || bottomY >= m_Height - Constants.NUM_TILES_ON_GAME_SCREEN_HEIGHT) return;
		m_CurrentBoundaryY = bottomY;
	}
	
	public void changeLevelBoundaryY(float change) {
		setLevelBoundaryY(getLevelBoundaryY() + change);
	}
	
	public float getLevelBoundaryY() {
		return m_CurrentBoundaryY;
	}
	
	//=========================================================================
	// Accessor Functions
	//=========================================================================
	
	public int getWidth() { 
		return m_Width;
	}
	
	public int getHeight() { 
		return m_Height;
	}
	
	public TiledMapTileLayer getForegroundLayer() {
		return (TiledMapTileLayer) m_ForegroundLayer;
	}
	
	public MapLayer getObjectLayer() { 
		return m_ObjectLayer;
	}
	
	public TiledMap getMap() {
		return m_TiledMap;
	}
	
	public boolean isLoaded() {
		return m_isLoaded;
	}
	
	public EntityManager getEntityManager() {
		return m_EntityManager;
	}
}
