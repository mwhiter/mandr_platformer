package com.mandr.entity.component;

import com.mandr.entity.Entity;
import com.mandr.entity.ItemStats;
import com.mandr.enums.EntityState;
import com.mandr.game.GameGlobals;
import com.mandr.level.Tile;
import com.mandr.util.StringUtils;

public class ItemComponent extends Component {
	ItemStats m_Stats;
	
	public ItemComponent(Entity entity, ItemStats stats) {
		super(entity);
		m_Stats = stats;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.COMPONENT_ITEM;
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stateChange(EntityState oldState, EntityState newState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void collision(Entity other) {
		if(m_Entity.isFriendly() != other.isFriendly()) return;
		if(m_Stats == null) return;
		
		StringUtils.debugPrint("Picked up item!");
		
		if(m_Stats.getWeaponID() != -1) {
			other.addWeapon(GameGlobals.getWeaponStats(m_Stats.getWeaponID()));
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
