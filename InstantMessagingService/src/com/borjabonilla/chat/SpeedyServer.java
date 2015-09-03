package com.borjabonilla.chat;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.spdy.server.SPDYServerConnector;

import com.borjabonilla.chat.server.listeners.SpeedyServerListener;

/**
 * This class contains configuration and initialization required to start the server. Protocol used is SPDY.
 * 
 * @author Borja Bonilla
 * 
 */
public class SpeedyServer {

	// Any exceptions will be logged in a log file.
	private static final Logger logger = Logger.getLogger(SpeedyServer.class);

	/**
	 * Will configure and start the server.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		try {
			// Frame listener that handles the communication over speedy.
			SpeedyServerListener frameListener = new SpeedyServerListener();

			// Wire up and start the connector.
			Server server = new Server();

			// Setup the connector.
			SPDYServerConnector connector = new SPDYServerConnector(server, frameListener);
			connector.setPort(8181);
			connector.setIdleTimeout(900000);

			// Add connector and start the server.
			server.addConnector(connector);
			server.start();
			server.join();

		} catch (Exception e) {
			logger.error(SpeedyServer.class, e);
		}
	}

}
