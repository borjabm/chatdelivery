package com.borjabonilla.chat.client;

/**
 * Class used to control and verify chat user status.
 * 
 * @author Borja Bonilla
 * 
 */
public class VerifyUser {
	private boolean response;
	private boolean pendingServer;

	/**
	 * Constructor that sets the status.
	 */
	public VerifyUser() {
		this.response = false;
		this.pendingServer = true;
	}

	/**
	 * The user is waiting server response.
	 */
	public void pendingServerON() {
		this.pendingServer = true;
	}

	/**
	 * The user has response and does not wait the server.
	 */
	public void VerifyOk() {
		this.response = true;
		this.pendingServer = false;
	}

	/**
	 * The user does not have response neither waits the server.
	 */
	public void VerifyKO() {
		this.response = false;
		this.pendingServer = false;
	}

	/**
	 * @return Returns true if the user is waiting server response.
	 */
	public boolean pendingServer() {
		return this.pendingServer;
	}

	/**
	 * @return Returns true if the user is not waiting the server and it has a response.
	 */
	public boolean isOK() {
		return !this.pendingServer && this.response;
	}

}
