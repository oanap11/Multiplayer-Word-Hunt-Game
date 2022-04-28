package wordhunt;

public class Player {
	private Connection connection;
	
	public Player(Connection connection) {
		this.connection = connection;
	}
	
	public void sendToClient(Packet packet) {
		connection.sendToClient(packet);
	}
	
	public void quit() {
		connection.quit();
	}
	
	public String getName() {
		return connection.getName();
	}
}
