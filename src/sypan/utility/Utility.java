package sypan.utility;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * {@code Utility} is an abstract class containing numerous static convenience methods used throughout the project.
 * @author Carl Linley
 **/
public abstract class Utility {

	/**
	 * A method used to return the current date as a string.
	 * 
	 * @param fileSafe - <i>true</i> if the string must be applicable to file names. This replaces the default splitter ('/') with '-'.
	 * @return the current date as a string.
	 **/
	public static String getDate(boolean fileSafe) {
		Calendar c = Calendar.getInstance();
		char s = (fileSafe ? '-' : '/');

		return doubleDigit(c.get(Calendar.DATE)) + s + Utility.doubleDigit(c.get(Calendar.MONTH) + 1) + s + c.get(Calendar.YEAR);
	}

	/**
	 * A method used to return the current time as a string.
	 * 
	 * @param fileSafe - <i>true</i> if the string must be applicable to file names. This replaces the default splitter (':') with '.'.
	 * @return the current date as a string.
	 **/
	public static String getTime(boolean fileSafe) {
		Calendar c = Calendar.getInstance();
		char s = (fileSafe ? '.' : ':');

		return doubleDigit(c.get(Calendar.HOUR_OF_DAY)) + s + doubleDigit(c.get(Calendar.MINUTE)) + s + doubleDigit(c.get(Calendar.SECOND));
	}

	/**
	 * Converts a single-digit number to a double-digit number, if applicable.<p>
	 * 
	 * For example:<br>
	 * - Passing 1 returns "01"<br>
	 * - Passing 4 returns "04"<br>
	 * - Passing 42 returns "42"<br>
	 * 
	 * @param toConvert - the integer to convert to a double digit format.
	 * @return a string containing the specified integer converted to a double digit format if applicable.
	 **/
	public static String doubleDigit(int i) {
		if (i < 10) {
			return "0"+i;
		}
		return i+"";
	}

	/**
	 * Formats the specified number by segmenting it using commas.<p>
	 * 
	 * For example:<p>
	 * 
	 * - Passing 1000 returns "1,000"<br>
	 * - Passing 14230 returns "14,230"<br>
	 * - Passing 131213 returns "131,213"<br>
	 * 
	 * @param toFormat - the integer to segment.
	 * @return a string containing the specified integer segmented by commas.
	 **/
	public static String formatInteger(int toFormat) {
		String rawNumber = String.valueOf(toFormat);

		if (toFormat < 0) {
			if (toFormat > -1000) {
				return rawNumber;
			}
			else if (toFormat > -10000) {//1,000 - 9,999
				return rawNumber.substring(0, 2)+","+rawNumber.substring(2, 5);
			}
			else if (toFormat > -100000) {//10,000 - 99,999
				return rawNumber.substring(0, 3)+","+rawNumber.substring(3, 6);
			}
			else if (toFormat > -1000000) {//100,000 - 999,999
				return rawNumber.substring(0, 4)+","+rawNumber.substring(4, 7);
			}
			else if (toFormat > -10000000) {//1,000,000 - 9,999,999
				return rawNumber.substring(0, 2)+","+rawNumber.substring(2, 5)+","+rawNumber.substring(5, 8);
			}
			else if (toFormat > -100000000) {//10,000,000 - 99,999,999
				return rawNumber.substring(0, 3)+","+rawNumber.substring(3, 6)+","+rawNumber.substring(6, 9);
			}
			else if (toFormat > -1000000000) {//100,000,000 - 999,999,999
				return rawNumber.substring(0, 4)+","+rawNumber.substring(4, 7)+","+rawNumber.substring(7, 10);
			}
			else if (toFormat > -2147483647) {//1,000,000,000 - 2,147,483,647
				return rawNumber.substring(0, 2)+","+rawNumber.substring(2, 5)+","+rawNumber.substring(5, 8)+","+rawNumber.substring(8, 11);
			}
		}
		else {
			if (toFormat < 1000) {
				return rawNumber;
			}
			else if (toFormat < 10000) {//1,000 - 9,999
				return rawNumber.substring(0, 1)+","+rawNumber.substring(1, 4);
			}
			else if (toFormat < 100000) {//10,000 - 99,999
				return rawNumber.substring(0, 2)+","+rawNumber.substring(2, 5);
			}
			else if (toFormat < 1000000) {//100,000 - 999,999
				return rawNumber.substring(0, 3)+","+rawNumber.substring(3, 6);
			}
			else if (toFormat < 10000000) {//1,000,000 - 9,999,999
				return rawNumber.substring(0, 1)+","+rawNumber.substring(1, 4)+","+rawNumber.substring(4, 7);
			}
			else if (toFormat < 100000000) {//10,000,000 - 99,999,999
				return rawNumber.substring(0, 2)+","+rawNumber.substring(2, 5)+","+rawNumber.substring(5, 8);
			}
			else if (toFormat < 1000000000) {//100,000,000 - 999,999,999
				return rawNumber.substring(0, 3)+","+rawNumber.substring(3, 6)+","+rawNumber.substring(6, 9);
			}
			else if (toFormat < 2147483647) {//1,000,000,000 - 2,147,483,647
				return rawNumber.substring(0, 1)+","+rawNumber.substring(1, 4)+","+rawNumber.substring(4, 7)+","+rawNumber.substring(7, 10);
			}
		}
		return null;
	}

