package cs158project;
import java.net.Socket;
import java.util.HashMap;

//Class for Hash map / list of connections
class ConnectionList{
	HashMap<Socket, Socket> connections;
	
	ConnectionList(){
		connections = new HashMap<Socket,Socket>();
	}
	
	// Adds a pair of connections to the set
	public void add(Socket s, Socket t){
		connections.put(s,t);
	}
	
	// Looks up socket for given IP. 
	// Returns socket if found, else returns null
	public Socket get(String t){
		Socket socket = null;
		//iterates over the values
		for( Socket s : connections.values()){
			if(socket.getInetAddress().toString() == t) socket = s;
		}
		//iterates over the keys
		for( Socket s: connections.keySet()){
			if(socket.getInetAddress().toString() == t) socket = s;
		}
		return socket;
	}
}