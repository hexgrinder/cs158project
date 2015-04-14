package cs158project;

/**
 * Represents a network resource.
 * 
 * @author Michael L
 */
public class ResourcePlugin {
	
	/**
	 * Connection settings to the resource. Read-only.
	 */
	public final ConnectionConfiguration configuration;
	
	@SuppressWarnings("unused")
	private ResourcePlugin() {
		this(null, 0, null);
	}
	
	public ResourcePlugin(String host, int port, String name) {
		this.configuration = new ConnectionConfiguration(host, port, name);
	}

	
}
