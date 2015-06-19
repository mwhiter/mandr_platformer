package com.mandr.entity;

import com.mandr.info.ItemInfo;

public class EntityStats {
	public int 		maxHealth 			= 0;		// Max health of the entity
	public float 	moveSpeed 			= 0;		// Movement speed of entity
	public float	maxFallSpeed 		= -100;		// Max fall speed of entity
	public float 	climbSpeed 			= 0;		// Maximum climb speed
	public float 	jumpSpeed 			= 0;		// Maximum jump speed
	public float 	crouchSpeed 		= 0;		// Maximum crouch speed
	public boolean 	friendly 			= false;	// Is the entity friendly?
	public boolean	ignoresScreenBounds = false;	// Does the entity ignore screen bounds?
	public boolean	dieOffScreen 		= false;	// Does the entity die when it goes offscreen?
	
	public ItemInfo itemStats			= null;		// Item stats
	
	// Projectile only thing, so why should every entity know?
	//public boolean	dieWhenCollide 		= false;	// Does the entity die when it collides with anything?
}
