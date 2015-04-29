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
	
	/**
	 * Constructor.
	 * 
	 * @param resources - Connections to resources that the algorithm 
	 * will draw from.
	 */
	public LoadBalancingAlgorithm(ResourcePool resources){
		this.resources = resources;
		plugins = new ArrayList<String>();
		connections = new Hashtable<ConnectionConfiguration, ConnectionConfiguration>();
	}
	
	/**
	 * Retrieves the next available connection to a resource.
	 * Does not save inbound-outgoing connection pair. 
	 */
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
	
	/**
	 * Given inbound connection, return corresponding outgoing connection.
	 * Also adds inbound-outbound connection pairs.
	 *
	 * @param inbound - A configuration describing the inbound connection. This
	 * will be used to find a resource previously assigned. If there is none
	 * found, the next available resource will be assigned to the inbound 
	 * IP address.
	 * @return A ConnectionConfiguration to a resource.
	 */
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