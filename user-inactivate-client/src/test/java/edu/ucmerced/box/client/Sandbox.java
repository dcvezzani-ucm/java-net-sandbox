package edu.ucmerced.box.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Sandbox{

	private static final String CHARSET = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()

	public static void main(String[] args) throws BoxServiceClientException
	{
//		new Sandbox().testHttps();
//		new Sandbox().testHttp();
//		new Sandbox().testHttpCookies();
//		new Sandbox().testHttpResponseHeaders();
//		new Sandbox().testHttpRequestHeaders();
//		new Sandbox().testHttpCookiesSet();
//		new Sandbox().testHttpPost();
//		new Sandbox().testHttpGetWithUrlParameters();
//		new Sandbox().testHttpPostWithUrlParameters();

		new Sandbox().apacheClient();
	}

	private Logger logger;

	protected Logger logger(){
		if (logger == null)
			logger=LoggerFactory.getLogger("edu.ucmerced.box.client.BoxServiceClient");

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

		logger().debug("Created query: {}", query);

		return query;
	}

	public void apacheClientStep01(CloseableHttpClient httpclient, Properties properties) throws BoxServiceClientException {
		Exception e1 = null;
		CloseableHttpResponse response1 = null;
		HttpEntity entity1 = null;
		String request_token = null;

		try {
			String http_url = "https://app.box.com/api/oauth2/authorize";

			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("response_type", "code");

			String client_id = properties.getProperty("client_id");
			parameters.put("client_id", client_id);

			String query = create_query(parameters);
			HttpGet httpGet = new HttpGet(String.format("%s%s", http_url, query));

			response1 = httpclient.execute(httpGet);

			logger().debug("Status: {}", response1.getStatusLine());
			entity1 = response1.getEntity();
			InputStream is = entity1.getContent();

			BufferedReader br = new BufferedReader(new InputStreamReader(entity1.getContent()));
			String input;
			StringBuffer sb_response = new StringBuffer();

			while ((input = br.readLine()) != null){
				sb_response.append(input);
			}
			br.close();

			String response_content = sb_response.toString();
			logger().debug("Response content: {}", response_content);

			request_token = parse_request_token(response_content);
			logger().debug("Request token: {}", request_token);

		} catch (Exception e) {
			e1 = e;
		} finally {
			try {
				// do something useful with the response body
				// and ensure it is fully consumed
				EntityUtils.consume(entity1);

				response1.close();

			} catch (IOException e) {
				logger().warn("Unable to close the response object: {}", e);
			}

			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}
	}


	public void apacheClientStep02(CloseableHttpClient httpclient, Properties properties) throws BoxServiceClientException {
		Exception e1 = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		String request_token = null;

		String username = properties.getProperty("username");
		String userpass = properties.getProperty("userpass");

		try {
			String http_url = "https://app.box.com/api/oauth2/authorize";

			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("response_type", "code");

			String client_id = properties.getProperty("client_id");
			parameters.put("client_id", client_id);

			String query = create_query(parameters);

			HttpPost httpPost = new HttpPost(String.format("%s%s", http_url, query));

			List <BasicNameValuePair> nvps = new ArrayList <BasicNameValuePair>();
			nvps.add(new BasicNameValuePair("login", username));
			nvps.add(new BasicNameValuePair("password", userpass));
			nvps.add(new BasicNameValuePair("login_submit", "Authorizing..."));
			nvps.add(new BasicNameValuePair("dologin", "1"));
			nvps.add(new BasicNameValuePair("client_id", client_id));
			nvps.add(new BasicNameValuePair("response_type", "code"));
			nvps.add(new BasicNameValuePair("redirect_uri", "https://www.google.com"));
			nvps.add(new BasicNameValuePair("scope", "root_readwrite manage_enterprise"));
			nvps.add(new BasicNameValuePair("folder_id", ""));
			nvps.add(new BasicNameValuePair("file_id", ""));
			nvps.add(new BasicNameValuePair("state", ""));
			nvps.add(new BasicNameValuePair("reg_step", ""));
			nvps.add(new BasicNameValuePair("submit1", "1"));
			nvps.add(new BasicNameValuePair("folder", ""));
			nvps.add(new BasicNameValuePair("login_or_register_mode", "login"));
			nvps.add(new BasicNameValuePair("new_login_or_register_mode", ""));
			nvps.add(new BasicNameValuePair("__login", "1"));
			nvps.add(new BasicNameValuePair("_redirect_url", "/api/oauth2/authorize?response_type=code&client_id=" + client_id));
			nvps.add(new BasicNameValuePair("request_token", request_token));
			nvps.add(new BasicNameValuePair("_pw_sql", ""));

			httpPost.setEntity(new UrlEncodedFormEntity((List<? extends org.apache.http.NameValuePair>) nvps));
			response = httpclient.execute(httpPost);

			logger().debug("Status: {}", response.getStatusLine());
			entity = response.getEntity();
			InputStream is = entity.getContent();

			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
			String input;
			StringBuffer sb_response = new StringBuffer();

			while ((input = br.readLine()) != null){
				sb_response.append(input);
			}
			br.close();

			String response_content = sb_response.toString();
//			System.out.println(response_content);
			logger().debug("Response content: {}", response_content);

//			request_token = parse_request_token(response_content);
//			logger().debug("Request token: {}", request_token);
//
//			properties.put("request_token", request_token);

		} catch (Exception e) {
			e1 = e;
		} finally {
			try {
				// do something useful with the response body
				// and ensure it is fully consumed
				EntityUtils.consume(entity);

				response.close();

			} catch (Exception e) {
				logger().warn("Unable to close the response object", e);
			}

			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}
	}


	public void apacheClient() throws BoxServiceClientException {
		Exception e1 = null;

		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			Properties properties = getProperties();

			apacheClientStep01(httpclient, properties);
			apacheClientStep02(httpclient, properties);

		} catch (Exception e) {
			e1 = e;
		} finally {
			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}
	}

	protected String parse_request_token(String response_content) {
		String request_token = null;
		Pattern pattern = Pattern.compile(".*<input type=\"hidden\" name=\"request_token\" value=\"([^\"]*)\">.*");
		Matcher m = pattern.matcher(response_content);

		if (m.find( )) {
			request_token = m.group(1);
		}

		return request_token;
	}

	public void testHttp(){

		String http_url = "http://127.0.0.1:3771/greeting/test";
		URL url;
		try {

			url = new URL(http_url);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();

			//dump all the content
			print_content(con);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void testHttpGetWithUrlParameters(){

		String http_url = "http://127.0.0.1:3771/greeting/test";

		String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
		String param1 = "value1";
		String param2 = "value2";

		URL url;
		try {

			String query = String.format("param1=%s&param2=%s",
				     URLEncoder.encode(param1, charset),
				     URLEncoder.encode(param2, charset));

			url = new URL(http_url + "?" + query);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestProperty("Accept-Charset", charset);

			//dump all the content
			print_content(con);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void testHttpPostWithUrlParameters(){

		String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
		String param1 = "value1";
		String param2 = "value2";

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
      con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
			con.setDoOutput(true);

			String query = String.format("param1=%s&param2=%s",
				     URLEncoder.encode(param1, charset),
				     URLEncoder.encode(param2, charset));

			// there has to be a non-null value for the body, even if it is an
			// empty string or there will be a 411 error reported
//			BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
//			bf.write(query.getBytes(charset));
//			bf.flush();

			try (OutputStream output = con.getOutputStream()) {
			    output.write(query.getBytes(charset));
			}

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

	public void testHttpCookiesSet(){

		String http_url = "http://127.0.0.1:3771/greeting/test";
		URL url;
		try {
			// instantiate CookieManager
			CookieManager manager = new CookieManager();
			manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
			CookieHandler.setDefault(manager);
			CookieStore cookieJar =  manager.getCookieStore();

			// create cookie
			HttpCookie cookie = new HttpCookie("blah", "bleh");

			url = new URL(http_url);
			cookieJar.add(url.toURI(), cookie);

			HttpURLConnection con = (HttpURLConnection)url.openConnection();

			//dump all the content
			print_content(con);

			//dump all the cookies
			print_cookies(manager, con);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

	public void testHttpCookies(){

		String http_url = "http://127.0.0.1:3771/greeting/test";
		URL url;
		try {
			// Instantiate CookieManager;
			// make sure to set CookiePolicy
			CookieManager manager = new CookieManager();
			manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
			CookieHandler.setDefault(manager);

			url = new URL(http_url);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();

			//dump all the content
			print_content(con);

			//dump all the cookies
			print_cookies(manager, con);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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

	public void testHttpResponseHeaders(){
		String http_url = "http://127.0.0.1:3771/greeting/test";
		URL url;
		try {

			url = new URL(http_url);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();

			//dump all the content
			print_content(con);

			//dump all the headers
			print_headers(con);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testHttpRequestHeaders(){
		String http_url = "http://127.0.0.1:3771/greeting/test";
		URL url;
		try {

			url = new URL(http_url);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();

			con.setRequestProperty("blah", "bleh");

			//dump all the content
			print_content(con);

			//dump all the headers
			print_headers(con);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void print_headers(HttpURLConnection con) {
		//get all headers
		Map<String, List<String>> map = con.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			System.out.println("Key : " + entry.getKey() +
					" ,Value : " + entry.getValue());
		}

		//get header by 'key'
		String server = con.getHeaderField("Server");
		System.out.println("[HEADER] Server: " + server);
	}

	private void testHttps(){

		String http_url = "https://www.google.com/";
		URL url;
		try {

			url = new URL(http_url);
			HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

			//dump all cert info
			print_http_cert(con);

			//dump all the content
			print_content(con);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void print_http_cert(HttpsURLConnection con){

		if(con!=null){

			try {

				System.out.println("Response Code : " + con.getResponseCode());
				System.out.println("Cipher Suite : " + con.getCipherSuite());
				System.out.println("\n");

				Certificate[] certs = con.getServerCertificates();
				for(Certificate cert : certs){
					System.out.println("Cert Type : " + cert.getType());
					System.out.println("Cert Hash Code : " + cert.hashCode());
					System.out.println("Cert Public Key Algorithm : "
							+ cert.getPublicKey().getAlgorithm());
					System.out.println("Cert Public Key Format : "
							+ cert.getPublicKey().getFormat());
					System.out.println("\n");
				}

			} catch (SSLPeerUnverifiedException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}

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

}
