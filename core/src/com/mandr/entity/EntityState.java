package com.mandr.entity;

// Unique states an entity can be in
public enum EntityState {
	NO_ENTITY_STATE,
	ENTITY_STATE_DEATH,		// Entity is dead
	ENTITY_STATE_JUMP,		// Entity is jumping 
	ENTITY_STATE_CROUCH,	// Entity is crouching
	ENTITY_STATE_LADDER,	// Entity is on a ladder
}
