package com.mandr.info;

import com.mandr.database.DatabaseRow;

public class ItemInfo extends Info {
	private String m_Name;		// Name of Item
	private int m_WeaponID;		// Stored weapon
	private int m_Health;		// Health restored
	
	public String getName() { return m_Name; }
	public int getWeaponID() { return m_WeaponID; }
	public int getHealth() { return m_Health; }

	@Override
	public boolean cacheRow(DatabaseRow row) {
		if(row == null) return false;
		if(!super.cacheRow(row)) return false;
		
		m_Name 		= row.getText("Name");
		m_WeaponID 	= row.getInt("WeaponID");
		m_Health 	= row.getInt("Health");
		
		return true;
	}	
}
