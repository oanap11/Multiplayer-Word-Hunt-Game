package wordhunt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;

import javax.swing.JPanel;



public class GamePanel extends JPanel  {

	private static final long serialVersionUID = 1L;
	private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 50);
	
	private static final Color HIGHLIGHT = new Color(255, 255, 0, 120);
	private static final BasicStroke WIDE_STROKE = new BasicStroke(55.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	private WordHunt wordHunt;
	private int width = LetterTile.SIZE * 5;
	private int height = LetterTile.SIZE * 5;
	private LetterTile[][] tiles = new LetterTile[5][5];
	private FontMetrics fm;
	
	private boolean allowInput = false;
	private int row = -1;
	private int col = -1;
	private GeneralPath selectedPath = new GeneralPath();
	private String path = "";
	
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
		
		//listeners
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int button = e.getButton();
				if(allowInput && button == MouseEvent.BUTTON1) {
					int x = e.getX();
					int y = e.getY();
					startSelection(x, y);
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (allowInput) {
					int x = e.getX();
					int y = e.getY();
					addToSelection(x, y);
							
				}
			}
		});
	}
	
	private void addToSelection(int x, int y) {
		int newRow = y / LetterTile.SIZE;
		int newCol = x / LetterTile.SIZE;
		int movedRow = newRow - row;
		int movedCol = newCol - col;
		
		if(newRow >= 0 && newRow < 5
			&& newCol >= 0 && newCol < 5
			&& movedRow >= -1 && movedRow <= 1
			&& movedCol >= -1 && movedCol <= 1
			&& !tiles[newRow][newCol].isSelected()
			&& tiles[newRow][newCol].inBounds(x, y)){
				row = newRow;
				col = newCol;
				tiles[row][col].select();
				int centerX = tiles[row][col].getCenterX();
				int centerY = tiles[row][col].getCenterY();
				selectedPath.lineTo(centerX, centerY);
				
				repaint();
			}
	}
	
	private void startSelection(int x, int y) {
		row = y / LetterTile.SIZE;
		col = x / LetterTile.SIZE;
		tiles[row][col].select();
		int centerX = tiles[row][col].getCenterX();
		int centerY = tiles[row][col].getCenterY();
		selectedPath.moveTo(centerX, centerY);
		repaint();
	}
	
	public Dimension getPrefferedSize() {
		Dimension size = new Dimension(width, height);
		return size;
	}
	
	public void paintComponent(Graphics g) {
		//background
		g.setColor(Color.RED);
		g.fillRect(0, 0, width, height);
		
		//selected path
		g.setColor(Color.GREEN);
		Graphics2D g2D = (Graphics2D)g;
		g2D.setStroke(WIDE_STROKE);
		g2D.draw(selectedPath);
		
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
	
	public void allowInput(boolean allow) {
		allowInput = allow;
	}
	
	public boolean isInputAllowed() {
		return allowInput;
	}

}
