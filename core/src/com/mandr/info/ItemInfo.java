package com.mandr.info;

import com.mandr.database.DatabaseRow;

public class ItemInfo implements Info {
	private String m_Name;		// Name of Item
	private int m_WeaponID;		// Stored weapon
	private int m_Health;		// Health restored
	
	public String getName() { return m_Name; }
	public int getWeaponID() { return m_WeaponID; }
	public int getHealth() { return m_Health; }

	@Override
	public boolean cacheRow(DatabaseRow result) {
		if(result == null) return false;
		
		m_Name 		= result.getText("Name");
		m_WeaponID 	= result.getInt("WeaponID");
		m_Health 	= result.getInt("Health");
		
		return true;
	}	
}
