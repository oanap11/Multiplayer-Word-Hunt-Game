package wordhunt;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Packet {
	
	private String actionCode;
	private ArrayList<String> parameters = new ArrayList<String>();
	
	public Packet(String s) {
		if(s != null && s.length() > 0) {
			StringTokenizer tokenizer = new StringTokenizer(s,"|");
			actionCode = tokenizer.nextToken();
			
			while(tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				parameters.add(token);
			}
		}
		else {
			actionCode = "";
		}
	} 
	
	//metode pentru adaugarea de variabile de tipuri diferite la lista de parametri
	public void add(String s) {
		parameters.add(s);
	}
	
	public void add(int i) {
		parameters.add("" + i);
	}
	
	public void add(boolean b) {
		parameters.add("" + b);
	}
	
	//adauga mai multe String-uri in pachet in acelasi timp
	public void add(ArrayList<String> strings) {
		for(int i = 0; i < strings.size(); i++) {
			String s = strings.get(i);
			parameters.add(s);
		}
	}
	
	//toate pachetele trebuie convertite in String-uri inainte sa fie expediate
	public String toString() {
		String s = actionCode;
		for(int i = 0; i < parameters.size(); i++) {
			s += "|" + parameters.get(i);
		}
		return s;
	}
	
	//getteri si setteri
	public String getActionCode() {
		return actionCode;
	}
	
	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}
	
	public ArrayList<String> getParameters(){
		return parameters;	
	}
	
	public String getParameter(int index) {
		return parameters.get(index);
	}
	
	//metoda care verifica daca doua pachete sunt egale/au acelasi continut
	public boolean equals(Packet packet) {
		String thisPacketString = toString();
		String comparePacketString = packet.toString();
		return thisPacketString.equals(comparePacketString);
	}
	
}
