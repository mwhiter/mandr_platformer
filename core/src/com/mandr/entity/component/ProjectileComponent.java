package com.mandr.entity.component;

import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;

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
		// TODO Auto-generated method stub
		return null;
	}

}
