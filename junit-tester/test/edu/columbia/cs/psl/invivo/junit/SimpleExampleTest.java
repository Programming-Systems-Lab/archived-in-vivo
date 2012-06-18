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

import edu.columbia.cs.psl.invivo.junit.annotation.InvivoTest;
import edu.columbia.cs.psl.invivo.junit.annotation.VariableReplacement;

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

	public static void regularArgsStatic(int a)
	{
		int b = 2;
		int c = 1;
		a =3;
	}
	public void regularArgs(int a)
	{
		int b = 2;
		int c = 1;
		a = 3;
	}
	/**
	 * Test method for {@link edu.columbia.cs.psl.invivo.junit.SimpleExample#multiply(int, int)}.
	 */
	@Test
	@InvivoTest(replacements={"tester","otherNumber"})
	public void testMultiply() {
		SimpleExample tester = new SimpleExample();
		int number = 100;
		Integer otherNumber = 50;
		assertEquals(number, tester.multiply(otherNumber, 2));
	}
	
	public void testMultipl(SimpleExample tester, int number) {
		System.out.println("We are in test and the value passed is: " + number);
		assertEquals(2 * number, tester.multiply(2, number));
	}

}
