package com.borjabonilla.chat.client.impl;

import org.eclipse.jetty.spdy.api.Stream;

import com.borjabonilla.chat.client.AbstractMessageProcessorClient;
import com.borjabonilla.chat.client.VerifyUser;
import com.borjabonilla.chat.server.protocol.MessageType;

/**
 * This class implements all the methods of the interface AbstractMessageProcessorClient. It will execute the corresponding actions
 * depending on the processed information.
 * 
 * @author Borja Bonilla
 * 
 */
public class MessageProcessorClientImpl extends AbstractMessageProcessorClient {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.borjabonilla.chat.client.AbstractMessageProcessorClient#process(org.eclipse.jetty.spdy.api.Stream, java.lang.String,
	 * com.borjabonilla.chat.client.VerifyUser)
	 */
	@Override
	public void process(Stream stream, String message, VerifyUser usr) {

		if (message != null) {
			message = message.trim();

			// LOGIN_OK("#LOGINOK#"),//Response: Login OK
			if (message.startsWith(MessageType.LOGIN_OK.getValue())) {
				usr.VerifyOk();

				// LOGIN_NOK("#LOGINNOK#"),//Response: Login NOK
			} else if (message.startsWith(MessageType.LOGIN_NOK.getValue())) {
				usr.VerifyKO();
				String username = message.split(MessageType.LOGIN_NOK.getValue())[1].trim();
				System.out.println(String.format("User %s already exist.", username));

				// LIST_ALL_USERS("#LISTUSERS#"),//Response: Listusers. The
				// server responds with a complete list of users.
			} else if (message.startsWith(MessageType.LIST_ALL_USERS.getValue())) {
				System.out.println("***************");
				System.out.println("*  USER LIST  *");
				System.out.println("***************");
				String txt = message.split(MessageType.LIST_ALL_USERS.getValue())[1].trim();
				String[] users = txt.split("#");
				for (int i = 0; i < users.length; i++) {
					System.out.println("* " + users[i]);
				}
				System.out.println("***************");

				// LIST_ALL_USERS_EMPTY("#LISTUSERSEMPTY#"),//Response:
				// Listusersempty. There are no users.
			} else if (message.startsWith(MessageType.LIST_ALL_USERS_EMPTY.getValue())) {
				System.out.println("Empty user list.");

				// USER_NOT_FOUND("#USERNOTFOUND#"),//Response: Usernotfound.
				// The recipient of the message is not found or does not exist.
			} else if (message.startsWith(MessageType.USER_NOT_FOUND.getValue())) {
				String username = message.split(MessageType.USER_NOT_FOUND.getValue())[1].trim();
				System.out.println(String.format("User %s not found.", username));

				// NEW_MESSAGE("#NEWMESSAGE#"),//Response: Newmessage. The
				// client receives a message from another user.
			} else if (message.startsWith(MessageType.NEW_MESSAGE.getValue())) {
				String txt = message.split(MessageType.NEW_MESSAGE.getValue())[1].trim();
				int sep = txt.indexOf("#");
				String user = txt.substring(0, sep);
				String msg = txt.substring(sep + 1);
				System.out.println(String.format("[%s]: %s", user, msg));

				// NEW_BROADCAST("#NEWBROADCAST#"),//Response: Newbroadcast. The
				// client receives a broadcast message from another user.
			} else if (message.startsWith(MessageType.NEW_BROADCAST.getValue())) {
				String txt = message.split(MessageType.NEW_BROADCAST.getValue())[1].trim();
				int sep = txt.indexOf("#");
				String user = txt.substring(0, sep);
				String msg = txt.substring(sep + 1);
				System.out.println(String.format("[**BROADCAST(%s)**]: %s", user, msg));

				// NEW_USER_CONNECTED("#NEWUSER#"),//NewUserConnected (notificar
				// quan un usuari nou se connecta)
			} else if (message.startsWith(MessageType.NEW_USER_CONNECTED.getValue())) {
				String username = message.split(MessageType.NEW_USER_CONNECTED.getValue())[1].trim();
				System.out.println(String.format("New user %s is connected.", username));

				// USER_DISCONNECTED("#USERDISCONNECTED#");//UserDisconnected.
				// Clients will be notified when a logged-in user disconnects.
			} else if (message.startsWith(MessageType.USER_DISCONNECTED.getValue())) {
				String username = message.split(MessageType.USER_DISCONNECTED.getValue())[1].trim();
				System.out.println(String.format("User %s is disconnected.", username));

			}
		}

	}

}
