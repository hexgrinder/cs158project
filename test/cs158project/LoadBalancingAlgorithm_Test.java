package cs158project;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoadBalancingAlgorithm_Test {

	ResourcePool resources;
	LoadBalancingAlgorithm lba;
	
	@Before
	public void setUp() throws Exception {
		resources = new ResourcePool();
		resources.register(new ResourcePlugin("10.10.10.2", 80, "Web Server 1"));
		resources.register(new ResourcePlugin("10.10.10.3", 80, "Web Server 2"));
		
		lba = new LoadBalancingAlgorithm(resources);
		
	}

	@Test
	public void testGetResourceConnectionConfiguration_ipaddressport() {
	
		ConnectionConfiguration config;
		ConnectionConfiguration client1;
		
		// first assignment
		client1 = new ConnectionConfiguration("199.199.199.1", 9999);
		config = lba.getResource(client1);
		assertNotNull(config);
		assertEquals("10.10.10.2", config.host);
		
		// re-acquire same destination ip address, but client now has a
		// different port number -> should not matter
		client1 = new ConnectionConfiguration("199.199.199.1", 1037);
		config = lba.getResource(client1);
		assertNotNull(config);
		assertEquals("10.10.10.2", config.host);
	}

	@Test
	public void testGetResourceConnectionConfiguration() {
	
		ConnectionConfiguration config;
		ConnectionConfiguration client1;
		ConnectionConfiguration client2;
		
		// first assignment
		client1 = new ConnectionConfiguration("199.199.199.1", 1037);
		config = lba.getResource(client1);
		assertNotNull(config);
		assertEquals("10.10.10.2", config.host);
		
		// second assignment
		client2 = new ConnectionConfiguration("199.199.199.2", 1557);
		config = lba.getResource(client2);
		assertNotNull(config);
		assertEquals("10.10.10.3", config.host);
		
		// re-acquire same destination ip address
		client1 = new ConnectionConfiguration("199.199.199.1", 1037);
		config = lba.getResource(client1);
		assertNotNull(config);
		assertEquals("10.10.10.2", config.host);
	}

	@Test
	public void testGetResourceConnectionConfigurationConsecutive() {
	
		ConnectionConfiguration config;
		ConnectionConfiguration client1;
		
		// first assignment
		client1 = new ConnectionConfiguration("199.199.199.1", 1037);
		config = lba.getResource(client1);
		assertNotNull(config);
		assertEquals("10.10.10.2", config.host);
		
		// re-acquire same destination ip address
		client1 = new ConnectionConfiguration("199.199.199.1", 1037);
		config = lba.getResource(client1);
		assertNotNull(config);
		assertEquals("10.10.10.2", config.host);
	}

}
