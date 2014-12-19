package edu.ucmerced.box.client;

import org.junit.*;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

/**
 * The class <code>NameValuePairTest</code> contains tests for the class <code>{@link NameValuePair}</code>.
 *
 * @generatedBy CodePro at 12/18/14 3:41 PM
 * @author dvezzani
 * @version $Revision: 1.0 $
 */
public class NameValuePairTest {
	/**
	 * Run the String getName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12/18/14 3:41 PM
	 */
	@Test
	public void testGetName_1()
		throws Exception {
		BasicNameValuePair fixture = new BasicNameValuePair("", "");
		fixture.logger = NOPLogger.NOP_LOGGER;

		String result = fixture.getName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the String getValue() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12/18/14 3:41 PM
	 */
	@Test
	public void testGetValue_1()
		throws Exception {
		BasicNameValuePair fixture = new BasicNameValuePair("", "");
		fixture.logger = NOPLogger.NOP_LOGGER;

		String result = fixture.getValue();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the void setName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12/18/14 3:41 PM
	 */
	@Test
	public void testSetName_1()
		throws Exception {
		BasicNameValuePair fixture = new BasicNameValuePair("", "");
		fixture.logger = NOPLogger.NOP_LOGGER;
		String name = "";

		fixture.setName(name);

		// add additional test code here
	}

	/**
	 * Run the void setValue(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12/18/14 3:41 PM
	 */
	@Test
	public void testSetValue_1()
		throws Exception {
		BasicNameValuePair fixture = new BasicNameValuePair("", "");
		fixture.logger = NOPLogger.NOP_LOGGER;
		String value = "";

		fixture.setValue(value);

		// add additional test code here
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12/18/14 3:41 PM
	 */
	@Test
	public void testToString_1()
		throws Exception {
		BasicNameValuePair fixture = new BasicNameValuePair("", "");
		fixture.logger = NOPLogger.NOP_LOGGER;

		String result = fixture.toString();

		// add additional test code here
		assertEquals("=", result);
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12/18/14 3:41 PM
	 */
	@Test
	public void testToString_2()
		throws Exception {
		BasicNameValuePair fixture = new BasicNameValuePair("", "");
		fixture.logger = NOPLogger.NOP_LOGGER;

		String result = fixture.toString();

		// add additional test code here
		assertEquals("=", result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 12/18/14 3:41 PM
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
	 * @generatedBy CodePro at 12/18/14 3:41 PM
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}
}