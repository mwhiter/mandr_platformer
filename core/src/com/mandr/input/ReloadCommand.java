package com.mandr.input;

import com.mandr.entity.Actor;
import com.mandr.weapons.Weapon;

public class ReloadCommand extends Command {

	public ReloadCommand(InputHandler input) {
		super(input);
	}

	@Override
	public void execute(InputTrigger keyType, Actor actor) {
		if(keyType == InputTrigger.INPUT_JUST_PRESSED) {	
			Weapon weapon = actor.getActiveWeapon();
			if(weapon == null)
				return;
			
			weapon.reload();
		}
	}

	@Override
	public String toString() {
		// TODO localize
		return "Reload";
	}	
}
