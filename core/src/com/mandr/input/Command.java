package com.mandr.input;

import com.mandr.entity.Entity;
import com.mandr.enums.InputTrigger;

public abstract class Command {
	protected InputHandler input;
	protected boolean validDuringPause;
	
	public Command(InputHandler input) {
		this.input = input;
		validDuringPause = false;
	}
	
	abstract public void execute(InputTrigger keyType, Entity entity);
	public abstract String toString();
	
	public boolean validDuringPause() {
		return validDuringPause;
	}
}