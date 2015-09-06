package com.mandr.level;

import java.util.LinkedList;

import com.mandr.entity.Entity;
import com.mandr.entity.component.*;
import com.mandr.weapons.Weapon;

// A snapshot of the player's state (for respawn purposes)
public class PlayerState {
	private int health;
	private LinkedList<Weapon> weapons;
	private int activeWeapon;
	
	/** Fills out the player state with entity's data.
	 * @param entity The Entity to take a snapshot of. */
	public void snapshot(Entity entity) {
		Component control;
		control = entity.getComponent(ComponentType.COMPONENT_HEALTH);
		if(control != null) {
			health = ((HealthComponent) control).getHealth();
		}
		control = entity.getComponent(ComponentType.COMPONENT_WEAPON);
		if(control != null) {
			LinkedList<Weapon> currentWeapons = ((WeaponComponent) control).getWeapons();
			weapons = new LinkedList<Weapon>();
			// Create copies of the weapons currently.
			for(Weapon weap : currentWeapons) {
				weap = new Weapon(entity, weap.getWeaponStats(), weap.getCurrentMagSize(), weap.getTotalAmmo());
			}
			
			activeWeapon = ((WeaponComponent) control).getActiveWeaponIndex();
		}
	}
	
	/** Give the entity the state
	 * @param entity The entity to fill out.
	 * @param state The player state to use. */
	public static void give(Entity entity, PlayerState state) {
		Component control;
		control = entity.getComponent(ComponentType.COMPONENT_HEALTH);
		if(control != null) {
			((HealthComponent) control).setHealth(state.health);
		}
		control = entity.getComponent(ComponentType.COMPONENT_WEAPON);
		if(control != null) {
			LinkedList<Weapon> weapons = state.weapons;
			for(Weapon weapon : weapons) {
				((WeaponComponent) control).addWeapon(weapon.getWeaponStats());
			}
			
			((WeaponComponent) control).setActiveWeapon(state.activeWeapon);
		}
	}
}
