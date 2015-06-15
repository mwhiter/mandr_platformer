package com.mandr.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.mandr.entity.Entity;
import com.mandr.game.MyGame;
import com.mandr.game.screens.GameScreen;
import com.mandr.graphics.ui.GameHUD;
import com.mandr.level.Level;
import com.mandr.util.Constants;

public class GameRenderer {

	private GameScreen m_GameScreen;
	
	private SpriteBatch m_SpriteBatch;
	private ShapeRenderer m_ShapeRenderer;
	
	private OrthographicCamera m_Camera;
	private OrthogonalTiledMapRenderer m_MapRenderer;

	// The actor in which the game renders around
	private Entity m_TrackedEntity;
	
	private GameHUD m_HUD;
	
	public GameRenderer(GameScreen gameScreen) {
		m_GameScreen = gameScreen;
		
		m_SpriteBatch = new SpriteBatch();
		m_ShapeRenderer = new ShapeRenderer();
		m_Camera = new OrthographicCamera(Constants.NUM_TILES_ON_GAME_SCREEN_WIDTH,Constants.NUM_TILES_ON_GAME_SCREEN_HEIGHT);
		
		m_TrackedEntity = GameScreen.getLevel().getPlayer();
		
		m_HUD = new GameHUD();
		
		// 16 pixels = 1 unit		
		float unitScale = 1 / (float) Constants.NUM_PIXELS_PER_TILE;
		m_MapRenderer = new OrthogonalTiledMapRenderer(GameScreen.getLevel().getMap(), unitScale);
		
		// unload method?
		//m_MapRenderer = null;
	}
	
	public GameScreen getGameScreen() {
		return m_GameScreen;
	}
	
	public void resize(int width, int height) {
	}
	
	public void draw(float deltaTime) {
		// Update the camera
		updateCamera();
		
		// Draw tiled map
		renderTiledMap();
		
		// SpriteBatch
		m_SpriteBatch.begin();
			// Render the level
			m_SpriteBatch.setProjectionMatrix(m_Camera.combined);
			GameScreen.getLevel().draw(deltaTime, m_SpriteBatch);

			
			m_SpriteBatch.setProjectionMatrix(getNormalProjection());
			m_HUD.draw(m_SpriteBatch);
			
			// Render HUD overlay
			// TODO: temporary?
			if(MyGame.isDebug()) {
				m_HUD.drawDebugHUD(m_SpriteBatch);
			}
			
		m_SpriteBatch.end();
		
		// ShapeRenderer
		if(MyGame.isDebug()) {
			m_ShapeRenderer.begin(ShapeType.Line);
				m_ShapeRenderer.setProjectionMatrix(m_Camera.combined);
				m_HUD.drawLookLine(m_ShapeRenderer);
				GameScreen.getLevel().getEntityManager().draw(m_ShapeRenderer);
			m_ShapeRenderer.end();
		}
	}
	
	public OrthographicCamera getCamera() {
		return m_Camera;
	}
	
	public Matrix4 getNormalProjection() {
		return new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	/** Return the what the camera is looking at*/
	public Rectangle getCameraBounds() {
		float halfWidth = m_Camera.viewportWidth/2;
		float halfHeight = m_Camera.viewportHeight/2;
		float x = m_Camera.position.x - halfWidth;
		float y = m_Camera.position.y - halfHeight;
		return new Rectangle(x,y,m_Camera.viewportWidth,m_Camera.viewportHeight);
	}
	
	/** Move the camera centered around the tracked actor */
	private void updateCamera() {
		Level level = GameScreen.getLevel();
		
		float centerX = m_TrackedEntity.getEndPosition().x + m_TrackedEntity.getSize().x/2;
		float centerY = m_TrackedEntity.getEndPosition().y + m_TrackedEntity.getSize().y/2;
		
		float minX = level.getLevelBoundaryX() + (Constants.NUM_TILES_ON_GAME_SCREEN_WIDTH / 2);
		float minY = level.getLevelBoundaryY() + (Constants.NUM_TILES_ON_GAME_SCREEN_HEIGHT / 2);
		
		float maxX = level.getWidth() - Constants.NUM_TILES_ON_GAME_SCREEN_WIDTH/2;
		float maxY = level.getHeight() - Constants.NUM_TILES_ON_GAME_SCREEN_HEIGHT/2;
		
		m_Camera.position.x = MathUtils.clamp(centerX, minX, maxX);
		m_Camera.position.y = MathUtils.clamp(centerY, minY, maxY);
		
		m_Camera.update();
	}
	
	private void renderTiledMap() {
		m_MapRenderer.setView(m_Camera);
		m_MapRenderer.render();
	}
	
	public Entity getTrackedEntity() {
		return m_TrackedEntity;
	}
	
	public void setTrackedEntity(Entity actor) {
		m_TrackedEntity = actor;
	}
	
	public SpriteBatch getSpriteBatch() {
		return m_SpriteBatch;
	}
	
	public ShapeRenderer getShapeRenderer() {
		return m_ShapeRenderer;
	}
}
