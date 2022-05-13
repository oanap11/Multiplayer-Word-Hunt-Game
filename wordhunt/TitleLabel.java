package wordhunt;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

public class TitleLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	
	private Color myColor;
	//167, 112, 148
	
	public TitleLabel(String title) {
		Font font = new Font(Font.SERIF, Font.BOLD, 32);
		setFont(font);
		setBackground(new Color(97, 164, 188));
		setForeground(Color.BLACK);
		setOpaque(true);
		setHorizontalAlignment(JLabel.CENTER);
		setText(title);
	}

}
