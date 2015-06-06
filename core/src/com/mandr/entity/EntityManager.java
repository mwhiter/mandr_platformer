package com.mandr.entity;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mandr.game.screens.GameScreen;
import com.mandr.physics.EntityCollider;

public class EntityManager {
	
	private EntityCollider m_EntityCollider;
	private LinkedList<DynamicEntity> m_Entities;
	private ArrayList<DynamicEntity> m_ActiveEntities;	// entities that are active (near the screenBounds)
	private ArrayList<DynamicEntity> m_AddEntities;
	private ArrayList<DynamicEntity> m_RemoveEntities;
	
	public EntityManager() {
		m_EntityCollider = new EntityCollider();
		m_Entities = new LinkedList<DynamicEntity>();
		m_ActiveEntities = new ArrayList<DynamicEntity>();
		m_AddEntities = new ArrayList<DynamicEntity>();
		m_RemoveEntities = new ArrayList<DynamicEntity>();
	}
	
	public void update(float deltaTime) {
		// Restart active entities every frame
		m_ActiveEntities.clear();
		
		// Add any entities to the main entity list
		for(DynamicEntity add : m_AddEntities) {
			m_Entities.add(add);
		}
		m_AddEntities.clear();
		
		// Activate only entities on the screen
		for(DynamicEntity ent : m_Entities) {
			boolean activate = false;
			// Player always active
			// TODO: Maybe something smarter?
			activate = (ent == GameScreen.getLevel().getPlayer());
			activate |= (ent.isOnScreen());
			
			if(activate)
				m_ActiveEntities.add(ent);
		}
		
		// Update active entities
		for(DynamicEntity ent : m_ActiveEntities) {
			ent.update(deltaTime);
		}
		// Handle entity collisions
		m_EntityCollider.update();
		
		// Remove any entities from the main entity list
		for(DynamicEntity dead : m_RemoveEntities) {
			m_Entities.remove(dead);
		}
		m_RemoveEntities.clear();		
	}
	
	public void draw(SpriteBatch batch) {
		for(DynamicEntity ent : m_ActiveEntities) {
			ent.draw(batch);
		}
	}
	
	public void draw(ShapeRenderer render) {
		for(DynamicEntity ent : m_ActiveEntities) {
			ent.draw(render);
		}
		
		m_EntityCollider.draw(render);
	}
	
	/** Adds an entity to the entity manager. 
	 * @param The entity to add
	 * @param Is this entity friendly? (TODO: better way)
	 * @param Should be force this entity to be added right away? */
	public void addEntity(DynamicEntity ent, boolean friendly, boolean force) {
		ent.setFriendly(friendly);
		if(force) {
			m_Entities.add(ent);
		}
		else
			m_AddEntities.add(ent);
	}
	
	public void removeEntity(DynamicEntity ent) {
		m_RemoveEntities.add(ent);
	}
	
	// Kill everything
	public void reset() {
		m_Entities.clear();
	}
	
	public int getNumActiveEntities() {
		return m_ActiveEntities.size();
	}
	
	public int getNumEntities() {
		return m_Entities.size();
	}
	
	public ArrayList<DynamicEntity> getActiveEntities() {
		return m_ActiveEntities;
	}
	
	//public LinkedList<DynamicEntity> getEntities() {
	//	return m_Entities;
	//}
	
	public Player getPlayer() {
		return (Player) m_Entities.get(0);
	}
}
