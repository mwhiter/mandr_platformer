package com.mandr.input;

import com.mandr.entity.Actor;

public class PauseCommand extends Command {

	public PauseCommand(InputHandler input) {
		super(input);
		validDuringPause = true;
	}

	@Override
	public void execute(InputTrigger keyType, Actor actor) {
		if(keyType == InputTrigger.INPUT_JUST_PRESSED) {
			if(input.getGameScreen().isPaused()) {
				input.getGameScreen().resume();
			}
			else {
				input.getGameScreen().pause();
			}
		}
	}

	@Override
	public String toString() {
		// TODO localize
		return "Pause Game";
	}
}
