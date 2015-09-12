package com.mandr.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
		
		// TODO figure out a good way to define these buttons. Not particularly thrilled with how they are done now
		
		m_Stage = new Stage();
		Gdx.input.setInputProcessor(m_Stage);
	
		BitmapFont font = new BitmapFont();
		m_Skin = new Skin();		
		m_Skin.addRegions(new TextureAtlas(Gdx.files.internal("resources/ui/buttons/uibuttons.pack")));
		
		TextButtonStyle style = new TextButtonStyle();
		style.font = font;
		style.up = m_Skin.getDrawable("button_test");
		style.down = m_Skin.getDrawable("button_test");
		style.checked = m_Skin.getDrawable("button_test");
		style.over = m_Skin.getDrawable("button_test");
		
		// TODO need some way to query screen width
		int screenWidth = 1024;
		// Play button
		addButton(((screenWidth * 33 ) / 100) - 100,200,"PLAY", style, new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				loadLevel("test_map.tmx");
			}
		});
		
		// Options button
		addButton(((screenWidth * 66) / 100) - 100,200,"Options (NYI)", style, new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// Not functional.
			}
		});
		
		TextButtonStyle exitstyle = new TextButtonStyle();
		exitstyle.font = font;
		exitstyle.up = m_Skin.getDrawable("button_exit_test");
		exitstyle.down = m_Skin.getDrawable("button_exit_test");
		exitstyle.checked = m_Skin.getDrawable("button_exit_test");
		exitstyle.over = m_Skin.getDrawable("button_exit_test");
		
		// Exit button
		addButton(screenWidth - 200,0,"EXIT", exitstyle, new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.exit(0);
			}
		});
	}
	
	private void addButton(float x, float y, String text, TextButtonStyle style, ChangeListener listener) {
		TextButton button = new TextButton(text, style);
		button.addListener(listener);
		button.setPosition(x, y);
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
