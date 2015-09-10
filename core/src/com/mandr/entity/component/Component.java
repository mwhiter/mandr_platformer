package com.mandr.entity.component;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.level.Tile;

public abstract class Component {
	public static final int MAX_NUM_COMPONENTS = 32;
	
	public Entity m_Entity;
	private ArrayBlockingQueue <ComponentMessage> m_Messages;
	
	public Component(Entity entity) {
		m_Entity = entity;
		m_Messages = new ArrayBlockingQueue<ComponentMessage>(ComponentMessage.MAX_COMPONENT_MESSAGES);
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
	
	protected abstract void receiveMessage(ComponentMessage msg);
	
	/** Add a message to be processed next frame
	 * @param msg : The message
	 * */
	public void addMessage(ComponentMessage msg) {
		if(!m_Messages.contains(msg))
			m_Messages.add(msg);
	}
	
	/** Process all received messages
	 * @param msg : The message
	 * */
	public void processMessages() {
		Iterator<ComponentMessage> it = m_Messages.iterator();
		while(it.hasNext()) {
			ComponentMessage msg = it.next();
			receiveMessage(msg);
		}
		
		m_Messages.clear();
	}
	
	public Entity getEntity() {
		return m_Entity;
	}
}
