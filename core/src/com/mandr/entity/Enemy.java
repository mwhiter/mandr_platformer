package com.mandr.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mandr.entity.ai.AITypes;
import com.mandr.entity.ai.EntityAI;
import com.mandr.entity.ai.WalkingAI;
import com.mandr.game.screens.GameScreen;
import com.mandr.level.Level;

public class Enemy extends Actor {
	private EntityAI ai;
	
	public Enemy(AITypes aiType, Texture texture, float x, float y, float sx, float sy) {
		super(texture, x, y, sx, sy);
		init(aiType);
	}
	
	public Enemy(Level level, AITypes aiType, Texture texture, Vector2 pos, Vector2 size) {
		super(texture, pos, size);
		init(aiType);
	}
	
	private void init(AITypes aiType) {
		switch(aiType) {
			case AI_TYPE_WALKING: ai = new WalkingAI(this);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		ai.update(deltaTime);
		super.update(deltaTime);
	}

	@Override
	public void notifyStateChange(ActorStates oldState, ActorStates newState) {
		// TODO Auto-generated method stub
		
	}
	
	public EntityAI getAI() {
		return ai;
	}

	@Override
	public void handleCollision(DynamicEntity other) {
		if(!other.isFriendly())
			return;
	}

	@Override
	public void damage(int damage) {
		if(!canReceiveDamage()) return;
		changeHealth(-damage);
	}
	
	@Override
	public void kill() {
		GameScreen.getLevel().getEntityManager().removeEntity(this);
	}
}
