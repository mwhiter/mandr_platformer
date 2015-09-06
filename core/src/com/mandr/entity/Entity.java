package com.mandr.entity;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mandr.entity.component.*;
import com.mandr.enums.EntityState;
import com.mandr.game.screens.GameScreen;
import com.mandr.info.EntityInfo;
import com.mandr.info.WeaponInfo;
import com.mandr.level.Tile;
import com.mandr.util.AABB;
import com.mandr.util.Constants;
import com.mandr.util.Directions;
import com.mandr.util.StringUtils;

/** An entity with components. */
public class Entity {	
	private int m_ComponentList;
	HashMap<ComponentType, Component> m_Components;
	
	private Vector2 m_StartPosition;
	private Vector2 m_EndPosition;
	
	private Vector2 m_Size;
	private EntityState m_State;
	
	private Vector2 m_LookVector;
	
	private boolean m_Dead;
	
	private EntityInfo m_Stats;
	private boolean m_Friendly;
	
	/** Constructs a new Entity with the given parameters
	 * x The x-position
	 * y The y-position
	 * texture The entity's texture
	 * stats The stats of the entity
	 * @throw IllegalArgumentException If stats is null
	 * */
	public Entity(float x, float y, Texture texture, EntityInfo stats) {
		if(stats == null) throw new IllegalArgumentException("Stats cannot be null!");
		
		m_ComponentList = stats.getComponents();
		m_Components = new HashMap<ComponentType, Component>();
		
		m_StartPosition = new Vector2(x,y);
		m_EndPosition = new Vector2(x,y);
		m_Size = new Vector2(stats.getSizeX(), stats.getSizeY());
		m_State = EntityState.NO_ENTITY_STATE;
		
		m_Friendly = stats.isFriendly();
		m_LookVector = new Vector2(1,0);
	
		m_Dead = false;
		m_Stats= stats;
		
		// todo: animation should be loaded separately
		if(hasComponent(ComponentType.COMPONENT_RENDER))	m_Components.put(ComponentType.COMPONENT_RENDER, new RenderComponent(this, "test_player.xml"));
		
		// The following components need to have a move controller to function
		if(hasComponent(ComponentType.COMPONENT_MOVE)) {
			// Put the move component
			m_Components.put(ComponentType.COMPONENT_MOVE, new MoveComponent(this, stats.getMoveSpeed()));
			
			if(hasComponent(ComponentType.COMPONENT_CROUCH))	m_Components.put(ComponentType.COMPONENT_CROUCH, new CrouchComponent(this, stats.getCrouchSpeed()));
			if(hasComponent(ComponentType.COMPONENT_JUMP)) {
				m_Components.put(ComponentType.COMPONENT_JUMP, 	new JumpComponent(this, stats.getJumpSpeed()));
			}
			if(hasComponent(ComponentType.COMPONENT_LADDER))	m_Components.put(ComponentType.COMPONENT_LADDER, new LadderComponent(this, stats.getClimbSpeed()));
		}
		
		if(hasComponent(ComponentType.COMPONENT_WEAPON)) 		m_Components.put(ComponentType.COMPONENT_WEAPON, new WeaponComponent(this));
		if(hasComponent(ComponentType.COMPONENT_HEALTH)) 		m_Components.put(ComponentType.COMPONENT_HEALTH, new HealthComponent(this, stats.getMaxHealth()));
		if(hasComponent(ComponentType.COMPONENT_PROJECTILE))	m_Components.put(ComponentType.COMPONENT_PROJECTILE, new ProjectileComponent(this));
		if(hasComponent(ComponentType.COMPONENT_ITEM)) 			m_Components.put(ComponentType.COMPONENT_ITEM, new ItemComponent(this, stats.getItemInfo()));
	}

	/** Reset the entity and all its components. */
	public void reset() {
		Iterator<HashMap.Entry<ComponentType, Component>> it = m_Components.entrySet().iterator();
		while(it.hasNext()) {
			HashMap.Entry<ComponentType, Component> pair = (HashMap.Entry<ComponentType, Component>) it.next();
			if(hasComponent((ComponentType) pair.getKey())) {
				((Component) pair.getValue()).reset();
			}
		}
	}
	
	/** Update the entity */
	public void update(float deltaTime) {
		
		//StringUtils.debugPrint(hasComponent(ComponentType.COMPONENT_ITEM) + " " + m_StartPosition);
		
		m_StartPosition.set(m_EndPosition);
		
		updateComponent(ComponentType.COMPONENT_CROUCH, deltaTime);
		updateComponent(ComponentType.COMPONENT_JUMP, 	deltaTime);
		updateComponent(ComponentType.COMPONENT_LADDER, deltaTime);
		updateComponent(ComponentType.COMPONENT_MOVE, 	deltaTime);
		updateComponent(ComponentType.COMPONENT_WEAPON, deltaTime);
		
		//StringUtils.debugPrint(hasComponent(ComponentType.COMPONENT_ITEM) + " " + m_EndPosition);
		validate();
	}
	
