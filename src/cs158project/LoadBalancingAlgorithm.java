package cs158project;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Implements ConnectionProtocol
 * Defines behavior for providing connections to network resources.
 * Assigns connections in a round robin fashion.
 * 
 * @author Torjus D.
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
	
	
	//Does not save inbound-outgoing connection pair.
	public ConnectionConfiguration getResource(){
		
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
		
		return config;
	}
	
	//Given inbund connection, return corresponding outgoing connection.
	//Also adds inbound-outbound connection pairs.
	public ConnectionConfiguration getResource(ConnectionConfiguration inbound){
		 //If Already contains the connection-pair:
		for( ConnectionConfiguration configuration : connections.keySet()){
			if(configuration.getHost().equals(inbound.getHost())) return connections.get(configuration);
		}
		//else:
		ConnectionConfiguration config = getResource();
		connections.put(inbound, config);
		return config;
	}	
}