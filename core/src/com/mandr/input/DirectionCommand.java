package com.mandr.input;

import com.mandr.entity.Entity;
import com.mandr.entity.component.ComponentType;
import com.mandr.entity.component.CrouchComponent;
import com.mandr.entity.component.LadderComponent;
import com.mandr.entity.component.MoveComponent;
import com.mandr.enums.InputTrigger;
import com.mandr.util.Directions;

public class DirectionCommand extends Command {
	private Directions m_Direction;
	
	public DirectionCommand(InputHandler input, Directions moveDirection) {
		super(input);
		this.m_Direction = moveDirection;
	}
	
	@Override
	public void execute(InputTrigger keyTrigger, Entity entity) {
		MoveComponent move = (MoveComponent) entity.getComponent(ComponentType.COMPONENT_MOVE);
		if(move == null) return;
		
		LadderComponent ladder = (LadderComponent) entity.getComponent(ComponentType.COMPONENT_LADDER);		
		CrouchComponent crouch = (CrouchComponent) entity.getComponent(ComponentType.COMPONENT_CROUCH);
		
		switch(keyTrigger) {
		case INPUT_JUST_PRESSED:
			if(m_Direction == Directions.DIRECTION_LEFT || m_Direction == Directions.DIRECTION_RIGHT) {
				if(ladder != null) ladder.detachLadder();
			}
			if(m_Direction == Directions.DIRECTION_UP || m_Direction == Directions.DIRECTION_DOWN) {
				// Try to attach to a ladder
				if(ladder != null) {
					if(ladder.attachLadder(m_Direction)) {
						//move.setMoveDirectionX(Directions.NO_DIRECTION);	// TODO: wtf is this?
					}
				}
			}
		case INPUT_STILL_PRESSED:
			if(m_Direction == Directions.DIRECTION_LEFT || m_Direction == Directions.DIRECTION_RIGHT) {
				startMoveX(entity, m_Direction);
			}
			if(m_Direction == Directions.DIRECTION_DOWN || m_Direction == Directions.DIRECTION_UP) {
				if(ladder != null) ladder.climbLadder(m_Direction);
				if(m_Direction == Directions.DIRECTION_DOWN) {
					if(crouch != null) crouch.crouch();
				}
			}
			break;
		case INPUT_JUST_RELEASED:
			if(m_Direction == Directions.DIRECTION_LEFT || m_Direction == Directions.DIRECTION_RIGHT) {
				stopMoveX(entity, m_Direction);
			}
			else {
				if(ladder != null) ladder.climbLadder(Directions.NO_DIRECTION);
				if(crouch != null) crouch.uncrouch();
			}
		case INPUT_STILL_RELEASED:
			break;
		}
	}
	
	// Set the entity's velocity in the specified direction
	private void startMoveX(Entity entity, Directions direction) {
		MoveComponent move = (MoveComponent) entity.getComponent(ComponentType.COMPONENT_MOVE);
		if(move == null) return;
		
		float speed = move.getSpeed();
		move.getVelocity().x = direction == Directions.DIRECTION_RIGHT ? speed : -speed;
	}
	
	// Stop entity's movement in direction if he's already moving in that direction
	private void stopMoveX(Entity entity, Directions direction) {
		MoveComponent move = (MoveComponent) entity.getComponent(ComponentType.COMPONENT_MOVE);
		if(move == null) return;
		
		if(move.getMoveDirectionX() == direction)
			move.getVelocity().x = 0;
	}
	
	@Override
	public String toString() {
		// TODO localize
		return "Move " + m_Direction;
	}
}
