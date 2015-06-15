package com.mandr.level.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mandr.entity.Entity;
import com.mandr.entity.EntityStats;
import com.mandr.game.screens.GameScreen;
import com.mandr.level.Tile;
import com.mandr.physics.Quadtree;
import com.mandr.util.AABB;
import com.mandr.util.Constants;

// A class to handle collisions between two entities
public class EntityCollider {
	Quadtree m_Quadtree;
	
	// Hash map containing primary Entity, and a linked list of everything he collides with.
	HashMap<Entity, LinkedList<Entity>> m_EntityCollisions;
	HashMap<Entity, LinkedList<Tile>> m_TileCollisions;
	
	public EntityCollider() {
		m_Quadtree = new Quadtree(0, new Rectangle(0,0,Constants.NUM_TILES_ON_GAME_SCREEN_WIDTH, Constants.NUM_TILES_ON_GAME_SCREEN_WIDTH));
		m_EntityCollisions = new HashMap<Entity, LinkedList<Entity>>();
		m_TileCollisions = new HashMap<Entity, LinkedList<Tile>>();
	}
	
	public void update() {
		
		calculateCollision();
		handleCollision();
	
		m_Quadtree.clear();
		m_EntityCollisions.clear();
		m_TileCollisions.clear();
	}
	
	public void draw(ShapeRenderer render) {		
		m_Quadtree.draw(render);
	}

	// Does Entity a collide with Entity b?
	private static boolean collide(Entity a, Entity b) {
		// Trivial collision
		if(Entity.collide(a, b))
			return true;
		// Continuous collision case - do the entities collide during movement?
		else {
			AABB aMovementBox = AABB.enclosingBox(new AABB(a.getEndPosition(), a.getSize()), a.getEndBoundingBox());
			AABB bMovementBox = AABB.enclosingBox(new AABB(b.getEndPosition(), b.getSize()), b.getEndBoundingBox());
				
			return AABB.collide(aMovementBox, bMovementBox);
		}
	}
	
	private void calculateCollision() {
		Rectangle screenBounds = GameScreen.getRenderer().getCameraBounds();
		
		float padX = screenBounds.getWidth() * 0.25f;
		float padY = screenBounds.getHeight() * 0.25f;
		
		screenBounds.x = screenBounds.x - padX/2;
		screenBounds.y = screenBounds.y - padY/2;
		screenBounds.width = screenBounds.width + padX;
		screenBounds.height = screenBounds.height + padY;
		
		m_Quadtree.setBounds(screenBounds);
		ArrayList<Entity> entities = GameScreen.getLevel().getEntityManager().getActiveEntities();
		for(Entity ent : entities) {
			m_Quadtree.insert(ent);
		}
		ArrayList<Entity> possibleCollideEntities = new ArrayList<Entity>();
		for(Entity ent : entities) {
			possibleCollideEntities.clear();
			m_Quadtree.retrieve(possibleCollideEntities, ent);
			for(Entity other : possibleCollideEntities) {
				if(ent == other)
					continue;
				
				if(collide(ent, other)) {
					addCollision(ent, other);
				}
			}
		}
	}
	
	// Loop through all collisions and do something if they are
	private void handleCollision() {
		// Collide with tiles
		Iterator<HashMap.Entry<Entity, LinkedList<Tile>>> tileIterator = m_TileCollisions.entrySet().iterator();
		while(tileIterator.hasNext()) {
			HashMap.Entry<Entity, LinkedList<Tile>> pair = (HashMap.Entry<Entity, LinkedList<Tile>>) tileIterator.next();
			for(Tile tile : pair.getValue()) {
				collideTile(pair.getKey(), tile);
			}
		}
		
		Iterator<HashMap.Entry<Entity, LinkedList<Entity>>> entIterator = m_EntityCollisions.entrySet().iterator();
		while(entIterator.hasNext()) {
			HashMap.Entry<Entity, LinkedList<Entity>> pair = (HashMap.Entry<Entity, LinkedList<Entity>>) entIterator.next();
			for(Entity ent : pair.getValue()) {
				collideEntity(pair.getKey(), ent);
			}
		}
	}
	
	// TODO: maybe these functions belong elsewhere?
	private void collideEntity(Entity primary, Entity other) {
		EntityStats primaryStats = primary.getStats();
		EntityStats otherStats = other.getStats();
		if(primaryStats.dieWhenCollide) primary.setDead(true);
		if(otherStats.dieWhenCollide) 	other.setDead(true);
	}

	// TODO: maybe these functions belong elsewhere?
	private void collideTile(Entity primary, Tile tile) {
		EntityStats primaryStats = primary.getStats();
		
		// TODO: Weird ricochet bug when colliding at an angle.
		if(primaryStats.dieWhenCollide) primary.setDead(true);
	}
	
	public void addCollision(Entity primary, Tile tile) {
		addUniqueToContainer(primary, tile, m_TileCollisions);
	}
	
	// Add a collision, avoiding duplicate collisions
	public void addCollision(Entity primary, Entity other) {
		addUniqueToContainer(primary, other, m_EntityCollisions);
	}
	
	/** Add <K,V> pair of <primary, other> into map, ensuring that we don't have duplicate values (i.e. A collides with B, so B cannot collide with A) */
	private <T> void addUniqueToContainer(Entity primary, T other, HashMap<Entity, LinkedList<T>> map) {		
		// Do we already contain other?
		if(map.containsKey(primary)) {
			LinkedList<T> collidingEntities = map.get(primary);
			
			if(collidingEntities.contains(other))
				return;
		}
		
		// Does other already collide with primary?
		if(map.containsKey(other)) {
			LinkedList<T> collidingEntities = map.get(other);
			if(collidingEntities.contains(primary))
				return;
		}
		
		// Create a new linked list if we need to
		if(!map.containsKey(primary)) {
			map.put(primary, new LinkedList<T>());
		}
		
		map.get(primary).add(other);
	}
}
