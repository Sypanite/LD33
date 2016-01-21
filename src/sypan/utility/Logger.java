package sypan.utility;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Carl Linley
 **/
public final class Logger {

	private final static java.util.logging.Logger defaultLogger = java.util.logging.Logger.getLogger("sypan");

	public static void init() {
		java.util.logging.Logger.getLogger("com.jme3").setLevel(Level.SEVERE);
		defaultLogger.setUseParentHandlers(false);

		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new LoggingFormat());
		defaultLogger.addHandler(handler);
	}

	public static void log(Level level, String message) {
		defaultLogger.log(level, message);
	}

	public static void logInfo(String s) {
		defaultLogger.info(s);
	}

	public static void logSevere(String s) {
		defaultLogger.severe(s);
	}

	public static void logWarning(String s) {
		defaultLogger.warning(s);
	}

	public static void logException(String errorMessage, Exception exceptionThrown) {
		logSevere(errorMessage + ": " + exceptionThrown);
		exceptionThrown.printStackTrace();
	}

	public static void logDebug(String toLog) {
		System.out.println("[" + Utility.getDate(false) + " - " + Utility.getTime(false) + "] DEBUG: " + toLog);
	}
}
class LoggingFormat extends Formatter {

	@Override
	public String format(LogRecord record) {
		return "[" + Utility.getDate(false) + " - " + Utility.getTime(false) + "] " + record.getLevel() + ": " + record.getMessage() + "\r\n";
	}
}