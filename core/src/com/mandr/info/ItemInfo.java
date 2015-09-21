package com.mandr.info;

import com.mandr.database.DatabaseRow;
import com.mandr.database.DatabaseUtility;

public class ItemInfo extends Info {
	private String name;	// Name of Item
	private int weaponID;	// Stored weapon
	private int health;		// Health restored
	private int animID;
	private float sizeX;
	private float sizeY;

	@Override
	public boolean cacheRow(DatabaseRow row) {
		if(row == null) return false;
		if(!super.cacheRow(row)) return false;
		
		name 		= row.getText("Name");
		sizeX		= row.getFloat("SizeX");
		sizeY		= row.getFloat("SizeY");
		weaponID 	= row.getInt("WeaponID");
		health 		= row.getInt("Health");
		animID		= DatabaseUtility.getIDFromTypeName(row.getText("AnimType"), "Animations");
		
		return true;
	}	

	
	public String getName() { return name; }
	public int getWeaponID() { return weaponID; }
	public int getHealth() { return health; }
	public int getAnimID() { return animID; }


	public float getSizeX() { return sizeX; }
	public float getSizeY() { return sizeY; }
}
