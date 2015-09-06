package com.mandr.entity.component;

import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.level.Tile;

public abstract class Component {
	public static final int MAX_NUM_COMPONENTS = 32;
	
	public Entity m_Entity;
	
	public Component(Entity entity) {
		m_Entity = entity;
	}
	
	public abstract ComponentType getType();
	public abstract void update(float deltaTime);
	public abstract void reset();
	
	/** Called when an entity changes its state.
	 * oldState : previous state
	 * newState : new state
	 * */
	public abstract void stateChange(EntityState oldState, EntityState newState);
	
	/** Called when an entity collides with another entity. 
	 * @param other : entity it collides with
	 * */
	public abstract void collision(Entity other);
	
	/** Called when an entity collides with a tile.
	 * @param tile : tile it collides with
	 * */
	public abstract void collision(Tile tile);
	
	public Entity getEntity() {
		return m_Entity;
	}
}
