package com.mandr.input;

import com.mandr.entity.Entity;
import com.mandr.entity.component.ComponentType;
import com.mandr.entity.component.WeaponComponent;
import com.mandr.weapons.Weapon;

public class ReloadCommand extends Command {

	public ReloadCommand(InputHandler input) {
		super(input);
	}

	@Override
	public void execute(InputTrigger keyType, Entity entity) {
		if(keyType == InputTrigger.INPUT_JUST_PRESSED) {	
			WeaponComponent weapon = (WeaponComponent) entity.getComponent(ComponentType.COMPONENT_WEAPON);
			if(weapon == null) return;
			Weapon activeWeapon = weapon.getActiveWeapon();
			if(activeWeapon == null)
				return;
			
			activeWeapon.reload();
		}
	}

	@Override
	public String toString() {
		// TODO localize
		return "Reload";
	}	
}
