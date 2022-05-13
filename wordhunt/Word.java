package wordhunt;

public class Word {
	private String word = "";
	private int points = 0;
	
	public Word(String word) {
		this.word = word;
		int length = word.length();
		
		if(length > 3) {
			switch(length) {
			case 4 :
				points = 1;
				break;
			case 5 :
				points = 2;
				break;
			case 6 :
				points = 3;
				break;
			case 7 :
				points = 5;
				break;
			default :
				points = 11;
			}
		}
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public String getWord() {
		return word;
	}
}
