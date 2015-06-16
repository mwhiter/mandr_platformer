package com.mandr.entity;

public class ItemStats {
	private int m_WeaponID;		// Stored weapon
	private int m_Health;		// Health restored
	
	public ItemStats(int weaponID, int health) {
		// TODO: Don't do this
		m_WeaponID = weaponID;
		m_Health= health;
	}
	
	public void load() {
		// TODO: implement. This function will read in from some sort of database and assign variables to that.
		//m_WeaponID = 1;
		//m_Health = 0;
	}
	
	public int getWeaponID() { return m_WeaponID; }
	public int getHealth() { return m_Health; }	
}
