package cs158project;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ResourcePool_Test {

	ResourcePlugin res1;
	ResourcePlugin res2;
	ResourcePlugin resNull;
	
	@Before
	public void setUp() throws Exception {
	
		res1 = new ResourcePlugin("localhost", 1099, "Resource 1");
		res2 = new ResourcePlugin("localhost", 1088, "Resource 2");
		resNull = new ResourcePlugin("localhost", 1003, null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRegister() {
		
		ResourcePool pool = new ResourcePool();
		
		assertTrue(pool.register(res1));
		assertTrue(pool.register(res2));
		assertFalse(pool.register(resNull));
		assertFalse(pool.register(res1));
	}

	@Test
	public void testGetAvailableResources() {
		
		ResourcePool pool = new ResourcePool();
		
		assertTrue(pool.register(res1));
		assertTrue(pool.register(res2));
		
		// test read of size
		//assertEquals(pool.getAvailableResources()..size(), 2);
		
		// no register of invalid resource
		assertFalse(pool.register(resNull));
		//assertEquals(pool.getAvailableResources().size(), 2);
		
		// remove test
		pool.unregister(res1.configuration.name);
		//assertEquals(pool.getAvailableResources().size(), 1);
	}

	@Test
	public void testGetResource() {
	
		ResourcePool pool = new ResourcePool();
		
		assertTrue(pool.register(res1));
		assertTrue(pool.register(res2));
		
		// valid id test
		ResourcePlugin res = pool.getResource(res1.configuration.name);
		assertNotNull(res);
		
		// invalid id
		res = pool.getResource("JUNK");
		assertNull(res);
	}

	@Test
	public void testUnregister() {
		
		ResourcePool pool = new ResourcePool();
		
		assertTrue(pool.register(res1));
		assertTrue(pool.register(res2));
		
		// valid
		pool.unregister(res1.configuration.name);
		assertNull(pool.getResource(res1.configuration.name));
		
		// invalid id test
		pool.unregister("JUNK");
	}

}
