package edu.ucmerced.box.client;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The Class BasicNameValuePairTest.
 */
public class BasicNameValuePairTest {

	/**
	 * Test basic name value pair_1.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testBasicNameValuePair_1()
		throws Exception {
		String name = "";
		String value = "";

		BasicNameValuePair result = new BasicNameValuePair(name, value);

		// add additional test code here
		assertNotNull(result);
		assertEquals("=", result.toString());
		assertEquals("", result.getName());
		assertEquals("", result.getValue());
	}

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}
}