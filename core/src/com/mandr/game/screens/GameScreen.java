package com.mandr.game.screens;

import com.badlogic.gdx.Screen;
import com.mandr.game.GameGlobals;
import com.mandr.game.MyGame;
import com.mandr.graphics.GameRenderer;
import com.mandr.input.InputHandler;
import com.mandr.level.Level;

public class GameScreen implements Screen {
	public enum GameState {
		GAME_RUNNING,
		GAME_PAUSED
	}
	
	private MyGame m_Game;
	private static GameRenderer m_Renderer;
	private static Level m_Level;
	private GameState m_GameState;
	
	// Input handler is the input listener
	private InputHandler input;
	
	public GameScreen(MyGame game, Level level) {
		m_Game = game;
		
		m_Level = level;
		m_Renderer = new GameRenderer(this);
		input = new InputHandler(this);
		
		m_GameState = GameState.GAME_RUNNING;
	}

	public static Level getLevel() {
		return m_Level;
	}
	
	public static GameRenderer getRenderer() {
		return m_Renderer;
	}
	
	public InputHandler getInput() {
		return input;
	}
	
	public MyGame getGame() {
		return m_Game;
	}
	
	public boolean isPaused() {
		return m_GameState == GameState.GAME_PAUSED;
	}
	
	@Override
	public void show() {
	}
	
	@Override
	public void render(float delta) {
		if(m_GameState == GameState.GAME_PAUSED)
			delta = 0;
		
		GameGlobals.changeGameTime(delta);
		
		input.update();
		
		// On update when running
		if(m_GameState == GameState.GAME_RUNNING) {
			m_Level.update(delta);
		}
		
		// Always render
		m_Renderer.draw(delta);
	}

	@Override
	public void resize(int width, int height) {
		m_Renderer.resize(width, height);
	}

	@Override
	public void pause() {
		m_GameState = GameState.GAME_PAUSED;
	}

	@Override
	public void resume() {
		m_GameState = GameState.GAME_RUNNING;
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
