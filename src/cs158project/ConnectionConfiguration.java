package cs158project;

import java.util.UUID;

/**
 * Contains configuration settings to build a connection.
 * 
 * @author Michael L
 */
public class ConnectionConfiguration {
	
	/**
	 * Unique connection identifier. Read-only.
	 */
	public final UUID id;
	
	/**
	 * Connection host name. Read-only.
	 */
	public final String host;
	
	/**
	 * Assigned port. Read-only.
	 */
	public final int port;
	
	/**
	 * Connection name. Read-only.
	 */
	public final String name;
	
	//Constructor 1
	public ConnectionConfiguration(String host, int port) {
		this(host, port, "");
	}
	
	//Constructor 2
	public ConnectionConfiguration(String host, int port, String name) {
		this.id = UUID.randomUUID();
		this.port = port;
		this.host = host;
		this.name = name;
	}
	public UUID getID(){
		return id;
	}
	public String host(){
		return host;
	}
	public int port(){
		return port;
	}
	public String getName(){
		return name;
	}
	
}
