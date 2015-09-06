package com.mandr.graphics.anim;

import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;

public class AnimLoader {
	// Data class to store needed variables for parsing.
	public static class AnimData {
		public Texture tex = null;				// texture
		public TextureRegion[][] split = null;	// split texture
		public int px = 0;						// pixels per frame
		HashMap<String, Animation> anims = null; // hashmap of anims
	}
	
	/** Parse the animation definition and return animations, indexed by names
	 * @param file Name of the animdef XML file */
	public static HashMap<String, Animation> parse(String file) {
		XmlReader xml = new XmlReader();
		AnimData data = new AnimData();
		data.anims = new HashMap<String, Animation>();
		try {
			XmlReader.Element element = xml.parse(Gdx.files.internal("resources/anim/" + file));
			parseFileData(data, element.getChildByName("File"));
			parseStates(data, element.getChildByName("States"));
			
			return data.anims;
		}
		// If this fails, do not give any animations
		catch(IOException ex) {
			ex.printStackTrace();
			return null;
		}	
	}
	
	private static void parseFileData(AnimData data, XmlReader.Element element) {
		data.tex = new Texture(Gdx.files.internal(element.getChildByName("Path").getText()));
		data.px = element.getInt("PixelsPerFrame");
		data.split = TextureRegion.split(data.tex, data.px, data.px);
	}
	
	private static void parseStates(AnimData data, XmlReader.Element element) {
		// A frame offset, because if we are parsing multiple animations then we want them to start at different indices. Just keep a running count
		int offset = 0;
		
		// Parse each state individually
		for(int i=0; i < element.getChildCount(); i++) {
			// parseState() will return number of frames so that offset increases each time.
			// anim_1 (8 frames) is [0-7]
			// anim_2 (x frames) goes from at [8-x+8]
			offset += parseState(data, element.getChild(i), offset);
		}
	}
	
	private static int parseState(AnimData data, XmlReader.Element state, int offset) {
		int numFrames = state.getInt("NumFrames");
		int framesPerRow = data.tex.getWidth() / data.px;
		
		TextureRegion[] state_frames = new TextureRegion[numFrames];
		int index = 0;
		for(int i=offset; i < numFrames + offset; i++) {
			
			int row = i / framesPerRow;
			int col = i % framesPerRow;
			state_frames[index++] = data.split[row][col];
		}

		// Create an animation and insert into the hash map
		Animation anim = new Animation(1.0f / (float) numFrames, state_frames);
		data.anims.put(state.getChildByName("ID").getText(), anim);
		
		return numFrames;
	}
}
