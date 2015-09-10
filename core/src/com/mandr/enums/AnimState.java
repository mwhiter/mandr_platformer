package com.mandr.enums;

public enum AnimState {
	ANIM_STATE_IDLE("IDLE"),
	ANIM_STATE_MOVE_RIGHT("MOVE_RIGHT"),
	ANIM_STATE_MOVE_LEFT("MOVE_LEFT");
	
	public String name;
	AnimState(String name) {
		this.name = name;
	}
}
