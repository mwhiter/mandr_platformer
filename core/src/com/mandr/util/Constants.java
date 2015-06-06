package com.mandr.util;

public class Constants {
	// Core engine stuff
	public static final int NUM_TILES_ON_GAME_SCREEN_WIDTH = 24;
	public static final int NUM_TILES_ON_GAME_SCREEN_HEIGHT = 18;
	public static final int NUM_PIXELS_PER_TILE = 16;
	
	// Input
	public static final float INPUT_NO_LOOKDOWN_ANGLE = 30;			// Angle below the player we can't look down. (e.g. 30 degrees means we can't look from (270-30) to (270+30) degrees
	
	// Player only stuff
	public static final float FALL_PADDING = 0.12f;					// Number of seconds that can elapse that allows us to jump if we walked off a platform.
	public static final float PLAYER_DAMAGE_IMMOBILE_TIME = 0.8f;	// Time after being hit where player cannot control his player.
	public static final float PLAYER_DAMAGE_INVINCIBLE_TIME = 2.0f;	// Time after being hit where player is invincible and cannot receive anymore damage.
	
	// Physics
	public static final float GRAVITY = -0.85f;
	public static final float MAX_VELOCITY_Y = 32.0f;	// The maximum velocity in Y direction
	public static final float PLAYER_SHORT_JUMP_THRESHOLD = 8.0f;	// The minimum speed the player can jump before he's locked into it
	
	// Weapons
	public static final int MAX_WEAPONS = 4;
	public static final float COF_MAX_TIME = 0.25f;		// Max time in seconds that cone of fire penalty is applied
}
