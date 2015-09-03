package com.borjabonilla.chat.server.protocol;

/**
 * This enum will hold the protocol headers.
 * 
 * @author Borja Bonilla
 * 
 */
public enum MessageType {

	LOGIN("#LOGIN#"), // Request: Login
	LOGIN_OK("#LOGINOK#"), // Response: Login OK
	LOGIN_NOK("#LOGINNOK#"), // Response: Login NOK
	GET_ALL_USERS("#GETUSERS#"), // Request: Getusers. The client asks for all
									// users.
	LIST_ALL_USERS("#LISTUSERS#"), // Response: Listusers. The server responds
									// with a complete list of users.
	LIST_ALL_USERS_EMPTY("#LISTUSERSEMPTY#"), // Response: Listusersempty. There
												// are no users.
	SEND_TO_USER("#SENDTO#"), // Request: Sendto. The client wants to send a
								// message to a given user.
	USER_NOT_FOUND("#USERNOTFOUND#"), // Response: Usernotfound. The recipient
										// of the message is not found or does
										// not exist.
	NEW_MESSAGE("#NEWMESSAGE#"), // Response: Newmessage. The client receives a
									// message from another user.
	SEND_TO_ALL("#SENDALL#"), // Request: SendAll.The client wants to send a
								// message to all users.
	NEW_BROADCAST("#NEWBROADCAST#"), // Response: Newbroadcast. The client
										// receives a broadcast message from
										// another user.
	NEW_USER_CONNECTED("#NEWUSER#"), // NewUserConnected. Clients will be
										// notified when a new user joins.
	DISCONNECT_USER("#DISCONNECTUSER#"), // Disconnectuser. Client wants to
											// disconnect.
	USER_DISCONNECTED("#USERDISCONNECTED#");// UserDisconnected. Clients will be
											// notified when a logged-in user
											// disconnects.

	private String value;

	/**
	 * Constructor.
	 * 
	 * @param value
	 */
	MessageType(String value) {
		this.value = value;
	}

	/**
	 * Getter method
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

}
