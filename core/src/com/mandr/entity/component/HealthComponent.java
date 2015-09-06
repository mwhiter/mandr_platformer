package com.mandr.entity.component;

import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.level.Tile;

public class HealthComponent extends Component {
	private final int m_MaxHealth;
	private int m_Health;
	
	public HealthComponent(Entity entity, int maxHealth) {
		super(entity);
		m_MaxHealth = maxHealth;
		m_Health = maxHealth;
	}

	@Override
	public void reset() {
		m_Health = m_MaxHealth;
	}

	@Override
	public void update(float deltaTime) {
	}

	@Override
	public void stateChange(EntityState oldState, EntityState newState) {		
	}

	@Override
	public ComponentType getType() {
		return null;
	}
	
	public int getMaxHealth() {
		return m_MaxHealth;
	}
	
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

	@Override
	public void collision(Entity other) {}

	@Override
	public void collision(Tile tile) {}
}
