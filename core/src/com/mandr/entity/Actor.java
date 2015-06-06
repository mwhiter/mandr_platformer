package com.mandr.entity;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mandr.game.screens.GameScreen;
import com.mandr.level.Tile;
import com.mandr.level.TileType;
import com.mandr.util.AABB;
import com.mandr.util.Constants;
import com.mandr.util.Directions;
import com.mandr.weapons.Weapon;
import com.mandr.weapons.WeaponStats;

public abstract class Actor extends DynamicEntity {
	// TODO: Organize this variable
	protected Vector2 m_LookVector;
	
	// Health
	private int m_Health;
	
	// Movement
	private Directions m_MoveDirectionX;		// Movement in X direction
	protected float m_MoveSpeed;				// how fast an entity moves

	protected Tile m_CollisionTileX;		// Tile we collided on X
	protected Tile m_CollisionTileY;		// Tile we collided on Y
	
	protected Tile m_GroundTile;		// Tile on the entity's ground
	protected Tile m_CeilingTile;		// Tile on the entity's ceiling
	
	// Ladders
	protected Tile m_LadderTile;
	private float m_ClimbSpeed;
	private Directions m_ClimbLadder;
	
	// Crouching
	private boolean m_WantsToReleaseCrouch;
	
	// Weapons
	private LinkedList<Weapon> m_Weapons;
	private int m_ActiveWeaponIndex;
	
	// Entity State
	private ActorStates m_ActorState;
	
	//private Directions m_LookDirectionX;
	//private Directions m_LookDirectionY;

	public abstract void notifyStateChange(ActorStates oldState, ActorStates newState);
	public abstract void kill();
	public abstract void damage(int damage);
	
	//=========================================================================
	// Initialization
	//=========================================================================
	
	public Actor(Texture texture, float x, float y, float sx, float sy) {
		super(texture, x, y, sx, sy);
		initActor();
	}

	public Actor(Texture texture, Vector2 position, Vector2 size) {
		super(texture, position, size);
		initActor();
	}
	
	private void initActor() {
		// TODO: Organize this.
		m_LookVector = new Vector2(0,0);
		
		// Health
		m_Health = 100;	// TODO: This needs to be defined per-entity
		
		// X-movement
		m_MoveDirectionX = Directions.NO_DIRECTION;
		m_MoveSpeed = 0.0f;
		
		// Ladders
		m_ClimbSpeed = 0.0f;
		m_LadderTile = null;
		m_ClimbLadder = Directions.NO_DIRECTION;
		
		// Crouching
		m_WantsToReleaseCrouch = false;
		
		// Weapons
		m_Weapons = new LinkedList<Weapon>();
		
		m_ActorState = ActorStates.NO_ENTITY_STATE;
		//m_LookDirectionX = Directions.DIRECTION_RIGHT;
		//m_LookDirectionY = Directions.NO_DIRECTION;
		
		m_GroundTile = null;
		m_CeilingTile = null;
		m_CollisionTileX = null;
		m_CollisionTileY = null;
	}
	
	//=========================================================================
	// Update
	//=========================================================================
	
	public void update(float deltaTime) {
		if(getActiveWeapon() != null) {
			getActiveWeapon().update();
		}
		
		move(deltaTime);
		
		if(shouldKill())
			kill();
	}
	
	private boolean shouldKill() {
		if(getHealth() <= 0)
			return true;
		
		// If we are below the current level boundary, we are dead.
		if(getBoundingBox().getTopY() < GameScreen.getLevel().getLevelBoundaryY())
			return true;
		
		return false;
	}
	

	//=========================================================================
	// Movement - X direction
	//=========================================================================
	
