package wordhunt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;


public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 50);
	
	private WordHunt wordHunt;
	private int width = LetterTile.SIZE * 5;
	private int height = LetterTile.SIZE * 5;
	private LetterTile[][] tiles = new LetterTile[5][5];
	private FontMetrics fm;
	
	public GamePanel(WordHunt wordHunt) {
		this.wordHunt = wordHunt;
		fm = getFontMetrics(FONT);
		
		for(int row=0; row<5; row++) {
			for(int col = 0; col <5; col++) {
				tiles[row][col] = new LetterTile("", fm, row, col);
			}
		}
		
		initGUI();
	}
	
	private void initGUI() {
		setFont(FONT);
	}
	
	public Dimension getPrefferedSize() {
		Dimension size = new Dimension(width, height);
		return size;
	}
	
	public void paintComponent(Graphics g) {
		//background
		g.setColor(Color.RED);
		g.fillRect(0, 0, width, height);
		
		//LETTER TILES
		for(int row=0; row <5; row++) {
			for(int col=0; col <5; col++) {
				tiles[row][col].draw(g);
			}
		}
		
	}
	
	public void setLetterTiles(String letters) {
		int i = 0;
		for(int row = 0; row < 5; row++) {
			for(int col = 0; col < 5; col++) {
				char character = letters.charAt(i);
				String letter = "" + character;
				if(character == 'Q') {
					letter = "Qu";
				}

				tiles[row][col] = new LetterTile(letter, fm, row, col);
				i++;
			}
		}
		repaint();
	}

}
