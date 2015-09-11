package com.mandr.info;

import com.mandr.database.DatabaseRow;
import com.mandr.database.DatabaseUtility;
import com.mandr.entity.component.ComponentType;
import com.mandr.game.Globals;

public class EntityInfo extends Info {
	private float sizeX;
	private float sizeY;
	private int 	maxHealth;				// Max health of the entity
	private float 	moveSpeed;				// Movement speed of entity
	private float	maxFallSpeed;			// Max fall speed of entity
	private float 	climbSpeed;				// Maximum climb speed
	private float 	jumpSpeed;				// Maximum jump speed
	private float 	crouchSpeed;			// Maximum crouch speed
	private boolean friendly;				// Is the entity friendly?
	private boolean	ignoresScreenBounds;	// Does the entity ignore screen bounds?
	private boolean	dieOffScreen; 			// Does the entity die when it goes offscreen?
	private ItemInfo itemInfo;				// Item that it's holding
	private int 	components;
	private int 	animID;
	
	@Override
	public boolean cacheRow(DatabaseRow row) {
		if(row == null) return false;
		if(!super.cacheRow(row)) return false;
		
		sizeX				= row.getFloat("SizeX");
		sizeY				= row.getFloat("SizeY");
		
		maxHealth 			= row.getInt("MaxHP");
		moveSpeed 			= row.getFloat("Speed");
		maxFallSpeed 		= row.getFloat("FallSpeedMax");
		climbSpeed 			= row.getFloat("ClimbSpeed");
		jumpSpeed 			= row.getFloat("JumpSpeed");
		crouchSpeed 		= row.getFloat("CrouchSpeed");
		friendly 			= row.getBool("Friendly");
		ignoresScreenBounds = row.getBool("IgnoreBounds");
        dieOffScreen		= row.getBool("DiesOffScreen");
        
        itemInfo			= Globals.getItemInfo(DatabaseUtility.getIDFromTypeName(row.getText("ItemType"), "Items"));
        
        animID				= DatabaseUtility.getIDFromTypeName(row.getText("AnimType"), "Animations");
        
        // Components are built dynamically based on entity specs
        components = 0;
        if(maxHealth > 0) 	components |= ComponentType.COMPONENT_HEALTH.getFlag();
        
        components |= ComponentType.COMPONENT_MOVE.getFlag();
        if(climbSpeed > 0) 	components |= ComponentType.COMPONENT_LADDER.getFlag();
        if(jumpSpeed > 0) 	components |= ComponentType.COMPONENT_JUMP.getFlag();
        if(crouchSpeed > 0) components |= ComponentType.COMPONENT_CROUCH.getFlag();
        if(itemInfo != null) components |= ComponentType.COMPONENT_ITEM.getFlag();
        
        // TODO The following are test-related and should probably be built a different way
        if(row.getBool("Projectile")) components |= ComponentType.COMPONENT_PROJECTILE.getFlag();
        if(!row.getBool("Invisible")) components |= ComponentType.COMPONENT_RENDER.getFlag();		// For example, maybe something is invisible only if it doesn't have a texture...?
        if(row.getBool("CanHaveWeapons")) components |= ComponentType.COMPONENT_WEAPON.getFlag();
        
        return true;
	}

	public float getSizeX() {
		return sizeX;
	}
	
	public float getSizeY() {
		return sizeY;
	}
	
	public float getCrouchSpeed() {
		return crouchSpeed;
	}

	public int getComponents() {
		return components;
	}

	public float getJumpSpeed() {
		return jumpSpeed;
	}

	public float getClimbSpeed() {
		return climbSpeed;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public ItemInfo getItemInfo() {
		return itemInfo;
	}

	public float getMoveSpeed() {
		return moveSpeed;
	}

	public boolean isIgnoreScreenBounds() {
		return ignoresScreenBounds;
	}

	public boolean isDieOffScreen() {
		return dieOffScreen;
	}

	public boolean isFriendly() {
		return friendly;
	}

	public float getMaxFallSpeed() {
		return maxFallSpeed;
	}
	
	public int getAnimID() {
		return animID;
	}
}