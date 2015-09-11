package com.mandr.game;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Animation;
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

	private static ArrayList<WeaponInfo> m_WeaponStats = new ArrayList<WeaponInfo>();
	private static ArrayList<ItemInfo> m_ItemStats = new ArrayList<ItemInfo>();
	private static ArrayList<EntityInfo> m_EntityInfos = new ArrayList<EntityInfo>();
	
	public static ArrayList<WeaponInfo> getWeaponStats() { return m_WeaponStats; }
	public static WeaponInfo getWeaponStat(int id) {
		if(id < 0 || id >= m_WeaponStats.size()) return null;
		return m_WeaponStats.get(id);
	}

	public static ArrayList<ItemInfo> getItemInfos() { return m_ItemStats; }
	public static ItemInfo getItemInfo(int id) {
		if(id < 0 || id >= m_ItemStats.size()) return null;
		return m_ItemStats.get(id);
	}
	
	public static ArrayList<EntityInfo> getEntityInfos() { return m_EntityInfos; }
	public static EntityInfo getEntityInfo(int id) {
		if(id < 0 || id >= m_EntityInfos.size()) return null;
		return m_EntityInfos.get(id);
	}
	
	// Animations
	// Done separate because could appear complicated
	
	// I want an array of animation data.
	
	// [0] = Animation Set #1
	// [1] = Animation Set #2
	// [2] = Animation Set #3
	// ...
	
	// Each animation set contains a hash map. It looks like this:
	// Animation Set #1
		// |    Key		|    Value		|
		// |----------------------------|
		// | IDLE 		| Animation 0	|
		// | MOVE_LEFT 	| Animation 1	|
		// | MOVE_RIGHT | Animation 2	|
	
	// Each animation has an array of frames, which RenderComponent cares about.
	
	// TODO: Just contain HashMap<String, Animation> into a class called AnimationSet, for readability's sake!
	
	private static ArrayList<HashMap<String, Animation>> m_AnimInfos = new ArrayList<HashMap<String, Animation>>();
	public static ArrayList<HashMap<String, Animation>> getAnimInfos() { return m_AnimInfos; }
	
	// TODO Incorrect right now. Need some way to index the animation infos like the other database stuff. Should they be put into the database?
	public static HashMap<String, Animation> getAnimInfo(int id) {
		if(id < 0 || id >= m_EntityInfos.size()) return null;
		return m_AnimInfos.get(id);
	}
}