	public void draw(SpriteBatch batch) {
		if(hasComponent(ComponentType.COMPONENT_RENDER))
			((RenderComponent)m_Components.get(ComponentType.COMPONENT_RENDER)).draw(batch);
	}
	
	public void draw(ShapeRenderer render) {
		if(hasComponent(ComponentType.COMPONENT_RENDER))
			((RenderComponent)m_Components.get(ComponentType.COMPONENT_RENDER)).draw(render);
	}
	
	/** Validate the position so we can never go out of bounds. */
	private void validate() {
		float leftX = GameScreen.getLevel().getLevelBoundaryX();
		float bottomY = GameScreen.getLevel().getLevelBoundaryY();
		
		if(!m_Stats.isIgnoreScreenBounds()) {
			m_EndPosition.x = Math.max(leftX,  m_EndPosition.x);
			m_EndPosition.x = Math.min(m_EndPosition.x, GameScreen.getLevel().getWidth() - m_Size.x);			
		}
		
		// Reached the bottom of the level - always kill
		if(m_EndPosition.y < bottomY) {
			setDead(true);
		}
		// If the entity dies off screen, then kill once it has passed screen boundaries
		else if(m_Stats.isDieOffScreen()) {
			// TODO: magic number
			if(!isOnScreen(0.1f)) {
				setDead(true);
			}
		}
	}
	
	/** Is the entity on the screen?
	 * @param how much extra padding to give to screen bounds. (Ex: 0.25 would mean extend the bounds by 25%)
	 * @return whether or not the entity is visible on the screen */
	public boolean isOnScreen(float percentPadding) {	
		Rectangle screenBounds = GameScreen.getRenderer().getCameraBounds();
		Rectangle entityBounds = new Rectangle(getEndPosition().x, getEndPosition().y, getSize().x, getSize().y);
		
		float padX = screenBounds.getWidth() * percentPadding;
		float padY = screenBounds.getHeight() * percentPadding;
		
		screenBounds.x = screenBounds.x - padX/2;
		screenBounds.y = screenBounds.y - padY/2;
		screenBounds.width = screenBounds.width + padX;
		screenBounds.height = screenBounds.height + padY;
		
		return screenBounds.overlaps(entityBounds);
		
	}
	
	// Force the entity's position at (x,y)
	public void setPosition(float x, float y) {
		m_StartPosition.set(x,y);
		m_EndPosition.set(x,y);
	}
	
	/** Get entity's old position. 
	 * @return position of the entity */
	public Vector2 getStartPosition() {
		return m_StartPosition;
	}
	
	/** Get entity's current position. 
	 * @return position of the entity */
	public Vector2 getEndPosition() {
		return m_EndPosition;
	}
	
	/** Get entity's size. 
	 * @return size of the entity */
	public Vector2 getSize() {
		return m_Size;
	}
	
	public void setDead(boolean value) {
		m_Dead = value;
	}
	
	public boolean isDead() {
		return m_Dead;
	}
	
	public EntityInfo getStats() {
		return m_Stats;
	}
	
	//=========================================================================
	// Components
	//=========================================================================
	
	private void updateComponent(ComponentType componentType, float deltaTime) {
		Component component = getComponent(componentType);
		if(component != null) {
			component.update(deltaTime);
		}
	}
	
	/** Returns if entity has a component. */
	public boolean hasComponent(ComponentType component) {
		return ((m_ComponentList & component.getFlag()) == component.getFlag());
	}
	
	/** Return entity's current state.
	 * @return current state of entity. */
	public EntityState getState() {
		return m_State;
	}
	
	/** Change the actor's state, if different than current state.*/
	public void changeState(EntityState newState) {
		
		EntityState oldState = m_State;
		if(oldState != newState) {
			// TODO: little debug thing to see which function called it
			StringUtils.debugPrint("Changing state from " + oldState + " to " + newState + " " + Thread.currentThread().getStackTrace()[2]);
			
			m_State = newState;

			// Notify all components of state change
			Iterator<HashMap.Entry<ComponentType, Component>> it = m_Components.entrySet().iterator();
			while(it.hasNext()) {
				HashMap.Entry<ComponentType, Component> pair = (HashMap.Entry<ComponentType, Component>) it.next();
				if(hasComponent((ComponentType) pair.getKey())) {
					((Component) pair.getValue()).stateChange(oldState, newState);
				}
			}
		}
	}
	
	/** Returns the entity's component if he has it. */
	public Component getComponent(ComponentType componentType) {
		return m_Components.get(componentType);
	}
	
	//=========================================================================
	// Bound methods
	//=========================================================================
	
	/** Get the entity's starting bounding box (before any movement).
	 * @return The bounds of the entity */
	public AABB getStartBoundingBox() {
		return new AABB(m_StartPosition.x, m_StartPosition.y, m_Size.x, m_Size.y);
	}
	
	/** Get the entity's starting bounding box (after any movement).
	 * @return The bounds of the entity */
	public AABB getEndBoundingBox() {
		return new AABB(m_EndPosition.x, m_EndPosition.y, m_Size.x, m_Size.y);
	}
	
	//=========================================================================
	// Looking
	//=========================================================================
	