	/**
	 * Based on example code found on StackOverflow, posted by a user named <i>Marcel</i>.
	 * I had absolutely no idea how to go about doing this, so thanks Marcel!<p>
	 * 
	 * Link: <a href="http://stackoverflow.com/questions/38955/is-it-possible-to-get-the-maximum-supported-resolution-of-a-connected-display-in">
	 * http://stackoverflow.com/questions/38955/is-it-possible-to-get-the-maximum-supported-resolution-of-a-connected-display-in
	 * </a>
	 * 
	 * @return an {@code ArrayList} of type String containing every resolution supported by the main display in the format "WIDTH x HEIGHT".
	 * @author Carl Linley
	 * @author Marcel
	 **/
	public static ArrayList<String> getSupportedResolutions() {
		DisplayMode[] supportedModes = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayModes();
		ArrayList<String> resolutionList = new ArrayList<String>(supportedModes.length);
		String resolutionType;

		for (int i = 0; i < supportedModes.length; i++) {
			resolutionType = supportedModes[i].getWidth() + " x " + supportedModes[i].getHeight();

			if (!resolutionList.contains(resolutionType)) {
				resolutionList.add(resolutionType);
			}
		}
		return resolutionList;
	}

	public static Dimension parseDimension(String asString) {
		String[] values = asString.split(" x ");

		return new Dimension(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
	}

	/**
	 * Formats the specified string to a reasonable standard for names.
	 * Spaces and dashes constitute a following upper-case letter.
	 * 
	 * @param toFormat - the string to format.
	 * @return the formatted string.
	 **/
	public static String formatName(String toFormat) {
		toFormat = toFormat.toLowerCase().replaceAll("_", " ");

		char buf[] = toFormat.toCharArray();

		boolean endMarker = true;	

		for (int i = 0; i < buf.length; i++) {
            char c = buf[i];

            if (endMarker && c >= 'a' && c <= 'z') {
				buf[i] -= 0x20;
				endMarker = false;
			}
        	endMarker = (c == ' ' || c == '-');
		}
		return new String(buf, 0, buf.length);
	}

	public static String formatText(String toFormat) {
		toFormat = toFormat.toLowerCase().replaceAll("_", " ");

		char buf[] = toFormat.toCharArray();

		boolean endMarker = true;	

		for (int i = 0; i < buf.length; i++) {
            char c = buf[i];

            if (endMarker && c >= 'a' && c <= 'z') {
				buf[i] -= 0x20;
				endMarker = false;
			}
		}
		return new String(buf, 0, buf.length);
	}

	public static boolean validResolution(String toCheck) {
		return getSupportedResolutions().contains(toCheck);
	}

	/**
	 * @return the specified time, in seconds, as MM:SS.
	 **/
	public static String formatTime(int timeRemaining) {
		return doubleDigit(timeRemaining / 60) + ":" + doubleDigit(timeRemaining % 60);
	}
}