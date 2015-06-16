package com.mandr.entity.component;

import com.badlogic.gdx.math.Vector2;
import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.enums.TileType;
import com.mandr.game.screens.GameScreen;
import com.mandr.level.Tile;
import com.mandr.util.AABB;
import com.mandr.util.Constants;
import com.mandr.util.Directions;

// TODO: way too many to-dos here. Fix them?
public class MoveComponent extends Component {
	private Vector2 m_Velocity;
	
	private float m_Speed;
	
	protected Tile m_GroundTile;		// Tile on the entity's ground
	protected Tile m_CeilingTile;		// Tile on the entity's ceiling

	public MoveComponent(Entity entity, float speed) {
		super(entity);
		
		m_Velocity = new Vector2();
		
		m_GroundTile = null;
		m_Speed = speed;
		
		m_GroundTile = null;
		m_CeilingTile = null;
	}

	@Override
	public void update(float deltaTime) {
		boolean groundedBefore = isGrounded();
		
		m_Velocity.x = giveVelocityX();
		m_Velocity.y = giveVelocityY();
		
		m_Velocity.x = stepX();
		m_Velocity.y = stepY();
		
		// TODO: Event system (i.e. what to do when we collide with a tile?)
		// TODO: right now I have two functions GetCollisionTileX() and GetCollisionTileY()... correct?
		
		Vector2 position = m_Entity.getEndPosition();
		position.add(m_Velocity);
		
		postMovement();
		
		boolean groundedAfter = isGrounded();
		
		// Started a fall: tell jump component that a fall started
		// TODO: Tell all components that a fall started?
		if(groundedBefore && !groundedAfter) {
			if(m_Entity.getState() == EntityState.NO_ENTITY_STATE || m_Entity.getState() == EntityState.ENTITY_STATE_CROUCH) {
				JumpComponent jump = (JumpComponent) m_Entity.getComponent(ComponentType.COMPONENT_JUMP);
				if(jump != null) jump.setFallBegun();
				
				System.out.println("Fall started!");
			}
		}
	}
	
	// Fired after the movement occurs
	private void postMovement() {
		postMovementTileChecks();
		
		// Report a collision with the ground tile if we're on the ground
		if(isGrounded()) {
			GameScreen.getLevel().getEntityManager().getEntityCollider().addCollision(m_Entity, m_GroundTile);
		}
	}

	@Override
	public void stateChange(EntityState oldState, EntityState newState) {
		// Entering or leaving a ladder: reset velocity
		if(oldState == EntityState.ENTITY_STATE_LADDER || newState == EntityState.ENTITY_STATE_LADDER) {
			m_Velocity.set(0, 0);
		}
	}

	@Override
	public ComponentType getType() {
		return ComponentType.COMPONENT_MOVE;
	}

	@Override
	public void collision(Entity other) {}

	@Override
	public void collision(Tile tile) {}

	//=========================================================================
	// Movement - X direction
	//=========================================================================
	// Step movement in the X direction, setting the new velocity to m_Velocity.x
	private float stepX() {
		float velocityX = m_Velocity.x;
		if(velocityX == 0) return 0;
		
		AABB box = m_Entity.getStartBoundingBox();
		boolean right = velocityX > 0;
		int iTilesAhead = right ? 1 + (int) velocityX : (int) velocityX - 1;
		int startX = right ? box.getRightXIndex() : box.getLeftXIndex();
		int endX = startX + iTilesAhead;
		Tile collideTile = null;
		Tile loopTile = null;
		// Process x first because this will move us one row away each iteration.
		for(int x = startX; right ? x <= endX : x >= endX; x = (right ? x + 1 : x - 1)) {
			// Process y second because if we find an obstacle here, then it's the closest one
			for(int y = box.getBottomYIndex(); y <= box.getTopYIndex(); y++) {
				loopTile = GameScreen.getLevel().getTile(x, y);
				if(loopTile == null) continue;
				if(loopTile.shouldCollide(box, true)) {
					float distance = right ? loopTile.getX() - box.getRightX() : loopTile.getX()+1 - box.getLeftX();
					if(right ? velocityX > distance : velocityX < distance) {
						velocityX = distance;
						collideTile = loopTile;
						break;
					}
				}
			}
			// Once we collided, no need to check further
			if(collideTile != null) break;
		}
		
		if(collideTile != null) {
			GameScreen.getLevel().getEntityManager().getEntityCollider().addCollision(m_Entity, collideTile);
		}
		
		return velocityX;
	}
	