	/** Updates the entity to look at (x,y). Use through the input handler.
	 * @param x The x-coordinate to look at.
	 * @param y The y-coordinate to look at. */
	public void updateLook(int x, int y) {
		// TODO: Maybe I want to have multiple player support. This certainly WILL NOT WORK
		// Unproject the mouse coordinates to screen space
		Vector3 mouseCoords = GameScreen.getRenderer().getCamera().unproject(new Vector3(x, y, 0f));
		
		// The player's center coordinates
		float centerX = m_StartPosition.x + m_Size.x/2;
		float centerY = m_StartPosition.y + m_Size.y/2;
		
		Vector2 newLookVector = new Vector2(mouseCoords.x - centerX, mouseCoords.y - centerY);
		
		float angle = newLookVector.angle();
		
		// To prevent accidental stupidity, the player can never look more than 45 degrees away from down.
		float noLookdownAngle = Math.min(Constants.INPUT_NO_LOOKDOWN_ANGLE, 45);
		
		float min = 270 - noLookdownAngle;
		float max = 270 + noLookdownAngle;
		
		// Normalize vector because we only want the direction!
		newLookVector.nor();
		
		// If the vector angle is in this range
		if(angle > min && angle < max) {
			// LibGDX does all this for us, but all you have to do is say x = cos(x), y = sin(x), since it's normalized.
			
			// set to the closer sit
			float newAngle = (max - angle <= angle - min) ? max : min;
			newLookVector.setAngle(newAngle);
		}
		
		setLookAt(newLookVector);
	}
	
	public Vector2 getLookVector() {
		return m_LookVector;
	}
	
	public void setLookAt(Vector2 vector) {
		m_LookVector = vector;
	}
	
	public Directions getLookDirectionX() {
		return (m_LookVector.angle() > 90 && m_LookVector.angle() <= 270 ? Directions.DIRECTION_LEFT : Directions.DIRECTION_RIGHT);
	}
	
	//=========================================================================
	// Helper methods
	//=========================================================================
	
	public void addWeapon(WeaponInfo stats) {
		if(!hasComponent(ComponentType.COMPONENT_WEAPON)) return;
		((WeaponComponent)getComponent(ComponentType.COMPONENT_WEAPON)).addWeapon(stats);
	}
	
	public boolean isGrounded() {
		if(!hasComponent(ComponentType.COMPONENT_MOVE)) return false;
		return ((MoveComponent)getComponent(ComponentType.COMPONENT_MOVE)).isGrounded();
	}
	
	/** Is the entity on a ladder?
	 * @return Whether the entity is on a ladder. */
	public boolean isOnLadder() {
		return m_State == EntityState.ENTITY_STATE_LADDER;
	}
	
	/** Is the entity crouch?
	 * @return Whether the entity is crouched. */
	public boolean isCrouched() {
		return m_State == EntityState.ENTITY_STATE_CROUCH;
	}
	
	/** Is the entity jumping?
	 * @return Whether the entity is jumping. */
	public boolean isJumping() {
		return m_State == EntityState.ENTITY_STATE_JUMP;
	}
	
	/** Is the entity attached to a wall?
	 * @return Whether the entity is attached to a wall. */
	public boolean isAttachedToWall() {
		return m_State == EntityState.ENTITY_STATE_WALL_ATTACH;
	}
	
	/** Helper function to determine whether or not entity collides with the world.
	 * @return whether or not entity collides with world. */
	public static boolean collideWorld(AABB box) {
		Tile loopTile = null;
		for(int x = box.getLeftXIndex(); x < box.getRightXIndex(); x++) {
			for(int y = box.getBottomYIndex(); y < box.getTopYIndex(); y++) {
				loopTile = GameScreen.getLevel().getTile(x, y);
				if(loopTile == null) continue;
				if(loopTile.shouldCollide(box, false)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean collide(Entity a, Entity b) {
		return AABB.collide(a.getEndBoundingBox(), b.getEndBoundingBox());
	}

	public void notifyCollide(Tile tile) {
		// Notify all components of collision
		Iterator<HashMap.Entry<ComponentType, Component>> it = m_Components.entrySet().iterator();
		while(it.hasNext()) {
			HashMap.Entry<ComponentType, Component> pair = (HashMap.Entry<ComponentType, Component>) it.next();
			ComponentType componentType = pair.getKey();
			if(hasComponent(componentType)) {
				Component component = (Component) pair.getValue();
				component.collision(tile);
			}
		}
	}

	public void notifyCollide(Entity ent) {
		// Notify all components of collision
		Iterator<HashMap.Entry<ComponentType, Component>> it = m_Components.entrySet().iterator();
		while(it.hasNext()) {
			HashMap.Entry<ComponentType, Component> pair = (HashMap.Entry<ComponentType, Component>) it.next();
			ComponentType componentType = pair.getKey();
			if(hasComponent(componentType)) {
				Component component = (Component) pair.getValue();
				component.collision(ent);
			}
		}
	}

	public boolean isFriendly() {
		return m_Friendly;
	}
	
	public void setFriendly(boolean friendly) {
		m_Friendly = friendly;
	}
}
