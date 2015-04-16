package cs158project;

public class DebugConnection implements ConnectionProtocol {

	private final ResourcePool resources_;
	
	public DebugConnection(ResourcePool resources) {
		this.resources_ = resources;
	}
	
	@Override
	public ConnectionConfiguration getResource() {
		
		if (!resources_.getAvailableResources().hasNext()) {
			return null;
		}
		
		// TODO: pulls only the first for testing
		String id = resources_.getAvailableResources().next();
		
		return resources_.getResource(id).configuration;
	}

	@Override
	public ConnectionConfiguration getResource(ConnectionConfiguration inbound) {
		// TODO Auto-generated method stub
		return null;
	}

}