	// Step movement in the Y direction, setting the new velocity to m_Velocity.x
	private float stepY() {
		float velocityY = m_Velocity.y;
		if(velocityY == 0) return 0;
		
		AABB box = m_Entity.getStartBoundingBox();
		boolean up = velocityY > 0;
		int iTilesAhead = up ? 1 + (int) velocityY : (int) velocityY - 1;
		int startY = up ? box.getTopYIndex() : box.getBottomYIndex();
		int endY = startY + iTilesAhead;
		Tile collideTile = null;
		Tile loopTile = null;
		// Process y first because this will move us one column away each iteration.
		for(int y = startY; up ? y <= endY : y >= endY; y = (up ? y + 1 : y - 1)) {
			// Process x second because if we find an obstacle here, then it's the closest one
			for(int x = box.getLeftXIndex(); x <= box.getRightXIndex(); x++) {
				loopTile = GameScreen.getLevel().getTile(x, y);
				if(loopTile == null) continue;
				if(loopTile.shouldCollide(box, false)) {
					float distance = up ? loopTile.getY() - box.getTopY() : loopTile.getY()+1 - box.getBottomY();
					if(up ? velocityY > distance : velocityY < distance) {
						velocityY = distance;
						collideTile = loopTile;
						break;
					}
				}
			}
			// Once we collided, no need to check further
			if(collideTile != null) break;
		}
		
		if(collideTile != null) {
			GameScreen.getLevel().getEntityManager().getEntityCollider().addCollision(m_Entity, collideTile);
		}
		
		return velocityY;
	}
	
	
	private float giveVelocityX() {
		if(m_Entity.isOnLadder()) return 0;
		if(getMoveDirectionX() == Directions.NO_DIRECTION) return 0;
		
		float speed = m_Velocity.x; 
		if(m_Entity.isCrouched()) {
			CrouchComponent crouch = ((CrouchComponent) m_Entity.getComponent(ComponentType.COMPONENT_CROUCH));
			speed = m_Velocity.x > 0 ? crouch.getCrouchSpeed() : -crouch.getCrouchSpeed();
		}
			
		speed = applySlopeClimb(speed);
		return speed;
	}
	
	public float applySlopeClimb(float speed) {
		Tile groundTile = getGroundTile();
		if(groundTile == null) return speed;
		if(groundTile.getTileType() != TileType.TILE_SLOPE) return speed;
		
		float slope = groundTile.getSlope();
		float change = 1.0f / slope / Constants.SLOPE_CLIMB_DIVISOR;	// TODO: magic number
		
		// Moving up the slope
		if(getMoveDirectionX() == groundTile.getSlopeDirection()) {
			speed *= change;
		}
		
		return speed;
	}
	
	public Directions getMoveDirectionX() {
		if(m_Velocity.x == 0) return Directions.NO_DIRECTION;
		return m_Velocity.x > 0 ? Directions.DIRECTION_RIGHT : Directions.DIRECTION_LEFT;
	}
	
	//=========================================================================
	// Movement - Y direction
	//=========================================================================
	
	private float giveVelocityY() {
		if(m_Entity.isOnLadder()) return m_Velocity.y;
		float speed = m_Velocity.y;
		speed = applyGravity(speed);
		speed = slopeDescend(speed);
		return speed;
	}
	
	// Function will change our Y speed depending on if we're going down a slope
	private float slopeDescend(float speed) {
		if(m_GroundTile == null) return speed;
		if(m_GroundTile.getTileType() != TileType.TILE_SLOPE) return speed;
		if(getMoveDirectionX() == m_GroundTile.getSlopeDirection()) return speed;
		if(!m_Entity.isJumping()) return speed;
		
		float slope = m_GroundTile.getSlope();
		
		// This code will smoothly descend us down the slope.
		float slope_factor = Math.abs(m_Velocity.x * slope);
		System.out.println(slope_factor);
		speed = Math.min(speed, -slope_factor);
		
		return speed;
	}
	
