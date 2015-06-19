package com.mandr.entity.component;

import java.util.LinkedList;

import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.info.WeaponInfo;
import com.mandr.level.Tile;
import com.mandr.util.Constants;
import com.mandr.util.StringUtils;
import com.mandr.weapons.Weapon;

public class WeaponComponent extends Component {

	private LinkedList<Weapon> m_Weapons;
	private int m_ActiveWeaponIndex;
	
	public WeaponComponent(Entity entity) {
		super(entity);
		
		m_Weapons = new LinkedList<Weapon>();
		m_ActiveWeaponIndex = -1;
	}

	@Override
	public void update(float deltaTime) {
		if(getActiveWeapon() != null) {
			getActiveWeapon().update();
		}
	}

	@Override
	public void stateChange(EntityState oldState, EntityState newState) {
		// On any change of state besides crouch and nothing, stop reloading.
		if(newState != EntityState.ENTITY_STATE_CROUCH || newState != EntityState.NO_ENTITY_STATE) {
			if(getActiveWeapon() != null)
				getActiveWeapon().stopReload();
		}
	}

	@Override
	public void collision(Entity other) {}

	@Override
	public void collision(Tile tile) {}
	
	@Override
	public ComponentType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	/** Set the active weapon to the index. For example, if the Pistol is in index 0 and we call setActiveWeapon(index), we will set the pistol to be the current weapon. 
	 * @param (int) index: the index to set active
	 * */
	public void setActiveWeapon(int index) {
		if(index < 0 || index >= m_Weapons.size()) return;
		if(index == m_ActiveWeaponIndex) return;
		
		StringUtils.debugPrint("Switching active weapon. TODO: add event.");
		
		m_ActiveWeaponIndex = index;
	}
	
	/** Get the active weapon of the actor. 
	 * @return The active weapon.
	 * */
	public Weapon getActiveWeapon() {
		return getWeapon(m_ActiveWeaponIndex);
	}
	
	public Weapon getWeapon(int index) {
		if(index < 0 || index >= m_Weapons.size()) return null;
		return m_Weapons.get(index);
	}
	
	public boolean canReload() {
		if(m_Entity.getState() != EntityState.ENTITY_STATE_CROUCH && m_Entity.getState() != EntityState.NO_ENTITY_STATE)
			return false;
		if(!((MoveComponent)m_Entity.getComponent(ComponentType.COMPONENT_MOVE)).isGrounded())
			return false;
		
		return true;
	}
	
	public boolean canFireWeapon() {
		if(getActiveWeapon() == null)
			return false;
		
		if(m_Entity.isOnLadder())
			return false;
		
		return true;
	}
	
	public void addWeapon(WeaponInfo stats) {
		if(stats == null) return;
		
		// If a weapon like this already exists, just give it some ammo instead
		for(Weapon weap : m_Weapons) {
			if(weap.getWeaponStats() == stats) {
				StringUtils.debugPrint("Found duplicate weapon. Giving ammo instead.");
				weap.giveAmmo(weap.getWeaponStats().getMagSize());
			}
		}
		
		// Unique weapon, trying to add weapon now...
		// Don't add weapon if we are full
		if(m_Weapons.size() == Constants.MAX_WEAPONS)
			return;
		
		// TODO: Change weapons to use new entities
		Weapon newWeapon = new Weapon(m_Entity, stats);
		m_Weapons.add(newWeapon);
		
		StringUtils.debugPrint("Picked up " + newWeapon + " (TODO: Add weapon pickup event!)");
		
		// If we didn't have an active weapon and we just added a weapon, set this new weapon as the active weapon
		if(getActiveWeapon() == null) {
			setActiveWeapon(m_Weapons.indexOf(newWeapon));
		}
	}
	
	public void removeWeapon(Weapon weapon) {
		m_Weapons.remove(weapon);
	}
	
	public void removeWeapon(int index) {
		m_Weapons.remove(index);
	}
}
