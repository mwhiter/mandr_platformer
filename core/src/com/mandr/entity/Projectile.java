package com.mandr.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.mandr.game.screens.GameScreen;
import com.mandr.level.Tile;
import com.mandr.level.TileType;
import com.mandr.util.AABB;
import com.mandr.util.Constants;
import com.mandr.weapons.Weapon;

public class Projectile extends DynamicEntity {
	
	private Weapon m_Weapon;
	private boolean m_HitSlope;
	private Tile m_CollisionTileX;
	private Tile m_CollisionTileY;
	
	public Projectile(Weapon weapon, Texture texture, float x, float y, float width, float height) {
		super(texture, x,y,width,height);
		init(weapon);
	}

	public Projectile(Weapon weapon, Texture texture, Vector2 position, Vector2 size) {
		super(texture, position, size);
		init(weapon);
	}
	
	private void init(Weapon weapon) {
		m_Weapon = weapon;
		m_HitSlope = false;
		m_CollisionTileX = null;
		m_CollisionTileY = null;
	}

	@Override
	public void update(float deltaTime) {
		move(deltaTime);
		
		if(shouldKill())
			GameScreen.getLevel().getEntityManager().removeEntity(this);
	}

	// TODO: I don't know about this...figure out a way to make this nice.
		// TODO: For example, one global collide() function that knows if we've hit a tile or an entity
		// TODO: OR just an event handler. We collide with a tile or entity, an event fires, which does stuff...
	@Override
	public void handleCollision(DynamicEntity other) {
		// Friendly fire will be ignored
		if(isFriendly() == other.isFriendly())
			return;
		
		int damage = m_Weapon.getWeaponStats().getDamage();
		if(other instanceof Actor) {
			((Actor) other).damage(damage);
			GameScreen.getLevel().getEntityManager().removeEntity(this);	 // TODO: This should be a clue that something is wrong.
		}
	}
	
	private void move(float deltaTime) {
		// Small gravity added
		m_Velocity.y += Constants.GRAVITY;
		
		m_PositionBeforeMovement.x = m_Position.x;
		m_PositionBeforeMovement.y = m_Position.y;
		
		// If we're hitting a slope, move to intersection point and then kill
		m_HitSlope = hitSlope(deltaTime);
		if(m_HitSlope) return;
		
		m_CollisionTileX = collideX(deltaTime);
		m_CollisionTileY = collideY(deltaTime);
		
		m_Position.x += m_Velocity.x * deltaTime;
		m_Position.y += m_Velocity.y * deltaTime;
	}
	
	/** Does this projectile hit a slope?
	 * @param deltaTime: The deltaTime. 
	 * @return Whether or not the projectile hit a slope*/
	private boolean hitSlope(float deltaTime) {
		if(m_Velocity.x == 0.0) return false;
		
		Vector2 startPosition = new Vector2(m_Position);
		Vector2 endPosition = new Vector2(m_Position.x + m_Velocity.x * deltaTime, m_Position.y + m_Velocity.y * deltaTime);
		
		if(startPosition.x == endPosition.x) return false;
		
		float slope = (startPosition.y - endPosition.y) / (startPosition.x - endPosition.x);
		
		// TODO: this is actually an amazing method for calculating collisions with regular actors, too!!!
		boolean goingRight = startPosition.x < endPosition.x;
		
		for(int x = (int)startPosition.x; (goingRight ? x <= (int)endPosition.x : x >= (int)endPosition.x); x = (goingRight ? x+1 : x-1)) {
			// Calculate the line of movement
			int y = (int)(slope * x - slope * startPosition.x + startPosition.y);
			
			// Is there a slope tile here?
			Tile slopeTile = GameScreen.getLevel().getTile(x, y);
			if(slopeTile == null) continue;
			if(slopeTile.getTileType() != TileType.TILE_SLOPE) continue;
			
			Vector2 slopeTileStart = new Vector2(slopeTile.getX(), slopeTile.getY() + (float) slopeTile.getSlopeLeft() / 16.0f);
			Vector2 slopeTileEnd = new Vector2(slopeTile.getX()+1, slopeTile.getY() + (float) slopeTile.getSlopeRight() / 16.0f);
			Vector2 intersection = new Vector2();
			// DO NOT USE Intersector.intersectLines(). it's ray-ray check, not a segment-segment check like we need
			if(Intersector.intersectSegments(startPosition, endPosition, slopeTileStart, slopeTileEnd, intersection)) {
				m_Position.x = intersection.x;
				m_Position.y = intersection.y;
				
				return true;
			}
		}
		
		return false;
	}
	
