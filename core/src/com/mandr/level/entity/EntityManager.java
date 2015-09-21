package com.mandr.level.entity;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mandr.entity.Entity;
import com.mandr.game.ProjectileInfo;
import com.mandr.game.screens.GameScreen;
import com.mandr.info.ActorInfo;
import com.mandr.info.ItemInfo;

public class EntityManager {
	private static int entity_id;
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
		for(Entity ent : m_Entities) {
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
	
	/** Adds an actor to the entity manager. 
	 * @param x the x-coord
	 * @param y the y-coord
	 * @param info the info
	 * @param force Should be force this entity to be added right away? */
	public Entity addActor(float x, float y, ActorInfo info, boolean force) {
		Entity entity = new Entity(entity_id++, x,y,info);
		if(force)
			m_Entities.add(entity);
		else
			m_AddEntities.add(entity);
		
		return entity;
	}
	
	/** Adds an item to the entity manager. 
	 * @param x the x-coord
	 * @param y the y-coord
	 * @param info the info
	 * @param force Should be force this entity to be added right away? */
	public Entity addItem(float x, float y, ItemInfo info, boolean force) {
		Entity entity = new Entity(entity_id++, x,y,info);
		if(force)
			m_Entities.add(entity);
		else
			m_AddEntities.add(entity);
		return entity;
	}
	
	/** Adds an projectile to the entity manager.
	 * @param x the x-coord
	 * @param y the y-coord 
	 * @param info the info
	 * @param force Should be force this entity to be added right away? */
	public Entity addProjectile(float x, float y, ProjectileInfo info, boolean force) {
		Entity entity = new Entity(entity_id++, x,y,info);
		if(force)
			m_Entities.add(entity);
		else
			m_AddEntities.add(entity);
		return entity;
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
