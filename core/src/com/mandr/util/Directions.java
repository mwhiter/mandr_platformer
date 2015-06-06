package com.mandr.util;

public enum Directions {
	NO_DIRECTION,
	DIRECTION_LEFT,
	DIRECTION_RIGHT,
	DIRECTION_UP,
	DIRECTION_DOWN,
	DIRECTION_LEFT_UP,
	DIRECTION_RIGHT_UP,
	DIRECTION_LEFT_DOWN,
	DIRECTION_RIGHT_DOWN;
	
	public static Directions getOpposite(Directions direction) {
		switch(direction) {
		case DIRECTION_LEFT: 	return Directions.DIRECTION_RIGHT;
		case DIRECTION_RIGHT: 	return Directions.DIRECTION_LEFT;
		case DIRECTION_UP: 		return Directions.DIRECTION_DOWN;
		case DIRECTION_DOWN: 	return Directions.DIRECTION_UP;
		case DIRECTION_LEFT_UP: 	return Directions.DIRECTION_RIGHT_DOWN;
		case DIRECTION_RIGHT_UP: 	return Directions.DIRECTION_LEFT_DOWN;
		case DIRECTION_LEFT_DOWN: 	return Directions.DIRECTION_RIGHT_UP;
		case DIRECTION_RIGHT_DOWN: 	return Directions.DIRECTION_LEFT_UP;
		default: return Directions.NO_DIRECTION;
		}
	}
	
	public boolean isOpposite(Directions direction) {
		return this.equals(getOpposite(direction));
	}
}
