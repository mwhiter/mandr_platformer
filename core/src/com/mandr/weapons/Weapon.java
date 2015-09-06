package com.mandr.weapons;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mandr.database.DatabaseUtility;
import com.mandr.entity.Entity;
import com.mandr.entity.component.ComponentType;
import com.mandr.entity.component.MoveComponent;
import com.mandr.entity.component.RenderComponent;
import com.mandr.entity.component.WeaponComponent;
import com.mandr.game.Globals;
import com.mandr.game.screens.GameScreen;
import com.mandr.info.EntityInfo;
import com.mandr.info.WeaponInfo;
import com.mandr.util.AABB;
import com.mandr.util.Constants;
import com.mandr.util.StringUtils;

public class Weapon {
	public enum WeaponType {
		WEAPON_TYPE_SEMI_AUTO,
		WEAPON_TYPE_THREE_ROUND_BURST,
		WEAPON_TYPE_FULL_AUTO
	}
	
	private Entity m_Entity;
	
	private WeaponInfo m_WeaponStats;
	
	private int m_CurrentMagSize;		// current number of rounds in the magazine
	private int m_CurrentAmmoReserve;	// current number of ammo in reserves
	
	private long m_ReloadStartTime;
	private long m_LastFireTime;
	
	private Vector2 m_ProjectileSpawnPosition;
	private Vector2 m_ProjectileVelocity;
	
	/** Constructs a new weapon. Equivalent to Weapon(entity, stats, stats.magSize(), stats.maxAmmo()
	 * @param entity The entity to give the weapon to.
	 * @param stats The stats of the weapon
	 * */
	public Weapon(Entity entity, WeaponInfo stats) {
		this(entity, stats, stats == null ? 0 : stats.getMagSize(), stats == null ? 0 : stats.getMaxAmmo());
	}

	/** Constructs a new weapon.
	 * @param entity The entity to give the weapon to.
	 * @param stats The stats of the weapon
	 * @param currMagSize The current size of the magazine (cannot be larger than the weapon info)
	 * @param currTotalAmmo The current total ammo of the weapon (cannot be larger than the weapon info)
	 * */
	public Weapon(Entity entity, WeaponInfo stats, int currMagSize, int currTotalAmmo) {
		if(stats == null) throw new IllegalArgumentException("Stats cannot be null!");
		
		m_Entity = entity;
		m_WeaponStats = stats;
		
		m_ReloadStartTime = -1;
		m_LastFireTime = -1;
		
		m_CurrentMagSize = Math.min(currMagSize, m_WeaponStats.getMagSize());
		m_CurrentAmmoReserve = Math.min(m_WeaponStats.getMaxAmmo(), currTotalAmmo) - m_CurrentMagSize;
		
		m_ProjectileSpawnPosition = new Vector2();
		m_ProjectileVelocity = new Vector2();
	}
	
	public void update() {
		updateSpawner();
		
		if(isReloading()) {
			if(getReloadPercent() >= 1.0f) {
				finishReload();
			}
		}
	}
	
	/** Updates the spawn of the projectile based on the entity's look vector. */
	private void updateSpawner() {
		AABB entityBox = m_Entity.getEndBoundingBox();
		
		Vector2 lookVector = m_Entity.getLookVector();
		
		// (u*v/u*u) * u
		float scalar = (Vector2.dot(lookVector.x, lookVector.y, lookVector.x * (m_Entity.getSize().x /2), lookVector.y * (m_Entity.getSize().y / 2)) / lookVector.dot(lookVector));
		Vector2 position = new Vector2(entityBox.getCenterX() + lookVector.x * scalar, entityBox.getCenterY() + lookVector.y * scalar);
		
		float bulletVelocity = m_WeaponStats.getBulletVelocity();
		Vector2 velocity = new Vector2(lookVector.x * bulletVelocity, lookVector.y * bulletVelocity);
		
		m_ProjectileSpawnPosition = position;
		m_ProjectileVelocity = velocity;
	}
	
	public Vector2 getProjectileSpawnPosition() { return m_ProjectileSpawnPosition; }
	public Vector2 getProjectileVelocity() { return m_ProjectileVelocity; }
	
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
		m_LastFireTime = Globals.getGameTime();
		
