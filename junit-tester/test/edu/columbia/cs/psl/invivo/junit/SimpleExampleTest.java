/**
 * 
 */
package edu.columbia.cs.psl.invivo.junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author jon
 *
 */
public class SimpleExampleTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link edu.columbia.cs.psl.invivo.junit.SimpleExample#multiply(int, int)}.
	 */
	@Test
	public void testMultiply() {
		SimpleExample tester = new SimpleExample();
		assertEquals(100, tester.multiply(50, 2));
	}

}
