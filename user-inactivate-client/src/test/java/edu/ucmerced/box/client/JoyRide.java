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

public class JoyRide {

//	private static final String CHARSET = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()

	BoxServiceClient _bsc;
	BoxServiceClient bsc;
	HttpURLConnection con;
	Logger logger;

	@Before
	public void setUp() throws Exception {
		_bsc = new BoxServiceClient("the client id", "the client secret", "user name", "user password");
		bsc = spy(_bsc);

		con = mock(HttpURLConnection.class);
	}

	@After
	public void tearDown() throws Exception {
	}

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

	private Logger logger(){
		if (logger == null)
			logger=LoggerFactory.getLogger(JoyRide.class);
		return logger;
	}

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
