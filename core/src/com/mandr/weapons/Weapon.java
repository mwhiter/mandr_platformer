package com.mandr.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mandr.entity.Actor;
import com.mandr.entity.Projectile;
import com.mandr.game.GameGlobals;
import com.mandr.game.screens.GameScreen;
import com.mandr.util.AABB;
import com.mandr.util.Constants;

public class Weapon {
	public enum WeaponType {
		WEAPON_TYPE_SEMI_AUTO,
		WEAPON_TYPE_THREE_ROUND_BURST,
		WEAPON_TYPE_FULL_AUTO
	}
	
	private Actor m_Actor;
	
	private WeaponStats m_WeaponStats;
	
	private int m_CurrentMagSize;		// current number of rounds in the magazine
	private int m_CurrentAmmoReserve;	// current number of ammo in reserves
	
	private float m_ReloadStartTime;
	private float m_LastFireTime;
	
	/** Constructs a new weapon.
	 * @param (float) reloadSpeed: Reload speed in seconds.
	 * @param (int) rpm: Rounds per minute
	 * @param: (int) damage: Damage of bullet
	 * @param: (int) maxAmmo: Maximum ammo of the weapon.
	 * @param: (int) magSize: Maximum size of the magazine.
	 * */
	public Weapon(Actor actor, WeaponStats stats) {
		m_Actor = actor;
		m_WeaponStats = stats;
		
		m_ReloadStartTime = -1;
		m_LastFireTime = -1;
		
		m_CurrentMagSize = m_WeaponStats.getMagSize();
		m_CurrentAmmoReserve = m_WeaponStats.getMaxAmmo() - m_CurrentMagSize;
	}
	
	public void update() {
		if(isReloading()) {
			if(getReloadPercent() >= 1.0f) {
				finishReload();
			}
		}
	}
	
	//=========================================================================
	// Fire
	//=========================================================================
	
	public void fire() {
		if(!canFire())
			return;
		
		// Reload if we have to
		if(isMagazineEmpty()) {
			reload();
			return;
		}
		
		// Fire done here
		// TODO: fix this code up
		spawnProjectile();
		
		m_CurrentMagSize -= 1;
		m_LastFireTime = GameGlobals.getGameTime();
		
		// TODO: Burst fire support
	}
	
	// TODO: Test function to spawn projectile. Figure out a better way to implement
	private void spawnProjectile() {
		
		AABB box = m_Actor.getBoundingBox();
		
		Vector2 lookVector = m_Actor.getLookVector();
		Vector2 projectileSpawn = new Vector2(lookVector.x * (m_Actor.getSize().x/2), lookVector.y * (m_Actor.getSize().y/2));
		
		// (u*v / u*u) * u
		float scalar = (lookVector.dot(projectileSpawn) / lookVector.dot(lookVector));
		projectileSpawn = new Vector2(lookVector.x * scalar, lookVector.y * scalar);
		projectileSpawn.add(box.getCenterX(), box.getCenterY());
		
		float width = 0.25f;
		float height = 0.125f;
		Vector2 projVelocity = new Vector2(lookVector);
		projVelocity.scl(m_WeaponStats.getBulletVelocity());
		
		Projectile proj = new Projectile(this,  new Texture("resources/entities/test_bullet.png"), projectileSpawn.x, projectileSpawn.y, width, height);
		
		float timeSinceFire = timeSinceLastFire();
		float cofPercent = 1 - ((timeSinceFire - m_WeaponStats.getFireSpeed()) / Constants.COF_MAX_TIME);
		cofPercent = MathUtils.clamp(cofPercent, 0, 1);
		float cof = m_WeaponStats.getConeOfFire() * cofPercent;
		
		if(m_Actor.isCrouched())
			cof /= 2;
		
		// Apply some cone of fire
		float rotation = MathUtils.random(-cof, cof);
		projVelocity.rotate(rotation);
		
		proj.getVelocity().x = projVelocity.x;
		proj.getVelocity().y = projVelocity.y;
		proj.getSprite().setOrigin(proj.getSprite().getWidth()/2, proj.getSprite().getHeight()/2);
		proj.getSprite().setRotation(projVelocity.angle());
		
		// TODO: Something better?
		GameScreen.getLevel().getEntityManager().addEntity(proj, m_Actor.isFriendly(), false);
	}
	
