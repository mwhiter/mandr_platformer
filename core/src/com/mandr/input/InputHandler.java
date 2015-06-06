package com.mandr.input;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.mandr.entity.Player;
import com.mandr.game.screens.GameScreen;
import com.mandr.util.Directions;

// This class will handle ALL the input in the game. It basically overrides Gdx.input to provide full control for me
public class InputHandler {
	private ArrayList<Integer> m_RegisteredKeyCodes;
	private ArrayList<Integer> m_RegisteredMouseButtons;
	
	private final int MAX_BUTTONS = 5;	// Max number off Mouse Buttons located in libGDX Buttons class
	private Command[] m_Buttons;
	private boolean[] m_ButtonsCurrentState;
	private boolean[] m_ButtonsLastState;
	
	private final int MAX_KEYS = 256;	// Max number of Keys located in libGDX Keys class
	private Command[] m_Keys;
	private boolean[] m_KeysCurrentState;
	private boolean[] m_KeysLastState;
	
	private GameScreen m_GameScreen;
	
	public DirectionCommand COMMAND_LEFT = new DirectionCommand(this, Directions.DIRECTION_LEFT);
	public DirectionCommand COMMAND_RIGHT = new DirectionCommand(this, Directions.DIRECTION_RIGHT);
	public DirectionCommand COMMAND_UP = new DirectionCommand(this, Directions.DIRECTION_UP);
	public DirectionCommand COMMAND_DOWN = new DirectionCommand(this, Directions.DIRECTION_DOWN);
	public JumpCommand COMMAND_JUMP = new JumpCommand(this);
	public ReloadCommand COMMAND_RELOAD = new ReloadCommand(this);
	public PauseCommand COMMAND_PAUSE = new PauseCommand(this);
	public FireWeaponCommand COMMAND_FIRE_WEAPON = new FireWeaponCommand(this);
	
	public InputHandler(GameScreen gameScreen) {	
		
		m_GameScreen = gameScreen;
		
		m_Keys = new Command[MAX_KEYS];				// Stores a command for each button (ex: "left button executes MoveLeftCommand")
		m_KeysCurrentState = new boolean[MAX_KEYS];	// Stores the state of the button this input (ex: "left button is pressed")
		m_KeysLastState = new boolean[MAX_KEYS];	// Stores the state of the button rior (ex: "left button is released")
		
		m_Buttons = new Command[MAX_BUTTONS];				// Stores a command for each button (ex: "left button executes MoveLeftCommand")
		m_ButtonsCurrentState = new boolean[MAX_BUTTONS];	// Stores the state of the button this input (ex: "left button is pressed")
		m_ButtonsLastState = new boolean[MAX_BUTTONS];	// Stores the state of the button rior (ex: "left button is released")
		
		for(int i=0; i<MAX_KEYS;i++) {
			m_Keys[i] = null;
			m_KeysCurrentState[i] = false;
			m_KeysLastState[i] = false;
		}
		for(int i=0; i<MAX_BUTTONS;i++) {
			m_Buttons[i] = null;
			m_ButtonsCurrentState[i] = false;
			m_ButtonsLastState[i] = false;
		}
		
		m_RegisteredKeyCodes = new ArrayList<Integer>();
		m_RegisteredMouseButtons = new ArrayList<Integer>();
		
		registerInputs();
	}
	
	public GameScreen getGameScreen() {
		return m_GameScreen;
	}
	
	//=========================================================================
	// Update methods
	//=========================================================================	
	
	public void update() {
		// Update the states for all keys
		for(int key=0; key < MAX_KEYS; key++) {
			m_KeysLastState[key] = m_KeysCurrentState[key];
			m_KeysCurrentState[key] = Gdx.input.isKeyPressed(key);
		}
		
		for(int button=0; button < MAX_BUTTONS; button++) {
			m_ButtonsLastState[button] = m_ButtonsCurrentState[button];
			m_ButtonsCurrentState[button] = Gdx.input.isButtonPressed(button);
		}
		
		// Only look through registered key-codes (they are the only ones that matter)
		for(Integer keycode : m_RegisteredKeyCodes) {
			handleKeyInput(keycode);
		}
		// Only look through registered buttons (they are the only ones that matter)
		for(Integer keycode : m_RegisteredMouseButtons) {
			handleButtonInput(keycode);
		}
	}
	
	private void handleKeyInput(int keycode) {
		Player entity = GameScreen.getLevel().getPlayer();
		Command command = m_Keys[keycode];
		
		if(entity == null || command == null) return;
		if((isKeyJustPressed(keycode) || isKeyStillPressed(keycode)) && m_GameScreen.isPaused() && !command.validDuringPause()) return;
		
		if(isKeyJustPressed(keycode)) 	command.execute(InputTrigger.INPUT_JUST_PRESSED, entity);
		
		if(isKeyJustReleased(keycode)) 	command.execute(InputTrigger.INPUT_JUST_RELEASED, entity);
		
		if(isKeyStillPressed(keycode)) 	command.execute(InputTrigger.INPUT_STILL_PRESSED, entity);
		
		if(isKeyStillReleased(keycode)) command.execute(InputTrigger.INPUT_STILL_RELEASED, entity);
	}
	
