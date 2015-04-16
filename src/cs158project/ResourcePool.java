package cs158project;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * Directory of plugin resources.
 * 
 * @author Michael L
 */
public class ResourcePool {
	
	private final Hashtable<String, ResourcePlugin> resources_ = new Hashtable<String, ResourcePlugin>();
	
	/**
	 * Registers resources with the pool. Uses the plugin's configuration name to 
	 * register the plugin. Plugin names must be unique. The method ignores 
	 * configuration names that are null, empty or already exist in the pool.
	 *  
	 * @param plugin Resource to be registered.
	 * @return Returns TRUE if the plugin was successfully registered, FALSE 
	 * otherwise.
	 */
	public boolean register(ResourcePlugin plugin) {
		String name = plugin.configuration.name;
		if (null == name || name.isEmpty() || resources_.containsKey(name)) {
			return false;
		}
		resources_.put(name, plugin);
		return true;
	}

	/**
	 * Returns an iterator of registered resources.
	 * 
	 * @return Returns an iterator of resource names.
	 */
	public Iterator<String> getAvailableResources() {
		return resources_.keySet().iterator();
	}
	
	
	
	/**
	 * Retrieves the resource for the given id.
	 * 
	 * @param id Resource id.
	 * @return ResourcePlugin object or null if the id is not found.
	 */
	public ResourcePlugin getResource(String id) {
		return resources_.get(id);
	}
	
	/**
	 * Unregisters the resource from the pool.
	 * 
	 * @param id Id of the resource to remove.
	 */
	public void unregister(String id) {
		resources_.remove(id);
	}
}
