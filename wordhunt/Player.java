package wordhunt;

import java.util.ArrayList;

public class Player {
	private Connection connection;
	private ArrayList<Word> words = new ArrayList<Word>();
	
	private int points = 0;
	private int score = 0;
	
	public Player(Connection connection) {
		this.connection = connection;
	}
	
	public int getPoints() {
		return points;
	}
	
	public int getScore() {
		return score;
	}
	
	
	public void updatePoints() {
		points = 0;
		for(int i = 0; i < words.size(); i++) {
			Word word = words.get(i);
			points += word.getPoints(); //insumeaza punctele pt cuvintele din lista
		}
		score += points;
	}
	
	public ArrayList<Word> getWords(){
		return words;
	}
	
	private void removePoints(int index) {
		Word word = words.get(index);
		word.setPoints(0);
	}
	
	
	public void sendToClient(Packet packet) {
		connection.sendToClient(packet);
	}
	
	public void quit() {
		connection.quit();
	}
	
	public String getName() {
		return connection.getName();
	}
	
	//adauga cuvintele in lista jucatorului in ordine alfabetica
	public void addWord(String s) {
		boolean greaterThan = true;
		Word word = new Word(s);
		
		for(int i = 0; i<words.size() && greaterThan; i++) {
			Word wordFromList = words.get(i);
			String wordFromListString = wordFromList.getWord();
			
			// < 0 daca String-ul s este mai mic dpdv lexicografic 
			//decat cuvantul din lista cu care este comparat
			if(s.compareTo(wordFromListString) <= 0 ) {
				greaterThan = false;
				words.add(i, word);
			}
		}
		
		if(greaterThan) {
			words.add(word);
		}
	}
	
	//verifica daca un anumit cuvant se afla deja in lista cuvintelor gasite a jucatorului
	public boolean hasWord(String s) {
		boolean found = false;
		for(int i = 0; i < words.size() && !found; i++) {
			Word word = words.get(i);
			String wordString = word.getWord();
			if(wordString.equals(s)) {
				found = true;
			}
		}
		return found;
	}
}