	// If we are moving in an X-direction, set his X velocity to speed (depending on direction)
	private void giveVelocityX() {
		// If we are on a ladder, cannot have x-velocity.
		if(isOnLadder()) {
			m_Velocity.x = 0.0f;
			return;
		}
		
		float maxSpeed = getMoveSpeed();
		
		if(isCrouched()) maxSpeed /= 2;				// Crouched speed is slower.
		
		if(getMoveDirectionX() == Directions.DIRECTION_LEFT) m_Velocity.x = -maxSpeed;
		if(getMoveDirectionX() == Directions.DIRECTION_RIGHT) m_Velocity.x = maxSpeed;
		if(getMoveDirectionX() == Directions.NO_DIRECTION) m_Velocity.x = 0.0f;
		
		applySlopeClimb();		// When we're on a slope, our speed can vary
	}

	
	// Reduces x-velocity if we are climbing a slope
	private void applySlopeClimb() {		
		if(m_Velocity.x == 0.0f) return;
		Tile groundTile = getGroundTile();
		if(groundTile == null) return;
		if(groundTile.getTileType() != TileType.TILE_SLOPE) return;
		
		float slope = groundTile.getSlope();
		float change = 1.0f / slope / 4.0f;	// TODO: magic number
		float maxSpeed = getMoveSpeed();
		
		// Moving up the slope
		if(getMoveDirectionX() == groundTile.getSlopeDirection()) {
			maxSpeed *= change;
		}
		// Moving down the slope
		else {
			// This check is necessary to prevent it from triggering if we're jumping.
			if(!isJumping()) {
				// This code will smoothly descend us down the slope.
				float slope_factor = Math.abs(m_Velocity.x * slope);					
				m_Velocity.y = Math.min(m_Velocity.y, -slope_factor);
			}
		}
		
		m_Velocity.x = MathUtils.clamp(m_Velocity.x, -maxSpeed, maxSpeed);
	}

	public void setMoveSpeed(float speed) {
		m_MoveSpeed = Math.max(0, speed);
	}
	
	public float getMoveSpeed() {
		return m_MoveSpeed;
	}
	
	public void setMoveDirectionX(Directions moveDirection) {
		m_MoveDirectionX = moveDirection;
	}
	
	public void stopMoveDirectionX(Directions moveDirection) {
		if(m_MoveDirectionX == moveDirection) {
			m_MoveDirectionX = Directions.NO_DIRECTION;
		}
	}
	
	public Directions getMoveDirectionX() {
		return m_MoveDirectionX;
	}
	
	//=========================================================================
	// Movement - Y direction
	//=========================================================================
	/** Sets velocity in Y direction. */
	private void giveVelocityY() {
		moveLadder();
		applyGravity();
	}
	
	private void applyGravity() {
		if(!isAffectedByGravity())
			return;
		
		// Subtract velocity by gravity
		m_Velocity.y += Constants.GRAVITY;
	}
	
	public boolean isAffectedByGravity() {
		if(isGrounded())
			return false;
		
		if(isOnLadder())
			return false;
		
		return true;
	}
	
	//=========================================================================
	// Movement
	//=========================================================================d
	private void move(float deltaTime) {
		giveVelocityX();
		giveVelocityY();
		
		// Velocity can never exceed the maximum velocity constants
		m_Velocity.y = MathUtils.clamp(m_Velocity.y, -Constants.MAX_VELOCITY_Y, Constants.MAX_VELOCITY_Y);
		Vector2 frameVelocity = new Vector2(m_Velocity.x * deltaTime, m_Velocity.y * deltaTime);
		
		// Do we collide with any tiles in the X, Y directions?
		if(!isOnLadder()) {
			m_CollisionTileX = moveX(frameVelocity);
			m_CollisionTileY = moveY(frameVelocity);
		}
		
		m_VelocityAfterMovement = frameVelocity;
		
		// TODO: Event system (i.e. what to do when we collide with a tile?)
		// TODO: right now I have two functions GetCollisionTileX() and GetCollisionTileY()... correct?
		
		// Clamp the delta velocity between the maximum velocities
		m_PositionBeforeMovement = new Vector2(m_Position);		// Store the last position
		m_Position.add(m_VelocityAfterMovement);		// Increment current position
		
		// An actor can never leave the level boundaries on the X plane
		m_Position.x = MathUtils.clamp(m_Position.x, 0, GameScreen.getLevel().getWidth()+1);
			
		postMovementTileChecks();
		// TODO: is there a cleaner way to do this?
		if(isGrounded()) { 
			if(isJumping() ||  isOnLadder()) {
				changeState(ActorStates.NO_ENTITY_STATE);
			}
		}
		stopCrouch();
	}
	
