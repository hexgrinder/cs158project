package cs158project;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Implements ConnectionProtocol
 * Defines behavior for providing connections to network resources.
 * 
 * @author Michael L.
 */
class LoadBalancingAlgorithm implements ConnectionProtocol {
	private int numberOfConnections = 0;
	private int flip = 0;
	ResourcePool resources;
	ArrayList<String> plugins; 
	//inbound and outgoing connection
	Hashtable<ConnectionConfiguration, ConnectionConfiguration> connections;
	
	LoadBalancingAlgorithm(ResourcePool resources){
		this.resources = resources;
		plugins = new ArrayList<String>();
		connections = new Hashtable<ConnectionConfiguration, ConnectionConfiguration>();
	}
	
	
	
	public ConnectionConfiguration getResource(){
		
		// code for round robin assignment of resources.
		// flips between one and zero.
		Iterator<String> iterator = resources.getAvailableResources();
		
		while (iterator.hasNext()) {
			plugins.add( iterator.next() );
		}
		
		ConnectionConfiguration config =  resources.getResourceFromName(plugins.get(flip)).configuration;
		flip = (flip + 1) % 2;
		plugins.clear();
		
		return config;
	}
	
	//Given inbund connection, return corresponding outgoing connection.
	public ConnectionConfiguration getResource(ConnectionConfiguration inbound){
		//Already contains the connection-pair.
		if(connections.containsKey(inbound)) return connections.get(inbound);
		
		// code for round robin assignment of resources.
		// flips between one and zero.
		Iterator<String> iterator = resources.getAvailableResources();
				
		while (iterator.hasNext()) {
			plugins.add( iterator.next() );
			numberOfConnections++;
		}
				
		ConnectionConfiguration config =  resources.getResourceFromName(plugins.get(flip)).configuration;
		flip = (flip + 1) % numberOfConnections;
		plugins.clear();
		numberOfConnections = 0;
		connections.put(inbound, config);
		
		return config;
	}	
}