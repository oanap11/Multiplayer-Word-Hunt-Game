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
	
	private WordHunt client;
	private int width = LetterTile.SIZE * 6;
	private int height = LetterTile.SIZE * 6;
	private LetterTile[][] tiles = new LetterTile[6][6];
	private FontMetrics fm;
	
	private boolean allowInput = false;
	private int row = -1;
	private int col = -1;
	private GeneralPath selectedPath = new GeneralPath();
	private String path = "";
	
	private Packet packet;
	
	public GamePanel(WordHunt client) {
		this.client = client;
		fm = getFontMetrics(FONT);
		
		for(int row=0; row<6; row++) {
			for(int col = 0; col <6; col++) {
				tiles[row][col] = new LetterTile("", fm, row, col);
			}
		}
		
		initGUI();
	}
	
	private void initGUI() {
		setFont(FONT);
		
		addMouseListener(new MouseAdapter() {
			//selecteaza prima litera din selectie la click-stanga
			public void mousePressed(MouseEvent e) {
				int button = e.getButton();
				if(allowInput && button == MouseEvent.BUTTON1) {
					int x = e.getX();
					int y = e.getY();
					startSelection(x, y);
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				int button = e.getButton();
				if(allowInput && button == MouseEvent.BUTTON1) {
					endSelection();
				}
				
			}
		});
		
		//listener pentru deplasarea mouse-ului
		addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
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
		
		//conditii pentru adaugarea unei litere la selectie:
		//cursorul nu s-a deplasat mai mult de un rand sau o coloana,
		//celula nu a fost deja selectata in cadrul aceleasi selectii, 
		//cursorul este localizat in limitele celulei
		boolean A =  newRow >= 0 && newRow < 6;
		boolean B = newCol >= 0 && newCol < 6;
		boolean C = movedRow >= -1 && movedRow <= 1;
		boolean D = movedCol >= -1 && movedCol <= 1;
		boolean E = !tiles[newRow][newCol].isSelected();
		boolean F = tiles[newRow][newCol].inBounds(x, y);
		
		if(A && B && C && D && E && F){
			row = newRow;
			col = newCol;
			tiles[row][col].select();
			int centerX = tiles[row][col].getCenterX();
			int centerY = tiles[row][col].getCenterY();
			selectedPath.lineTo(centerX, centerY);
				
			repaint();
			
			//(jos, +) (sus, -) (dreapta, +) (stanga, -)
			if(movedRow > 0) {
				path += "+"; //dreapta
			}
			else if(movedRow < 0) {
				path += "-"; //stanga
			}
			else {
				path += "0"; //acelasi rand
			}
				
				
			if(movedCol > 0) {
				path += "+"; //jos
			}
			else if(movedCol < 0) {
				path += "-"; //sus
			}
			else {
				path += "0"; //aceeasi coloana
			} 
					
		}
	}
	
	private void endSelection() {
		row = -1;
		col = -1;
		selectedPath.reset();
		for(int row = 0; row <6; row++) {
			for(int col = 0; col <6; col++) {
				tiles[row][col].unselect();
			}
		}
		repaint();
		
		packet.add(path);
		path = "";
		client.send(packet);
	}
	
	private void startSelection(int x, int y) {
		row = y / LetterTile.SIZE;
		col = x / LetterTile.SIZE;
		tiles[row][col].select();
		int centerX = tiles[row][col].getCenterX();
		int centerY = tiles[row][col].getCenterY();
		selectedPath.moveTo(centerX, centerY);
		repaint();
		
		packet = new Packet(ActionCode.WORD);
		packet.add(row);
		packet.add(col);
	}
	
	public void clearSelection() {
		selectedPath.reset();
		path = "";
		repaint();
	}
	
	public Dimension getPrefferedSize() {
		Dimension size = new Dimension(width, height);
		return size;
	}
	
	public void paintComponent(Graphics g) {
		//background
		g.setColor(new Color (97, 164, 188));
		g.fillRect(0, 0, width, height);
		
		//directia de selectie
		g.setColor(Color.GREEN);
		Graphics2D g2D = (Graphics2D)g;
		g2D.setStroke(WIDE_STROKE);
		g2D.draw(selectedPath);
		
		//celulele
		for(int row=0; row <6; row++) {
			for(int col=0; col <6; col++) {
				tiles[row][col].draw(g);
			}
		}
		
	}
	
	//metoda pentru popularea panoului de joc cu litere
	public void setLetterTiles(String letters) {
		int i = 0;
		for(int row = 0; row < 6; row++) {
			for(int col = 0; col < 6; col++) {
				char character = letters.charAt(i);
				String letter = "" + character;

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
