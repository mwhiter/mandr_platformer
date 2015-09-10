package com.mandr.entity.component;

import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.enums.TileType;
import com.mandr.game.screens.GameScreen;
import com.mandr.level.Tile;
import com.mandr.util.AABB;
import com.mandr.util.StringUtils;
import com.mandr.util.Directions;

// TOOD: Absolutely broken. Redesign.
public class LadderComponent extends Component {
	private Tile m_LadderTile;
	private boolean m_AttachLadder;
	private boolean m_DetachLadder;
	private float m_ClimbSpeed;
	private Directions m_ClimbDirection;
	
	public LadderComponent(Entity entity, float climbSpeed) {
		super(entity);
		m_LadderTile = null;
		m_AttachLadder = false;
		m_DetachLadder = false;
		m_ClimbSpeed = climbSpeed;
		m_ClimbDirection = Directions.NO_DIRECTION;
	}
	
	@Override
	public void reset() {
		m_LadderTile = null;
		m_AttachLadder = false;
		m_DetachLadder = false;
		m_ClimbDirection = Directions.NO_DIRECTION;
	}
	
	@Override
	public void update(float deltaTime) {
		processMessages();
		
		if(m_AttachLadder) {
			m_AttachLadder = false;
			doAttach();
		}
		if(m_DetachLadder) {
			m_DetachLadder = false;
			doDetach();
		}
		moveLadder();
		
		calcLadderTile();
		validate();
	}

	@Override
	public void receiveMessage(ComponentMessage msg) {

	}
	
	@Override
	public void stateChange(EntityState oldState, EntityState newState) {
		// No longer on a ladder
		if(oldState == EntityState.ENTITY_STATE_LADDER) {
			StringUtils.debugPrint("(Ladder Component) No longer on ladder.");
			m_AttachLadder = false;
			m_DetachLadder = false;
			m_ClimbDirection = Directions.NO_DIRECTION;
		}
		
		// Jumping?
		if(newState == EntityState.ENTITY_STATE_JUMP) {
			detachLadder();
		}
	}

	@Override
	public ComponentType getType() {
		return ComponentType.COMPONENT_LADDER;
	}
	
	/** Validate that we are still on a ladder, taking us off if we are no longer. */
	private void validate() {
		if(!m_Entity.isOnLadder()) return;
		if(m_LadderTile == null) {
			detachLadder();
		}
		else {
			// If we're climbing down and we hit the ground
			boolean grounded = ((MoveComponent) m_Entity.getComponent(ComponentType.COMPONENT_MOVE)).isGrounded();
			if(m_ClimbDirection == Directions.DIRECTION_DOWN && grounded) {
				detachLadder();
			}
		}
	}
	
	//=========================================================================
	// Climbing the Ladder
	//=========================================================================
	private void moveLadder() {
		if(!m_Entity.isOnLadder()) return;
		
		MoveComponent move = (MoveComponent) m_Entity.getComponent(ComponentType.COMPONENT_MOVE);
		switch(m_ClimbDirection) {
		case DIRECTION_UP: move.setVelocityY(m_ClimbSpeed); break;
		case DIRECTION_DOWN: move.setVelocityY(-m_ClimbSpeed); break;
		// If we are "climbing" left or right, simply detach from the ladder.
		case DIRECTION_LEFT:
		case DIRECTION_RIGHT:
			detachLadder();
			break;
		default:
			move.setVelocityY(0);
		}
	}
	
	public void climbLadder(Directions climbDirection) {
		if(!m_Entity.isOnLadder()) return;
		
		if(climbDirection == Directions.DIRECTION_LEFT || climbDirection == Directions.DIRECTION_RIGHT)
			detachLadder();
		else
			m_ClimbDirection = climbDirection;
	}
	
	//=========================================================================
	// Attaching to Ladder
	//=========================================================================
	
	/** Actually attach the entity to the ladder tile. */
	private void doAttach() {
		StringUtils.debugPrint("Attached to ladder.");
		m_Entity.getEndPosition().x = m_LadderTile.getX();
		
		if(m_LadderTile.isLadderTop()) {
			m_Entity.getEndPosition().y -= 0.25f;
		}
		
		m_Entity.changeState(EntityState.ENTITY_STATE_LADDER);
	}
	
	/** Try to attach to a ladder. 
	 * @param attachDirection Direction in which we attached to the ladder.
	 * @return If the attach was successful. */
	public boolean attachLadder(Directions attachDirection) {
		if(!canAttachLadder(attachDirection)) return false;
		m_AttachLadder = true;
		return false;
	}
	
	public boolean canAttachLadder(Directions attachDirection) {
		// Cannot be on ladder already.
		if(m_Entity.isOnLadder())
			return false;
		// Must be intersecting a ladder tile.
		if(m_LadderTile == null)
			return false;
		
		AABB box = m_Entity.getStartBoundingBox();
		AABB tileBox = m_LadderTile.getBoundingBox();
		
		switch(attachDirection) {
		case DIRECTION_UP:
			// Do not attach to a ladder if we're fully above the ladder
			if(box.getBottomY() >= tileBox.getTopY())
				return false;
			// Do not attach to a ladder if we're fully below it
			if(box.getTopY() < tileBox.getBottomY())
				return false;
			break;
		case DIRECTION_DOWN:
			// If the ladder isn't a ladder top tile, we can't attach by pressing down
			if(!m_LadderTile.isLadderTop())
				return false;
			// If we're below the top of the tile, then we can't attach by pressing down
			if(box.getBottomY() < tileBox.getTopY())
				return false;
			break;
		default:
			return false;
		}
		
		return true;
	}
	
	//=========================================================================
	// Detaching to Ladder
	//=========================================================================
	
	private void doDetach() {
		StringUtils.debugPrint("Detaching to ladder.");
		m_Entity.changeState(EntityState.NO_ENTITY_STATE);
	}
	
	/** Try to detach from a ladder. */
	public boolean detachLadder() {
		if(!canDetachLadder()) return false;
		m_DetachLadder = true;
		return true;
	}
	
	public boolean canDetachLadder() {
		// Cannot detach from a ladder if we are not on one
		if(!m_Entity.isOnLadder())
			return false;
		return true;
	}
	
	//=========================================================================
	// Calculating Ladder Tile
	//=========================================================================
	
	/** Return the ladder tile
	 * @return Ladder tile the entity intersects, updated each frame */
	public Tile getLadderTile() {
		return m_LadderTile;
	}
	
	private void calcLadderTile() {
		AABB boundingBox = m_Entity.getStartBoundingBox();
		
		Tile loopTile = null;
		int x = boundingBox.getCenterXIndex();
		for(int y = boundingBox.getBottomYIndex(); y <= boundingBox.getTopYIndex(); y++) {
			loopTile = GameScreen.getLevel().getTileByType(x, y, TileType.TILE_LADDER);
			if(loopTile == null) continue;
			
			m_LadderTile = loopTile;
			return;
		}
		
		// Perhaps standing on a ladder?
		Tile belowTile = GameScreen.getLevel().getTileByType(x, boundingBox.getBottomYIndex()-1, TileType.TILE_LADDER);
		if(belowTile != null) {
			//StringUtils.debugPrint(Float.toString(m_Entity.getStartPosition().y - (float)(belowTile.getY()+1)));
			if(m_Entity.getStartPosition().y - (float)(belowTile.getY()+1) <= 0.0001f) {
				m_LadderTile = belowTile;
				return;
			}
		}
		
		m_LadderTile = null;
	}

	@Override
	public void collision(Entity other) {}

	@Override
	public void collision(Tile tile) {}
}
