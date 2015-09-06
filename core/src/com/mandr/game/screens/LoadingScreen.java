package com.mandr.game.screens;

import com.badlogic.gdx.Screen;
import com.mandr.game.MyGame;
import com.mandr.level.Level;

public class LoadingScreen implements Screen {
	private MyGame m_Game;
	
	private Level m_LoadingLevel;
	private String m_LevelName;
	public LoadingScreen(MyGame game, String levelName) {
		m_Game = game;
		m_LoadingLevel = new Level();
		m_LevelName = levelName;
	}
	
	@Override
	public void show() {
	}

	// TODO: This is so basic and terrible it hurts. Figure out what to do and how to do it, and then make this nice.
	// TODO: To make this nice, I want to do some fo the following:
		// 1. Have a loading bar
		// 2. If the level loading fails, just return the main menu with an error message
	@Override
	public void render(float delta) {
		if(m_LoadingLevel.isLoaded()) {
			m_Game.setScreen(new GameScreen(m_Game, m_LoadingLevel));
		}
		else {
			try {
				m_LoadingLevel.loadMap(m_LevelName);
			}
			// TODO: Don't exactly know what I'm doing here yet
			catch (Exception e) {
				System.out.println("Failed to load level: " + e.getMessage());
				e.printStackTrace();
				this.dispose();
				System.exit(0);
			}
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
		this.dispose();
	}

	@Override
	public void dispose() {
	}
}
