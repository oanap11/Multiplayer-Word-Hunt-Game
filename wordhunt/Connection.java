package wordhunt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Connection implements Runnable {
	
	private static final String DEFAULT_NAME = "(New Client)";
	
	private WordHuntServer server;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	private Game game;
	private String name = DEFAULT_NAME;
	private int id = 0;
	
	public Connection(WordHuntServer server, Socket socket, Game game) {
		this.server = server;
		this.socket = socket;
		this.game = game;
		
		new Thread(this).start();
		
	}

	@Override
	public void run() {
		
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			Packet packet = new Packet(ActionCode.SUBMIT);
			sendToClient(packet);
			
			boolean validName = false;
			boolean keepRunning = true;
			
			while(keepRunning) {
				String input = in.readLine();
				server.log(input + " received from: " + name);
				if(input == null) {
					keepRunning = false;
				}
				else {
					packet = new Packet(input);
					String actionCode = packet.getActionCode();
					
					switch(actionCode) {
					case ActionCode.NAME :
						String submittedName = packet.getParameter(0);
						if(game.isValidName(submittedName)) {
							validName = true;
							name = submittedName;
							packet = new Packet(ActionCode.ACCEPTED);
							int numberOfPlayers = game.getMaxNumberOfPlayers();
							packet.add(numberOfPlayers);
							sendToClient(packet);
						}
						else {
							packet = new Packet(ActionCode.REJECTED);
							sendToClient(packet);
						}
						break;
					case ActionCode.QUIT :
						keepRunning = false;
						break;
					}
				}
			}
		}
		catch(IOException e) {
			server.log("error when connecting to/communicating to the client");
		}
		finally {
			quit();
		}

	}
	
	public void sendToClient(Packet packet) {
		String packetString = packet.toString();
		out.println(packetString);
		server.log("Sent to " + name + ": " + packetString);
	}
	
	public void quit() {
		server.log("Connection ended for " + name);
		
		try {
			socket.close();
		}
		catch (IOException e) {}
	}

}
