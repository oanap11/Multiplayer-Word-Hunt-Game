package wordhunt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScorePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private int initialScore = 0;
	private int score = 0;
	
	private JLabel scoreTitleLabel;
	private JLabel scoreLabel = new JLabel("0");
	
	public ScorePanel(int initialScore, Color color, String text) {
		this.initialScore = initialScore;
		score = initialScore;
		setBackground(color);
		
		scoreTitleLabel = new JLabel(text);
		Font smallFont = new Font(Font.DIALOG, Font.PLAIN, 12);
		scoreTitleLabel.setFont(smallFont);
		add(scoreTitleLabel);
		
		Font bigFont = new Font(Font.DIALOG, Font.BOLD, 36);
		scoreLabel.setFont(bigFont);
		Dimension size = new Dimension(60, 36);
		scoreLabel.setPreferredSize(size);
		scoreLabel.setText("" + score);
		add(scoreLabel);
	}
	
	public void setTitleLabel(String title) {
		scoreTitleLabel.setText(title);
	}
	
	public void addToScore(int points) {
		score += points;
		scoreLabel.setText("" + score);
	}
	
	public int getScore() {
		return score;
	}
	
	public void reset() {
		score = initialScore;
		scoreLabel.setText("" + score);
	}

}