	private Tile collideX(float deltaTime) {
		if(m_Velocity.x == 0.0)
			return null;
		
		Tile collideTile = null;
		
		Vector2 startPosition = new Vector2(m_Position);
		Vector2 endPosition = new Vector2(startPosition.x + m_Velocity.x * deltaTime, startPosition.y + m_Velocity.y * deltaTime);
		AABB enclosingBox = AABB.enclosingBox(startPosition, endPosition);
		int startIndex, endIndex;
		if(m_Velocity.x > 0.0) {
			startIndex = enclosingBox.getLeftXIndex();
			endIndex = enclosingBox.getRightXIndex();
			for(int y = enclosingBox.getBottomYIndex(); y <= enclosingBox.getTopYIndex(); y++) {
				for(int x = startIndex; x <= endIndex; x++) {
					Tile loopTile = GameScreen.getLevel().getTile(x, y);
					if(loopTile == null) continue; 
					
					if(shouldCollide(loopTile, true)) {
						collideTile = loopTile;
					}
				}
			}
		}
		else if(m_Velocity.x < 0.0) {
			startIndex = enclosingBox.getRightXIndex();
			endIndex = enclosingBox.getLeftXIndex();
			for(int y = enclosingBox.getBottomYIndex(); y <= enclosingBox.getTopYIndex(); y++) {
				for(int x = startIndex; x >= endIndex; x--) {
					Tile loopTile = GameScreen.getLevel().getTile(x, y);
					if(loopTile == null) continue; 
					
					if(shouldCollide(loopTile, true)) {
						collideTile = loopTile;
					}
				}
			}
		}		
		
		return collideTile;
	}
	
	public Tile collideY(float deltaTime) {
		if(m_Velocity.y == 0.0)
			return null;
		
		Tile collideTile = null;
		
		Vector2 startPosition = new Vector2(m_Position);
		Vector2 endPosition = new Vector2(startPosition.x + m_Velocity.x * deltaTime, startPosition.y + m_Velocity.y * deltaTime);
		AABB enclosingBox = AABB.enclosingBox(startPosition, endPosition);
		int startIndex, endIndex;
		if(m_Velocity.y > 0.0) {
			startIndex = enclosingBox.getBottomYIndex();
			endIndex = enclosingBox.getTopYIndex();
			for(int x = enclosingBox.getLeftXIndex(); x < enclosingBox.getRightXIndex(); x++) { 
				for(int y = startIndex; y <= endIndex; y++) {
					Tile loopTile = GameScreen.getLevel().getTile(x, y);
					if(loopTile == null) continue; 
					
					if(shouldCollide(loopTile, false)) {
						collideTile = loopTile;
					}
				}
			}
		}
		else  {
			startIndex = enclosingBox.getTopYIndex();
			endIndex = enclosingBox.getBottomYIndex();
			for(int x = enclosingBox.getLeftXIndex(); x < enclosingBox.getRightXIndex(); x++) { 
				for(int y = startIndex; y >= endIndex; y--) {
					Tile loopTile = GameScreen.getLevel().getTile(x, y);
					if(loopTile == null) continue; 
					
					if(shouldCollide(loopTile, true)) {
						collideTile = loopTile;
					}
				}
			}
		}
		
		return collideTile;
	}

	public Actor getOwner() { 
		return m_Weapon.getOwner();
	}
	
	private boolean shouldKill() {
		// Level collision of some sort
		if(m_HitSlope || m_CollisionTileX != null || m_CollisionTileY != null) {
			return true;
		}
		
		if(!isOnScreen())
			return true;
		
		return false;
	}
	
	@Override
	public boolean shouldCollide(Tile tile, boolean x_axis) {
		if(tile == null) return false;
		
		// TODO: fix this logic
		switch(tile.getTileType()) {
		case TILE_SOLID:
			return true;
		case TILE_SLOPE:
			return false;
		case TILE_ONE_WAY_STRICT:
		case TILE_ONE_WAY:
			if(x_axis)
				return false;
			return true;
		default:
			return false;
		}
	}
}
