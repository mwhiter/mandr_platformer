package com.mandr.entity.component;

import com.mandr.entity.EntityState;
import com.mandr.entity.Entity;

public class HealthComponent extends Component {
	private final int m_MaxHealth;
	private int m_Health;
	
	public HealthComponent(Entity entity, int maxHealth) {
		super(entity);
		m_MaxHealth = maxHealth;
		m_Health = maxHealth;
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateChange(EntityState oldState, EntityState newState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ComponentType getType() {
		// TODO Auto-generated method stub
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

}
