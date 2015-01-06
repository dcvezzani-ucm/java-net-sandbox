package edu.ucmerced.box.client;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

public class BoxServiceClientTest {

	BoxServiceClient _bsc;
	BoxServiceClient bsc;
	HttpURLConnection con;

	@Before
	public void setUp() throws Exception {
		_bsc = new BoxServiceClient("the client id", "the client secret");
		bsc = spy(_bsc);

		con = mock(HttpURLConnection.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	public Logger mock_logger(){
		Logger logger = mock(Logger.class);

		doNothing().when(logger).debug(anyString());
		when(bsc.logger()).thenReturn(logger);
		return logger;
	}

	@Test
	public void test_create_url() throws BoxServiceClientException, MalformedURLException {
		String expectedHostAndPath = "https://asdf.com:8080/sample";
		String[] _expectedParameters = new String[]{"one=1", "two=2"};

		String http_url = expectedHostAndPath;

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("one", "1");
		parameters.put("two", "2");
		URL actual = bsc.create_url(http_url, parameters);

		String actualHostAndPath = String.format("%s://%s:%d%s", actual.getProtocol(), actual.getHost(), actual.getPort(), actual.getPath());

		String actualQuery = actual.toString().split("\\?")[1];
		String[] _actualParameters = actualQuery.split("\\&");

		String[] expectedParameters = create_ordered_list_of_strings(_expectedParameters);
		String[] actualParameters = create_ordered_list_of_strings(_actualParameters);

		assertEquals(expectedHostAndPath, actualHostAndPath);
		assertArrayEquals(expectedParameters, actualParameters);
	}

	private String[] create_ordered_list_of_strings(String[] array) {
		List<String> orderedList = new ArrayList<String>();
		for(String item : array){
			orderedList.add(item);
		}
		Collections.sort(orderedList);
		return orderedList.toArray(new String[]{});
	}

	@Test
	public void test_get_content_handle_null_http_connection() throws BoxServiceClientException {
		Logger logger = mock_logger();
		con = null;

		assertNull(bsc.get_content(con));
	}

	@Test
	public void test_get_content_handle_exception() {
		Logger logger = mock_logger();

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
		Logger logger = mock_logger();

		try {
			doNothing().when(buffered_reader).close();
			when(buffered_reader.readLine()).thenReturn("one ", "two ", "three", null);
			when(con.getInputStream()).thenReturn(is);
			when(bsc.get_buffered_reader(is)).thenReturn(buffered_reader);

			String res = bsc.get_content(con);

			assertEquals("one two three", res);

			verify(logger, times(2)).debug(anyString());

		} catch (BoxServiceClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
