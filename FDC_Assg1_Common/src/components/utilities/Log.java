/* Utility for printing debug and error messages.
 * Set the logging modes as needed.
 * 
 * Usage:
 * 1. Set the mode as follows at the start of the program:
 * 		1.1 During development, Log.setDebugMode() for full verbose messages of all levels.
 * 		1.2 For deployment, Log.setUserFriendlyMode() in case you want to display a friendly message for application errors.
 * 		1.3 For deployment, Log.setSilentMode() in case you want no messages displayed.
 * You can manually set the verbosity and level using Log.VERBOSE and Log.LEVEL for any configuration you want.
 * 
 * 2. To call the logger, use:
 * 		2.1 Log.debug(...) for logging a debug message.
 * 		2.2 Log.error(...) for logging an error message.
 */
package components.utilities;

public class Log {

	// All supported debug levels
	public static enum Level {
		None, Error, Debug
	};

	// Currently set debug level; dictates what messages are displayed
	public static Level LEVEL = Level.None;

	// All supported verbosity settings
	public static enum Verbosity {
		Low, High
	}

	// Currently set verbosity; controls the amount of metadata supplied with messages
	public static Verbosity VERBOSE = Verbosity.Low;

	/**
	 * Print a debug message.
	 * Only displayed if debug mode is set to Debug.
	 * 
	 * @param caller
	 *            Identify the calling class. Only displayed if verbosity is high.
	 * @param context
	 *            Identify the context (eg. the current function). Only displayed if verbosity is high.
	 * @param message
	 *            Descriptive message to be printed.
	 */
	public static void debug(String caller, String context, String message) {
		if (LEVEL.compareTo(Level.Debug) >= 0) {
			if (VERBOSE.equals(Verbosity.High))
				System.out.print(caller + "." + context + "\t");
			System.out.println("Debug: " + message);
		}
	}

	/**
	 * Print an error message.
	 * Only displayed if debug mode is set to Error or higher.
	 * 
	 * @param caller
	 *            Identify the calling class. Only displayed if verbosity is high.
	 * @param context
	 *            Identify the context (eg. the current function). Only displayed if verbosity is high.
	 * @param message
	 *            Descriptive message for the error.
	 * @param exception
	 *            Actual exception, if any, for showing stack trace. Only displayed if verbosity is high.
	 */
	public static void error(String caller, String context, String message, Exception exception) {
		if (LEVEL.compareTo(Level.Error) > 0) {
			if (VERBOSE.equals(Verbosity.High))
				System.out.print(caller + "." + context + "\t");
			System.out.println("Error: " + message);
			if (VERBOSE.equals(Verbosity.High) && exception!=null)
				System.err.println(exception.toString());
		}
	}

	/**
	 * Prepare the logger for printing fully verbose debug and error messages.
	 */
	public static void setDebugMode() {
		LEVEL = Level.Debug;
		VERBOSE = Verbosity.High;
		Log.debug("Log", "Mode", "set to debug mode");
	}

	/**
	 * Prepare the logger for printing user-friendly error messages only.
	 */
	public static void setUserFriendlyMode() {
		LEVEL = Level.Error;
		VERBOSE = Verbosity.Low;
		Log.debug("Log", "Mode", "set to user-friendly mode");
	}

	/**
	 * Prevent the logger from printing any log messages.
	 */
	public static void setSilentMode() {
		LEVEL = Level.None;
		Log.debug("Log", "Mode", "set to silent mode");
	}
}
