package cs158project;

/**
 * Defines behavior for providing connections to network resources.
 * 
 * @author Michael L.
 */
public interface ConnectionProtocol {
	public ConnectionConfiguration getResource();
	public ConnectionConfiguration getResource(ConnectionConfiguration inbound);
}
