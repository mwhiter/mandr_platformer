package com.mandr.physics;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mandr.entity.DynamicEntity;
import com.mandr.util.AABB;

public class Quadtree {
	private final int MAX_TREE_CAPACITY = 6;
	private final int MAX_TREE_LEVELS = 4;
	
	private int level;
	private Rectangle bounds;
	private Quadtree[] children;
	ArrayList<DynamicEntity> entities;
	
	/** Constructs a Quadtree object.
	 * @param int level: The level of the quadtree (always start at 0!) 
	 * @param Rectangle bounds: The bounds of the rectangle */
	public Quadtree(int level, Rectangle bounds) {
		this.level = level;
		this.entities = new ArrayList<DynamicEntity>();
		this.bounds = bounds;
		this.children = new Quadtree[4];
	}
	
	public void draw(ShapeRenderer render) {
		if(children[0] != null) {
			for(Quadtree child : children) {
				child.draw(render);
			}
		}
		//System.out.println(bounds.getX() + " " + bounds.getY() + " " + bounds.getWidth() + " " + bounds.getHeight());
		render.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}
	
	/** Clears the Quadtree recursively. */
	public void clear() {
		entities.clear();
		for(int i = 0; i < children.length; i++) {
			if(children[i] != null) {
				children[i].clear();
				children[i] = null;
			}
		}
	}
	
	public void setBounds(Rectangle newBounds) {
		bounds.set(newBounds);
	}
	
	/** Splits the Quadtree into four equal corners */
	private void split() {
		float x = bounds.getX();
		float y = bounds.getY();
		float sWidth = (bounds.getWidth()/2);
		float sHeight = (bounds.getHeight()/2);
		int newLevel = level+1;
		
		// Bottom-left
		children[0] = new Quadtree(newLevel, new Rectangle(x,y,sWidth,sHeight));
		// Bottom-right
		children[1] = new Quadtree(newLevel, new Rectangle(x+sWidth,y,sWidth,sHeight));
		// Top-left
		children[2] = new Quadtree(newLevel, new Rectangle(x,y+sHeight,sWidth,sHeight));
		// Top-right
		children[3] = new Quadtree(newLevel, new Rectangle(x+sWidth,y+sHeight,sWidth,sHeight));
	}
	
	/** Determine the child which an object's AABB belongs to.
	 * @param DynamicEntity ent: the entity to add to the quadtree */
	private int getIndex(DynamicEntity ent) {
		
		// Entity bounding box is his before and after
		AABB entBoundingBox = AABB.enclosingBox(new AABB(ent.getPositionBeforeMovement(), ent.getSize()), ent.getBoundingBox());

		int horizontalMidpoint = (int)(bounds.getX() + bounds.getWidth()/2);
		int verticalMidpoint = (int)(bounds.getY() + bounds.getHeight()/2);
		
		boolean left 	= entBoundingBox.max.x < horizontalMidpoint;
		boolean right 	= entBoundingBox.min.x > horizontalMidpoint;
		boolean bottom 	= entBoundingBox.max.y < verticalMidpoint;
		boolean top 	= entBoundingBox.min.y > verticalMidpoint;
		
		if(left) {
			if(bottom) return 0;	// In bottom-left
			if(top) return 2;		// In top-left
		}
		else if(right) {
			if(bottom) return 1;	// In bottom-right
			if(top) return 3;		// In top-right
		}
		
		return -1;
	}
	
	/** Insert an entity into a quadtree
	 * @param DynamicEntity ent: The entity to insert
	 * */
	public void insert(DynamicEntity ent) {
		// If we have children
		if(children[0] != null) {
			int index = getIndex(ent);
			// If we fit perfectly into a node, then insert into the child (otherwise we will just insert into the parent)
			if(index != -1) {
				children[index].insert(ent);
				return;
			}
		}
		
		// Add the entity into this quadtree
		entities.add(ent);
		
		// If size is greater than capacity and we're not at the max level yet
		if(entities.size() > MAX_TREE_CAPACITY && level < MAX_TREE_LEVELS) {
			// Split the quadtree if there are no children
			if(children[0] == null) {
				split();
			}
			
			// Loop through each entity in this node
			int i=0;
			while(i < entities.size()) {
				// Find out which children this entity should go into it
				int index = getIndex(entities.get(i));
				// If it fits perfectly into a child, then insert into the tree
				if(index != -1) {
					children[index].insert(entities.remove(i));
				}
				// If it doesn't fit perfectly into any child, then just skip to next one
				else {
					i++;
				}
			}
		}
	}
	
	/** Return the objects that could collide with this Entity */
	public ArrayList<DynamicEntity> retrieve(ArrayList<DynamicEntity> collidableEntities, DynamicEntity ent) {
		// Get index of the object
		int index = getIndex(ent);
		// If it fits perfectly in a sub-tree and has children, keep retrieving
		if(index != -1 && children[0] != null) {
			children[index].retrieve(collidableEntities, ent);
		}
		// Add every object in this node
		collidableEntities.addAll(entities);
		return collidableEntities;
	}
}
