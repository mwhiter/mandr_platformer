package com.mandr.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mandr.game.screens.GameScreen;
import com.mandr.level.Tile;
import com.mandr.util.AABB;
import com.mandr.util.Constants;

public abstract class DynamicEntity implements Drawable {
	protected Sprite m_Sprite;
	protected Vector2 m_Size;
	
	protected Vector2 m_Velocity;				// Real velocity
	protected Vector2 m_VelocityAfterMovement;	// Velocity after we've moved (and scaled by dT)
	
	protected Vector2 m_Position;
	protected Vector2 m_PositionBeforeMovement;	// The position last frame
	
	private boolean m_Friendly;
	
	public DynamicEntity(Texture texture, float x, float y, float sx, float sy) {
		this(texture, new Vector2(x,y), new Vector2(sx,sy));
	}
	
	public DynamicEntity(Texture texture, Vector2 position, Vector2 size) {
		m_Size = size;
		
		m_Sprite = new Sprite(texture);
		
		m_Position = position;
		m_PositionBeforeMovement = new Vector2(position);
		
		m_Velocity = new Vector2(0,0);
		m_VelocityAfterMovement = new Vector2(0,0);
		
		m_Friendly = false;
	}
	
	@Override
	public String toString() {
		return "Pos: " + m_Position + "; Vel: " + m_Velocity;
	}
	
	public Sprite getSprite() { return m_Sprite; }
	public Vector2 getPosition() { return m_Position; }
	public Vector2 getSize() { return m_Size; }
	public Vector2 getVelocity() { return m_Velocity; }
	public AABB getBoundingBox() { return new AABB(m_Position, m_Size); }
	
	public Vector2 getPositionBeforeMovement() { return m_PositionBeforeMovement; }
	public Vector2 getVelocityAfterMovement() { return m_VelocityAfterMovement; }
	
	public abstract void handleCollision(DynamicEntity other);
	public abstract void update(float deltaTime);
	public abstract boolean shouldCollide(Tile tile, boolean x_axis);
	
	// Draws the entity's sprite.
	public void draw(SpriteBatch batch) {
		float scale = 1 / (float) Constants.NUM_PIXELS_PER_TILE;
		
		// Width/Height of the Sprite scaled to game size
		float spriteWidth = m_Sprite.getWidth() * scale;
		float spriteHeight = m_Sprite.getHeight() * scale;
		
		// Entity's game position
		float centerX = m_Position.x + m_Size.x / 2;
		
		// Where we will draw the sprite
		float x = centerX - spriteWidth/2;	// Centered around the entity's centerX
		float y = m_Position.y;					// Bottom of the game entity position
		
		// OriginX and OriginY should be width/2 and height/2.
		batch.draw(m_Sprite, x, y, spriteWidth/2, spriteHeight/2, spriteWidth, spriteHeight, 1, 1, m_Sprite.getRotation());
	}
	
	// Draws bounding box around entity. Should be for debugging purposes only
	public void draw(ShapeRenderer render) {
		render.setColor(Color.WHITE);
		render.rect(m_Position.x, m_Position.y, m_Size.x, m_Size.y);
	}
	
	/** Is the entity on the screen?
	 * @return Whether or not the entity is in the screen */
	public boolean isOnScreen() {
		Rectangle screenBounds = GameScreen.getRenderer().getCameraBounds();
		Rectangle entityBounds = new Rectangle(getPosition().x, getPosition().y, getSize().x, getSize().y);
		
		float padX = screenBounds.getWidth() * 0.25f;
		float padY = screenBounds.getHeight() * 0.25f;
		
		screenBounds.x = screenBounds.x - padX/2;
		screenBounds.y = screenBounds.y - padY/2;
		screenBounds.width = screenBounds.width + padX;
		screenBounds.height = screenBounds.height + padY;
		
		return screenBounds.overlaps(entityBounds);
	}
	
	public boolean collide(DynamicEntity other) {
		return AABB.collide(getBoundingBox(), other.getBoundingBox());
	}
	
	/** Is this a friendly entity? (i.e. belongs to player) */
	public void setFriendly(boolean value) {
		m_Friendly = value;
	}
	
	/** Is this a friendly entity? (i.e. belongs to player) 
	 * @return whether the entity is friendly */
	public boolean isFriendly() {
		return m_Friendly;
	}
}
