package edu.ucmerced.box.client;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>BasicNameValuePairTest</code> contains tests for the class <code>{@link BasicNameValuePair}</code>.
 *
 * @generatedBy CodePro at 12/18/14 3:40 PM
 * @author dvezzani
 * @version $Revision: 1.0 $
 */
public class BasicNameValuePairTest {
	/**
	 * Run the BasicNameValuePair(String,String) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12/18/14 3:40 PM
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
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 12/18/14 3:40 PM
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 12/18/14 3:40 PM
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}
}