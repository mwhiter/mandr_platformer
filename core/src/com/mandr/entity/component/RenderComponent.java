package com.mandr.entity.component;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mandr.entity.Entity;
import com.mandr.enums.AnimState;
import com.mandr.enums.EntityState;
import com.mandr.game.Globals;
import com.mandr.level.Tile;
import com.mandr.util.Constants;

public class RenderComponent extends Component {
	private float m_Rotation;
	
	private TextureRegion m_CurrentFrame;
	private AnimState m_AnimState;
	private float stateTime;
	
	// HashMap of animations. Each animation has a name.
	HashMap<String, Animation> m_Animations;
	
	public RenderComponent(Entity entity, int animID) {
		super(entity);
		
		m_Animations = Globals.getAnimInfo(animID);
		m_AnimState = AnimState.ANIM_STATE_IDLE;
		stateTime = 0.0f;
		m_Rotation = 0.0f;
	}
	
	@Override
	public void reset() { }

	@Override
	public void update(float deltaTime) {
		processMessages();
	}
	
	@Override
	public void receiveMessage(ComponentMessage msg) {
		// TODO: This is too simple. Animation states are more complicated than this.
		switch(msg) {
		case MESSAGE_MOVE_LEFT:
			m_AnimState = AnimState.ANIM_STATE_MOVE_LEFT;
			break;
		case MESSAGE_MOVE_RIGHT:
			m_AnimState = AnimState.ANIM_STATE_MOVE_RIGHT;
			break;
		case MESSAGE_MOVE_STOP:
			m_AnimState = AnimState.ANIM_STATE_IDLE;
			break;
		default: break;
		}
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
		if(m_Animations == null) return null;
		return m_Animations.get(m_AnimState.name);
	}
	
	public AnimState getAnimState() {
		return m_AnimState;
	}
	
	public void draw(float delta, SpriteBatch batch) {
		float scale = 1 / (float) Constants.NUM_PIXELS_PER_TILE;
		
		stateTime += delta;
		
		// TODO What do we do if this is null?
		Animation anim = getAnim();
		if(anim == null) {
			System.out.println("WARNING! Entity animation is null! What do we do?");
			return;
		}
		
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
		batch.draw(m_CurrentFrame, x, y, spriteWidth/2, spriteHeight/2, spriteWidth, spriteHeight, 1, 1, m_Rotation);
	}
	
	public void draw(ShapeRenderer render) {
		Vector2 position = m_Entity.getEndPosition();
		Vector2 size = m_Entity.getSize();
		
		render.setColor(Color.WHITE);
		render.rect(position.x, position.y, size.x, size.y);
	}
	
	public float getRotation() {
		return m_Rotation;
	}
	
	public void setRotation(float degrees) {
		while(degrees >= 360.0f)
			degrees -= 360.0f;
		while(degrees < 0.0f)
			degrees += 360.0f;
		
		m_Rotation = degrees;
	}
}
