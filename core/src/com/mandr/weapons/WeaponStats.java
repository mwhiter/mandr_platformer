package com.mandr.weapons;

import com.mandr.weapons.Weapon.WeaponType;


public class WeaponStats {
	private final int ms_per_round;		// millisec per round
	
	private final String name;
	private final WeaponType type;
	private final int reloadSpeed;		// Reload speed in ms
	private final int rpm;				// Rate of fire in rounds per minute
	private final float bulletVelocity;	// Speed of bullets (in tiles per second)
	private final int cof;				// In degrees
	private final int damage;			// Damage of each bullet
	private final int max_ammo;			// Maximum ammunition
	private final int mag_size;			// Size of the magazine
	
	/** Constructs a new weapons stats object
	 * @param (String) weaponName: Name of the weapon
	 * @param (WeaponType) weaponType: Type of the weapon
	 * @param (float) reload_speed: Reload speed in seconds
	 * @param (int) maxammo: Maximum ammunition the weapon can possibly hold
	 * @param (int) magsize: Size of the magazine
	 * @param (int) firerate: Fire rate of the weapon in rounds per minute
	 * @param (int) cone_of_fire: Degrees of the weapon's randomized cone of fire
	 * @param (int) bullet_velocity: Velocity of each bullet in tiles per second
	 * @param (int) bullet_damage: Damage of each bullet
	 * */
	public WeaponStats(String weaponName, WeaponType weaponType, int reload_speed, int maxammo, int magsize, int rate_of_fire, int cone_of_fire, float bullet_velocity, int bullet_damage) {
		name = weaponName;
		type = weaponType;
		reloadSpeed = reload_speed;
		max_ammo = maxammo;
		
		if(max_ammo > 0)
			mag_size = Math.min(max_ammo, magsize);
		else
			mag_size = magsize;
		
		rpm = rate_of_fire;
		cof = cone_of_fire;
		bulletVelocity = bullet_velocity;
		damage = bullet_damage;
		ms_per_round = 60000 / rpm;	// 60000 is MS_PER_MIN
	}
	
	public String getName() {
		return name;
	}
	
	public WeaponType getWeaponType() {
		return type;
	}
	
	public int getReloadSpeed() {
		return reloadSpeed;
	}
	
	public int getMaxAmmo() {
		return max_ammo;
	}
	
	public int getMagSize() {
		return mag_size;
	}
	
	public int getRPM() {
		return rpm;
	}
	
	public int getConeOfFire() {
		return cof;
	}
	
	public float getBulletVelocity() {
		return bulletVelocity;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public int getFireSpeed() {
		return ms_per_round;
	}
}
