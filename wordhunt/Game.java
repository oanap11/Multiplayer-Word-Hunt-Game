package wordhunt;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Game {
	private static final String LETTERS = 
	"AAAAAAAAABBCCCCCDDDDEEEEEEEEEEEEEEEFGGGHHHIIIIIIIIIIIIJKLLLLLLLMMMMMNNNNNNNNNOOOOOOOOPPPPQNNNNNNNNNSSSSSSSSSSSSTTTTTTTTUUUUVWXYYZ";
	
	private ArrayList<String> availableNames;
	private int playersReady = 0;
	private int maxNumberOfPlayers;
	private Player[] players;
	private String[][] board = new String[6][6];
	private int WINNING_SCORE = 10;
	
	private Dictionary dictionary = new Dictionary();
	
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
		
		//transmite numele jucatorilor care s-au logat pana la momentul respectiv
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
		
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(180000);
				}
				catch(InterruptedException e) {}
				
				timesUp();
			}
		}).start();
	}
	
	private void timesUp() {
		Packet packet = new Packet(ActionCode.TIMES_UP);
		broadcast(packet);
		playersReady = 0;
		
		int highScore = 0;
		Packet winnerPacket = new Packet(ActionCode.WINNER);
		
		//pentru fiecare jucator in parte, transmite numele, punctele si 
		//lista de cuvinte gasite
		for(int p=0; p<players.length;p++) {
			players[p].updatePoints();
			Packet packet1 = new Packet(ActionCode.POINTS);
			packet1.add(players[p].getName());
			packet1.add(players[p].getPoints());
			ArrayList<Word> words = players[p].getWords();
			for(int w=0; w<words.size(); w++) {
				Word word = words.get(w);
				packet1.add(word.getPoints());
				packet1.add(word.getWord());
			}
			broadcast(packet1);
			
			int score = players[p].getScore();
			if(score >= WINNING_SCORE) {
				if(score > highScore) {
					highScore = score;
					winnerPacket = new Packet(ActionCode.WINNER);
				}
				if(score == highScore) {
					String name = players[p].getName();
					winnerPacket.add(name);
				}
			}
		}
		ArrayList<String> winners = winnerPacket.getParameters();
		if(winners.size() > 0) {
			broadcast(winnerPacket);
		}
	}
	
	//trimite un pachet tuturor oponentilor unui jucator
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
		for(int row=0; row<6; row++) {
			for(int col =0; col<6; col++) {
				int chooseFromLength = chooseFrom.length();
				int pick = rand.nextInt(chooseFromLength);
				char letter = chooseFrom.charAt(pick);
				letters += letter;
				
				board[row][col] = "" + letter;
				
				//scoate din lista litera aleasa
				String beforePick = chooseFrom.substring(0, pick);
				String afterPick = chooseFrom.substring(pick + 1);
				chooseFrom = beforePick + afterPick;
			}
		}
		return letters;
	}
	
	public void processInput(int playerId, String input) {
		Packet packet = new Packet(input);
		String actionCode = packet.getActionCode();
		
		switch(actionCode) {
		case ActionCode.WORD :
			ArrayList<String> parameters = packet.getParameters();
			//cuvintele trebuie sa aiba 3 parametri: randul de unde incepe selectia, 
			//coloana de unde incepe selectia si un sir de -1,0,1 care indica directia selectiei
			if(parameters.size() == 3) {
				int row = Integer.parseInt(parameters.get(0));
				int col = Integer.parseInt(parameters.get(1));
				String path = parameters.get(2);
				String word = pathToWord(row, col, path);
				validateWord(word, players[playerId]);
			}
			break;
		}
	}
	
	
	public String pathToWord(int row, int col, String path) {
		String word = board[row][col];
		ArrayList<Point> usedCells = new ArrayList<Point>();
		Point usedCell = new Point(row, col);
		usedCells.add(usedCell);
		
		try {
			boolean valid =true;
			for(int i = 0; i<path.length() && valid; i += 2) {
				char code = path.charAt(i);
				if(code == '+') {
					row++;
				}
				else if(code == '-') {
					row--;
				}
				
				code = path.charAt(i+1);
				if(code == '+') {
					col++;
				}
				else if(code == '-') {
					col--;
				}
				
				//clientul nu poate folosi de mai multe ori aceeasi celula in cadrul
				//unei singure selectii
				usedCell = new Point(row, col);
				if(usedCells.contains(usedCell)) {
					word = "";
					valid = false;
				}
				else {
					usedCells.add(usedCell);
					word += board[row][col];
				}
			}
		}
		catch (Exception e) {
			word = "";
		}
		return word;
	}
	
	//metoda verifica daca cuvantul intruneste toate
	//toate conditiile de validitate si adauga cuvantul in lista jucatorului
	public void validateWord(String word, Player player) {
		if(word.length() < 4) {
			Packet packet = new Packet(ActionCode.TOO_SHORT);
			packet.add(word);
			player.sendToClient(packet);
		}
		else if(player.hasWord(word)) {
			Packet packet = new Packet(ActionCode.DUPLICATE);
			packet.add(word);
			player.sendToClient(packet);
		}
		else if(!dictionary.isAWord(word)) {
			Packet packet = new Packet(ActionCode.NOT_A_WORD);
			packet.add(word);
			player.sendToClient(packet);
		}
		else {
			player.addWord(word);
			Packet packet = new Packet(ActionCode.ADD_WORD);
			packet.add(word);
			player.sendToClient(packet);
		}
	}
}//class
