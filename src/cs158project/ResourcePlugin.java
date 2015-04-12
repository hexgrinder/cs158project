package cs158project;

/**
 * 
 * @author Michael L
 */
public class ResourcePlugin {
	
	@SuppressWarnings("unused")
	private ResourcePlugin() {
		this(null, 0, null);
	}
	
	public ResourcePlugin(String host, int port, String name) {
		this.configuration = new ConnectionConfiguration(host, port, name);
	}

	/**
	 * Connection settings to the resource. Read-only.
	 */
	public final ConnectionConfiguration configuration;
	
}
