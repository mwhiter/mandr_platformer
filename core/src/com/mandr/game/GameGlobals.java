package com.mandr.game;

import com.badlogic.gdx.graphics.Texture;
import com.mandr.weapons.Weapon;
import com.mandr.weapons.WeaponStats;

public class GameGlobals {
	static private long m_GameTime = 0;
	
	public void reset() {
		m_GameTime = 0;
	}
	
	public static long getGameTime() {
		return m_GameTime;
	}
	
	public static void changeGameTime(long gameTime) {
		m_GameTime += gameTime;
	}
	
	// TODO: Basically a simplified version of how we'll store our textures for the game.
	// TODO: Test texture stuff. Maybe should be in its own class or something.
	static private Texture[] m_Textures = {
		new Texture("resources/entities/test_player_img.png"),
		new Texture("resources/entities/test_enemy_img.png"),
		new Texture("resources/entities/test_bullet.png"),
		new Texture("resources/entities/test_item.png")
	};
	
	public static Texture getTexture(int texID) {
		if(texID < 0 || texID >= m_Textures.length) throw new ArrayIndexOutOfBoundsException("Texture ID does not exist!");
		return m_Textures[texID];
	}

	// TODO Read from database or something.
	static private WeaponStats[] m_WeaponStats = { 
		new WeaponStats("Pistol", 	Weapon.WeaponType.WEAPON_TYPE_SEMI_AUTO, 1200, 0, 10, 400, 3, 2, 15),		// Test Pistol
		new WeaponStats("Sniper", 	Weapon.WeaponType.WEAPON_TYPE_SEMI_AUTO, 2000, 40, 5, 45, 1, 6, 75),		// Test Sniper
		new WeaponStats("LMG", 		Weapon.WeaponType.WEAPON_TYPE_FULL_AUTO, 6800, 300, 100, 600, 5, 3, 35),	// Test LMG
		new WeaponStats("SMG", 		Weapon.WeaponType.WEAPON_TYPE_FULL_AUTO, 3000, 1000, 30, 900, 3, 2, 25)		// Test SMG
	};
	
	public static WeaponStats getWeaponStats(int statsID) {
		if(statsID < 0 || statsID >= m_WeaponStats.length) throw new ArrayIndexOutOfBoundsException("Weapon Stat ID does not exist!");
		return m_WeaponStats[statsID];
	}
}
