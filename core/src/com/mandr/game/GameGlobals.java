package com.mandr.game;

import com.mandr.weapons.Weapon;
import com.mandr.weapons.WeaponStats;

public class GameGlobals {
	static private float m_GameTime = 0.0f;
	
	// TODO Read from database or something.
	static private WeaponStats[] m_WeaponStats = { 
		new WeaponStats("Pistol", Weapon.WeaponType.WEAPON_TYPE_SEMI_AUTO, 1.2f, 0, 10, 400, 5, 80, 15),	// Test Pistol
		new WeaponStats("Sniper", Weapon.WeaponType.WEAPON_TYPE_SEMI_AUTO, 2.0f, 40, 5, 45, 2, 250, 75),	// Test Sniper
		new WeaponStats("SMG", Weapon.WeaponType.WEAPON_TYPE_FULL_AUTO, 3.0f, 1000, 30, 900, 6, 80, 25),		// Test SMG
		new WeaponStats("LMG", Weapon.WeaponType.WEAPON_TYPE_FULL_AUTO, 6.8f, 300, 100, 600, 8, 110, 35)	// Test LMG
	};
	
	public void reset() {
		m_GameTime = 0.0f;
	}
	
	public static WeaponStats[] getWeaponStats() {
		return m_WeaponStats;
	}
	
	public static float getGameTime() {
		return m_GameTime;
	}
	
	public static void changeGameTime(float gameTime) {
		m_GameTime += gameTime;
	}
}
