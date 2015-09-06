package com.mandr.level;

import com.badlogic.gdx.math.Vector2;
import com.mandr.entity.Entity;

public class Checkpoint {
	private Entity entity;
	private Vector2 position;
	private PlayerState state;
	
	public Checkpoint(Entity ent, Vector2 pos, PlayerState state) {
		entity = ent;
		position = pos;
		this.state = state;
	}
	
	public void load() {
		entity.reset();
		entity.setPosition(position.x, position.y);
		PlayerState.give(entity, state);
	}
}
