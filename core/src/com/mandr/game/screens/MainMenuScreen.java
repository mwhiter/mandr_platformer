package com.mandr.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mandr.game.MyGame;

public class MainMenuScreen implements Screen {
	private Skin m_Skin;
	
	// TODO background image, music
	// TODO menu buttons
	
	private Stage m_Stage;
	private MyGame m_Game;
	
	public MainMenuScreen(MyGame game) {
		m_Game = game;
		create();
	}
	
	public void create() {
		m_Stage = new Stage();
		Gdx.input.setInputProcessor(m_Stage);
	
		// TODO define in JSON file?
		m_Skin = new Skin();
		
		Pixmap pixmap = new Pixmap(100, 100, Format.RGBA8888);
		pixmap.setColor(Color.GREEN);
		pixmap.fill();
		
		m_Skin.add("white", new Texture(pixmap));
		
		BitmapFont font = new BitmapFont();
		m_Skin.add("default", font);
		
		TextButtonStyle style = new TextButtonStyle();
		style.up = m_Skin.newDrawable("white", Color.DARK_GRAY);
		style.down = m_Skin.newDrawable("white", Color.DARK_GRAY);
		style.checked = m_Skin.newDrawable("white", Color.DARK_GRAY);
		style.over = m_Skin.newDrawable("white", Color.DARK_GRAY);
		style.font = m_Skin.getFont("default");
		
		m_Skin.add("default", style);
		
		TextButton button = new TextButton("PLAY", style);
		button.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				loadLevel("test_map.tmx");
			}
			
		});
		button.setPosition(200, 200);
		m_Stage.addActor(button);
	}
	
	private void loadLevel(String level) {
		// TODO set screen on button press
		m_Game.setScreen(new LoadingScreen(m_Game, level));
		this.dispose();
	}
	
	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		m_Stage.draw();
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {
		m_Stage.dispose();
		m_Skin.dispose();
	}

}
