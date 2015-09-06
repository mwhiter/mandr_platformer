package com.mandr.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.mandr.info.*;

public class Globals {
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

	private static ArrayList<WeaponInfo> m_WeaponStats = new ArrayList<WeaponInfo>();
	public static ArrayList<WeaponInfo> getWeaponStats() { return m_WeaponStats; }
	public static WeaponInfo getWeaponStat(int id) {
		if(id < 0 || id >= m_WeaponStats.size()) return null;
		return m_WeaponStats.get(id);
	}

	private static ArrayList<ItemInfo> m_ItemStats = new ArrayList<ItemInfo>();
	public static ArrayList<ItemInfo> getItemInfos() { return m_ItemStats; }
	public static ItemInfo getItemInfo(int id) {
		if(id < 0 || id >= m_ItemStats.size()) return null;
		return m_ItemStats.get(id);
	}
	
	private static ArrayList<EntityInfo> m_EntityInfos = new ArrayList<EntityInfo>();
	public static ArrayList<EntityInfo> getEntityInfos() { return m_EntityInfos; }
	public static EntityInfo getEntityInfo(int id) {
		if(id < 0 || id >= m_EntityInfos.size()) return null;
		return m_EntityInfos.get(id);
	}
}
