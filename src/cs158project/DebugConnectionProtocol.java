package cs158project;

/**
 * Returns a single connection configuration. Debug purposes only.
 *
 * @author Michael L.
 */
public class DebugConnectionProtocol implements ConnectionProtocol {

	private final ConnectionConfiguration CONFIG;
	
	/**
	 * Constructor does not assign resources.
	 * 
	 * Sets default configuration to ("10.10.10.1", 80, "DEBUG").
	 *  
	 * @param resources IGNORED
	 */
	public DebugConnectionProtocol(ResourcePool resources) {
		this("10.10.10.1", 80, "DEBUG");
	}
	
	/**
	 * Configures the protocol to return a connection configuration
	 * specified by the parameters.
	 * 
	 * @param host Host name
	 * @param port Port number
	 * @param name Host alias
	 */
	public DebugConnectionProtocol(String host, int port, String name) {
		CONFIG = new ConnectionConfiguration(host, port, name);
	}
	
	@Override
	public ConnectionConfiguration getResource() {
		return CONFIG;
	}

	@Override
	public ConnectionConfiguration getResource(ConnectionConfiguration config) {
		return CONFIG;
	}

}
