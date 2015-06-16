package com.mandr.entity.component;

import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.level.Tile;

public class ProjectileComponent extends Component {

	// TODO: defined in own file?
	public enum ProjectileType {
		PROJECTILE_NORMAL			// A normal bullet
	}
	
	public ProjectileComponent(Entity entity) {
		super(entity);
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
