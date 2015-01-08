package edu.ucmerced.box.client;

import static edu.ucmerced.box.client.ExceptionAssert.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoxServiceClientTest {

	BoxServiceClient _bsc;
	BoxServiceClient bsc;
	HttpURLConnection con;
	Logger logger;

	protected Logger logger(){
		if (logger == null)
			logger=LoggerFactory.getLogger(BoxServiceClientTest.class);
		return logger;
	}

	@Before
	public void setUp() throws Exception {
		_bsc = new BoxServiceClient("the client id", "the client secret");
		bsc = spy(_bsc);

		con = mock(HttpURLConnection.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	static final String REQUEST_TOKEN_FILE_NAME = "./src/test/resources/request_token_content.html";

//	@Test
	public void test_request_token_for_real() throws Exception {
		BoxServiceClientException e = null;
		Properties properties = null;
		properties = getProperties();

		_bsc = new BoxServiceClient(properties);
		bsc = spy(_bsc);
		String request_token_html = get_request_token_html();

		try {
			bsc.request_token();
			logger().debug("request_token: {}", bsc.get_request_token_partial());

		} catch (BoxServiceClientException e1) {
			e = e1;
		}
	}

	@Test
	public void test_parse_request_token(){
		BoxServiceClientException e = null;

		_bsc = new BoxServiceClient("asdf", "qwer");
		bsc = spy(_bsc);

		String request_token_html = get_request_token_html();
		String request_token = bsc.parse_request_token(request_token_html);

		assertNotNull(request_token);
		assertEquals("blah", request_token);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_request_token_with_properties() throws Exception {
		BoxServiceClientException e = null;
		Properties properties = null;
		properties = getProperties();

		_bsc = new BoxServiceClient(properties);
		bsc = spy(_bsc);
		Logger logger = mock_logger();
		String request_token_html = get_request_token_html();

		Map<String, String> _parameters = new HashMap<String, String>();
		Map<String, String> parameters = spy(_parameters);
		doReturn(parameters).when(bsc).init_parameters();

		HttpURLConnection con = mock(HttpURLConnectionImpl.class);
		doNothing().when(con).setRequestProperty(anyString(), anyString());
		doReturn(con).when(bsc).open_connection(anyString(), anyMap());
		doReturn("").when(bsc).get_content(con);
		doReturn("qwelkrj3l4k5jl3kjdlir23ijls").when(bsc).parse_request_token(anyString());

		try {
			bsc.request_token();

		} catch (BoxServiceClientException e1) {
			e = e1;
		}

		assertNotThrowsException(e);

		verify(parameters).put("response_type", "code");
		verify(parameters).put(eq("client_id"), anyString());
//		verify(bsc.open_connection(endsWith("/api/oauth2/authorize"), anyMap()));
		verify(logger, times(1)).debug(anyString());

	}

	@Test
	public void test_create_url_handle_null_host_and_path() {
		BoxServiceClientException e = null;

		String http_url = null;

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("one", "1");
		parameters.put("two", "2");

		try {
			bsc.create_url(http_url, parameters);

		} catch (BoxServiceClientException e1) {
			e = e1;
		}

		assertThrowsException(e);
	}

	@Test
	public void test_create_url_handle_empty_host_and_path() {
		BoxServiceClientException e = null;

		String http_url = "";

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("one", "1");
		parameters.put("two", "2");

		try {
			bsc.create_url(http_url, parameters);

		} catch (BoxServiceClientException e1) {
			e = e1;
		}

		assertThrowsException(e);
	}

	@Test
	public void test_create_url_handle_null_parameters() {
		BoxServiceClientException e = null;

		String http_url = "https://asdf.com:8080/sample";
		Map<String, String> parameters = null;

		try {
			bsc.create_url(http_url, parameters);

		} catch (BoxServiceClientException e1) {
			e = e1;
		}

		assertNotThrowsException(e);
	}

	@Test
	public void test_create_url_handle_empty_parameters() {
		BoxServiceClientException e = null;

		String http_url = "https://asdf.com:8080/sample";
		Map<String, String> parameters = new HashMap<String, String>();

		try {
			bsc.create_url(http_url, parameters);

		} catch (BoxServiceClientException e1) {
			e = e1;
		}

		assertNotThrowsException(e);
	}

	@Test
	public void test_create_url_with_unsupported_encoding() {
		BoxServiceClientException e = null;

		when(bsc.charset()).thenReturn("asdf");

		String http_url = "https://asdf.com:8080/sample";

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("one", "1");
		parameters.put("two", "2");

		try {
			bsc.create_url(http_url, parameters);

		} catch (BoxServiceClientException e1) {
			e = e1;
		}

		assertThrowsException(e, java.io.UnsupportedEncodingException.class);
	}

	@Test
	public void test_create_url_with_malformed_url() {
		BoxServiceClientException e = null;

		String http_url = "asdf://qwer.com>9132\\zxcv";

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("one", "1");
		parameters.put("two", "2");

		try {
			bsc.create_url(http_url, parameters);

		} catch (BoxServiceClientException e1) {
			e = e1;
		}

		assertThrowsException(e, java.net.MalformedURLException.class);
	}

	@Test
	public void test_create_url() throws BoxServiceClientException, MalformedURLException {
		String expectedHostAndPath = "https://asdf.com:8080/sample";
		String[] expectedParameters = new String[]{"one=1", "two=2"};

		// create path minus the url parameters
		String http_url = expectedHostAndPath;

		// create map with parameters
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("one", "1");
		parameters.put("two", "2");

		// create URL from full path and parameters
		URL actual = bsc.create_url(http_url, parameters);

		assertHostAndPathMatch(expectedHostAndPath, actual);
		assertParametersMatch(expectedParameters, actual);
	}

	@Test
	public void test_get_content_handle_null_http_connection() throws BoxServiceClientException {
		con = null;

		assertNull(bsc.get_content(con));
	}

	@Test
	public void test_get_content_handle_exception() {
		BoxServiceClientException e = null;

		try {
			doThrow(new IOException()).when(con).getInputStream();

			bsc.get_content(con);
		} catch (BoxServiceClientException e1) {
			e = e1;
		} catch (Exception e1) { }

		assertNotNull("Expected exception to be thrown and it wasn't", e);
	}

	@Test
	public void test_get_content() {
		InputStream is = mock(InputStream.class);
		BufferedReader buffered_reader = mock(BufferedReader.class);

		try {
			doNothing().when(buffered_reader).close();
			when(buffered_reader.readLine()).thenReturn("one ", "two ", "three", null);
			when(con.getInputStream()).thenReturn(is);
			when(bsc.get_buffered_reader(is)).thenReturn(buffered_reader);

			String res = bsc.get_content(con);

			assertEquals("one two three", res);

		} catch (BoxServiceClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Logger mock_logger(){
		Logger logger = mock(LoggerImpl.class);

		doNothing().when(logger).debug(anyString());
		doReturn(true).when(logger).isDebugEnabled();
		doNothing().when(logger).debug(anyString());
		when(bsc.logger()).thenReturn(logger);
		return logger;
	}

	private Properties getProperties() throws FileNotFoundException,
	IOException {
		Properties properties;
//		FileInputStream application_properties = new FileInputStream("/application.properties");

		String mainBasedir = System.getProperty("main.basedir");
		FileInputStream application_properties = new FileInputStream(mainBasedir + "/application.properties");

		properties = new Properties();
		properties.load(application_properties);
		application_properties.close();
		return properties;
	}

	private String read_file_into_string(String filename) throws IOException {
		String request_token_html;
		File file = new File(filename);
		request_token_html = FileUtils.readFileToString(file , bsc.charset());
		return request_token_html;
	}

	private String get_request_token_html() {
		String request_token_html = null;
		try {
			request_token_html = read_file_into_string(REQUEST_TOKEN_FILE_NAME);
		} catch (IOException e2) {
			fail(String.format("Unable to read source file: %s; %s", REQUEST_TOKEN_FILE_NAME, e2.getMessage()));
		}
		return request_token_html;
	}

}
