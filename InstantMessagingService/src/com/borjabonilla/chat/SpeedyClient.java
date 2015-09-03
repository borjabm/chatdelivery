package com.borjabonilla.chat;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.spdy.api.DataInfo;
import org.eclipse.jetty.spdy.api.SPDY;
import org.eclipse.jetty.spdy.api.Session;
import org.eclipse.jetty.spdy.api.Stream;
import org.eclipse.jetty.spdy.api.StreamFrameListener;
import org.eclipse.jetty.spdy.api.StringDataInfo;
import org.eclipse.jetty.spdy.api.SynInfo;
import org.eclipse.jetty.spdy.client.SPDYClient;
import org.eclipse.jetty.util.Fields;

import com.borjabonilla.chat.client.VerifyUser;
import com.borjabonilla.chat.client.impl.MessageProcessorClientImpl;
import com.borjabonilla.chat.server.protocol.MessageType;

/**
 * This class contains configuration and initialization of the client. Protocol used is SPDY.
 * 
 * @author Borja Bonilla
 * 
 */
public class SpeedyClient {

	/**
	 * Will configure and start the client.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {

		final VerifyUser usr = new VerifyUser();

		// Create listener to receive data from the server.
		StreamFrameListener streamListener = new StreamFrameListener.Adapter() {
			public void onData(Stream stream, DataInfo dataInfo) {
				// Data received from server
				String data = dataInfo.asString("UTF-8", true);
				MessageProcessorClientImpl processor = new MessageProcessorClientImpl();
				processor.process(stream, data, usr);
			}
		};
		// Create client
		SPDYClient.Factory clientFactory = new SPDYClient.Factory();
		clientFactory.start();

		// Create one SPDYClient instance
		SPDYClient client = clientFactory.newSPDYClient(SPDY.V2);
		client.setIdleTimeout(900000);

		// Create a session
		Session session = client.connect(new InetSocketAddress("localhost", 8181), null);

		// Start a new session, and configure the stream listener
		Stream stream = session.syn(new SynInfo(new Fields(), false), streamListener);

		// create a scanner so we can read the command-line input
		Scanner scanner = new Scanner(System.in);

		printWelcome();

		while (!usr.isOK()) {
			System.out.print("Enter your name: ");
			String username = scanner.nextLine();
			username = username.trim();
			if (username.length() == 0) {
				System.out.println("Empty username.");
				continue;
			}
			if (username.matches(".*[^\\w]+.*")) {
				System.out.println("Username can only contain alphanumeric characters.");
				continue;
			}
			usr.pendingServerON();
			sendServer(stream, MessageType.LOGIN.getValue() + username);
			while (usr.pendingServer()) {
				Thread.sleep(100);
			}
		}

		printMenu();

		while (true) {
			String instr = scanner.nextLine();
			if (!instr.startsWith("#") && !instr.startsWith("@")) {
				// INSTRUCTION 1
				if (instr.length() == 0) {
					System.out.println("Empty message.");
					continue;
				}
				sendServer(stream, MessageType.SEND_TO_ALL.getValue() + instr);
			} else if (instr.startsWith("@")) {
				// INSTRUCTION 2
				int space = instr.indexOf(" ");
				if (space < 2) {
					System.out.println("The correct format is @username message");
					continue;
				}
				String user = instr.substring(1, space);
				String message = instr.substring(space + 1);
				if (message.length() < 2) {
					System.out.println("The correct format is @username message");
					continue;
				}
				sendServer(stream, MessageType.SEND_TO_USER.getValue() + user + "#" + message);

			} else if (instr.equals("#users")) {
				// INSTRUCTION 3
				sendServer(stream, MessageType.GET_ALL_USERS.getValue());
			} else if (instr.equals("#exit")) {
				// INSTRUCTION 4
				sendServer(stream, MessageType.DISCONNECT_USER.getValue());
				scanner.close();
				System.exit(0);
			} else if (instr.startsWith("#")) {
				// INSTRUCTION 5
				printMenu();
			}
		}

	}

	/**
	 * Will print the welcome header.
	 */
	public static void printWelcome() {
		System.out.println("**************************************************************");
		System.out.println("*                       WELCOME TO CHAT                      *");
		System.out.println("**************************************************************");
	}

	/**
	 * Will print the menu explaining the instructions.
	 */
	public static void printMenu() {
		System.out.println("**************************************************************");
		System.out.println("*                        INSTRUCTIONS                        *");
		System.out.println("**************************************************************");
		System.out.println("* 1) To send global message, simply write it and press enter *");
		System.out.println("* 2) To send private message, use @username message          *");
		System.out.println("* 3) Use #users to view list of connected users              *");
		System.out.println("* 4) Use #exit to close chat                                 *");
		System.out.println("* 5) Use # to view this menu                                 *");
		System.out.println("**************************************************************");
	}

	/**
	 * To send a message to the server
	 * 
	 * @param stream
	 *            Stream of the session.
	 * @param message
	 *            Message
	 */
	public static void sendServer(Stream stream, String message) {
		try {
			stream.data(new StringDataInfo(message, false));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

}
