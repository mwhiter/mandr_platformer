package com.mandr.graphics.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class UIButton {
	TextButton button;
	TextButtonStyle style;
	BitmapFont font;
	Skin skin;
	TextureAtlas atlas;
	
	public UIButton(Stage stage, String text, String up, String down, String checked) {
	
		
		button = new TextButton(text, style);
		stage.addActor(button);
	}
}
