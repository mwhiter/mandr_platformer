package com.mandr.graphics.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mandr.entity.Entity;
import com.mandr.entity.component.*;
import com.mandr.game.GameGlobals;
import com.mandr.game.screens.GameScreen;
import com.mandr.level.Tile;
import com.mandr.util.StringUtils;
import com.mandr.weapons.Weapon;

public class GameHUD {
	private BitmapFont m_Font;

	// TODO: Don't know the best way to do this, so for now, just do it like this
	private Sprite m_HealthBarBorder;
	private Sprite m_HealthBar;
	
	public GameHUD() {
		
		m_Font = new BitmapFont();
		m_Font.setColor(Color.WHITE);
		
		m_HealthBarBorder = new Sprite(new Texture("resources/ui/health_bar_border.png"));
		m_HealthBar = new Sprite(new Texture("resources/ui/health_bar.png"));
	}
	
	public void drawDebugHUD(SpriteBatch batch) {
		Entity entity = GameScreen.getRenderer().getTrackedEntity();
		if(entity == null)
			return;
		
		Vector2 position = entity.getStartPosition();
		Vector2 lookVector = entity.getLookVector();
		Vector2 velocity = Vector2.Zero;
		Tile groundTile = null;
		Tile ladderTile = null;
		MoveComponent move = (MoveComponent) entity.getComponent(ComponentType.COMPONENT_MOVE);
		LadderComponent ladder = (LadderComponent) entity.getComponent(ComponentType.COMPONENT_LADDER);
		WeaponComponent weapon = (WeaponComponent) entity.getComponent(ComponentType.COMPONENT_WEAPON);
		Weapon activeWeapon = null;
		
		if(move != null) {
			velocity = move.getVelocity();
			groundTile = move.getGroundTile();
		}
		
		if(ladder != null) {
			ladderTile = ladder.getLadderTile();
		}
		
		if(weapon != null) {
			activeWeapon = weapon.getActiveWeapon();
		}
		
		String pos = "Pos: [" + trim(Float.toString(position.x), 6) + " " + trim(Float.toString(position.y), 6) + "]";
		String vel = "Vel: [" + trim(Float.toString(velocity.x), 6) + " " + trim(Float.toString(velocity.y), 6) + "]";
		String fps = "FPS: " + Integer.toString(Gdx.graphics.getFramesPerSecond());
		String ground = "Grounded: " + Boolean.toString(groundTile != null);
		if(groundTile != null)
			ground = ground + " (" + groundTile.getTileType() + ")";
		String ladderStr = "Ladder Tile: " + ladderTile; 
		String state = "State: " + entity.getState();
		String camera = "Camera: " + GameScreen.getRenderer().getCamera().position;

		String look = "Look: " + lookVector.angle();
		String gameEntityCount = "Entity count: " + Integer.toString(GameScreen.getLevel().getEntityManager().getNumActiveEntities()) + "/" + Integer.toString(GameScreen.getLevel().getEntityManager().getNumEntities());
		String gameTime = "Game Time: " + StringUtils.timeString(GameGlobals.getGameTime());
		
		String weaponReload = "";
		
		if(activeWeapon != null) {
			weaponReload = "Reloading: " + (int)(activeWeapon.getReloadPercent() * 100) + "%";
		}
		
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		
		// Game Info
		m_Font.draw(batch, gameTime, 		screenWidth - 350, screenHeight - 20);
		m_Font.draw(batch, fps, 			screenWidth - 350, screenHeight - 40 );
		m_Font.draw(batch, gameEntityCount, screenWidth - 350, screenHeight - 60);
		
		// Camera
		m_Font.draw(batch, camera, 			screenWidth - 350, screenHeight - 80 );
		
		// Entity state
		m_Font.draw(batch, pos, 			screenWidth - 350, screenHeight - 120);
		m_Font.draw(batch, vel, 			screenWidth - 350, screenHeight - 140);
		m_Font.draw(batch, ground, 			screenWidth - 350, screenHeight - 180 );
		m_Font.draw(batch, ladderStr, 		screenWidth - 350, screenHeight - 200 );
		m_Font.draw(batch, state, 			screenWidth - 350, screenHeight - 220 );
		
		// Weapons
		m_Font.draw(batch, look, 	screenWidth - 350, screenHeight - 240);
		m_Font.draw(batch, weaponReload, 	screenWidth - 350, screenHeight - 260);
	}
	
