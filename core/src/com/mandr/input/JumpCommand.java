package com.mandr.input;

import com.mandr.entity.Actor;
import com.mandr.entity.Player;
import com.mandr.util.Directions;

public class JumpCommand extends Command {
	
	public JumpCommand(InputHandler input) {
		super(input);
	}
	
	@Override
	public void execute(InputTrigger keyTrigger, Actor actor) {
		switch(keyTrigger) {
		case INPUT_JUST_PRESSED:
			if(actor.isOnLadder()) {
				actor.detachLadder();
				if(actor.getMoveDirectionX() == Directions.NO_DIRECTION) {
					return;
				}
			}
			
			// The player will have special rules for jumping
			if(actor instanceof Player) {
				((Player) actor).playerJump();
			}
			break;
		case INPUT_JUST_RELEASED:
			// The player can release a jump to jump a shorter height
			if(actor instanceof Player) {
				((Player) actor).releaseJump();
			}
			break;
			
		// Does nothing
		case INPUT_STILL_PRESSED:
		case INPUT_STILL_RELEASED:
			break;
		}
	}
	
	@Override
	public String toString() {
		// TODO localize
		return "Jump";
	}
}
