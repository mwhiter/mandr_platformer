package com.mandr.game;

import com.mandr.weapons.Weapon;
import com.mandr.weapons.WeaponStats;

public class GameGlobals {
	static private long m_GameTime = 0;
	
	// TODO Read from database or something.
	static private WeaponStats[] m_WeaponStats = { 
		new WeaponStats("Pistol", Weapon.WeaponType.WEAPON_TYPE_SEMI_AUTO, 1200, 0, 10, 400, 3, 2, 15),	// Test Pistol
		new WeaponStats("Sniper", Weapon.WeaponType.WEAPON_TYPE_SEMI_AUTO, 2000, 40, 5, 45, 1, 6, 75),	// Test Sniper
		new WeaponStats("LMG", Weapon.WeaponType.WEAPON_TYPE_FULL_AUTO, 6800, 300, 100, 600, 5, 3, 35),	// Test LMG
		new WeaponStats("SMG", Weapon.WeaponType.WEAPON_TYPE_FULL_AUTO, 3000, 1000, 30, 900, 3, 2, 25)	// Test SMG
	};
	
	public void reset() {
		m_GameTime = 0;
	}
	
	public static WeaponStats[] getWeaponStats() {
		return m_WeaponStats;
	}
	
	public static long getGameTime() {
		return m_GameTime;
	}
	
	public static void changeGameTime(long gameTime) {
		m_GameTime += gameTime;
	}
}
