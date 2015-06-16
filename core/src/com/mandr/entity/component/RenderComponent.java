package com.mandr.entity.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.level.Tile;
import com.mandr.util.Constants;

public class RenderComponent extends Component implements Drawable {
	private Sprite m_Sprite;
	
	public RenderComponent(Entity entity, Texture texture) {
		super(entity);
		
		m_Sprite = new Sprite(texture);
	}

	@Override
	public void update(float deltaTime) {
		// empty method, use drawable interface instead
	}

	@Override
	public void stateChange(EntityState oldState, EntityState newState) {
		// TODO: I have decided to use some sort of file to store starting IDs and animation numbers for various entity states
	}

	@Override
	public void collision(Entity other) {}

	@Override
	public void collision(Tile tile) {}
	
	@Override
	public ComponentType getType() {
		return ComponentType.COMPONENT_RENDER;
	}

	@Override
	public void draw(SpriteBatch batch) {
		float scale = 1 / (float) Constants.NUM_PIXELS_PER_TILE;
		
		// Width/Height of the Sprite scaled to game size
		float spriteWidth = m_Sprite.getWidth() * scale;
		float spriteHeight = m_Sprite.getHeight() * scale;
		
		// Entity's game position
		float centerX = m_Entity.getEndPosition().x + m_Entity.getSize().x / 2;
		
		// Where we will draw the sprite
		float x = centerX - spriteWidth/2;	// Centered around the entity's centerX
		float y = m_Entity.getEndPosition().y;					// Bottom of the game entity position
		
		// OriginX and OriginY should be width/2 and height/2.
		batch.draw(m_Sprite, x, y, spriteWidth/2, spriteHeight/2, spriteWidth, spriteHeight, 1, 1, m_Sprite.getRotation());
	}
	
	public void draw(ShapeRenderer render) {
		Vector2 position = m_Entity.getEndPosition();
		Vector2 size = m_Entity.getSize();
		
		render.setColor(Color.WHITE);
		render.rect(position.x, position.y, size.x, size.y);
	}

	public Sprite getSprite() {
		return m_Sprite;
	}
}
