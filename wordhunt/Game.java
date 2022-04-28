package wordhunt;

import java.util.ArrayList;

public class Game {
	
	private ArrayList<String> availableNames;
	private int playersReady = 0;
	private int maxNumberOfPlayers;
	private Player[] players;
	
	public Game(ArrayList<String> availableNames) {
		this.availableNames = availableNames;
		maxNumberOfPlayers = availableNames.size();
		players = new Player[maxNumberOfPlayers];
	}
	
	public int getMaxNumberOfPlayers(){
		return maxNumberOfPlayers;
	}
	
	public boolean isValidName(String name) {
		boolean validName = false;
		
		if(availableNames.contains(name)) {
			validName = true;
			availableNames.remove(name);
		}
		
		return validName;
	}
	
	public void broadcast(Packet packet) {
		for(int i = 0; i <players.length; i++) {
			if(players[i] != null) {
				players[i].sendToClient(packet);
			}
		}
	}
	
	public synchronized int addPlayer(Connection connection) {
		int playerId = playersReady;
		players[playerId] = new Player(connection);
		playersReady++;
		
		//broadcast the names of the players logged in so far
		Packet packet = new Packet(ActionCode.PLAYERS);
		for(int p = 0; p < players.length; p++) {
			String name = "Player " + p;
			if(players[p] != null) {
				name = players[p].getName();
			}
			packet.add(name);
		}
		broadcast(packet);
		
		return playerId;
	}
}
