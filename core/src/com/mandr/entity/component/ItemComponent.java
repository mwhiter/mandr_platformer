package com.mandr.entity.component;

import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.game.Globals;
import com.mandr.info.ItemInfo;
import com.mandr.level.Tile;
import com.mandr.util.StringUtils;

public class ItemComponent extends Component {
	ItemInfo m_Stats;
	
	public ItemComponent(Entity entity, ItemInfo stats) {
		super(entity);
		m_Stats = stats;
	}

	@Override
	public void reset() {}

	@Override
	public ComponentType getType() {
		return ComponentType.COMPONENT_ITEM;
	}

	@Override
	public void update(float deltaTime) {
	}

	@Override
	public void stateChange(EntityState oldState, EntityState newState) {
	}

	@Override
	public void collision(Entity other) {
		if(m_Entity.isFriendly() != other.isFriendly()) return;
		if(m_Stats == null) return;
		
		StringUtils.debugPrint("Picked up item!");
		
		if(m_Stats.getWeaponID() != -1) {
			other.addWeapon(Globals.getWeaponStat(m_Stats.getWeaponID()));
		}
		if(m_Stats.getHealth() != 0) {
			HealthComponent health = (HealthComponent) other.getComponent(ComponentType.COMPONENT_HEALTH);
			if(health != null) {
				health.changeHealth(m_Stats.getHealth());
			}
		}
		
		// Kill items after pickup.
		m_Entity.setDead(true);
	}

	@Override
	public void collision(Tile tile) { }
}