	// TODO: Test function just to see how aiming / weapons look like
	public void drawLookLine(ShapeRenderer shape) {

		Entity entity = GameScreen.getRenderer().getTrackedEntity();
		if(entity == null)
			return;
		
		Vector2 lookVector = entity.getLookVector();
		Vector2 centerPlayer = new Vector2(entity.getEndPosition().x + entity.getSize().x/2, entity.getEndPosition().y + entity.getSize().y/2);
		Vector2 centerToLook = new Vector2(centerPlayer.x + lookVector.x, centerPlayer.y + lookVector.y);
		
		WeaponComponent weapon = (WeaponComponent) entity.getComponent(ComponentType.COMPONENT_WEAPON);
		if(weapon == null) return;

		Weapon activeWeapon = weapon.getActiveWeapon();
		if(activeWeapon == null) return;
		
		shape.setColor(Color.RED);
		shape.rect(activeWeapon.getProjectileSpawnPosition().x - 0.125f,  activeWeapon.getProjectileSpawnPosition().y - 0.125f, 0.25f, 0.25f);
		
		Vector2 centerToProjVelocity = new Vector2(centerPlayer.x + activeWeapon.getProjectileVelocity().x, centerPlayer.y + activeWeapon.getProjectileVelocity().y);
		shape.setColor(Color.BLUE);
		shape.line(centerPlayer, centerToProjVelocity);

		shape.setColor(Color.WHITE);
		shape.line(centerPlayer, centerToLook);
		/*
		Vector2 projLine = new Vector2(lookVector.x * (entity.getSize().x/2), lookVector.y * (entity.getSize().y/2));
		
		// (u*v / u*u) * u
		float scalar = (lookVector.dot(projLine) / lookVector.dot(lookVector));
		Vector2 projection = new Vector2(lookVector.x * scalar, lookVector.y * scalar);
		projection.add(centerPlayer);
		
		// TODO: Weapon Debug
		// TODO: This draws a box where our weapon is going to shoot
		
		shape.setColor(Color.RED);
		shape.rect(projection.x - 0.125f,  projection.y - 0.125f, 0.25f, 0.25f);
		
		
		
		Vector2 projVelocity = new Vector2(lookVector);
		projVelocity.scl(activeWeapon.getWeaponStats().getBulletVelocity());
		float cof = activeWeapon.getWeaponStats().getConeOfFire();
		
		shape.setColor(Color.BLUE);
		Vector2 centerToProjVelocity = new Vector2(centerPlayer.x + projVelocity.x, centerPlayer.y + projVelocity.y);
		shape.line(centerPlayer, centerToProjVelocity.rotate(-cof));
		centerToProjVelocity.rotate(cof);
		shape.line(centerPlayer, centerToProjVelocity);
		shape.line(centerPlayer, centerToProjVelocity.rotate(cof));
		*/
	}
	
	public void draw(SpriteBatch batch) {
		Entity entity = GameScreen.getRenderer().getTrackedEntity();
		if(entity == null)
			return;
		
		HealthComponent health = (HealthComponent) entity.getComponent(ComponentType.COMPONENT_HEALTH);
		WeaponComponent weapon = (WeaponComponent) entity.getComponent(ComponentType.COMPONENT_WEAPON);
		int healthPercent = health.getHealth() * 100 / health.getMaxHealth();

		String weaponInfo = "";
		if(weapon.getActiveWeapon() != null) {
			weaponInfo = weapon.getActiveWeapon().getWeaponString();
		}
		
		m_Font.draw(batch, weaponInfo, 25 + m_HealthBarBorder.getWidth() + 10, Gdx.graphics.getHeight() - 25);
		
		batch.draw(m_HealthBarBorder, 25, Gdx.graphics.getHeight() - 50);
		batch.draw(m_HealthBar, 25, Gdx.graphics.getHeight() - 50, m_HealthBar.getWidth() * healthPercent / 100, m_HealthBar.getHeight());
	}

	private String trim(String s, int size) {
		return s.substring(0, Math.min(s.length(), size));
	}
}
