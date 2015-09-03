package com.borjabonilla.chat.server.impl;

import org.eclipse.jetty.spdy.api.Stream;
import org.eclipse.jetty.spdy.api.StringDataInfo;

import com.borjabonilla.chat.server.AbstractMessageProcessorServer;
import com.borjabonilla.chat.server.AvailableUsers;
import com.borjabonilla.chat.server.protocol.MessageType;

/**
 * This class implements all the methods of the interface AbstractMessageProcessorServer. It will execute the corresponding actions
 * depending on the processed information.
 * 
 * @author Borja Bonilla
 * 
 */
public class MessageProcessorServerImpl extends AbstractMessageProcessorServer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.borjabonilla.chat.server.AbstractMessageProcessorServer#login(org.eclipse.jetty.spdy.api.Stream, java.lang.String)
	 */
	@Override
	public boolean login(Stream userStream, String userName) throws Exception {
		if (!AvailableUsers.getUsers().containsValue(userName.toLowerCase())) {
			AvailableUsers.addUser(userStream, userName);
			userStream.data(new StringDataInfo(MessageType.LOGIN_OK.getValue() + userName, false));
			return true;
		} else {
			userStream.data(new StringDataInfo(MessageType.LOGIN_NOK.getValue() + userName, false));
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.borjabonilla.chat.server.AbstractMessageProcessorServer#getAllUsers(org.eclipse.jetty.spdy.api.Stream)
	 */
	@Override
	public void getAllUsers(Stream userStream) throws Exception {
		StringBuilder list = new StringBuilder();
		for (Stream stream : AvailableUsers.getUsers().keySet()) {
			System.out.println(userStream.toString());
			System.out.println(stream.toString());
			if (!stream.equals(userStream)) {
				System.out.println("OK!");
				list.append(AvailableUsers.getUsers().get(stream) + "#");
			}
		}
		if (!list.toString().equals("")) {
			userStream.data(new StringDataInfo(MessageType.LIST_ALL_USERS.getValue() + list, false));
		} else
			userStream.data(new StringDataInfo(MessageType.LIST_ALL_USERS_EMPTY.getValue(), false));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.borjabonilla.chat.server.AbstractMessageProcessorServer#sendTo(org.eclipse.jetty.spdy.api.Stream, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void sendTo(Stream senderUserStream, String receiverUserName, String message) throws Exception {

		if (AvailableUsers.getUsers().containsValue(receiverUserName)) {

			for (Stream stream : AvailableUsers.getUsers().keySet()) {
				if (AvailableUsers.getUsers().get(stream).equalsIgnoreCase(receiverUserName)) {

					String senderUsername = AvailableUsers.getUsers().get(senderUserStream);
					stream.data(new StringDataInfo(MessageType.NEW_MESSAGE.getValue() + senderUsername + "#" + message, false));
					break;
				}
			}

		} else
			senderUserStream.data(new StringDataInfo(MessageType.USER_NOT_FOUND.getValue() + receiverUserName, false));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.borjabonilla.chat.server.AbstractMessageProcessorServer#senToAllExceptMe(org.eclipse.jetty.spdy.api.Stream,
	 * java.lang.String)
	 */
	@Override
	public void senToAllExceptMe(Stream userStream, String message) throws Exception {
		String userName = AvailableUsers.getUsers().get(userStream);
		for (Stream stream : AvailableUsers.getUsers().keySet()) {
			if (!stream.equals(userStream)) {
				stream.data(new StringDataInfo(MessageType.NEW_BROADCAST.getValue() + userName + "#" + message, false));
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.borjabonilla.chat.server.AbstractMessageProcessorServer#newUserConnectedExceptMe(org.eclipse.jetty.spdy.api.Stream,
	 * java.lang.String)
	 */
	@Override
	public void newUserConnectedExceptMe(Stream userStream, String userName) throws Exception {
		for (Stream stream : AvailableUsers.getUsers().keySet()) {
			if (!stream.equals(userStream)) {
				stream.data(new StringDataInfo(MessageType.NEW_USER_CONNECTED.getValue() + userName, false));
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.borjabonilla.chat.server.AbstractMessageProcessorServer#userDisconnected(org.eclipse.jetty.spdy.api.Stream)
	 */
	@Override
	public void userDisconnected(Stream userStream) throws Exception {
		String userName = AvailableUsers.getUsers().get(userStream);
		AvailableUsers.removeUser(userStream);
		for (Stream stream : AvailableUsers.getUsers().keySet()) {
			stream.data(new StringDataInfo(MessageType.USER_DISCONNECTED.getValue() + userName, false));
		}
	}

}