	private float applyGravity(float speed) {
		if(!isAffectedByGravity())
			return speed;
		
		// Subtract velocity by gravity
		speed += Constants.GRAVITY;
		return speed;
	}
	
	public boolean isAffectedByGravity() {
		if(isGrounded())
		 	return false;
		
		if(m_Entity.isOnLadder())
			return false;
		
		return true;
	}
	
	public float getSpeed() {
		return m_Speed;
	}
	
	// Will update the ground and ceiling tiles
	private void postMovementTileChecks() {
		// Ground - calculate and snap (if necessary)
		calcGroundTile();
		snapToGroundTile();
		
		// Ceiling - calculate and snap (if necessary)
		calcCeilingTile();
		snapToCeilingTile();
	}
	
	// Snap the player to the floor if he is on the ground
	private void snapToGroundTile() {
		Tile groundTile = getGroundTile();
		if(groundTile == null) return;
		
		Vector2 position = m_Entity.getEndPosition();
		Vector2 size = m_Entity.getSize();
		AABB box = m_Entity.getEndBoundingBox();
		
		float centerX = box.getCenterX();
		float t = (float)(centerX - groundTile.getX());
		position.y = groundTile.floorY(t);
		m_Velocity.y = Math.max(0.0f, m_Velocity.y);
		
		// TODO: This is a dumb hack. Perhaps the movement logic needs a rethink
		// Basically, we moved on to a slope tile. The moveX() code doesn't care it's a slope, because we correct for the slopeY after the fact.
		// But, if there's a ceiling above, we snap to the ground, then snap to the ceiling...which is just dumb
		// For now, just reject the movement if that happens
		if(groundTile.getTileType() == TileType.TILE_SLOPE) {
			float aboveOurHeads = position.y + size.y;
			int indexAboveHead = (int) (aboveOurHeads - 0.000001f);
			Tile loopTile = null;
			for(int x = box.getLeftXIndex(); x <= box.getRightXIndex(); x++) {
				loopTile = GameScreen.getLevel().getTile(x, indexAboveHead);
				if(loopTile.shouldCollide(box, true)) {
					position.x = m_Entity.getStartPosition().x;
					position.y = m_Entity.getStartPosition().y;
				}
			}
		}
		
		return;
	}
	
	
	// Snap to ceiling if the player's head is touching it
	private void snapToCeilingTile() {
		
		Tile ceilingTile = getCeilingTile();
		if(ceilingTile == null) return;

		// TODO: doesn't seem decoupled at all...
		m_Entity.getEndPosition().y = (float) ceilingTile.getY() - m_Entity.getSize().y;
		m_Velocity.y = Math.min(0.0f, m_Velocity.y);
	}

	// Calculate whether or not the player is on the ground
	private void calcGroundTile() {
		// Returns null if no slope tile exists
		m_GroundTile = calcSlopeTile();
		if(m_GroundTile != null) {
			return;
		}
		
		AABB box = m_Entity.getEndBoundingBox();
		// Get our indices
		int leftX = (int) box.getLeftXIndex();
		int bottomY = (int) box.getBottomYIndex();
		int rightX = (int) box.getRightXIndex();
		
		Tile groundTile = null;
		Tile loopTile = null;
		float minDistance = Float.MAX_VALUE;
		// Check tiles below our feet - bottomY and bottomY-1
		for(int x = leftX; x <= rightX; x++) {
			for(int y = bottomY; y >= bottomY-1; y--) {
				loopTile = GameScreen.getLevel().getTile(x, y);
				if(loopTile != null) {
					if(loopTile.shouldCollide(box, false)) {
						// Need to actually be underneath the feet
						if(box.min.y - (y+1) >= 0.0001) continue;
						
						float distToCenter = Math.abs(box.getCenterX() - loopTile.getBoundingBox().getCenterX());
						
						if(distToCenter < minDistance) {
							minDistance = distToCenter;
							groundTile = loopTile;
						}
					}
				}
			}
		}
		
		m_GroundTile = groundTile;
	}
	
