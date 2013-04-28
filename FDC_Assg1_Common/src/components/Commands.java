/* Convenient way to refer to the operations supported by the system.
 * Allows for strict validation of commands being passed between clients and servers.
 * 
 * Usage: 
 * 1. Use Commands.Command to select from the available commands.
 * 2. Get the name of the command for display purposes by getName(...)
 * 3. Get a Command by:
 * 		3.1 getCommandByID(...) if you know the ID.
 * 		3.2 getCommandByName(...) if you know the display name.
 * 4. For validation, get the necessary number of arguments for a command by getArgumentCount(...)
 */
package components;

public class Commands {

	// All supported commands and their names as text
	public static enum Command {
		Connect((short) 1, "connect", 0), Disconnect((short) 2, "disconnect", 1), Deposit((short) 3, "deposit", 3), Retrieve(
				(short) 4, "retrieve", 1), Inquire((short) 5, "inquire", 2);
		private short id;
		private String name;
		private int argumentCount;

		private Command(short id, String name, int argumentCount) {
			this.id = id;
			this.name = name;
			this.argumentCount = argumentCount;
		}

		/**
		 * Returns the ID of the command.
		 * 
		 * @param command
		 *            The command in question.
		 * @return The ID of the command.
		 */
		public short getID() {
			return id;
		}

		/**
		 * Returns the display name for the command.
		 * Useful for checking against user-entered command strings.
		 * 
		 * @param command
		 *            The command in question.
		 * @return The string name of the command.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Get the number of arguments that must be supplied with this command.
		 * 
		 * @param command
		 *            The command in question.
		 * @return The count of the required arguments.
		 */
		public int getArgumentCount() {
			return argumentCount;
		}
	};

	/**
	 * Returns the command for the supplied ID.
	 * Useful for obtaining the command object based on a user-supplied ID.
	 * 
	 * @param id
	 *            The ID of the command.
	 * @return The corresponding Command object, or null if none is found.
	 */
	public static Command getCommandByID(short id) {
		if (id != IDGenerator.NULL_ID)
			for (Command command : Command.values())
				if (id == command.getID())
					return command;
		return null;
	}

	/**
	 * Returns the command for the supplied name.
	 * Useful for obtaining the command object based on a user-supplied string.
	 * 
	 * @param name
	 *            The string name of the command.
	 * @return The corresponding Command object, or null if none is found.
	 */
	public static Command getCommandByName(String name) {
		if (name != null)
			for (Command command : Command.values())
				if (name.equals(command.getName()))
					return command;
		return null;
	}
}
