package com.mandr.entity.ai;

import com.mandr.entity.Actor;
import com.mandr.util.Directions;

public class WalkingAI extends EntityAI {
	Directions moveDirection;
	public WalkingAI(Actor actor) {
		super(actor);
		moveDirection = Directions.DIRECTION_LEFT;
		actor.setMoveDirectionX(moveDirection);
	}
	
	public void update(float deltaTime) {
		actor.setMoveSpeed(getSpeedLevel() * 1.5f);
		
		// If we hit a wall and our velocity is in the direction we're trying to move, reverse direction
		if(actor.getCollisionTileX() != null) {
			if((actor.getMoveDirectionX() == Directions.DIRECTION_LEFT && actor.getCollisionTileX().getX() < actor.getPosition().x) ||
					(actor.getMoveDirectionX() == Directions.DIRECTION_RIGHT && actor.getCollisionTileX().getX() > actor.getPosition().x)) {
				moveDirection = Directions.getOpposite(moveDirection);
			}
		}
		actor.setMoveDirectionX(moveDirection);
	}
}
