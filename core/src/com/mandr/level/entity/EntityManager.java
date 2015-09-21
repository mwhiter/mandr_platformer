package com.mandr.level.entity;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mandr.entity.Entity;
//import com.mandr.entity.unused.DynamicEntity;
//import com.mandr.entity.unused.Player;
import com.mandr.game.screens.GameScreen;

public class EntityManager {
	
	private Entity m_Player;
	
	private EntityCollider m_EntityCollider;
	private LinkedList<Entity> m_Entities;
	private ArrayList<Entity> m_ActiveEntities;	// entities that are active (near the screenBounds)
	private ArrayList<Entity> m_AddEntities;
	private ArrayList<Entity> m_RemoveEntities;
	
	public EntityManager() {
		m_EntityCollider = new EntityCollider();
		m_Entities = new LinkedList<Entity>();
		m_ActiveEntities = new ArrayList<Entity>();
		m_AddEntities = new ArrayList<Entity>();
		m_RemoveEntities = new ArrayList<Entity>();
	}
	
	public void update(float deltaTime) {
		// Restart active entities every frame
		m_ActiveEntities.clear();
		
		// Add any entities to the main entity list
		for(Entity add : m_AddEntities) {
			m_Entities.add(add);
		}
		m_AddEntities.clear();
		
		// Activate only entities on the screen
		for(Entity ent : m_Entities) {
			boolean activate = false;
			// Player always active
			// TODO: Maybe something smarter?
			activate = (ent == GameScreen.getLevel().getPlayer());
			activate |= (ent.isOnScreen(0.25f));
			
			if(activate)
				m_ActiveEntities.add(ent);
		}
		
		// Update active entities
		for(Entity ent : m_ActiveEntities) {
			ent.update(deltaTime);
		}
		
		// Handle entity collisions
		m_EntityCollider.update();
		
		// Mark entities for deletion
		for(Entity ent : m_ActiveEntities) {
			if(ent.isDead()) {
				if(ent != GameScreen.getLevel().getPlayer()) {
					m_RemoveEntities.add(ent);
				}
				else {
					// TODO: Player death event
					GameScreen.getLevel().respawn();
				}
			}
		}
		
		// Remove any entities from the main entity list
		for(Entity dead : m_RemoveEntities) {
			m_Entities.remove(dead);
		}
		m_RemoveEntities.clear();		
	}
	
	public void draw(float delta, SpriteBatch batch) {
		for(Entity ent : m_ActiveEntities) {
			ent.draw(delta, batch);
		}
	}
	
	public void draw(ShapeRenderer render) {
		for(Entity ent : m_ActiveEntities) {
			ent.draw(render);
		}
		
		m_EntityCollider.draw(render);
	}
	
	/** Adds an entity to the entity manager. 
	 * @param The entity to add
	 * @param Should be force this entity to be added right away? */
	public void addEntity(Entity ent, boolean force) {
		if(force)
			m_Entities.add(ent);
		else
			m_AddEntities.add(ent);
	}
	
	public void removeEntity(Entity ent) {
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
	
	public ArrayList<Entity> getActiveEntities() {
		return m_ActiveEntities;
	}
	
	public void setPlayer(Entity ent) {
		if(ent != null)
			m_Player = ent;
	}
	
	public Entity getPlayer() {
		return m_Player;
	}

	public EntityCollider getEntityCollider() {
		return m_EntityCollider;
	}
}
