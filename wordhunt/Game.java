package wordhunt;

import java.util.ArrayList;
import java.util.Random;

public class Game {
	private static final String LETTERS = 
	"AAAAAAAAABBCCCCCDDDDEEEEEEEEEEEEEEEGGGHHHIIIIIIIIIIIIJKLLLLLLLMMMMMNNNNNNNNNOOOOOOOOPPPPQRRRRRRRRRSSSSSSSSSSSSTTTTTTTTUUUUVWXYYZ";
	
	private ArrayList<String> availableNames;
	private int playersReady = 0;
	private int maxNumberOfPlayers;
	private Player[] players;
	private String[][] board = new String[5][5];
	
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
		
		waitForOtherPlayers();
		
		return playerId;
	}
	
	public void waitForOtherPlayers() {
		if(playersReady == players.length) {
			playersReady = 0;
			playGame();
		}
	}
	
	public void playGame() {
		String letters = generateNewBoard();
		Packet packet = new Packet(ActionCode.NEW_BOARD);
		packet.add(letters);
		broadcast(packet);
	}
	
	//sends a packet to all the opponents of a given player
	public void sendToOpponents(int playerId, Packet packet) {
		for(int i = 0; i < players.length; i++) {
			if(i != playerId && players[i] != null) {
				players[i].sendToClient(packet);
			}
		}
	}
	
	public void shutDown() {
		Packet packet = new Packet(ActionCode.SHUT_DOWN);
		broadcast(packet);
		
		for(int i = 0; i <players.length; i++) {
			if(players[i] != null) {
				players[i].quit();
			}
		}
	}
	
	private String generateNewBoard() {
		String letters = "";
		Random rand = new Random();
		String chooseFrom = LETTERS;
		for(int row=0; row<5; row++) {
			for(int col =0; col<5; col++) {
				int chooseFromLength = chooseFrom.length();
				int pick = rand.nextInt(chooseFromLength);
				char letter = chooseFrom.charAt(pick);
				letters += letter;
				if(letter == 'Q') {
					board[row][col] = "QU";
				}
				else {
					board[row][col] = "" + letter;
				}
				
				//remove picked letter
				String beforePick = chooseFrom.substring(0, pick);
				String afterPick = chooseFrom.substring(pick + 1);
				chooseFrom = beforePick + afterPick;
			}
		}
		return letters;
	}
}//class
