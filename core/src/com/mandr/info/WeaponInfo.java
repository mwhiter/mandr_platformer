package com.mandr.info;

import com.mandr.database.DatabaseRow;
import com.mandr.weapons.Weapon.WeaponType;


public class WeaponInfo implements Info {
	private int ms_per_round;		// millisec per round
	
	private String name;
	private WeaponType type;
	private int reloadSpeed;		// Reload speed in ms
	private int rpm;				// Rate of fire in rounds per minute
	private float bulletVelocity;	// Speed of bullets (in tiles per second)
	private float cof;				// In degrees
	private int damage;			// Damage of each bullet
	private int max_ammo;			// Maximum ammunition
	private int mag_size;			// Size of the magazine

	@Override
	public boolean cacheRow(DatabaseRow row) {
		if(row == null) return false;
		
		name =				row.getText("Name");
		reloadSpeed = 		row.getInt("ReloadSpeed");
		max_ammo = 			row.getInt("MaxAmmo");
		mag_size = 			row.getInt("MagSize");
		rpm = 				row.getInt("RateOfFire");
		cof = 				row.getFloat("ConeOfFire");
		bulletVelocity = 	row.getFloat("Velocity");
		damage = 			row.getInt("Damage");
		
		if(max_ammo > 0) mag_size = Math.min(max_ammo, mag_size);
		
		String type_name = 	row.getText("Type");
		if(type_name.equalsIgnoreCase("semi")) type = WeaponType.WEAPON_TYPE_SEMI_AUTO;
		if(type_name.equalsIgnoreCase("full")) type = WeaponType.WEAPON_TYPE_FULL_AUTO;
		else type = null;
		
		ms_per_round = 60000 / rpm;	// 60000 is MS_PER_MIN
		
		return true;
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
	
	public float getConeOfFire() {
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