	private void calcCeilingTile() {
		AABB box = m_Entity.getEndBoundingBox();
		// Get our indices
		int leftX = (int) box.getLeftXIndex();
		int topY = (int) box.getTopYIndex();
		int rightX = (int) box.getRightXIndex();
		
		// Check tiles above our head - topY and topY-1
		for(int x = leftX; x <= rightX; x++) {
			for(int y = topY; y <= topY+1; y++) {
				Tile tile = GameScreen.getLevel().getTile(x, y);
				if(tile != null) {
					if(tile.shouldCollide(box, false)) {
						if(y - box.max.y < 0.0001) {
							m_CeilingTile = tile;
							return;
						}
					}
				}
			}
		}
		
		m_CeilingTile = null;
	}
	
	// Calculate whether or not the player is on a slope
	private Tile calcSlopeTile() {
		Tile slopeTile = null;
		
		AABB box = m_Entity.getEndBoundingBox();
		
		int bottomY = box.getBottomYIndex();
		int topY = box.getTopYIndex();
		int centerX = box.getCenterXIndex();

		// Get the first slope tile starting from our bottom Y index and moving upward (centerX only)
		for(int y = bottomY; y <= topY; y++) {
			Tile loopTile = GameScreen.getLevel().getTile(centerX, y);
			if(loopTile != null) {
				if(loopTile.getTileType() == TileType.TILE_SLOPE) {
					slopeTile = loopTile;
					break;
				}
			}
		}
		
		// Return if we didn't find a slope
		if(slopeTile == null)
			return null;
		
		float t = box.getCenterX() - slopeTile.getX();
		float floorY = slopeTile.floorY(t);
		
		// Are we actually on the slope?
		if(box.min.y <= floorY) {
			//System.out.println("moveSlope(): In a slope, setting y to " + floorY);
			return slopeTile;
		}
		else {
			return null;
		}
	}
	
	public Tile getCeilingTile() {
		return m_CeilingTile;
	}
	
	public boolean isOnCeiling() {
		return getCeilingTile() != null;
	}
	
	/** Get the ground tile of the entity
	 * @return Whether or not the entity is grounded */
	public Tile getGroundTile() {
		return m_GroundTile;
	}
	
	/** Is the entity on the ground?
	 * @return Whether the entity is grounded. */
	public boolean isGrounded() {
		return getGroundTile() != null;
	}

	public void addVelocity(float velocity) {
		m_Velocity.y += velocity;
	}

	public void setVelocityY(float velocity) {
		m_Velocity.y = velocity;
	}

	public Vector2 getVelocity() {
		return m_Velocity;
	}
	
	//=========================================================================
	// Helper Functions
	//=========================================================================

	protected Directions getMovementDirectionY() {
		if(m_Velocity.y > 0.0f) return Directions.DIRECTION_UP;
		if(m_Velocity.y < 0.0f) return Directions.DIRECTION_DOWN;
		return Directions.NO_DIRECTION;
	}
	

