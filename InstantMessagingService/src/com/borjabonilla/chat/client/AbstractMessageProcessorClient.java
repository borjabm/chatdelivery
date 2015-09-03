package com.borjabonilla.chat.client;

import org.eclipse.jetty.spdy.api.Stream;

/**
 * This abstract class is in charge of processing the information received in the synchronization. Will define methods to be
 * implemented.
 * 
 * @author Borja Bonilla
 * 
 */
public abstract class AbstractMessageProcessorClient {

	/**
	 * Processes the information received from the server.
	 * 
	 * @param stream Stream on which the information was received.
	 * @param message The payload of the message received.
	 * @param usr To verify user status.
	 */
	public abstract void process(Stream stream, String message, VerifyUser usr);

}