	public boolean canFire() {
		if(!m_Actor.canFireWeapon())
			return false;
		
		if(isReloading())
			return false;
		
		if(isOutOfAmmo())
			return false;
		
		// Too soon since we fired
		if(timeSinceLastFire() < m_WeaponStats.getFireSpeed())
			return false;
		
		return true;
	}
	
	//=========================================================================
	// Ammo and Magazine Size
	//=========================================================================
	
	public boolean isMagazineEmpty() {
		return m_CurrentMagSize == 0;
	}
	
	public boolean isInfiniteAmmo() {
		return m_WeaponStats.getMaxAmmo() <= 0;
	}
	
	public boolean isOutOfAmmo() {
		if(isInfiniteAmmo()) return false;
		return m_CurrentAmmoReserve == 0 && m_CurrentMagSize == 0;
	}
	
	//=========================================================================
	// Reloading
	//=========================================================================
	/** What percent of the reload have we completed? 
	 * Reload the current weapon.
	 * */
	public void reload() {
		if(!canReload())
			return;
		
		m_ReloadStartTime = GameGlobals.getGameTime();
	}
	
	/** Stop our current reload */
	public void stopReload() {
		m_ReloadStartTime = -1;
	}
	
	/** Finish the current reload, refilling our magazine size */
	private void finishReload() {
		if(!isReloading())
			return;
		stopReload();
		
		
		// If the weapon does not have infinite ammo, be sure not to put more ammo in the new magazine as we have bullets
		if(isInfiniteAmmo()) {
			m_CurrentMagSize = m_WeaponStats.getMagSize();
			return;
		}
		
		int startMag = m_CurrentMagSize;
		int startReserve = m_CurrentAmmoReserve;
		int magSize = m_WeaponStats.getMagSize();
		
		int magChange = Math.min(magSize - startMag, startReserve);
		
		int endMag = startMag + magChange;
		int endReserve = startReserve - magChange;
		
		m_CurrentMagSize = endMag;
		m_CurrentAmmoReserve = endReserve;
	}
	
	public boolean canReload() {
		if(!m_Actor.canReload())
			return false;
		if(m_CurrentMagSize == m_WeaponStats.getMagSize())
			return false;
		if(isReloading())
			return false;
		if(isOutOfAmmo())
			return false;
		if(m_CurrentAmmoReserve == 0)
			return false;
		
		return true;
	}
	
	/** Are we currently reloading? */
	public boolean isReloading() {
		return m_ReloadStartTime >= 0.0f;
	}
	
	/** What percent of the reload have we completed? 
	 * @return Percent from 0 to 1 of our current reload.
	 * */
	public float getReloadPercent() {
		if(!isReloading())
			return 0;
		
		float currentTime = GameGlobals.getGameTime();
		float reloadTime = currentTime - m_ReloadStartTime;
		
		return Math.min(1, reloadTime / m_WeaponStats.getReloadSpeed());
	}
	
	public WeaponStats getWeaponStats() { return m_WeaponStats; }
	public int getCurrentMagSize() { return m_CurrentMagSize; }
	public int getCurrentAmmoReserve() { return m_CurrentAmmoReserve; }
	
	//=========================================================================
	// Helper Methods
	//=========================================================================
	
	public Actor getOwner() { 
		return m_Actor;
	}
	
	private float timeSinceLastFire() {
		float currentTime = GameGlobals.getGameTime();
		return currentTime - m_LastFireTime;
	}
	
	public String getWeaponString() {
		String ammoReserveString = "";
		if(!isInfiniteAmmo())
			ammoReserveString = " / " + Integer.toString(getCurrentAmmoReserve());		
		
		return new String(m_WeaponStats.getName() + ": " + Integer.toString(getCurrentMagSize()) + ammoReserveString);
	}
}