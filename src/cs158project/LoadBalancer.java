package cs158project;

/**
 * Provides static convenience methods around the LoadBalancerService class.
 * This class is not thread-safe.
 * 
 * Primarily used for testing purposes.
 * 
 * @author Michael L.
 */
public class LoadBalancer {

	/**
	 * Defines the connection settings for load balancer 1. 
	 */
	private static final ConnectionConfiguration SERVICE_CONFIG_1 
		= new ConnectionConfiguration("10.10.10.1", 80, "Load_Balancer_1");
	
	private static ConnectionProtocol protocol_;
	private static LoadBalancerService service_;

	/**
	 * Sets the connection protocol for all load balancers managed by this
	 * class.  Will silently ignore any calls if the service is running; 
	 * i.e.: the service must be stopped before setting the protocol.
	 * 
	 * @param protocol Object that implements the ConnectionProtocol interface.
	 */
	public static void setProtocol(ConnectionProtocol protocol) {
		if (null != service_ && service_.isRunning()) {
			return;
		}
		protocol_ = protocol;
	}
	
	/**
	 * Starts all load balancer services.  Subsequent calls are silently ignored
	 * unless a stop() method was called prior.
	 */
	public static void start() {
		
		if (null != service_ && service_.isRunning()) {
			return;
		}
		
		// TODO: How to do this to setup multiple loadbalancer services?
		service_ = new LoadBalancerService(SERVICE_CONFIG_1, protocol_);
		service_.start();
	}
	
	/**
	 * Stops the load balancer services.
	 */
	public static void stop() {
		if (null == service_) {
			return;
		}
		service_.stop();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		if (null != service_) {
			service_.stop();
		}
		service_ = null;
		protocol_ = null;
		super.finalize();
	}
}
