package wordhunt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Connection implements Runnable {
	
	private static final String DEFAULT_NAME = "(Client nou)";
	
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
				server.log(input + " trimis de: " + name);
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
							
							id = game.addPlayer(this);
							server.log(name + " a intrat in joc.");
						}
						else {
							packet = new Packet(ActionCode.REJECTED);
							sendToClient(packet);
						}
						break;
					case ActionCode.QUIT :
						keepRunning = false;
						break;
					default :
						if(validName && input.length() > 0) {
							game.processInput(id, input);
						}
					}
				}
			}
		}
		catch(IOException e) {
			server.log("Eroare la comunicarea cu clientul.");
		}
		finally {
			quit();
		}

	}
	
	public void sendToClient(Packet packet) {
		String packetString = packet.toString();
		out.println(packetString);
		server.log("Trimis catre " + name + ": " + packetString);
	}
	
	public String getName() {
		return name;
	}
	
	public void quit() {
		server.log("Conexiune terminata pentru " + name);
		
		if(!name.equals(DEFAULT_NAME)) {
			Packet packet = new Packet(ActionCode.QUIT);
			packet.add(name);
			game.sendToOpponents(id, packet);
		}
		
		try {
			socket.close();
		}
		catch (IOException e) {}
	}

}
