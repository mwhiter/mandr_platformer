package com.mandr.input;

import com.mandr.entity.Actor;
import com.mandr.weapons.Weapon;
import com.mandr.weapons.Weapon.WeaponType;

public class FireWeaponCommand extends Command {

	public FireWeaponCommand(InputHandler input) {
		super(input);
	}

	@Override
	public void execute(InputTrigger keyType, Actor actor) {
		if(keyType == InputTrigger.INPUT_JUST_PRESSED || keyType == InputTrigger.INPUT_STILL_PRESSED) {				
			// Get the active weapon
			Weapon weapon = actor.getActiveWeapon();
			if(weapon == null)
				return;
			
			// Can only fire full-auto weapons if you're not holding the trigger down
			if(weapon.getWeaponStats().getWeaponType() != WeaponType.WEAPON_TYPE_FULL_AUTO && keyType == InputTrigger.INPUT_STILL_PRESSED)
				return;
			
			weapon.fire();
		}
	}

	@Override
	public String toString() {
		// TODO localize
		return "Fire Weapon";
	}
}