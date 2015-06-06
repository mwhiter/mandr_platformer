package com.mandr.level;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.mandr.entity.Enemy;
import com.mandr.entity.EntityManager;
import com.mandr.entity.Player;
import com.mandr.entity.ai.AITypes;
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
	Vector2 m_ActiveStartPosition;
	
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
		
		// TODO: test way of doing things
		ArrayList<Enemy> enemies = new ArrayList<Enemy>();
		
		MapLayer objectLayer = getObjectLayer();
		MapObjects objects = objectLayer.getObjects();
		MapObject object;
		for(int i = 0; i < objects.getCount(); i++) {
			object = objects.get(i);
			String type = (String) object.getProperties().get("type");
			System.out.println(i + ": " + type);
			if(type.equalsIgnoreCase("StartPosition")) {
				m_StartPositions.add(new Vector2((Float)object.getProperties().get("x") / 16.0f, (Float)object.getProperties().get("y") / 16.0f));
			}
			else if(type.equalsIgnoreCase("Enemy_00")) {
				enemies.add(new Enemy(AITypes.AI_TYPE_WALKING, new Texture("resources/entities/test_enemy_img.png"), (Float)object.getProperties().get("x") / 16.0f, (Float)object.getProperties().get("y") / 16.0f, 1, 1));
			}
		}
		
		// If no player starts were initialized, spawn the player at 3,1
		if(m_StartPositions.size() == 0) {
			m_StartPositions.add(new Vector2(3,1));
			System.out.println("WARNING! No player starts initialized! Spawning player at 3,1");
		}
		m_ActiveStartPosition = m_StartPositions.get(0);
		
		// Create the player at a start position
		Player player = new Player(new Texture("resources/entities/test_player_spritesheet.png"), m_ActiveStartPosition.x, m_ActiveStartPosition.y, 1, 2);
		
		m_EntityManager.addEntity(player, true, true);
		
		for(Enemy enemy : enemies) {
			enemy.getAI().setSpeedLevel(0);
			m_EntityManager.addEntity(enemy, false, false);
		}
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
		m_EntityManager.draw(batch);
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
	
	public Player getPlayer() { 
		return (Player) m_EntityManager.getPlayer();
	}
	
	//=========================================================================
	// Start Positions
	//=========================================================================
	
	public ArrayList<Vector2> getStartPositions() {
		return m_StartPositions;
	}
	
	public Vector2 getActiveStartPosition() {
		return m_ActiveStartPosition;
	}
	
	public void setActiveStartPosition(int i) {
		if(i < 0 || i >= m_StartPositions.size()) return;
		m_ActiveStartPosition = m_StartPositions.get(i);
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