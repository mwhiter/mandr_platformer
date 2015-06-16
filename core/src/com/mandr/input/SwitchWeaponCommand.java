package com.mandr.input;

import com.mandr.entity.Entity;
import com.mandr.entity.component.ComponentType;
import com.mandr.entity.component.WeaponComponent;
import com.mandr.enums.InputTrigger;

public class SwitchWeaponCommand extends Command {
	private int m_WeaponSlot;
	
	public SwitchWeaponCommand(InputHandler input, int slot) {
		super(input);
		m_WeaponSlot = slot;
	}

	@Override
	public void execute(InputTrigger keyType, Entity entity) {
		if(keyType != InputTrigger.INPUT_JUST_PRESSED) return;
		if(entity == null) return;
		
		WeaponComponent control = (WeaponComponent) entity.getComponent(ComponentType.COMPONENT_WEAPON);
		if(control == null) return;
		
		// Switch to weapon if we have it.
		if(control.getWeapon(m_WeaponSlot) != null)
			control.setActiveWeapon(m_WeaponSlot);
	}

	@Override
	public String toString() {
		// slot+1 because counting from 1,2,3... is more human-understandable than 0,1,2...
		return new String("Switch to weapon " + (m_WeaponSlot+1));
	}

}
