package com.mandr.input;

//import com.mandr.entity.component.ActorStates;
import com.mandr.entity.Entity;
import com.mandr.entity.component.ComponentType;
import com.mandr.entity.component.JumpComponent;
import com.mandr.entity.component.MoveComponent;

public class JumpCommand extends Command {
	
	public JumpCommand(InputHandler input) {
		super(input);
	}
	
	@Override
	public void execute(InputTrigger keyTrigger, Entity entity) {
		MoveComponent move = (MoveComponent) entity.getComponent(ComponentType.COMPONENT_MOVE);
		if(move == null) return;
		JumpComponent jump = (JumpComponent) entity.getComponent(ComponentType.COMPONENT_JUMP);
		if(jump == null) return;
		
		switch(keyTrigger) {
		case INPUT_JUST_PRESSED:
			jump.jump();
			break;
		case INPUT_JUST_RELEASED:
			jump.release();
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
