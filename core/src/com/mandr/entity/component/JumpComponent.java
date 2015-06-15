package com.mandr.entity.component;

//import com.mandr.game.GameGlobals;
import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.enums.TileType;
import com.mandr.game.GameGlobals;
import com.mandr.game.screens.GameScreen;
import com.mandr.level.Tile;
import com.mandr.util.AABB;
import com.mandr.util.Constants;

public class JumpComponent extends Component {

	private boolean m_ShouldJump;
	private boolean m_ShouldJumpDown;
	private float m_JumpVelocity;
	private long m_FallStartedTime;
	
	public JumpComponent(Entity entity, float jumpVelocity) {
		super(entity);
		m_ShouldJump = false;
		m_ShouldJumpDown = false;
		m_JumpVelocity = jumpVelocity;
		m_FallStartedTime = -1;
	}

	@Override
	public void update(float deltaTime) {
		// If should jump, then change state to the jump
		if(m_ShouldJump) {
			if(canJumpDown()) {
				m_ShouldJumpDown = true;
			}
			m_Entity.changeState(EntityState.ENTITY_STATE_JUMP);
			m_ShouldJump = false;
		}
		else {
			// If we were in the air and we've hit the ground now, reset state
			if(m_Entity.isJumping()) {
				MoveComponent move = ((MoveComponent) m_Entity.getComponent(ComponentType.COMPONENT_MOVE));
				if(move.getVelocity().y <= 0 && move.isGrounded()) {
					m_Entity.changeState(EntityState.NO_ENTITY_STATE);
				}
			}
		}
	}

	@Override
	public void stateChange(EntityState oldState, EntityState newState) {

		if(newState == EntityState.ENTITY_STATE_JUMP) {
			// Let ladder component take care of this
			if(m_Entity.isOnLadder())
				return;
			
			// If we want to jump down, do that instead.
			if(m_ShouldJumpDown) {	
				m_ShouldJumpDown = false;
				
				// TODO: Why is this end position and not start ?????
				m_Entity.getEndPosition().y += -0.001f;	// move his position down a small amount to allow him to fall
			}
			// Otherwise, jump up
			else {
				((MoveComponent) m_Entity.getComponent(ComponentType.COMPONENT_MOVE)).setVelocityY(m_JumpVelocity);
			}
		}
		
		// Getting off a ladder?
		if(oldState == EntityState.ENTITY_STATE_LADDER) {
			setFallBegun();
		}
	}
	
	@Override
	public ComponentType getType() {
		return ComponentType.COMPONENT_JUMP;
	}

	public void jump() {
		if(!canJump())
			return;
		
		m_ShouldJump = true;
	}
	
	/** Can this entity jump?
	 * @return Whether or not the entity can jump. */
	public boolean canJump() {
		if(m_Entity.isJumping())
			return false;
		
		if(m_Entity.isOnLadder())
			return true;
		
		// TODO: Obviously this is stupid. Perhaps I should figure out a way to save the real standing size.
		float sizeY = (m_Entity.isCrouched() ? m_Entity.getSize().y * 2 : m_Entity.getSize().y);
		float standingTopY = m_Entity.getStartPosition().y + sizeY;
		int standingTopIndex = (int) (standingTopY - 0.00001f);	// subtract by this constant otherwise we get the wrong index if we're directly on the line
		Tile loopTile = null;
		for(int x = m_Entity.getStartBoundingBox().getLeftXIndex(); x <= m_Entity.getStartBoundingBox().getRightXIndex(); x++) {
			loopTile = GameScreen.getLevel().getTile(x, standingTopIndex);
			if(loopTile == null) continue;
			if(loopTile.shouldCollide(m_Entity.getStartBoundingBox(), false)) {
				return false;
			}
		}
		
		if(!((MoveComponent)m_Entity.getComponent(ComponentType.COMPONENT_MOVE)).isGrounded()) {
			if(m_FallStartedTime == -1) return false;
			
			long difference = GameGlobals.getGameTime() - m_FallStartedTime;
			if(difference >= Constants.FALL_PADDING)
				return false;
		}
		
		return true;
	}
	
	/** Can we jump down?
	 * @return Whether or not we can jump down*/
	private boolean canJumpDown() {
		if(!m_Entity.isCrouched())
			return false;
		
		MoveComponent move = ((MoveComponent) m_Entity.getComponent(ComponentType.COMPONENT_MOVE));
		
		if(!move.isGrounded())
			return false;
		if(move.getGroundTile().getTileType() != TileType.TILE_ONE_WAY)
			return false;
		
		// Check the bounds one tile below us
		AABB bounds = m_Entity.getEndBoundingBox();
		AABB boundsChecking = new AABB(bounds.min.x, bounds.min.y - 1, bounds.size.x, 1);
		if(Entity.collideWorld(boundsChecking))
			return false;
		
		return true;
	}
	
	public void release() {
		MoveComponent move = (MoveComponent) m_Entity.getComponent(ComponentType.COMPONENT_MOVE);

		if(move.getVelocity().y > Constants.PLAYER_SHORT_JUMP_THRESHOLD)
			move.getVelocity().y = Constants.PLAYER_SHORT_JUMP_THRESHOLD;
	}
	
	public void setFallBegun() {
		m_FallStartedTime = GameGlobals.getGameTime();
	}
}
