package wordhunt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;


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
	
	private JLabel messageLabel = new JLabel("Se asteapta jucatorii...");
	private JLabel timerLabel = new JLabel("Timp ramas: 03:00");
	
	private GamePanel gamePanel = new GamePanel(this);
	private JTextArea wordListArea = new JTextArea();
	
	private ArrayList<String> words = new ArrayList<String>();
	
	private ArrayList<ArrayList<String>> pointsInfo = new ArrayList<ArrayList<String>>();
	private Timer timer;
	private int second, minute;
	private String ddSecond, ddMinute;	
	private DecimalFormat dFormat = new DecimalFormat("00");
	
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
						JOptionPane.showMessageDialog(this, name + " nu se afla pe lista invitatilor/numele acesta e folosit de alt jucator.");
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
							scorePanels[i] = new ScorePanel(0, new Color(247, 226, 226), playerName);
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
					case ActionCode.QUIT :
						String message = parameters.get(0) + " a parasit jocul.";
						JOptionPane.showMessageDialog(this, message);
						close();
						break;
					case ActionCode.SHUT_DOWN :
						JOptionPane.showMessageDialog(this, "Serverul a fost oprit.");
						close();
						break;
					case ActionCode.NEW_BOARD :
						gamePanel.setLetterTiles(parameters.get(0));
						messageLabel.setText("Start!");
						second = 0;
						minute = 3;
						countDown();
						timer.start();
						messageLabel.setBackground(Color.WHITE);
						gamePanel.allowInput(true);
						break;
					case ActionCode.ADD_WORD :
						if(gamePanel.isInputAllowed()) {
							String word = parameters.get(0);
							addWord(word);
						}
						break;
					case ActionCode.TOO_SHORT :
						messageLabel.setText("Cuvant invalid: " + parameters.get(0) + " are mai putin de 4 litere.");
						messageLabel.setBackground(new Color(253, 93, 93));
						break;
					case ActionCode.DUPLICATE :
						messageLabel.setText("Cuvant invalid: " + parameters.get(0) + " se afla deja in lista.");
						messageLabel.setBackground(new Color(248, 203, 46));
						break;
					case ActionCode.NOT_A_WORD :
						messageLabel.setText("Cuvant invalid: " + parameters.get(0) + " nu este in dictionar.");
						messageLabel.setBackground(new Color(253, 93, 93));
						break;
					case ActionCode.TIMES_UP :
						gamePanel.allowInput(false);
						gamePanel.clearSelection();
						messageLabel.setText("Timpul a expirat.");
						break;
					case ActionCode.POINTS :
						int id = pointsInfo.size();
						int newPoints = Integer.parseInt(parameters.get(1));
						scorePanels[id].addToScore(newPoints);
						pointsInfo.add(parameters);
						if(pointsInfo.size() == numberOfPlayers) {
							showPoints();
						}
						break;
					case ActionCode.WINNER :
						showWinner(parameters);
						break;
				}
			}
			
		}
		}
		catch (ConnectException e) {
			JOptionPane.showMessageDialog(this, "Serverul nu este pornit.");
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Conexiune pierduta.");
		}
		finally {
			close();
		}

	} //run
	
	private void showWinner(ArrayList<String> parameters) {
		String message = parameters.get(0);
		if(parameters.size() == 1) {
			message += " a castigat.";
		}
		else {
			for(int i = 1; i < parameters.size(); i++) {
				message += " si " + parameters.get(i);
			}
			message += " sunt la egalitate.";
		}
		
		JOptionPane.showMessageDialog(this, message);
	}
	
	public void countDown() {
		timer = new Timer(1000, new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				second--;
				
				ddSecond = dFormat.format(second);
				ddMinute = dFormat.format(minute);	
				timerLabel.setText("Timp ramas: " + ddMinute + ":" + ddSecond);
				
				if(second==-1) {
					second = 59;
					minute--;
					ddSecond = dFormat.format(second);
					ddMinute = dFormat.format(minute);	
					timerLabel.setText("Timp ramas: " + ddMinute + ":" + ddSecond);
				}
				if(minute==0 && second==0) {
					timer.stop();
				}
				
			}
		});
	}
	
	//adauga cuvantul in lista in ordine alfabetica
	private void addWord(String word) {
		if(words.size() == 0) {
			words.add(word);
			wordListArea.setText(word);
		}
		else {
			boolean greaterThan = true;
			for(int i =0; i<words.size() && greaterThan; i++) {
				String wordFromList = words.get(i);
				if(word.compareTo(wordFromList) <= 0) {
					greaterThan = false;
					words.add(i, word);
				}
			}
			if(greaterThan) {
				words.add(word);
			}
				
			wordListArea.setText(words.get(0));
			for(int i = 1; i<words.size(); i++) {
				wordListArea.append("\n" + words.get(i));
			}
		}
			
		messageLabel.setText("Cuvantul " + word + " a fost adaugat in lista.");
		messageLabel.setBackground(new Color(119, 217, 112));
	}
	
	private void showPoints() {
		int height = getContentPane().getHeight();
		PointsDialog dialog = new PointsDialog(pointsInfo, height);
		dialog.pack();
		int gamePanelWidth = gamePanel.getWidth();
		Point location = getLocationOnScreen();
		int x = location.x + gamePanelWidth;
		int y = location.y;
		dialog.setLocation(x, y);
		dialog.setVisible(true);
	}
	
	
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
		TitleLabel titleLabel = new TitleLabel("Word Hunt");
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
		mainPanel.setBackground(new Color (91, 125, 177));
		add(mainPanel, BorderLayout.CENTER);
				
		//scores panel
		//scoresPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, scoresPanel.getPreferredSize().height));
		scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.X_AXIS));
		scoresPanel.setBackground(Color.MAGENTA);
		mainPanel.add(scoresPanel);
		
		//message
		messageLabel.setAlignmentX(CENTER_ALIGNMENT);
		messageLabel.setAlignmentY(CENTER_ALIGNMENT);
		messageLabel.setOpaque(true);
		messageLabel.setBackground(Color.WHITE);
		EmptyBorder messageBorder = new EmptyBorder(5, 10, 5, 10);
		messageLabel.setBorder(messageBorder);
		mainPanel.add(messageLabel);
		
		timerLabel.setAlignmentX(CENTER_ALIGNMENT);
		timerLabel.setAlignmentY(CENTER_ALIGNMENT);
		timerLabel.setOpaque(true);
		timerLabel.setBackground(Color.WHITE);
		//EmptyBorder timerBorder = new EmptyBorder(5, 10, 5, 10);
		timerLabel.setBorder(messageBorder);
		mainPanel.add(timerLabel);
		
		//horizontal panel
		JPanel horizontalPanel = new JPanel();
		horizontalPanel.setBackground(new Color(247, 226, 226));
		horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
		EmptyBorder gameBorder = new EmptyBorder(10, 10, 10, 10);
		horizontalPanel.setBorder(gameBorder);
		mainPanel.add(horizontalPanel);
		
		//game panel
		gamePanel.setPreferredSize(new Dimension(500, 500));
		//gamePanel.setPreferredSize(getPreferredSize());
		horizontalPanel.add(gamePanel);
		
		//word list
		Insets insets = new Insets(4, 10, 10, 4);
		wordListArea.setMargin(insets);
		wordListArea.setEditable(false);
		Font font = new Font(Font.DIALOG, Font.BOLD, 12);
		wordListArea.setFont(font);
		JScrollPane scrollPane = new JScrollPane(wordListArea);
		Dimension size = new Dimension(200, 0);
		scrollPane.setPreferredSize(size);
		horizontalPanel.add(scrollPane);
		
		
	}
	
	public void send(Packet packet) {
		out.println(packet);
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
