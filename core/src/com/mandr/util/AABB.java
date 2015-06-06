package com.mandr.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class AABB {
	public final Vector2 min;
	public final Vector2 max;
	public final Vector2 size;
	
	public AABB(float left, float bottom, float width, float height) {
		this(new Vector2(left,bottom), new Vector2(width,height));
	}
	
	public AABB(Vector2 pos, Vector2 size) {
		this.min = pos;
		this.max = new Vector2(min.x + size.x, min.y + size.y);
		this.size = size;
	}
	
	public static boolean collide(AABB a, AABB b) {
		boolean test = 
				(a.max.x < b.min.x) ||
				(a.min.x > b.max.x) ||
				(a.max.y < b.min.y) ||
				(a.min.y > b.max.y);
		return !test;
	}
	
	public static boolean intersectOrigin(AABB md) {
		boolean collide =
				md.min.x <= 0 &&
				md.max.x >= 0 &&
				md.min.y <= 0 &&
				md.max.y >= 0;
		
		return collide;
	}
	
	public static boolean minkowskiCollide(AABB a, AABB b) {
		AABB md = AABB.minkowskiDifference(a, b);
		boolean collide =
				md.min.x <= 0 &&
				md.max.x >= 0 &&
				md.min.y <= 0 &&
				md.max.y >= 0;
		
		return collide;
	}
	
	public static AABB minkowskiDifference(AABB a, AABB b) {
		float left = a.min.x - b.max.x;
		float top = a.max.y - b.min.y;
		float width = a.size.x + b.size.x;
		float height = a.size.y + b.size.y;
		
		// Minkowski difference gives us left, top, width, height, but our constructor only accepts left, bottom, width, height
		float bottom = top - height;
		
		return new AABB(left, bottom, width, height);
	}
	
	public static AABB translate(AABB box, Vector2 translate) {
		return new AABB(box.min.x + translate.x, box.min.y + translate.y, box.size.x, box.size.y);
	}
	
	@Override
	public String toString() {
		return new String("{" + min + "; " + max + "}");
	}
	
	public void draw(ShapeRenderer shapeRender, Color color) {
		shapeRender.setColor(color);
		shapeRender.rect(min.x, min.y, size.x, size.y);
	}
	
	// Return the AABB box that surrounds the two AABB's
	public static AABB enclosingBox(AABB a, AABB b) {
		AABB returnBox;
		// oldBox is left of newBox
		if(a.min.x < b.min.x) {
			// Going top-right
			if(a.min.y < b.min.y) {
				returnBox = new AABB(a.min.x, a.min.y, b.max.x - a.min.x, b.max.y - a.min.y);
			}
			// Going bottom-right
			else {
				returnBox = new AABB(a.min.x, b.min.y, b.max.x - a.min.x, a.max.y - b.min.y);
			}
		}
		// newBox is left of oldBox
		else {
			// Going top-left
			if(a.min.y < b.min.y) {
				returnBox = new AABB(b.min.x, a.min.y, a.max.x - b.min.x, b.max.y - a.min.y);
			}
			// Going bottom-left
			else {
				returnBox = new AABB(b.min.x, b.min.y, a.max.x - b.min.x, a.max.y - b.min.y);
			}
		}
		return returnBox;
	}
	
	public static AABB enclosingBox(Vector2 a, Vector2 b) {
		AABB returnBox;
		
		// a is left of b 
		if(a.x < b.x) {
			// going top-right
			if(a.y < b.y) {
				returnBox = new AABB(a.x, a.y, b.x - a.x, b.y - a.y);
			}
			// going bottom-right
			else {
				returnBox = new AABB(a.x, b.y, b.x - a.x, a.y - b.y);
			}
		}
		// b is left of a
		else {
			// going top-left
			if(a.y < b.y) {
				returnBox = new AABB(b.x, a.y, a.x - b.x, b.y - a.y);
			}
			// going bottom-right
			else {
				returnBox = new AABB(b.x, a.y, a.x - b.x, a.y - b.y);
			}
		}
		
		return returnBox;
	}
	
	// Helper functions for returning an AABB's indices
	public int getLeftXIndex() { return (int) min.x; }
	public int getBottomYIndex() { return (int) min.y; }
	// subtract 0.00001 for the max because we're not actually in the tile that we'd get if we were directly the axis
	public int getRightXIndex() { return (int) (max.x - 0.00001); }
	public int getTopYIndex() { return (int) (max.y - 0.00001); }
	public int getCenterXIndex() { return (int) getCenterX(); }
	public float getLeftX() { return min.x; }
	public float getRightX() { return max.x; }
	public float getBottomY() { return min.y; }
	public float getTopY() { return max.y; }
	public float getCenterX() { return (min.x + size.x / 2); }
	public float getCenterY() { return (min.y + size.y / 2); }
}
