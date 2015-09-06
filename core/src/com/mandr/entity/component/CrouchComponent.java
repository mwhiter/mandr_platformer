package com.mandr.entity.component;

import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.game.screens.GameScreen;
import com.mandr.level.Tile;
import com.mandr.util.AABB;

public class CrouchComponent extends Component {
	private float m_CrouchSpeed;
	private boolean m_Crouch;
	private boolean m_WantsToReleaseCrouch;
	
	public CrouchComponent(Entity entity, float crouchSpeed) {
		super(entity);
		m_Crouch = false;
		m_CrouchSpeed = crouchSpeed;
	}

	@Override
	public void reset() {
		m_Crouch = false;
	}

	@Override
	public void update(float deltaTime) {
		if(m_Entity.isCrouched()) {
			stopCrouching();
		}
		if(m_Crouch) {
			m_Entity.changeState(EntityState.ENTITY_STATE_CROUCH);
		}
	}

	@Override
	public void stateChange(EntityState oldState, EntityState newState) {
		if(newState == EntityState.ENTITY_STATE_CROUCH)
			m_Entity.getSize().y /= 2;
		if(oldState == EntityState.ENTITY_STATE_CROUCH) {
			m_Entity.getSize().y *= 2;
			m_Crouch = false;
		}
	}

	@Override
	public ComponentType getType() {
		return ComponentType.COMPONENT_CROUCH;
	}
	
	// Can this entity crouch?
	public boolean canCrouch() {
		if(m_Entity.getState() != EntityState.NO_ENTITY_STATE)
			return false;
		
		// If we have a crouch, we have a move as well. No need for null check here.
		MoveComponent move = (MoveComponent) m_Entity.getComponent(ComponentType.COMPONENT_MOVE);
		if(!move.isGrounded())
			return false;
		
		return true;
	}

	// Can we actually release the crouch?
	public boolean canReleaseCrouch() {
		// Not crouched - can't release
		if(!m_Entity.isCrouched())
			return false;
		
		// If our standing box collides with the world
		AABB standingBox = new AABB(m_Entity.getStartPosition().x, m_Entity.getStartPosition().y, m_Entity.getSize().x, m_Entity.getSize().y * 2);
		if(Entity.collideWorld(standingBox)) {
			return false;
		}
		
		// Check above us at the height where we're supposed to stand. If we can't fit there, don't do it
		// TODO: Maybe consider something different, because if we change how much height the crouch reduces us by, this will not be changed
		// TODO: Maybe not a magic number.
		float standingTopY = m_Entity.getStartPosition().y + (m_Entity.getSize().y * 2);
		int standingTopIndex = (int) (standingTopY - 0.00001f);	// subtract by this constant otherwise we get the wrong index if we're directly on the line
		Tile loopTile = null;
		for(int x = m_Entity.getStartBoundingBox().getLeftXIndex(); x <= m_Entity.getStartBoundingBox().getRightXIndex(); x++) {
			loopTile = GameScreen.getLevel().getTile(x, standingTopIndex);
			if(loopTile == null) continue;
			if(loopTile.shouldCollide(m_Entity.getStartBoundingBox(), false)) {
				return false;
			}
		}
		
		return true;
	}
	
	public void crouch() {
		if(!canCrouch()) return;
		m_Crouch = true;
		m_WantsToReleaseCrouch = false;
	}
	
	public void uncrouch() {
		m_WantsToReleaseCrouch = true;
	}
	
	private void stopCrouching() {
		if(!m_WantsToReleaseCrouch) return;
		if(!canReleaseCrouch()) return;
		
		m_Crouch = false;
		m_Entity.changeState(EntityState.NO_ENTITY_STATE);
	}
	
	public float getCrouchSpeed() {
		return m_CrouchSpeed;
	}

	@Override
	public void collision(Entity other) {}

	@Override
	public void collision(Tile tile) {}
}
