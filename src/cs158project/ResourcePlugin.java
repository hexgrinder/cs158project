package cs158project;

/**
 * Represents a network resource.
 * 
 * @author Michael L
 */
public class ResourcePlugin {
	
	public ResourcePlugin() {
		this(null, 0, null);
	}
	
	/**
	 * ResourcePlugin constructor.
	 * 
	 * @param host Network resource host name.
	 * @param port Network resource port number.
	 * @param name Resource alias.
	 */
	public ResourcePlugin(String host, int port, String name) {
		this.configuration = new ConnectionConfiguration(host, port, name);
	}

	/**
	 * Connection settings to the resource. Read-only.
	 */
	public final ConnectionConfiguration configuration;
	
}
