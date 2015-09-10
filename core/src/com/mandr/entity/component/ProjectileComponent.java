package com.mandr.entity.component;

import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.level.Tile;

public class ProjectileComponent extends Component {	
	public ProjectileComponent(Entity entity) {
		super(entity);
	}
	
	@Override
	public void reset() { }

	@Override
	public void update(float deltaTime) {
		processMessages();
	}
	
	@Override
	public void receiveMessage(ComponentMessage msg) {

	}

	@Override
	public void stateChange(EntityState oldState, EntityState newState) {
	}

	@Override
	public ComponentType getType() {
		return ComponentType.COMPONENT_PROJECTILE;
	}

	@Override
	public void collision(Entity other) {
		if(m_Entity.isFriendly() == other.isFriendly()) return;
		m_Entity.setDead(true);
	}

	@Override
	public void collision(Tile tile) {
		// TODO: check if dies when touches a tile
		m_Entity.setDead(true);
	}
}