	protected Tile moveX(Vector2 frameVelocity) {	
		if(frameVelocity.x == 0.0f) return null;
		Tile returnTile = null;
		
		// Create a new AABB representing the new position in the X direction
		AABB oldBox = getBoundingBox();
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
		
		// If we're going right, start from left and increment x until right
		// If we're going left, start from right and decrement x until left
		int startX = getMoveDirectionX() == Directions.DIRECTION_RIGHT ? leftX : rightX;
		int endX = getMoveDirectionX() == Directions.DIRECTION_RIGHT ? rightX : leftX;
		for(int x = startX;																	// initialization
			getMoveDirectionX() == Directions.DIRECTION_RIGHT ? x <= endX : x >= endX;	// condition
			x = (getMoveDirectionX() == Directions.DIRECTION_RIGHT ? x + 1 : x - 1))	// increment/decrement
		{
			// Process each column in each row
			for(int y = bottomY; y <= topY; y++) {
				Tile tile = GameScreen.getLevel().getTile(x, y);
				if(tile == null) continue;
				if(shouldCollide(tile, true)) {
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
		AABB oldBox = getBoundingBox();
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
				if(shouldCollide(tile, false)) {
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
	
	// Did we collide with a tile in the X-direction this frame?
	public Tile getCollisionTileX() {
		return m_CollisionTileX;
	}
	
	// Did we collide with a tile in the Y-direction this frame?
	public Tile getCollisionTileY() {
		return m_CollisionTileY;
	}
	
	
	//=========================================================================
	// Post-Movement
	//=========================================================================
	
	// Will update the ground and ceiling tiles
	protected void postMovementTileChecks() {
		// Ground - calculate and snap (if necessary)
		calcGroundTile();
		snapToGroundTile();
		
		// Ceiling - calculate and snap (if necessary)
		calcCeilingTile();
		snapToCeilingTile();
		
		calcLadderTile();
	}
	
	// Snap the player to the floor if he is on the ground
	private void snapToGroundTile() {
		Tile groundTile = getGroundTile();
		if(groundTile == null) return;
		
		float centerX = m_Position.x + m_Size.x/2;
		float t = (float)(centerX - groundTile.getX());
		m_Position.y = groundTile.floorY(t);
		m_Velocity.y = Math.max(0.0f, m_Velocity.y);
		
		// TODO: This is a dumb hack. Perhaps the movement logic needs a rethink
		// Basically, we moved on to a slope tile. The moveX() code doesn't care it's a slope, because we correct for the slopeY after the fact.
		// But, if there's a ceiling above, we snap to the ground, then snap to the ceiling...which is just dumb
		// For now, just reject the movement if that happens
		if(groundTile.getTileType() == TileType.TILE_SLOPE) {
			float aboveOurHeads = m_Position.y + m_Size.y;
			int indexAboveHead = (int) (aboveOurHeads - 0.000001f);
			Tile loopTile = null;
			for(int x = getBoundingBox().getLeftXIndex(); x <= getBoundingBox().getRightXIndex(); x++) {
				loopTile = GameScreen.getLevel().getTile(x, indexAboveHead);
				if(shouldCollide(loopTile, true)) {
					m_Position.x = m_PositionBeforeMovement.x;
					m_Position.y = m_PositionBeforeMovement.y;
				}
			}
		}
		
		return;
	}
	
	// Snap to ceiling if the player's head is touching it
	private void snapToCeilingTile() {
		
		Tile ceilingTile = getCeilingTile();
		if(ceilingTile == null) return;

		m_Position.y = (float) ceilingTile.getY() - m_Size.y;
		m_Velocity.y = Math.min(0.0f, m_Velocity.y);
	}

	// Calculate whether or not the player is on the ground
	private void calcGroundTile() {
		// Returns null if no slope tile exists
		m_GroundTile = calcSlopeTile();
		if(m_GroundTile != null) {
			return;
		}
		
		AABB box = getBoundingBox();
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
					if(shouldCollide(loopTile, false)) {
						// Need to actually be underneath the feet
						if(m_Position.y - (y+1) >= 0.0001) continue;
						
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
		AABB box = getBoundingBox();
		// Get our indices
		int leftX = (int) box.getLeftXIndex();
		int topY = (int) box.getTopYIndex();
		int rightX = (int) box.getRightXIndex();
		
		// Check tiles above our head - topY and topY-1
		for(int x = leftX; x <= rightX; x++) {
			for(int y = topY; y <= topY+1; y++) {
				Tile tile = GameScreen.getLevel().getTile(x, y);
				if(tile != null) {
					if(shouldCollide(tile, false)) {
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
		
		AABB ourBox = getBoundingBox();
		
		int bottomY = ourBox.getBottomYIndex();
		int topY = ourBox.getTopYIndex();
		int centerX = ourBox.getCenterXIndex();

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
		
		float t = ourBox.getCenterX() - slopeTile.getX();
		float floorY = slopeTile.floorY(t);
		
		// Are we actually on the slope?
		if(ourBox.min.y <= floorY) {
			//System.out.println("moveSlope(): In a slope, setting y to " + floorY);
			return slopeTile;
		}
		else {
			return null;
		}
	}
		
	public Tile getGroundTile() {
		return m_GroundTile;
	}
	
	public boolean isGrounded() {
		return getGroundTile() != null;
	}
	
	public Tile getCeilingTile() {
		return m_CeilingTile;
	}
	
	public boolean isOnCeiling() {
		return getCeilingTile() != null;
	}
	//=========================================================================
	// Ladders
	//=========================================================================
	// Do we intersect a ladder tile?
	public void calcLadderTile() {
		AABB ourBox = getBoundingBox();
		int bottomY = ourBox.getBottomYIndex();
		int topY = ourBox.getTopYIndex();
		int leftX = ourBox.getLeftXIndex();
		int rightX = ourBox.getRightXIndex();
		
		
		m_LadderTile = null;
		for(int y = bottomY; y <= topY; y++) {
			for(int x = leftX; x <= rightX; x++) {
				Tile ladderTile = GameScreen.getLevel().getTile(x, y);
				if(ladderTile != null) {
					if(ladderTile.getTileType() == TileType.TILE_LADDER) {
						m_LadderTile = ladderTile;
						return;
					}
				}
			}
		}
		
		// If the above fails, perhaps the center of the entity is on top of a ladder tile?
		if(!isOnLadder()) {
			for(int x = leftX; x <= rightX; x++) {
				Tile ladderTile = GameScreen.getLevel().getTile(x, bottomY-1);
				if(ladderTile != null) {
					if(ladderTile.getTileType() == TileType.TILE_LADDER) {
						if(ladderTile.isLadderTop()) {
							m_LadderTile = ladderTile;
							return;
						}
					}
				}
				// Check top too
				ladderTile = GameScreen.getLevel().getTile(x, topY+1);
				if(ladderTile != null) {
					if(ladderTile.getTileType() == TileType.TILE_LADDER) {
						m_LadderTile = ladderTile;
						return;
					}
				}
			}
		}
	}
	
	public boolean isOnLadder() {
		return m_ActorState == ActorStates.ENTITY_STATE_LADDER;
	}
	
	// Climb up or down a ladder
	public void climbLadder(Directions direction) {
		if(!isOnLadder()) return;
		
		m_ClimbLadder = direction;
	}
	
	public Directions getClimbLadderDirection() {
		return m_ClimbLadder;
	}
	
	public float getClimbSpeed() {
		return m_ClimbSpeed;
	}
	
	public void setClimbSpeed(float speed) {
		m_ClimbSpeed = Math.max(0, speed);
	}
	
	private void moveLadder() {
		if(!isOnLadder()) return;
		
		Tile ladderTile = getLadderTile();
		if(ladderTile == null) {
			//System.out.println("Ladder tile null when climbing?");
			detachLadder();
			return;
		}
		
		if(m_ClimbLadder == Directions.DIRECTION_UP)
			m_Velocity.y = m_ClimbSpeed;
		else if(m_ClimbLadder == Directions.DIRECTION_DOWN)
			m_Velocity.y = -m_ClimbSpeed;
		else
			m_Velocity.y = 0.0f;
	}
	
	public boolean canAttachLadder(Directions direction) {
		if(isOnLadder()) return false;
		
		Tile ladderTile = getLadderTile();
		if(ladderTile == null) return false;
		
		AABB box = getBoundingBox();
		AABB tileBox = ladderTile.getBoundingBox();
		
		// Our center must be within the tile boundaries ladder tile to climb it.
		if(box.getCenterX() > tileBox.getRightX() || box.getCenterX() < tileBox.getLeftX())
			return false;
			
		// If we pressed up to try and attach to a ladder
		if(direction == Directions.DIRECTION_UP) {
			// Do not attach to a ladder if we're fully above the ladder
			if(box.getBottomY() >= tileBox.getTopY())
				return false;
			// Do not attach to a ladder if we're fully below it
			if(box.getTopY() < tileBox.getBottomY())
				return false;
		}
		// If we pressed down to try and attach to a ladder
		else if(direction == Directions.DIRECTION_DOWN) {
			// If the ladder isn't a ladder top tile, we can't attach by pressing down
			if(!ladderTile.isLadderTop())
				return false;
			// If we're below the top of the tile, then we can't attach by pressing down
			if(box.getBottomY() < tileBox.getTopY())
				return false;
		}
		
		return true;
	}
	
	public boolean canDetachLadder() {
		if(!isOnLadder())
			return false;
		
		return true;
	}
	
	// Attach to a ladder
	public boolean attachLadder(Directions direction) {
		if(!canAttachLadder(direction))
			return false;

		Tile ladderTile = getLadderTile();
		if(ladderTile == null) return false;
		
		m_Position.x = ladderTile.getX();
		
		// If we coming down a ladder from the top, set his position to be slightly below the top of the ladder, so that we don't actually collide with it and get sent back up
		if(ladderTile.isLadderTop()) {
			m_Position.y = (float) ladderTile.getY() + 0.5f;
			//System.out.println(m_Position.y);
		}

		// If we are reloading a weapon stop.
		if(getActiveWeapon() != null)
			getActiveWeapon().stopReload();
		
		changeState(ActorStates.ENTITY_STATE_LADDER);
		
		// Start to climb the ladder to reset our velocity
		climbLadder(direction);
		
		return true;
	}
	
	public boolean detachLadder() {
		if(!canDetachLadder())
			return false;
		
		m_Velocity.y = 0.0f;
		changeState(ActorStates.NO_ENTITY_STATE);
		
		return true;
	}
	
	public Tile getLadderTile() {
		return m_LadderTile;
	}
	
	//=========================================================================
	// Jumping
	//=========================================================================

	public boolean isInAir() {
		return !isGrounded();
	}
	
	// This entity jumps.
	public void jump(float velocity) {
		if(!canJump())
			return;

		// No matter what, this will occur
		// Stop crouching
		stopCrouch();
		// Stop reloading
		if(getActiveWeapon() != null)
			getActiveWeapon().stopReload();
		
		// Try jumping down. If we can, then do that instead
		if(canJumpDown()) {	
			m_Position.y -= 0.001f;	// move his position down a small amount to allow him to fall
			changeState(ActorStates.ENTITY_STATE_JUMP);	
		}
		else {
			m_Velocity.y = velocity;
			changeState(ActorStates.ENTITY_STATE_JUMP);	
		}
	}
	
	// Can we jump down?
	private boolean canJumpDown() {
		if(!isCrouched())
			return false;
		if(!isGrounded())
			return false;
		if(getGroundTile().getTileType() != TileType.TILE_ONE_WAY)
			return false;
		
		// If there's a solid tile below us, they can't jump down
		for(int x = getBoundingBox().getLeftXIndex(); x <= getBoundingBox().getRightXIndex(); x++) {
			Tile loopTile = GameScreen.getLevel().getTile(x, getBoundingBox().getBottomYIndex()-1);
			if(loopTile == null) continue;
			if(loopTile.getTileType() == TileType.TILE_ONE_WAY) continue;	// ignore one-way tiles for this check
			if(shouldCollide(loopTile, false))
				return false;
		}
		
		return true;
	}
	
	// Can an entity jump?
	public boolean canJump() {
		if(isJumping())
			return false;
		
		if(isOnCeiling())
			return false;
		
		if(isOnLadder())
			return false;
		
		return true;
	}
	
	public boolean isJumping() {
		return m_ActorState == ActorStates.ENTITY_STATE_JUMP;
	}
	//=========================================================================
	// Crouching
	//=========================================================================
	// Is this entity currently crouched?
	public boolean isCrouched() {
		return m_ActorState == ActorStates.ENTITY_STATE_CROUCH;
	}
	
	// Can this entity crouch?
	public boolean canCrouch() {
		if(m_ActorState != ActorStates.NO_ENTITY_STATE)
			return false;
		
		if(!isGrounded())
			return false;
		
		if(isCrouched())
			return false;
		
		if(isOnLadder())
			return false;
		
		return true;
	}
		
	// Can we actually release the crouch?
	public boolean canReleaseCrouch() {
		// We don't want to release crouch yet
		if(!m_WantsToReleaseCrouch)
			return false;
		
		if(!isCrouched())
			return false;
		
		// If we're touching both the ground and ceiling, we can't release a crouch
		//if(isGrounded() && isOnCeiling())
		//	return false;
		
		// Check above us at the height where we're supposed to stand. If we can't fit there, don't do it
		// TODO: Maybe consider something different, because if we change how much height the crouch reduces us by, this will not be changed
		// TODO: Maybe not a magic number.
		float standingTopY = m_Position.y + (m_Size.y * 2);
		int standingTopIndex = (int) (standingTopY - 0.00001f);	// subtract by this constant otherwise we get the wrong index if we're directly on the line
		Tile loopTile = null;
		for(int x = getBoundingBox().getLeftXIndex(); x <= getBoundingBox().getRightXIndex(); x++) {
			loopTile = GameScreen.getLevel().getTile(x, standingTopIndex);
			if(loopTile == null) continue;
			if(shouldCollide(loopTile, false)) {
				return false;
			}
		}
		
		return true;
	}
	
	// The entity crouches
	public boolean crouch() {
		if(!canCrouch())
			return false;
		
		detachLadder();
		changeState(ActorStates.ENTITY_STATE_CROUCH);
		
		return true;
	}
	
	// Tell entity to stop crouching
	public void uncrouch() {
		if(!isCrouched())
			return;
		
		m_WantsToReleaseCrouch = true;
	}
	
	// The entity actually stops crouching
	private boolean stopCrouch() {
		if(!canReleaseCrouch())
			return false;

		m_WantsToReleaseCrouch = false;
		changeState(ActorStates.NO_ENTITY_STATE);
		
		return true;
	}
	
	//=========================================================================
	// States
	// 1) All subclasses must inherit notifyStateChange()
	// 2) State change handled in m_State
	//=========================================================================
	
	private void changeState(ActorStates newState) {
		ActorStates oldState = m_ActorState;
		if(oldState != newState) {
			if(newState == ActorStates.ENTITY_STATE_CROUCH) {
				//m_Size.x *= 2;
				m_Size.y /= 2;
			}
			if(oldState == ActorStates.ENTITY_STATE_CROUCH) {
				//m_Size.x /= 2;
				m_Size.y *= 2;
			}
			
			m_ActorState = newState;
			notifyStateChange(oldState, newState);
		}
	}
	
	public ActorStates getState() {
		return m_ActorState;
	}
	
	@Override
	public boolean shouldCollide(Tile tile, boolean x_axis) {
		switch(tile.getTileType()) {
			// Solid tiles collide always, unless the player is near the tops of slopes, in which case the solid tile next to the slope entrance does not collide
			case TILE_SOLID: {
				int centerXIndex = getBoundingBox().getCenterXIndex();
				int bottomY = getBoundingBox().getBottomYIndex();
				for(int y = bottomY-1; y <= bottomY; y++) {
					Tile slopeTile = GameScreen.getLevel().getTile(centerXIndex, y);
					if(slopeTile != null) {
						// A slope tile?
						if(slopeTile.getTileType() == TileType.TILE_SLOPE) {
							// On the same row?
							if(slopeTile.getY() == tile.getY()) {
								if(slopeTile.getSlopeDirection() == Directions.DIRECTION_RIGHT) {
									//System.out.println(slopeTile + " " + slopeTile.getSlopeRight());
									if(slopeTile.getSlopeRight() == 16) {
										return false;
									}
								}
								else if(slopeTile.getSlopeDirection() == Directions.DIRECTION_LEFT) {
									if(slopeTile.getSlopeLeft() == 16) {
										return false;
									}
								}
							}
						}
					}
				}
				
				return true;
			}
			// One-way tiles only collide if the y position is greater than the tile's y
			case TILE_ONE_WAY:
			case TILE_ONE_WAY_STRICT:
				if(!x_axis) {
					return m_PositionBeforeMovement.y >= (tile.getY()+1);
				}
				return false;
			case TILE_LADDER:
				if(!x_axis && tile.isLadderTop()) {
					return m_PositionBeforeMovement.y >= (tile.getY()+1);
				}
				return false;
			case TILE_SLOPE:
			default:
				return false;
		}
	}

	//=========================================================================
	// Looking
	//=========================================================================
	public void setLookAt(Vector2 vector) {
		m_LookVector = vector;
	}
	
	public Vector2 getLookVector() {
		return m_LookVector;
	}
	
	public Directions getLookDirectionX() {
		return (m_LookVector.angle() > 90 && m_LookVector.angle() <= 270 ? Directions.DIRECTION_LEFT : Directions.DIRECTION_RIGHT);
	}
	
	/*
	public void lookX(Directions direction) {
		if(direction == Directions.DIRECTION_LEFT || direction == Directions.DIRECTION_RIGHT)
			m_LookDirectionX = direction;
	}
	public void lookY(Directions direction) {
		if(direction == Directions.DIRECTION_DOWN || direction == Directions.DIRECTION_UP)
			m_LookDirectionY = direction;
	}
	
	public void stopLookX(Directions direction) {
		if(direction == m_LookDirectionX)
			m_LookDirectionX = Directions.NO_DIRECTION;
	}
	
	public void stopLookY(Directions direction) {
		if(direction == m_LookDirectionY)
			m_LookDirectionY = Directions.NO_DIRECTION;
	}
	
	public Directions getLookDirectionX() {
		return m_LookDirectionX;
	}
	public Directions getLookDirectionY() {
		return m_LookDirectionY;
	}
	
	public Directions getLookDirection() {
		if(isOnLadder()) {
			return Directions.NO_DIRECTION;
		}
		if(isCrouched()) {
			return getLookDirectionX();
		}
		
		// TODO: FIGURE OUT A BETTER WAY!
		// So this is a hack, but I can't figure out a better way to do it (yet)
		// Basically, if we're looking up, but not holding left or right keys, then only look up
		if(getLookDirectionY() == Directions.DIRECTION_UP) {
			InputHandler input = GameScreen.getLevel().getGameScreen().getInput();
			if(!input.isKeyPressed(input.getCommandInputKey(input.COMMAND_LEFT)) &&
					!input.isKeyPressed(input.getCommandInputKey(input.COMMAND_RIGHT)))
			{
				return getLookDirectionY();
			}
		}
		
		switch(getLookDirectionX()) {
		case DIRECTION_LEFT:
			if(getLookDirectionY() == Directions.DIRECTION_UP) return Directions.DIRECTION_LEFT_UP;
			if(getLookDirectionY() == Directions.DIRECTION_DOWN) return Directions.DIRECTION_LEFT_DOWN;
			return Directions.DIRECTION_LEFT;
		case DIRECTION_RIGHT:
			if(getLookDirectionY() == Directions.DIRECTION_UP) return Directions.DIRECTION_RIGHT_UP;
			if(getLookDirectionY() == Directions.DIRECTION_DOWN) return Directions.DIRECTION_RIGHT_DOWN;
			return Directions.DIRECTION_RIGHT;
		default:
			return getLookDirectionY();
		}
	}
	*/
	
	//=========================================================================
	// Weapon Functions
	//=========================================================================
	
	/** Set the active weapon to the index. For example, if the Pistol is in index 0 and we call setActiveWeapon(index), we will set the pistol to be the current weapon. 
	 * @param (int) index: the index to set active
	 * */
	public void setActiveWeapon(int index) {
		if(index < 0 || index >= m_Weapons.size()) return;
		m_ActiveWeaponIndex = index;
	}
	
	/** Get the active weapon of the actor. 
	 * @return The active weapon.
	 * */
	public Weapon getActiveWeapon() {
		return getWeapon(m_ActiveWeaponIndex);
	}
	
	public Weapon getWeapon(int index) {
		if(index < 0 || index >= m_Weapons.size()) return null;
		return m_Weapons.get(index);
	}
	
	public boolean canReload() {
		if(isOnLadder())
			return false;
		if(isJumping())
			return false;
		if(!isGrounded())
			return false;
		
		return true;
	}
	
	public boolean canFireWeapon() {
		if(getActiveWeapon() == null)
			return false;
		
		if(isOnLadder())
			return false;
		
		return true;
	}
	
	public void addWeapon(WeaponStats m_WeaponStats) {
		if(m_Weapons.size() == Constants.MAX_WEAPONS)
			return;
		
		Weapon newWeapon = new Weapon(this, m_WeaponStats);
		m_Weapons.add(newWeapon);
		
		// If we didn't have an active weapon and we just added a weapon, set this new weapon as the active weapon
		if(getActiveWeapon() == null) {
			setActiveWeapon(m_Weapons.indexOf(newWeapon));
		}
	}
	
	public void removeWeapon(Weapon weapon) {
		m_Weapons.remove(weapon);
	}
	
	public void removeWeapon(int index) {
		m_Weapons.remove(index);
	}
	
	//=========================================================================
	// Health Functions
	//=========================================================================
	
	public int getHealth() {
		return m_Health;
	}

	public void setHealth(int newHealth) {
		m_Health = newHealth;
	}
	
	public void changeHealth(int change) {
		if(!canReceiveDamage() && change < 0) return;
		
		setHealth(getHealth() + change);
	}
	
	public boolean canReceiveDamage() {
		return true;
	}
	
	//=========================================================================
	// Helper Functions
	//=========================================================================

	protected Directions getMovementDirectionY() {
		if(m_Velocity.y > 0.0f) return Directions.DIRECTION_UP;
		if(m_Velocity.y < 0.0f) return Directions.DIRECTION_DOWN;
		return Directions.NO_DIRECTION;
	}
}
