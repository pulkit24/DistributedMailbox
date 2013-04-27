package client.network;

import java.util.List;

import components.Commands.Command;

public class Communication {
	private static Communication instance = null;

	private Communication() {

	}

	public static Communication getInstance() {
		if (instance == null)
			instance = new Communication();
		return instance;
	}

	public void sendOperation(Command command, List<String> args) {

	}
}