	/* The following functions are commented out because I decided to rewrite them. I kept them here as a reference, but they are dumb. */
	/*
	private Tile moveX(Vector2 frameVelocity) {
		if(frameVelocity.x == 0.0f) return null;
		Tile returnTile = null;
		
		// Create a new AABB representing the new position in the X direction
		AABB oldBox = m_Entity.getBoundingBox();
		AABB newBox = new AABB(oldBox.min.x + frameVelocity.x, oldBox.min.y, oldBox.size.x, oldBox.size.y);
		
		// New AABB representing all the tiles we'll need to check
		AABB distanceBox = AABB.enclosingBox(oldBox, newBox);
		
		// Indices of the box we're checking
		int leftX = distanceBox.getLeftXIndex();
		int bottomY = distanceBox.getBottomYIndex();
		int rightX = distanceBox.getRightXIndex();
		int topY = distanceBox.getTopYIndex();
		
		// Change in our X velocity
		float newVelX = frameVelocity.x;
		boolean goingRight = getMoveDirectionX() == Directions.DIRECTION_RIGHT;
		
		// If we're going right, start from left and increment x until right
		// If we're going left, start from right and decrement x until left
		int startX = goingRight ? leftX : rightX;
		int endX = goingRight ? rightX : leftX;
		for(int x = startX;																	// initialization
				goingRight ? x <= endX : x >= endX;	// condition
			x = goingRight ? x + 1 : x - 1)	// increment/decrement
		{
			// Process each column in each row
			for(int y = bottomY; y <= topY; y++) {
				Tile tile = GameScreen.getLevel().getTile(x, y);
				if(tile == null) continue;
				if(tile.shouldCollide(distanceBox, true)) {
					if(AABB.collide(distanceBox, tile.getBoundingBox())) {
						// If we're going right, then our new velocity must be the closest obstacle on the left (or, the minimum X)
						if(m_Velocity.x > 0.0f) {
							float dist = tile.getBoundingBox().min.x - oldBox.max.x;
							if(newVelX > dist) {
								newVelX = dist;
								returnTile = tile;
								break;
							}
						}
						// If we're going left, then our new velocity must be the closest obstacle on the right (or, the maximum X)
						else if(m_Velocity.x < 0.0f){
							float dist = tile.getBoundingBox().max.x - oldBox.min.x;
							if(newVelX < dist) {
								newVelX = dist;
								returnTile = tile;
								break;
							}
						}
					}
				}
			}
			
			// Break out of the for loop if we found a tile in this column (so that we don't need to keep checking)
			if(returnTile != null) {
				break;
			}
		}
		
		frameVelocity.x = newVelX;
		return returnTile;
	}
	
	protected Tile moveY(Vector2 frameVelocity) {
		if(frameVelocity.y == 0.0f) return null;
		Tile returnTile = null;
		
		// Create a new AABB representing the new position in the Y direction
		AABB oldBox = m_Entity.getBoundingBox();
		AABB newBox = new AABB(oldBox.min.x + frameVelocity.x, oldBox.min.y + frameVelocity.y, oldBox.size.x, oldBox.size.y);
		
		// New AABB representing all the tiles we'll need to check
		AABB distanceBox = AABB.enclosingBox(oldBox, newBox);
		
		int leftX = distanceBox.getLeftXIndex();
		int bottomY = distanceBox.getBottomYIndex();
		int rightX = distanceBox.getRightXIndex();
		int topY = distanceBox.getTopYIndex();
		
		// Change in our Y velocity
		float newVelY = frameVelocity.y;
		
		// If we're going up, start from bottom and increment y until top
		// If we're going down, start from top and decrement y until bottom
		int startY = getMovementDirectionY() == Directions.DIRECTION_UP ? bottomY : topY;
		int endY = getMovementDirectionY() == Directions.DIRECTION_UP ? topY : bottomY;
		for(int y = startY;																// initialization
			getMovementDirectionY() == Directions.DIRECTION_UP ? y <= endY : y >= endY;	// condition
			y = (getMovementDirectionY() == Directions.DIRECTION_UP ? y + 1 : y - 1))	// increment/decrement
		{
			for(int x = leftX; x <= rightX; x++) {
				Tile tile = GameScreen.getLevel().getTile(x, y);
				if(tile == null) continue;
				if(tile.shouldCollide(distanceBox, false)) {
					if(AABB.collide(distanceBox, tile.getBoundingBox())) {						
						// Two different behaviors: if we're going up, then our new velocity must be the closest obstacle on the up (or, the minimum Y)
						if(getMovementDirectionY() == Directions.DIRECTION_UP) {
							float dist = tile.getBoundingBox().min.y - oldBox.max.y;
							if(newVelY > dist) {
								newVelY = dist;
								//m_Velocity.y = dist;	// We need to change this velocity to the distance. (otherwise it shouldn't change)
								returnTile = tile;
								break;
							}
						}
						// If we're going down, then our new velocity must be the closest obstacle on the bottom (or, the maximum X)
						else if(getMovementDirectionY() == Directions.DIRECTION_DOWN) {
							float dist = tile.getBoundingBox().max.y - oldBox.min.y;
							if(newVelY < dist) {
								newVelY = dist;
								//m_Velocity.y = dist;	// We need to change this velocity to the distance. (otherwise it shouldn't change)
								returnTile = tile;
								break;
							}
						}
					}
				}
			}
			
			if(returnTile != null) {
				break;
			}
		}
		
		frameVelocity.y = newVelY;
		
		return returnTile;
	}
	*/
}
