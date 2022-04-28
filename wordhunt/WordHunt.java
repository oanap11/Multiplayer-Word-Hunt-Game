package wordhunt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class WordHunt extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private static final int PORT_NUMBER = 51593;
	
	private LogInDialog logInDialog = new LogInDialog("Word Hunt");
	private String host = "";
	private String name = "";
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	private boolean keepRunning = true;
	private int numberOfPlayers = 0;
	private JPanel scoresPanel = new JPanel();
	private ScorePanel[] scorePanels;
	
	public WordHunt() {
		logIn();
		
		new Thread(this).start();
	}
	
	private void logIn() {
		logInDialog.setVisible(true);
		
		if(!logInDialog.isCanceled()) {
			host = logInDialog.getIpAddress();
			name = logInDialog.getUserName();
		}
		else {
			close();
		}
	}
	
	public void close() {
		
		keepRunning = false;
		try {
			if(out != null) {
				Packet packet = new Packet(ActionCode.QUIT);
				out.println(packet);
			}
			if(socket != null) {
				socket.close();
			}
		}
		catch (Exception e) {} 
		System.exit(0);
	}

	@Override
	public void run() {
		
		try {
			socket = new Socket(host, PORT_NUMBER);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			boolean keepRunning = true;
			
			while(keepRunning) {
				String input = in.readLine();
				if(input == null) {
					keepRunning = false;
				}
				else if(input.length() > 0) {
					Packet packet = new Packet(input);
					String actionCode = packet.getActionCode();
					ArrayList<String> parameters = packet.getParameters();
					
					switch(actionCode) {
					case ActionCode.SUBMIT :
						packet = new Packet(ActionCode.NAME);
						packet.add(name);
						out.println(packet);
						break;
					case ActionCode.REJECTED :
						JOptionPane.showMessageDialog(this, name + " was not invited to the game / the name is already used");
						logIn();
						packet = new Packet(ActionCode.NAME);
						packet.add(name);
						out.println(packet);
						break;
					case ActionCode.ACCEPTED :
						numberOfPlayers = Integer.parseInt(parameters.get(0));
						scorePanels = new ScorePanel[numberOfPlayers];
						for(int i=0; i <numberOfPlayers; i++) {
							int playerNumber = i + 1;
							String playerName = "Player " + playerNumber;
							scorePanels[i] = new ScorePanel(0, Color.YELLOW, playerName);
							scoresPanel.add(scorePanels[i]);
						}
						openWindow();
						break;
					case ActionCode.PLAYERS :
						for(int i = 0; i < parameters.size(); i++) {
							String name = parameters.get(i);
							scorePanels[i].setTitleLabel(name);
						}
						pack();
						break;
					}
				}
			}
			
		}
		catch (ConnectException e) {
			JOptionPane.showMessageDialog(this, "Server not running");
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Lost connection");
		}
		finally {
			close();
		}

	} //run
	
	public void openWindow() {
		initGUI();
		
		setTitle(name);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	private void initGUI() {
		TitleLabel titleLabel = new TitleLabel("Word Hunt Server");
		add(titleLabel, BorderLayout.PAGE_START);
		
		//listeners
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		
		//main panel
				JPanel mainPanel = new JPanel();
				mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
				mainPanel.setBackground(Color.MAGENTA);
				add(mainPanel, BorderLayout.CENTER);
				
				//scores panel
				scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.X_AXIS));
				scoresPanel.setBackground(Color.MAGENTA);
				mainPanel.add(scoresPanel);
	}

	public static void main(String[] args) {
		
		try {
			String className = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(className);
		} catch (Exception e) {} 
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				new WordHunt();
			}
		});

	}

}
