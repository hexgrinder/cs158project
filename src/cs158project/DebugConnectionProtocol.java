package cs158project;

/**
 * Returns a debug connection configuration. Debug purposes only.
 * 
 * @author Michael L.
 */
public class DebugConnectionProtocol implements ConnectionProtocol {

	private static final ConnectionConfiguration CONFIG = 
		new ConnectionConfiguration("10.10.10.2", 80, "DEBUG");
	
	@Override
	public ConnectionConfiguration getResource() {
		return CONFIG;
	}

	@Override
	public ConnectionConfiguration getResource(ConnectionConfiguration config) {
		return CONFIG;
	}

}
