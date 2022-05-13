package wordhunt;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LogInDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private boolean canceled = false;
	private JTextField ipAddressField = new JTextField(2);
	private JTextField userNameField = new JTextField(2);
	private static final String FILE_NAME = "LogIn.txt";
	
	public String getIpAddress() {
		return ipAddressField.getText().trim();
	}
	
	public String getUserName() {
		return userNameField.getText().trim();
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
	public LogInDialog(String appName) {
		setTitle(appName);
		
		initGUI();
		
		setModal(true);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(FILE_NAME)));
			String ipAddress = in.readLine();
			ipAddressField.setText(ipAddress);
			String userName = in.readLine();
			userNameField.setText(userName);
			in.close();
		}catch (FileNotFoundException e) {}
		catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Eroare la citirea din fisierul " + FILE_NAME);
		}
	}
	
	public void ok() {
		canceled = false;
		String ipAddress = ipAddressField.getText().trim();
		String userName = userNameField.getText().trim();
		
		if(ipAddress.length() == 0) {
			JOptionPane.showMessageDialog(this, "Introduceti adresa ip.");
		}
		else if(userName.length() == 0)
			JOptionPane.showMessageDialog(this, "Introduceti numele de utilizator.");
		else {
			canceled = false;
			setVisible(false);
		
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(FILE_NAME)));
				out.write(ipAddress);
				out.newLine();
				out.write(userName);
				out.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Eroare la scrierea in fisierul " + FILE_NAME);
			}
		}
	}
	
	public void cancel() {
		canceled = true;
		setVisible(false);
	}
	
	private void initGUI() {
		//main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel, BorderLayout.CENTER);
		
		JLabel ipAddressLabel = new JLabel("Adresa IP:");
		mainPanel.add(ipAddressLabel);
		mainPanel.add(ipAddressField);
		
		JLabel userNameLabel = new JLabel("Utilizator:");
		mainPanel.add(userNameLabel);
		mainPanel.add(userNameField);
		
		//button panel
		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		buttonPanel.add(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		buttonPanel.add(cancelButton);
		getRootPane().setDefaultButton(okButton);
		
		//listeners
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				cancel();
				//System.exit(0);
			}
		});
	
	}//initGUI
}