	private void handleButtonInput(int button) {
		Player entity = GameScreen.getLevel().getPlayer();
		Command command = m_Buttons[button];
		
		if(entity == null || command == null) return;
		if((isButtonJustPressed(button) || isButtonStillPressed(button)) && m_GameScreen.isPaused() && !command.validDuringPause()) return;
		
		if(isButtonJustPressed(button)) 	command.execute(InputTrigger.INPUT_JUST_PRESSED, entity);
		
		if(isButtonJustReleased(button)) 	command.execute(InputTrigger.INPUT_JUST_RELEASED, entity);
		
		if(isButtonStillPressed(button)) 	command.execute(InputTrigger.INPUT_STILL_PRESSED, entity);
		
		if(isButtonStillReleased(button)) 	command.execute(InputTrigger.INPUT_STILL_RELEASED, entity);
	}
	
	//=========================================================================
	// Input registration - attach a command to a button
	//=========================================================================	
	// TODO: a screen should probably do this, and save it in MyGame or something
	public void registerInputs() {
		registerKeycode(COMMAND_UP, 	Keys.W);
		registerKeycode(COMMAND_LEFT, 	Keys.A);
		registerKeycode(COMMAND_DOWN, 	Keys.S);
		registerKeycode(COMMAND_RIGHT, 	Keys.D);
		registerKeycode(COMMAND_JUMP, 	Keys.SPACE);
		registerKeycode(COMMAND_PAUSE, 	Keys.P);
		registerKeycode(COMMAND_RELOAD, Keys.R);

		registerMouseButton(COMMAND_FIRE_WEAPON, Buttons.LEFT);
		registerMouseButton(null, Buttons.RIGHT);
		registerMouseButton(null, Buttons.MIDDLE);
	}
	
	public void registerMouseButton(Command command, int button) {
		// We'll send -1 to commands without a keybind.
		if(button == -1)
			return;
		
		// Really shouldn't happen
		if(command == null)
			return;
		
		for(Integer i : m_RegisteredMouseButtons) {
			if(button == i) {
				// TODO: Is that correct behavior?
				System.out.println("Tried to register already existing mouse button. Failed.");
				return;
			}
		}
		
		// Add a button, and configure the command
		m_RegisteredMouseButtons.add(button);
		m_Buttons[button] = command;
		System.out.println("Registered a command with button " + button);
	}
	
	// Register a command to a key-code.
	public void registerKeycode(Command command, int keycode) {
		// We'll send -1 to commands without a keybind.
		if(keycode == -1)
			return;
		
		if(command == null)
			return;
		
		// If key-code already registered, ignore
		for(Integer i : m_RegisteredKeyCodes) {
			if(keycode == i) {
				// TODO: Is that correct behavior?
				System.out.println("Tried to register already existing keycode. Failed.");
				return;
			}
		}
		
		// Add the key-code, and configure the command
		m_RegisteredKeyCodes.add(keycode);
		m_Keys[keycode] = command;
		System.out.println("Registered a command with key " + Keys.toString(keycode));
	}
	
	// Return command associate with a button
	public Command getCommand(int keycode) {
		return m_Keys[keycode];
	}
	
	/** 
	 * @return the Key associated with the command. -1 if the command is not registered. */
	public int getCommandInputKey(Command command) {
		for(Integer i : m_RegisteredKeyCodes) {
			if(m_Keys[i] == command)
				return i;
		}
		return -1;
	}
	/** 
	 * @return the Button associated with the command. -1 if the command is not registered. */
	public int getCommandInputButton(Command command) {
		for(Integer i : m_RegisteredMouseButtons) {
			if(m_Buttons[i] == command)
				return i;
		}
		return -1;
	}
	
	//=========================================================================
	// Keyboard buttons
	//=========================================================================	
	// Is this key pressed?
	public boolean isKeyPressed(int keycode) {
		if(keycode < 0 || keycode >= MAX_KEYS)
			return false;
		return m_KeysCurrentState[keycode];
	}
	
	// Is this key still not being pressed?
	public boolean isKeyStillReleased(int keycode) {
		return !m_KeysCurrentState[keycode] && !m_KeysLastState[keycode];
	}
	
	// Was this key just pressed?
	public boolean isKeyJustPressed(int keycode) {
		return m_KeysCurrentState[keycode] && !m_KeysLastState[keycode];
	}

	// Was this key just released?
	public boolean isKeyJustReleased(int keycode) {
		return !m_KeysCurrentState[keycode] && m_KeysLastState[keycode];
	}

	// Is this key being held down?
	public boolean isKeyStillPressed(int keycode) {
		return m_KeysCurrentState[keycode] && m_KeysLastState[keycode];
	}
	
	//=========================================================================
	// Mouse Button Methods
	//=========================================================================	
	// Is this button pressed?
	public boolean isButtonPressed(int button) {
		if(button < 0 || button >= MAX_BUTTONS)
			return false;
		return m_ButtonsCurrentState[button];
	}
	
	// Is this key still not being pressed?
	public boolean isButtonStillReleased(int keycode) {
		return !m_ButtonsCurrentState[keycode] && !m_ButtonsLastState[keycode];
	}
	
	// Was this key just pressed?
	public boolean isButtonJustPressed(int keycode) {
		return m_ButtonsCurrentState[keycode] && !m_ButtonsLastState[keycode];
	}

	// Was this key just released?
	public boolean isButtonJustReleased(int keycode) {
		return !m_ButtonsCurrentState[keycode] && m_ButtonsLastState[keycode];
	}

	// Is this key being held down?
	public boolean isButtonStillPressed(int keycode) {
		return m_ButtonsCurrentState[keycode] && m_ButtonsLastState[keycode];
	}
}
