package wordhunt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

public class PointsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	public PointsDialog(ArrayList<ArrayList<String>> pointsInfo, int height) {
		initGUI(pointsInfo, height);
		
		setModal(true);
	}
	
	private void initGUI(ArrayList<ArrayList<String>> pointsInfo, int height) {
		PointsPanel pointsPanel = new PointsPanel(pointsInfo);
		int width = pointsPanel.getWidth();
		JScrollPane scrollPane = new JScrollPane(pointsPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		Dimension size = new Dimension(width, height);
		scrollPane.setPreferredSize(size);
		add(scrollPane, BorderLayout.CENTER);
	}
}
