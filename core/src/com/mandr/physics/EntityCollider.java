package com.mandr.physics;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mandr.entity.DynamicEntity;
import com.mandr.game.screens.GameScreen;
import com.mandr.util.AABB;
import com.mandr.util.Constants;

// A class to handle collisions between two entities
public class EntityCollider {
	Quadtree m_Quadtree;
	
	public EntityCollider() {
		m_Quadtree = new Quadtree(0, new Rectangle(0,0,Constants.NUM_TILES_ON_GAME_SCREEN_WIDTH, Constants.NUM_TILES_ON_GAME_SCREEN_WIDTH));
	}
	
	public void update() {
		Rectangle screenBounds = GameScreen.getRenderer().getCameraBounds();
		
		float padX = screenBounds.getWidth() * 0.25f;
		float padY = screenBounds.getHeight() * 0.25f;
		
		screenBounds.x = screenBounds.x - padX/2;
		screenBounds.y = screenBounds.y - padY/2;
		screenBounds.width = screenBounds.width + padX;
		screenBounds.height = screenBounds.height + padY;
		
		m_Quadtree.clear();
		m_Quadtree.setBounds(screenBounds);
		ArrayList<DynamicEntity> entities = GameScreen.getLevel().getEntityManager().getActiveEntities();
		for(DynamicEntity ent : entities) {
			m_Quadtree.insert(ent);
		}
		ArrayList<DynamicEntity> possibleCollideEntities = new ArrayList<DynamicEntity>();
		for(DynamicEntity ent : entities) {
			possibleCollideEntities.clear();
			m_Quadtree.retrieve(possibleCollideEntities, ent);
			for(DynamicEntity other : possibleCollideEntities) {
				if(ent == other)
					continue;
				
				// TODO: reporting system?
				if(collide(ent, other)) {
					ent.handleCollision(other);
				}
			}
		}
	}
	
	public void draw(ShapeRenderer render) {		
		m_Quadtree.draw(render);
	}

	// Does Entity a collide with Entity b?
	private static boolean collide(DynamicEntity a, DynamicEntity b) {
		// Trivial collision
		if(a.collide(b))
			return true;
		// Continuous collision case - do the entities collide during movement?
		else {
			AABB aMovementBox = AABB.enclosingBox(new AABB(a.getPositionBeforeMovement(), a.getSize()), a.getBoundingBox());
			AABB bMovementBox = AABB.enclosingBox(new AABB(b.getPositionBeforeMovement(), b.getSize()), b.getBoundingBox());
				
			return AABB.collide(aMovementBox, bMovementBox);
		}
	}
}
