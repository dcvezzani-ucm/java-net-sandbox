package edu.ucmerced.box.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class Sandbox{

	public static void main(String[] args)
	{
//		new Sandbox().testHttps();
//		new Sandbox().testHttp();
//		new Sandbox().testHttpCookies();
//		new Sandbox().testHttpResponseHeaders();
//		new Sandbox().testHttpRequestHeaders();
//		new Sandbox().testHttpCookiesSet();
//		new Sandbox().testHttpPost();
//		new Sandbox().testHttpGetWithUrlParameters();
		new Sandbox().testHttpPostWithUrlParameters();
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

		String http_url = "http://www.google.com/";
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
