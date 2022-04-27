package wordhunt;

import java.util.ArrayList;

public class Game {
	
	private ArrayList<String> availableNames;
	private int playersReady = 0;
	private int maxNumberOfPlayers;
	
	public Game(ArrayList<String> availableNames) {
		this.availableNames = availableNames;
		maxNumberOfPlayers = availableNames.size();
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
}