		// TODO: Burst fire support
	}

	public boolean canFire() {
		if(!((WeaponComponent) m_Entity.getComponent(ComponentType.COMPONENT_WEAPON)).canFireWeapon())
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
	
	// TODO: Test function to spawn projectile. Figure out a better way to implement
	private void spawnProjectile() {
		EntityInfo projInfo = Globals.getEntityInfo(DatabaseUtility.getIDFromTypeName("ENTITY_PROJECTILE", "Entities"));
		if(projInfo == null) return;
		
		// TODO: Projectile stats!
		Entity projectile = new Entity(m_ProjectileSpawnPosition.x, m_ProjectileSpawnPosition.y, Globals.getTexture(2), projInfo);
		MoveComponent move = (MoveComponent) projectile.getComponent(ComponentType.COMPONENT_MOVE);
		if(move == null) {
			StringUtils.debugPrint("ERROR: Move component null for projectile!");
			return;
		}
		
		projectile.setFriendly(m_Entity.isFriendly());
		
		// Apply some random cone of fire.
		Vector2 velocity = m_ProjectileVelocity;
		float cof = m_WeaponStats.getConeOfFire();
		// percentage of cof from crouching. Maybe weapon based?
		cof = m_Entity.isCrouched() ? cof * Constants.COF_CROUCH_PERCENT : cof;
		float rotation = MathUtils.random(-cof, cof);
		velocity.rotate(rotation);
		
		move.getVelocity().set(velocity);

		// Graphical stuff. Adjust sprite based on variables.
		//RenderComponent render = (RenderComponent) projectile.getComponent(ComponentType.COMPONENT_RENDER);
		//if(render != null) {
		//	render.getSprite().setOrigin(render.getSprite().getWidth()/2, render.getSprite().getHeight()/2);
		//	render.getSprite().setRotation(velocity.angle());
		//}
		
		// Spawn the entity.
		GameScreen.getLevel().getEntityManager().addEntity(projectile, false);
	}
	
	//=========================================================================
	// Ammo and Magazine Size
	//=========================================================================
	
	public int getTotalAmmo() {
		if(isInfiniteAmmo()) return m_CurrentMagSize;
		return m_CurrentAmmoReserve + m_WeaponStats.getMagSize();
	}
	
	public boolean isAmmoFull() {
		return getTotalAmmo() >= m_WeaponStats.getMaxAmmo();
	}
	
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
		
		m_ReloadStartTime = Globals.getGameTime();
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
		if(!m_Entity.isGrounded())
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
		return m_ReloadStartTime >= 0;
	}
	
	/** What percent of the reload have we completed? 
	 * @return Percent from 0 to 1 of our current reload.
	 * */
	public float getReloadPercent() {
		if(!isReloading())
			return 0;
		
		long reloadTime = Globals.getGameTime() - m_ReloadStartTime;
		float percent = (float) reloadTime / m_WeaponStats.getReloadSpeed();
		
		return Math.min(1, percent);
	}
	
	public WeaponInfo getWeaponStats() { return m_WeaponStats; }
	public int getCurrentMagSize() { return m_CurrentMagSize; }
	public int getCurrentAmmoReserve() { return m_CurrentAmmoReserve; }
	
	public void giveAmmo(int ammo) {
		// Can't give ammo because we're full
		if(isAmmoFull()) return;
		int ammoToGive = Math.min(ammo, m_WeaponStats.getMaxAmmo() - getTotalAmmo());
	
		// Don't give negative ammo ever.
		m_CurrentAmmoReserve += Math.max(ammoToGive, 0);
	}
	
	//=========================================================================
	// Helper Methods
	//=========================================================================
	
	public Entity getOwner() { 
		return m_Entity;
	}
	
	private long timeSinceLastFire() {
		return Globals.getGameTime() - m_LastFireTime;
	}
	
	public String getWeaponString() {
		String ammoReserveString = "";
		if(!isInfiniteAmmo())
			ammoReserveString = " / " + Integer.toString(getCurrentAmmoReserve());		
		
		return new String(m_WeaponStats.getName() + ": " + Integer.toString(getCurrentMagSize()) + ammoReserveString);
	}
	
	@Override
	public String toString() {
		return new String(m_WeaponStats.getName());
	}
}