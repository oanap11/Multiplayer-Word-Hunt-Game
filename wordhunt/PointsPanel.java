package wordhunt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class PointsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int MARGIN = 5;
	private static final int ROW_SPACING = 15;
	private static final int POINT_WIDTH = 20;
	private static final int WORD_WIDTH = 100;
	
	private ArrayList<ArrayList<String>> pointsInfo;
	private int columns = 0;
	private int rows = 0;
	private int[] columnLoc;
	private int width = MARGIN * 2;
	private int height = MARGIN * 2;
	private FontMetrics fm;
	
	public PointsPanel(ArrayList<ArrayList<String>> pointsInfo) {
		this.pointsInfo = pointsInfo;
		
		//numarul de randuri este decis in functie de 
		//cea mai mare ArrayList
		int numberOfElements = 0;
		for(int i = 0; i<pointsInfo.size(); i++) {
			ArrayList<String> playerInfo = pointsInfo.get(i);
			int size = playerInfo.size();
			if(size > numberOfElements) {
				numberOfElements = size;
			}
		}
		
		// pe fiecare rand vor fi afisate 2 elemente (puncte si cuvant) 
		//cu exceptia primelor 2 randuri pe care vor fi afisate numele si totalul
		rows = numberOfElements / 2 + 1;
		
		//afiseaza 2 coloane pentru fiecare jucator
		columns = pointsInfo.size() * 2;
		
		//determina latimea si inaltimea panoului si locatia fiecarei coloane
		columnLoc = new int[columns];
		int x = MARGIN;
		for(int i = 0; i<columns; i+=2) {
			columnLoc[i] = x;
			x += POINT_WIDTH;
			columnLoc[i+1] = x;
			x+= WORD_WIDTH;
			width += POINT_WIDTH + WORD_WIDTH;
		}
		
		height += rows * ROW_SPACING;
		
		Font font = new Font(Font.DIALOG, Font.BOLD, 12);
		setFont(font);
		fm = getFontMetrics(font);
	}
	
	public Dimension getPrefferedSize() {
		Dimension size = new Dimension(width, height);
		return size;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		
		g.setColor(Color.black);
		for(int i = 0; i < pointsInfo.size(); i++) {
			ArrayList<String> playerInfo = pointsInfo.get(i);
			int x = columnLoc[i * 2];
			
			//afiseaza nume
			String s = playerInfo.get(0);
			int y = MARGIN + ROW_SPACING;
			g.drawString(s,  x, y);
			
			//total
			s = playerInfo.get(1);
			y += ROW_SPACING;
			g.drawString(s,x,y);
			
			//lista de cuvinte
			for(int p=2; p<playerInfo.size(); p+=2) {
				y += ROW_SPACING;
				
				String points = playerInfo.get(p);
				x = columnLoc[i * 2];
				g.drawString(points, x, y);
				
				String word = playerInfo.get(p+1);
				x = columnLoc[1 + i * 2];
				g.drawString(word, x, y);
			}
		}
	}

}
