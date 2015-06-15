package com.mandr.entity.component;

public enum ComponentType {
	COMPONENT_RENDER(1 << 0),		// Renders the entity.
	COMPONENT_MOVE	(1 << 1),		// Allows entity to move
	COMPONENT_CROUCH(1 << 2),		// Allows entity to crouch (if he can move)
	COMPONENT_JUMP	(1 << 3),		// Allows entity to jump (if he can move)
	COMPONENT_LADDER(1 << 4),		// Allows entity to climb ladders (if he can move)
	COMPONENT_WEAPON(1 << 5),		// Allows entity to equip weapons and shoot
	COMPONENT_HEALTH(1 << 6),		// Allows entity to equip weapons and shoot
	COMPONENT_PROJECTILE(1 << 7),	// Allows entity to behavior like a projectile.
	
	// Component Presets
	COMPONENT_PLAYER(
			COMPONENT_RENDER.getFlag() 	|
			COMPONENT_MOVE.getFlag() 	|
			COMPONENT_CROUCH.getFlag() 	|
			COMPONENT_JUMP.getFlag() 	|
			COMPONENT_LADDER.getFlag()	|
			COMPONENT_WEAPON.getFlag()	|
			COMPONENT_HEALTH.getFlag()),
	COMPONENT_BULLET( COMPONENT_RENDER.getFlag() | COMPONENT_MOVE.getFlag() | COMPONENT_PROJECTILE.getFlag());
	
	private int flag;
	
	ComponentType(int flag) {
		this.flag = flag;
	}
	
	public int getFlag() {
		return flag;
	}
}
