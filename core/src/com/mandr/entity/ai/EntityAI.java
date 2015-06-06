package com.mandr.entity.ai;

import com.mandr.entity.Actor;

public abstract class EntityAI {
	public Actor actor;
	public int speedLevel;
	
	EntityAI(Actor actor) {
		this.actor = actor;
		this.speedLevel = 0;
	}
	
	public abstract void update(float deltaTime);
	
	public void setSpeedLevel(int speed) {
		this.speedLevel = speed;
	}
	public int getSpeedLevel() {
		return speedLevel;
	}
}