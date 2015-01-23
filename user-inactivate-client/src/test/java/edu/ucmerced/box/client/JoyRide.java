package edu.ucmerced.box.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class JoyRide.
 */
public class JoyRide {

	/** The _bsc. */
	BoxServiceClient _bsc;
	
	/** The bsc. */
	BoxServiceClient bsc;
	
	/** The con. */
	HttpURLConnection con;
	
	/** The logger. */
	Logger logger;

	/**
	 * Sets up for tests.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		_bsc = new BoxServiceClient("the client id", "the client secret", "user name", "user password");
		bsc = spy(_bsc);

		con = mock(HttpURLConnection.class);
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test ic token for real.
   *
   * Even though this is run as a test, it will truly authenticate with
   * Box and establish an authorized session to pull back all 
   * attributes for the current user.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_ic_token_for_real() throws Exception {
		Properties properties = null;
		properties = getProperties();

		bsc = new BoxServiceClient(properties);

		bsc.request_token();
		logger().debug("request_token: {}", bsc.get_request_token_partial());

		String ic_token = bsc.ic_token();
		logger().debug("ic_token: {}", bsc.get_token_partial(ic_token));

		String auth_token = bsc.auth_token();
		logger().debug("auth_token: {}", bsc.get_token_partial(auth_token));

		String access_token = bsc.access_token();
		logger().debug("access_token: {}", bsc.get_token_partial(access_token));

		String my_box_profile = bsc.my_box_profile();
		logger().debug("my_box_profile: {}", bsc.get_token_partial(my_box_profile));
	}

	/**
	 * Logger.
	 *
	 * @return the logger
	 */
	private Logger logger(){
		if (logger == null)
			logger=LoggerFactory.getLogger(JoyRide.class);
		return logger;
	}

	/**
	 * Gets the properties.
   * <p></p>
   * <code>application.properties</code> should contain the credentials necessary for establishing
   * and authenticated and authorized Box session.  See <code>application.properties.sample</code>
   * for an example; feel free to copy it and make your own <code>application.properties</code>
   * file.
	 *
	 * @return the properties
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Properties getProperties() throws FileNotFoundException,
	IOException {
		Properties properties = null;

		String mainBasedir = System.getProperty("main.basedir");
		FileInputStream application_properties = new FileInputStream(mainBasedir + "/application.properties");

		properties = new Properties();
		properties.load(application_properties);
		application_properties.close();
		return properties;
	}
}
