package com.mandr.input;

import com.mandr.entity.Entity;
import com.mandr.entity.component.ComponentType;
import com.mandr.entity.component.WeaponComponent;
import com.mandr.enums.InputTrigger;
import com.mandr.weapons.Weapon;
import com.mandr.weapons.Weapon.WeaponType;

public class FireWeaponCommand extends Command {

	public FireWeaponCommand(InputHandler input) {
		super(input);
	}

	@Override
	public void execute(InputTrigger keyType, Entity entity) {
		if(keyType == InputTrigger.INPUT_JUST_PRESSED || keyType == InputTrigger.INPUT_STILL_PRESSED) {
			WeaponComponent weapon = (WeaponComponent) entity.getComponent(ComponentType.COMPONENT_WEAPON);
			if(weapon == null) return;
			Weapon activeWeapon = weapon.getActiveWeapon();
			if(activeWeapon == null)
				return;
			
			// Can only fire full-auto weapons if you're not holding the trigger down
			if(activeWeapon.getWeaponStats().getWeaponType() != WeaponType.WEAPON_TYPE_FULL_AUTO && keyType == InputTrigger.INPUT_STILL_PRESSED)
				return;
			
			activeWeapon.fire();
		}
	}

	@Override
	public String toString() {
		// TODO localize
		return "Fire Weapon";
	}
}