package wordhunt;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;


public class WordHuntServer extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private static int PORT = 51593;
	
	private String FILE_NAME = "PlayerNames.txt";
	
	private JTextField namesField = new JTextField("Jucator 1, Jucator 2");
	private JTextArea logArea = new JTextArea(10, 30);
	private JButton startStopButton = new JButton("Start");
	
	private ArrayList<String> names;
	private boolean listening = false;
	private ServerSocket serverSocket;
	private Game game;
	
	public WordHuntServer() {
		
		initGUI();
		
		setTitle("Word Hunt Server");
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		//de fiecare data cand ferestra este deschisa,
		//citeste numele din fisier si le afiseaza in namesField 
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(FILE_NAME)));
			String namesString = "";
			String name = in.readLine();
			
			while(name != null) {
				namesString += name + ",";
				name = in.readLine();
			}
			namesString = namesString.substring(0, namesString.length() - 1);
			namesField.setText(namesString);
			in.close();		
		}
		catch(FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Fisierul " + FILE_NAME + " nu a fost gasit");
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(this, "Eroare la citirea din fisierul " + FILE_NAME );
		}
		
	}//constructor
	
	private void initGUI() {
		TitleLabel titleLabel = new TitleLabel("Word Hunt Server");
		add(titleLabel, BorderLayout.PAGE_START);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				stopServer();
				System.exit(0);
			}
		});
		
		//main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel, BorderLayout.CENTER);
		
		//options panel
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		mainPanel.add(optionsPanel);
				
		JLabel namesLabel = new JLabel("Introduceti numele jucatorilor invitati (separate prin virgula): ");
		optionsPanel.add(namesLabel);
		namesField.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				JTextField field = (JTextField)e.getSource();
					field.selectAll();
			}
		});
		optionsPanel.add(namesField);
		
		//log area
		logArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(logArea);
		mainPanel.add(scrollPane);
		DefaultCaret caret = (DefaultCaret)logArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
				
		//button panel
		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.PAGE_END);
		startStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startServer();
			}
		});
		buttonPanel.add(startStopButton);
		getRootPane().setDefaultButton(startStopButton);
				
	}//initGUI
	
	private void startServer() {
		if(listening == false) {
			String namesString = namesField.getText();
			StringTokenizer tokenizer = new StringTokenizer(namesString, ",");
			names = new ArrayList<String>();
			
			while(tokenizer.hasMoreTokens()) {
				String name = tokenizer.nextToken().trim();
				names.add(name);
			}
			
			if(names.size() > 0) {
				listening = true;
				
				game = new Game(names);
				log("A fost creat un nou joc.");
				
				new Thread(this).start();
				startStopButton.setText("Stop");
				
				//de fiecare data cand este apasat butonul de start, numele introduse 
				//in namesField sunt salvate in fisierul PlayerNames.txt
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter(new File(FILE_NAME)));
					for(int i = 0; i< names.size(); i++) {
						String name = names.get(i);
						out.write(name);
						out.newLine();
					}
					out.close();
				}
				catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Eroarea la scrierea in fisierul " + FILE_NAME);
				}
			}
			else {
				JOptionPane.showMessageDialog(this, "Introduceti cel putin un nume.");
			}
		}
		else {
			stopServer();
		}
	}//startServer()
	
	public void stopServer() {
		listening = false;
		startStopButton.setText("Start");
		log("Serverul a fost oprit.");
		
		//nu mai asculta conexiuni
		if(serverSocket != null && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
			}
			catch (Exception e) {
				log("Exceptie generata la oprirea serverului.");
				log(e.getMessage());
			}
		}
		
		// opreste jocul
		if(game != null) {
			game.shutDown();
			game = null;
		}
	}//stopServer
	
	//metoda pentru a afisa informatii in fereastra de server
	public void log(String message) {
		Date time = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss: ");
		String timeStamp = dateFormat.format(time);
		logArea.append(timeStamp + message + "\n");
	}

	@Override
	public void run() {
		log("Serverul a fost pornit.");
		try {
			serverSocket = new ServerSocket(PORT);
			
			while(listening) {
				Socket socket = serverSocket.accept();
				new Connection(this, socket, game);
			}
		} catch (IOException e) {
			//ignora eroarea de tip IOException generata la apasarea butonului de stop 
			//(listening ia valoarea false)
			if(listening) {
				log("Exceptie la ascultarea portului " + PORT);
				log(e.getMessage());
				stopServer();
			}
		}

	}

	public static void main(String[] args) {
		
		try {
			String className = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(className);
		} catch (Exception e) {} 
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				new WordHuntServer();
			}
		});

	}

}
