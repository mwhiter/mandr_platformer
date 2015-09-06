package com.mandr.util;

import com.mandr.game.Globals;
import com.mandr.game.MyGame;

public class StringUtils {
	public static void debugPrint(String string) {
		if(MyGame.isDebug()) {
			System.out.println(Globals.getGameTime() + ": " + string);
		}
	}
	
	/** Converts a time (in long) to a string in the format hh:mm:ss:ms
	 * @param time The time to convert */
	public static String timeString(long time) {
		return timeString(time, true);
	}
	
	/** Converts a time (in long) to a string in the format hh:mm:ss:ms
	 * @param time The time to convert
	 * @param showMillis whether or not to show the milliseconds */
	public static String timeString(long time, boolean showMillis) {
		int ms = (int) (time % 1000) / 10;
		int s = (int) (time / 1000) % 60;
		int m = (int) (time / 1000 / 60) % 60;
		int h = (int) (time / 1000 / 60 / 60) % 60;
		
		String rtn = "";
		rtn += (h > 0 ? (Integer.toString(h) + ":") : "");
		rtn += (m > 0 ? (h > 0 && m < 10 ? ("0" + Integer.toString(m) + ":") : (Integer.toString(m) + ":")) : "");
		rtn += (m > 0 && s < 10 ? "0" + Integer.toString(s) : Integer.toString(s));
		if(showMillis)
			rtn += ":" + (ms < 10 ? "0" + Integer.toString(ms) : Integer.toString(ms));
				
		return rtn;
	}
}
