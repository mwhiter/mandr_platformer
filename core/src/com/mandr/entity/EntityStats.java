package com.mandr.entity;

public class EntityStats {
	public int 		maxHealth 	= 0;
	public float 	moveSpeed 	= 0.0f;
	public float 	climbSpeed 	= 0.0f;
	public float 	jumpSpeed 	= 0.0f;
	public float 	crouchSpeed = 0.0f;
	public boolean 	friendly 	= false;			// Is the entity friendly?
	public boolean	ignoresScreenBounds = false;	// Does the entity ignore screen bounds?
	public boolean	dieOffScreen = false;			// Does the entity die when it goes offscreen?
	public boolean	dieWhenCollide = false;			// Does the entity die when it collides with anything?
}
