package com.mandr.graphics.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mandr.entity.Actor;
import com.mandr.game.GameGlobals;
import com.mandr.game.screens.GameScreen;
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
		Actor trackedActor = GameScreen.getRenderer().getTrackedEntity();
		if(trackedActor == null)
			return;
		
		String pos = "Pos: [" + trim(Float.toString(trackedActor.getPosition().x), 6) + " " + trim(Float.toString(trackedActor.getPosition().y), 6) + "]";
		String vel = "Vel: [" + trim(Float.toString(trackedActor.getVelocity().x), 6) + " " + trim(Float.toString(trackedActor.getVelocity().y), 6) + "]";
		String fps = "FPS: " + Integer.toString(Gdx.graphics.getFramesPerSecond());
		String ground = "Grounded: " + Boolean.toString(trackedActor.getGroundTile() != null);
		if(trackedActor.getGroundTile() != null) {
			ground = ground + " (" + trackedActor.getGroundTile().getTileType() + ")";
		}
		String state = "State: " + trackedActor.getState();
		String camera = "Camera: " + GameScreen.getRenderer().getCamera().position;
		String bounds = "Screen Bounds: " + GameScreen.getRenderer().getCameraBounds();

		String lookVector = "Look: " + trackedActor.getLookVector().angle();
		String gameEntityCount = "Entity count: " + Integer.toString(GameScreen.getLevel().getEntityManager().getNumActiveEntities()) + "/" + Integer.toString(GameScreen.getLevel().getEntityManager().getNumEntities());
		String gameTime = "Game Time: " + GameGlobals.getGameTime();
		
		String weaponReload = "";
		Weapon weapon = trackedActor.getActiveWeapon();
		
		if(weapon != null) {
			weaponReload = "Reloading: " + trim(Float.toString(weapon.getReloadPercent() * 100), 6);
		}
		
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		
		m_Font.draw(batch, pos, screenWidth - 250, screenHeight - 40);
		m_Font.draw(batch, vel, screenWidth - 250, screenHeight - 60);
		m_Font.draw(batch, fps, screenWidth - 250, screenHeight - 80 );
		m_Font.draw(batch, ground, screenWidth - 250, screenHeight - 100 );
		m_Font.draw(batch, state, screenWidth - 250, screenHeight - 120 );
		m_Font.draw(batch, camera, screenWidth - 250, screenHeight - 140 );
		m_Font.draw(batch, bounds, screenWidth - 250, screenHeight - 160 );
		
		m_Font.draw(batch, lookVector, screenWidth - 250, screenHeight - 180);
		m_Font.draw(batch, gameTime, screenWidth - 250, screenHeight - 200);
		m_Font.draw(batch, gameEntityCount, screenWidth - 250, screenHeight - 220);
		m_Font.draw(batch, weaponReload, screenWidth - 250, screenHeight - 260);
	}
	
	// TODO: Test function just to see how aiming / weapons look like
	public void drawLookLine(ShapeRenderer shape) {
		Actor trackedActor = GameScreen.getRenderer().getTrackedEntity();
		if(trackedActor == null)
			return;
		
		Vector2 lookVector = trackedActor.getLookVector();
		Vector2 centerPlayer = new Vector2(trackedActor.getPosition().x + trackedActor.getSize().x/2, trackedActor.getPosition().y + trackedActor.getSize().y/2);
		Vector2 centerToLook = new Vector2(centerPlayer.x + lookVector.x, centerPlayer.y + lookVector.y);
		
		shape.setColor(Color.WHITE);
		shape.line(centerPlayer, centerToLook);
		
		// TODO: Weapon Debug
		// TODO: This draws a box where our weapon is going to shoot
		/*
		Vector2 projLine = new Vector2(lookVector.x * (trackedActor.getSize().x/2), lookVector.y * (trackedActor.getSize().y/2));
		
		// (u*v / u*u) * u
		float scalar = (lookVector.dot(projLine) / lookVector.dot(lookVector));
		Vector2 projection = new Vector2(lookVector.x * scalar, lookVector.y * scalar);
		projection.add(centerPlayer);
		
		shape.setColor(Color.RED);
		shape.rect(projection.x - 0.125f,  projection.y - 0.125f, 0.25f, 0.25f);
		

		Vector2 projVelocity = new Vector2(lookVector);
		projVelocity.scl(trackedActor.getActiveWeapon().getWeaponStats().getBulletVelocity());
		
		shape.setColor(Color.BLUE);
		Vector2 centerToProjVelocity = new Vector2(centerPlayer.x + projVelocity.x, centerPlayer.y + projVelocity.y);
		shape.line(centerPlayer, centerToProjVelocity);
		*/
	}
	
	public void draw(SpriteBatch batch) {
		Actor trackedActor = GameScreen.getRenderer().getTrackedEntity();
		if(trackedActor == null)
			return;
		
		float healthPercent = trackedActor.getHealth() / 100.0f;

		String weaponInfo = "";
		if(trackedActor.getActiveWeapon() != null) {
			weaponInfo = trackedActor.getActiveWeapon().getWeaponString();
		}
		
		m_Font.draw(batch, weaponInfo, 25 + m_HealthBarBorder.getWidth() + 10, Gdx.graphics.getHeight() - 25);
		
		batch.draw(m_HealthBarBorder, 25, Gdx.graphics.getHeight() - 50);
		batch.draw(m_HealthBar, 25, Gdx.graphics.getHeight() - 50, m_HealthBar.getWidth() * healthPercent, m_HealthBar.getHeight());
	}

	private String trim(String s, int size) {
		return s.substring(0, Math.min(s.length(), size));
	}
}
