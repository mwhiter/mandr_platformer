package com.mandr.entity.component;

import com.mandr.entity.Entity;
import com.mandr.enums.EntityState;
import com.mandr.game.Globals;
import com.mandr.info.ItemInfo;
import com.mandr.level.Tile;
import com.mandr.util.StringUtils;

public class ItemComponent extends Component {
	ItemInfo m_Info;
	
	public ItemComponent(Entity entity, ItemInfo stats) {
		super(entity);
		m_Info = stats;
	}

	@Override
	public void reset() {}

	@Override
	public ComponentType getType() {
		return ComponentType.COMPONENT_ITEM;
	}

	@Override
	public void update(float deltaTime) {
		processMessages();
	}

	@Override
	public void receiveMessage(ComponentMessage msg) {

	}
	
	@Override
	public void stateChange(EntityState oldState, EntityState newState) {
	}

	@Override
	public void collision(Entity other) {
		if(m_Info == null) return;
		
		StringUtils.debugPrint("Picked up item!");
		
		if(m_Info.getWeaponID() != -1) {
			other.addWeapon(Globals.getWeaponInfo(m_Info.getWeaponID()));
		}
		if(m_Info.getHealth() != 0) {
			HealthComponent health = (HealthComponent) other.getComponent(ComponentType.COMPONENT_HEALTH);
			if(health != null) {
				health.changeHealth(m_Info.getHealth());
			}
		}
		
		// Kill items after pickup.
		m_Entity.setDead(true);
	}

	@Override
	public void collision(Tile tile) { }
}
