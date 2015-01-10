package edu.ucmerced.box.client;

import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoyRide {

	private static final String CHARSET = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()

	BoxServiceClient _bsc;
	BoxServiceClient bsc;
	HttpURLConnection con;
	Logger logger;

	protected Logger logger(){
		if (logger == null)
			logger=LoggerFactory.getLogger(JoyRide.class);
		return logger;
	}

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
		BoxServiceClientException e = null;
		Properties properties = null;
		properties = getProperties();

		bsc = new BoxServiceClient(properties);
//		bsc = spy(_bsc);

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

//	@Test
	public void testHttpsPost(){
		URL url;
		try {
			Properties properties = getProperties();

			// get CSRF token (via GET)
			String http_url = "https://app.box.com/api/oauth2/authorize";

			// instantiate CookieManager
			CookieManager manager = new CookieManager();
			manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
			CookieHandler.setDefault(manager);
			CookieStore cookieJar =  manager.getCookieStore();

			// create cookie
			// does this only do something from the server side?
			//			HttpCookie cookie = new HttpCookie("blah", "bleh");

			Map<String, String> parameters = new HashMap<String,String>();
			parameters.put("response_type", "code");
			parameters.put("client_id", properties.getProperty("client_id"));

			String query = create_query(parameters);
			url = new URL(http_url + query);
			//			cookieJar.add(url.toURI(), cookie);

			javax.net.ssl.HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
			con.setRequestMethod("GET");

			//dump all the content
			print_content(con);

			//dump all the cookies
			print_cookies(manager, con);

			con.disconnect();


//			// make RESTful POST request
//			http_url = "http://127.0.0.1:3771/greeting/shake.json";
//			url = new URL(http_url);
//
//			con = (HttpURLConnection)url.openConnection();
//			con.setRequestMethod("POST");
//			con.setRequestProperty("sugar", "spice");
//
//			// there has to be a non-null value for the body, even if it is an
//			// empty string or there will be a 411 error reported
//			con.setDoOutput(true);
//			BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
//			bf.write("");
//			bf.flush();
//
//			//dump all the content
//			print_content(con);
//
//			print_cookies(manager, con);
//
//			con.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void testHttpPost(){
		URL url;
		try {

			// get CSRF token (via GET)
			String http_url = "http://127.0.0.1:3771/greeting/test";

			// instantiate CookieManager
			CookieManager manager = new CookieManager();
			manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
			CookieHandler.setDefault(manager);
			CookieStore cookieJar =  manager.getCookieStore();

			// create cookie
			// does this only do something from the server side?
			//			HttpCookie cookie = new HttpCookie("blah", "bleh");

			url = new URL(http_url);
			//			cookieJar.add(url.toURI(), cookie);

			HttpURLConnection con = (HttpURLConnection)url.openConnection();

			//dump all the content
			print_content(con);

			//dump all the cookies
			print_cookies(manager, con);

			con.disconnect();


			// make RESTful POST request
			http_url = "http://127.0.0.1:3771/greeting/shake.json";
			url = new URL(http_url);

			con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("sugar", "spice");

			// there has to be a non-null value for the body, even if it is an
			// empty string or there will be a 411 error reported
			con.setDoOutput(true);
			BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
			bf.write("");
			bf.flush();

			//dump all the content
			print_content(con);

			print_cookies(manager, con);

			con.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			//		} catch (URISyntaxException e) {
			//			e.printStackTrace();
		}
	}

	public void print_cookies(CookieManager manager, HttpURLConnection con) {
		// get cookies from underlying
		// CookieStore
		CookieStore cookieJar =  manager.getCookieStore();
		List <HttpCookie> cookies = cookieJar.getCookies();
		for (HttpCookie cookie: cookies) {
			System.out.println("CookieHandler retrieved cookie: " + cookie);
		}
	}

	private void print_content(HttpURLConnection con){
		if(con!=null){

			try {

				System.out.println("****** Content of the URL ********");
				BufferedReader br =
						new BufferedReader(
								new InputStreamReader(con.getInputStream()));

				String input;

				while ((input = br.readLine()) != null){
					System.out.println(input);
				}
				br.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected String create_query(Map<String, String> parameters) throws BoxServiceClientException {
		String query = "";
		Exception e1 = null;

		try {
			if(parameters != null && parameters.size() > 0){
				StringBuffer sb_parameters = new StringBuffer("?");
				for(String parameter_name : parameters.keySet()){
					String parameter_value = URLEncoder.encode(parameters.get(parameter_name), CHARSET);
					sb_parameters.append(String.format("%s=%s&", parameter_name, parameter_value));
				}

				// get rid of trailing ampersand ('&')
				if(sb_parameters.length() > 0){
					query = sb_parameters.substring(0, sb_parameters.length()-1);
				}
			}
		} catch (Exception e) {
			e1 = e;
		} finally {
			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}

		return query;
	}

}
