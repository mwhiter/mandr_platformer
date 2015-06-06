package com.mandr.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.mandr.game.screens.LoadingScreen;

public class MyGame extends Game  {
	static private boolean debug = true;
	
	@Override
	public void create() {
		// TODO: still highly incomplete, need to think of a good screen access system
		setScreen(new LoadingScreen(this, "test_map.tmx"));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				
		float deltaTime = Gdx.graphics.getDeltaTime();
		// A bit of a hack, but it's an issue with Lwjgl
		// DeltaTime will be maxed out at 10 fps (whichever is smaller).
		// This way, when the application is paused for whatever reason, the deltaTime will remain a somewhat sane value.
		deltaTime = Math.min(deltaTime, 1 / 30.0f);
		getScreen().render(deltaTime);
	}
	
	public void toggleDebug() {
		debug = !debug;
	}
	
	public static boolean isDebug() {
		return debug;
	}
}