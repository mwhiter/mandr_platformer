package com.mandr.game;

import com.mandr.database.DatabaseRow;
import com.mandr.database.DatabaseUtility;
import com.mandr.info.Info;

public class ProjectileInfo extends Info {

	private float sizeX;
	private float sizeY;
	private int animID;
	
	@Override
	public boolean cacheRow(DatabaseRow row) {
		if(row == null) return false;
		if(!super.cacheRow(row)) return false;
		
		sizeX	= row.getFloat("SizeX");
		sizeY	= row.getFloat("SizeY");
		animID	= DatabaseUtility.getIDFromTypeName(row.getText("AnimType"), "Animations");
		
		return true;
	}
	
	public float getSizeX() { return sizeX; }
	public float getSizeY() { return sizeY; }
	public int getAnimID() { return animID; }
}
