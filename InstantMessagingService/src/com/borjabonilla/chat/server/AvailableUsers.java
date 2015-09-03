package com.borjabonilla.chat.server;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.spdy.api.Stream;

/**
 * Class for save users in memory
 * @author borja.bonilla
 *
 */
public class AvailableUsers {

	/**
	 * The set of all names of clients in the chat room. Maintained so that we
	 * can check that new clients are not registering name already in use.
	 */
	public static Map<Stream, String> users = new HashMap<Stream, String>();
	
	public AvailableUsers() {
	}

	/**
	 * @return the users
	 */
	public static Map<Stream, String> getUsers() {
		return users;
	}
	
	/**
	 * add User
	 * @param stream
	 * @param userName
	 */
	public static void addUser(Stream stream, String userName){
		users.put(stream,userName);
	}
	
	public static void removeUser(Stream stream) {
		users.remove(stream);
	}
	
	
	

}
