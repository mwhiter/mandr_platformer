package com.mandr.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mandr.game.GameGlobals;
import com.mandr.game.screens.GameScreen;
import com.mandr.util.Constants;
import com.mandr.util.Directions;

public class Player extends Actor {
	//private Directions m_MovementX;
	private float m_LastTookDamageTime;	// Time when player last took damage
	private float m_FallStartedTime;	// Time when the current fall started
	
	public Player(Texture texture, float x, float y, float sx, float sy) {
		super(texture, x, y, sx, sy);
		init();
	}
	
	public Player(Texture texture, Vector2 pos, Vector2 size) {
		super(texture, pos, size);
		init();
	}
	
	private void init() {
		m_LastTookDamageTime = -1;
		m_FallStartedTime = -1;
		setMoveSpeed(6.0f);
		setClimbSpeed(6.0f);
		
		addWeapon(GameGlobals.getWeaponStats()[2]);
	}
	// TODO: test
	@Override
	public void draw(SpriteBatch batch) {
		int offset = spriteSheetRegion();
		float x = m_Position.x - m_Size.x/2;
		
		TextureRegion region = new TextureRegion(m_Sprite.getTexture(), offset * 32, 0, 32, 32);
		
		batch.draw(region, x, m_Position.y, 2, 2);
	}
	
	public void update(float deltaTime) {
		updateLook();
		
		// Were we grounded last frame?
		boolean groundedBefore = isGrounded();
		super.update(deltaTime);
		setFallTime(groundedBefore);	// done after move() because grounded will have changed
		
		// The player cannot go past the level boundary (rule does not apply to regular entities, who can move past it)
		if(m_Position.x < GameScreen.getLevel().getLevelBoundaryX())
			m_Position.x = GameScreen.getLevel().getLevelBoundaryX();
	}
	
	// The player looks at his input
	public void updateLook() {
		// TODO: Maybe I want to have multiple player support. This certainly WILL NOT WORK
		// Unproject the mouse coordinates to screen space
		Vector3 mouseCoords = GameScreen.getRenderer().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f));
		
		// The player's center coordinates
		float centerX = m_Position.x + m_Size.x/2;
		float centerY = m_Position.y + m_Size.y/2;
		
		Vector2 newLookVector = new Vector2(mouseCoords.x - centerX, mouseCoords.y - centerY);
		
		float angle = newLookVector.angle();
		
		// To prevent accidental stupidity, the player can never look more than 45 degrees away from down.
		float noLookdownAngle = Math.min(Constants.INPUT_NO_LOOKDOWN_ANGLE, 45);
		
		float min = 270 - noLookdownAngle;
		float max = 270 + noLookdownAngle;
		
		// Normalize vector because we only want the direction!
		newLookVector.nor();
		
		// If the vector angle is in this range
		if(angle > min && angle < max) {
			// LibGDX does all this for us, but all you have to do is say x = cos(x), y = sin(x), since it's normalized.
			
			// set to the closer sit
			float newAngle = (max - angle <= angle - min) ? max : min;
			newLookVector.setAngle(newAngle);
		}
		
		setLookAt(newLookVector);
	}

	@Override
	public void handleCollision(DynamicEntity other) {
		if(other.isFriendly())
			return;
		
		// We collided with an enemy
		if(other instanceof Enemy) {		
			if(canReceiveDamage()) {
				// Push the player back in the direction of getting hit
				// TODO: Magic number
				float pushBackVelocity = 15;
				pushBackVelocity = (m_Position.x > other.getPosition().x ? pushBackVelocity : -pushBackVelocity);
				
				m_Velocity.x = pushBackVelocity;;
			}
			
			// TODO: magic number. should be based off entity we collided with
			damage(25);
		}
	}
	
	/** Damages the Player */
	@Override
	public void damage(int damage) {
		if(!canReceiveDamage()) return;
		changeHealth(-damage);
		m_LastTookDamageTime = GameGlobals.getGameTime();
	}
	
	// Player is dead. What happens now?
	public void kill() {
		System.out.println("Kill player. Resetting his position to the start position first.");
		m_Position.x = GameScreen.getLevel().getActiveStartPosition().x;
		m_Position.y = GameScreen.getLevel().getActiveStartPosition().y;
		setHealth(100);
	}
	
	// TODO: test sprite sheet function
	public int spriteSheetRegion() {
		if(getState() == ActorStates.ENTITY_STATE_CROUCH) {
			if(getLookDirectionX() == Directions.DIRECTION_LEFT) {
				return 2;
			}
			else {
				return 3;
			}
		}
		if(getState() == ActorStates.ENTITY_STATE_LADDER) {
			return 4;
		}
		else {
			if(getLookDirectionX() == Directions.DIRECTION_LEFT) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}
	
	@Override
	public void notifyStateChange(ActorStates oldState, ActorStates newState) {
		if(oldState == newState) return;
		
		switch(newState) {
		case NO_ENTITY_STATE:
		case ENTITY_STATE_JUMP:
			if(oldState == ActorStates.ENTITY_STATE_LADDER) {
				m_FallStartedTime = GameGlobals.getGameTime();
			}
			break;
		case ENTITY_STATE_CROUCH:
			break;
		case ENTITY_STATE_LADDER:
			break;
		}
	}
	
	//=========================================================================
	// Jumping
	//=========================================================================	
	// Actually jump.
	public void playerJump() {
		if(!isGrounded()) {
			if(getState() == ActorStates.NO_ENTITY_STATE) {
				float currentTime = GameGlobals.getGameTime();
				if(currentTime >= m_FallStartedTime + Constants.FALL_PADDING)
					return;
			}
		}
		
		float jumpVelocity = 16.0f;
		jump(jumpVelocity);
	}
	
	// Special player control to allow variable jump height if the jump key is released in mid air
	public void releaseJump() {
		if(m_Velocity.y > Constants.PLAYER_SHORT_JUMP_THRESHOLD)
			m_Velocity.y = Constants.PLAYER_SHORT_JUMP_THRESHOLD;
	}
	
	/** Set the time that we've fallen off a ledge */
	private void setFallTime(boolean groundedBefore) {
		// Are we grounded now?
		boolean groundedAfter = isGrounded();
		
		// If last frame we were grounded, and this frame we're not grounded
		if(groundedBefore && !groundedAfter) {
			if(getMovementDirectionY() != Directions.DIRECTION_UP) {
				m_FallStartedTime = GameGlobals.getGameTime();
			}
		}
	}
	
	/** Is player invincible because he took damage recently?
	 * @param Whether the player is invincible because of damage. */
	public boolean isDamageInvincible() {
		float currentTime = GameGlobals.getGameTime();
		return (currentTime - m_LastTookDamageTime <= Constants.PLAYER_DAMAGE_INVINCIBLE_TIME);
	}
	
	@Override
	/** Can the player receive damage?
	 * @return Whether or not the player can receive damage. */
	public boolean canReceiveDamage() {
		if(isDamageInvincible())
			return false;
		
		return true;
	}
	
	/** Can the player move?
	 * @return Whether or not the player can move. */
	public boolean canMove() {
		float currentTime = GameGlobals.getGameTime();
		return (currentTime - m_LastTookDamageTime <= Constants.PLAYER_DAMAGE_IMMOBILE_TIME);
	}
}