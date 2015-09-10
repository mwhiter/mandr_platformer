package com.mandr.entity.component;

// TODO I don't know if this is the correct thing to do. My thought process is that we need a way to send messages from components to another
// TODO This should be separate from entity state.
// TODO Components will be able to accept or send messages to specific or all components
public enum ComponentMessage {
	NO_MESSAGE,
	MESSAGE_MOVE_LEFT,
	MESSAGE_MOVE_RIGHT,
	MESSAGE_MOVE_STOP;
	
	public static final int MAX_COMPONENT_MESSAGES = 4;
}
