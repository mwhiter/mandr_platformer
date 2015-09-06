package com.mandr.entity.component;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mandr.entity.Entity;
import com.mandr.enums.AnimState;
import com.mandr.enums.EntityState;
import com.mandr.graphics.anim.AnimLoader;
import com.mandr.level.Tile;
import com.mandr.util.Constants;

public class RenderComponent extends Component {
	private TextureRegion m_CurrentFrame;
	private AnimState m_AnimState;
	float stateTime;
	
	// HashMap of animations. Each animation has a name.
	HashMap<String, Animation> m_Animations;
	
	public RenderComponent(Entity entity, String animDef) {
		super(entity);
		
		m_Animations = AnimLoader.parse(animDef);
		m_AnimState = AnimState.ANIM_STATE_IDLE;
		stateTime = 0.0f;
	}
	
	@Override
	public void reset() { }

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

	/** Looks at the animation state, and returns the correct animation 
	 * @return The current animation */
	private Animation getAnim() {
		switch(m_AnimState) {
		case ANIM_STATE_IDLE: return m_Animations.get("IDLE");
		default: return null;
		}
	}
	
	// TODO: Render animation
	public void draw(SpriteBatch batch) {
		float scale = 1 / (float) Constants.NUM_PIXELS_PER_TILE;
		
		stateTime += Gdx.graphics.getDeltaTime();
		Animation anim = getAnim();
		m_CurrentFrame = anim.getKeyFrame(stateTime, true);
		
		// Width/Height of the Sprite scaled to game size
		float spriteWidth = m_CurrentFrame.getRegionWidth() * scale;
		float spriteHeight = m_CurrentFrame.getRegionHeight() * scale;
		
		// Entity's game position
		float centerX = m_Entity.getEndPosition().x + m_Entity.getSize().x / 2;
		
		// Where we will draw the sprite
		float x = centerX - spriteWidth/2;		// Centered around the entity's centerX
		float y = m_Entity.getEndPosition().y;	// Bottom of the game entity position
		
		// OriginX and OriginY should be width/2 and height/2.
		
		//batch.draw(m_CurrentFrame, 2,21, spriteWidth,spriteHeight);
		batch.draw(m_CurrentFrame, x, y, spriteWidth/2, spriteHeight/2, spriteWidth, spriteHeight, 1, 1, 0);
	}
	
	public void draw(ShapeRenderer render) {
		Vector2 position = m_Entity.getEndPosition();
		Vector2 size = m_Entity.getSize();
		
		render.setColor(Color.WHITE);
		render.rect(position.x, position.y, size.x, size.y);
	}
}
