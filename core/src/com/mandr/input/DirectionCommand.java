package com.mandr.input;

import com.mandr.entity.Actor;
import com.mandr.util.Directions;

public class DirectionCommand extends Command {
	private Directions m_Direction;
	
	public DirectionCommand(InputHandler input, Directions moveDirection) {
		super(input);
		this.m_Direction = moveDirection;
	}
	
	@Override
	public void execute(InputTrigger keyTrigger, Actor actor) {
		switch(keyTrigger) {
		case INPUT_JUST_PRESSED:
			if(m_Direction == Directions.DIRECTION_LEFT || m_Direction == Directions.DIRECTION_RIGHT) {
				//actor.lookX(m_Direction);
				actor.detachLadder();
				actor.setMoveDirectionX(m_Direction);
			}
			if(m_Direction == Directions.DIRECTION_UP || m_Direction == Directions.DIRECTION_DOWN) {				
				//actor.lookY(m_Direction);
				// Try to attach to a ladder
				if(actor.attachLadder(m_Direction)) {
					actor.setMoveDirectionX(Directions.NO_DIRECTION);
				}
				else {
					actor.climbLadder(m_Direction);
				}
			}
		case INPUT_STILL_PRESSED:
			if(m_Direction == Directions.DIRECTION_DOWN) {
				actor.crouch();
			}
			break;
		case INPUT_JUST_RELEASED:
			if(m_Direction == Directions.DIRECTION_LEFT || m_Direction == Directions.DIRECTION_RIGHT) {
				//actor.stopLookX(m_Direction);
				actor.stopMoveDirectionX(m_Direction);
			}
			else {
				//actor.stopLookY(m_Direction);
				actor.climbLadder(Directions.NO_DIRECTION);
				actor.uncrouch();
			}
		case INPUT_STILL_RELEASED:
			break;
		}
	}
	
	@Override
	public String toString() {
		// TODO localize
		return "Move " + m_Direction;
	}
}
