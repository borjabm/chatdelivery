package com.borjabonilla.chat.server;

import org.apache.log4j.Logger;
import org.eclipse.jetty.spdy.api.Stream;

import com.borjabonilla.chat.server.protocol.MessageType;

/**
 * This abstract class is in charge of processing the information received in the synchronization. Will define methods to be
 * implemented.
 * 
 * @author Borja Bonilla
 * 
 */
public abstract class AbstractMessageProcessorServer {

	// Any exceptions will be logged in a log file.
	private static final Logger logger = Logger.getLogger(AbstractMessageProcessorServer.class);

	/**
	 * Processes the information of the synchronization and executes the corresponding action.
	 * 
	 * @param stream
	 *            Stream on which the information was received.
	 * @param message
	 *            The payload of the message received.
	 */
	public void process(Stream stream, String message) {

		System.out.println("We begin process with: " + message);
		if (message != null && message.trim().startsWith(MessageType.LOGIN.getValue())) {
			try {
				String userName = message.split(MessageType.LOGIN.getValue())[1].trim();
				if (login(stream, userName))
					newUserConnectedExceptMe(stream, userName);

			} catch (Exception e) {
				logger.error(AbstractMessageProcessorServer.class + "Error on login", e);
			}
		} else if (message != null && message.trim().startsWith(MessageType.GET_ALL_USERS.getValue())) {
			try {
				getAllUsers(stream);
			} catch (Exception e) {
				logger.error(AbstractMessageProcessorServer.class + "Error on get all users", e);
			}
		} else if (message != null && message.trim().startsWith(MessageType.SEND_TO_USER.getValue())) {
			try {
				String[] payload = message.split(MessageType.SEND_TO_USER.getValue());
				int index = payload[1].indexOf("#");
				String userName = payload[1].substring(0, index);
				String messagePayload = payload[1].substring(index + 1);
				sendTo(stream, userName, messagePayload);
			} catch (Exception e) {
				logger.error(AbstractMessageProcessorServer.class + "Error sending message to a user", e);
			}
		} else if (message != null && message.trim().startsWith(MessageType.SEND_TO_ALL.getValue())) {
			try {
				String messagePayload = message.split(MessageType.SEND_TO_ALL.getValue())[1].trim();
				senToAllExceptMe(stream, messagePayload);
			} catch (Exception e) {
				logger.error(AbstractMessageProcessorServer.class + "Error sending a broadcast message", e);
			}
		} else if (message != null && message.trim().startsWith(MessageType.DISCONNECT_USER.getValue())) {
			try {

				userDisconnected(stream);
			} catch (Exception e) {
				logger.error(AbstractMessageProcessorServer.class + "Error disconnecting user", e);
			}
		}

	}

	/**
	 * This method will have to register the user in the hash map of users.
	 * 
	 * @param userStream
	 *            Stream of the user that wants to login.
	 * @param userName
	 *            Username that the user wants to use.
	 * @return Returns true if success. Otherwise returns false.
	 * @throws Exception
	 */
	protected abstract boolean login(Stream userStream, String userName) throws Exception;

	/**
	 * This method will send a list with all users to the stream of the user that requests it.
	 * 
	 * @param userStream
	 *            Stream of the user that wants the list.
	 * @throws Exception
	 */
	protected abstract void getAllUsers(Stream userStream) throws Exception;

	/**
	 * This method will send the message to receiver username.
	 * 
	 * @param senderUserStream
	 *            Stream of the user that generates the message.
	 * @param receiverUserName
	 *            Username of the user that has to receive the message.
	 * @param message
	 *            The payload of the message to deliver.
	 * @throws Exception
	 */
	protected abstract void sendTo(Stream senderUserStream, String receiverUserName, String message) throws Exception;

	/**
	 * This method will send a broadcast message to all connected users except the one that is originating it.
	 * 
	 * @param userStream
	 *            Stream of the user that generates the message.
	 * @param message
	 *            The payload of the message to deliver.
	 * @throws Exception
	 */
	protected abstract void senToAllExceptMe(Stream userStream, String message) throws Exception;

	/**
	 * This method will notify all connected users that a new user has joined. The new user will not be notified.
	 * 
	 * @param userStream
	 *            Stream of the user that joins the chat.
	 * @param userName
	 *            Username of the user that joins the chat.
	 * @throws Exception
	 */
	protected abstract void newUserConnectedExceptMe(Stream userStream, String userName) throws Exception;

	/**
	 * This method will notify all connected users that a logged user has disconnected.
	 * 
	 * @param userStream
	 *            Stream of the user that leaves the chat.
	 * @throws Exception
	 */
	protected abstract void userDisconnected(Stream userStream) throws Exception;

}
