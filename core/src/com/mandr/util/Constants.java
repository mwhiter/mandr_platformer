package com.mandr.util;

public class Constants {
	// Core engine stuff
	public static final int MS_PER_UPDATE = 33;
	public static final int NUM_TILES_ON_GAME_SCREEN_WIDTH = 24;
	public static final int NUM_TILES_ON_GAME_SCREEN_HEIGHT = 18;
	public static final int NUM_PIXELS_PER_TILE = 16;
	public static final int MIN_WALL_HEIGHT = 5;					// Number of valid tiles that must be present to be considered a wall-climbable wall
	public static final float OFF_SCREEN_PADDING_PERCENT = 0.1f;	// Percentage of screen width added to all sides of the screen to be considered "off-screen"			
	
	// Input
	public static final float INPUT_NO_LOOKDOWN_ANGLE = 30;			// Angle below the player we can't look down. (e.g. 30 degrees means we can't look from (270-30) to (270+30) degrees
	
	// Player only stuff
	public static final int MAX_PLAYER_HEALTH = 100;
	public static final long FALL_PADDING = 120;					// Number of seconds that can elapse that allows us to jump if we walked off a platform.
	public static final long PLAYER_DAMAGE_IMMOBILE_TIME = 800;		// Time after being hit where player cannot control his player.
	public static final long PLAYER_DAMAGE_INVINCIBLE_TIME = 2000;	// Time after being hit where player is invincible and cannot receive anymore damage.
	
	// Physics
	public static final float GRAVITY = -0.030f;
	public static final float PLAYER_SHORT_JUMP_THRESHOLD = 0.27f;	// The minimum speed the player can jump before he's locked into it
	public static final float SLOPE_CLIMB_DIVISOR = 4.0f;
	
	// Weapons
	public static final int MAX_WEAPONS = 4;
	public static final float COF_CROUCH_PERCENT = 0.5f;	// Percentage of cof from crouching.
	public static final float COF_MAX_TIME = 0.25f;			// Max time in seconds that cone of fire penalty is applied
}
