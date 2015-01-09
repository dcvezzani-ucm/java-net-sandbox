package edu.ucmerced.box.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import java.security.cert.Certificate;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoxServiceClient {
	private static final int DEFAULT_TOKEN_PARTIAL_LENGTH = 5;
	private static final String CHARSET = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()

	Logger logger = null;
	CookieManager manager;
	CookieStore cookieJar;

	String client_id;
	String client_secret;
	String username;
	String userpass;

	String request_token;
	String ic_token;

	@Deprecated
	public BoxServiceClient(String client_id, String client_secret){
		this.client_id = client_id;
		this.client_secret = client_secret;
		username = null;
		userpass = null;
	}

	public BoxServiceClient(String client_id, String client_secret, String username, String userpass){
		this.client_id = client_id;
		this.client_secret = client_secret;
		this.username = username;
		this.userpass = userpass;
	}

	public BoxServiceClient(Properties properties){
		client_id = properties.getProperty("client_id");
		client_secret = properties.getProperty("client_secret");
		username = properties.getProperty("username");
		userpass = properties.getProperty("userpass");
	}

	public String inactivate(String user_id){
		StringBuffer json = new StringBuffer();

		return json.toString();
	}

	protected void reset_cookies(){
		// instantiate CookieManager
		manager = new CookieManager();
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
		cookieJar =  manager.getCookieStore();
	}

	protected String ic_token() throws BoxServiceClientException{
//		  curl 'https://ucmerced.app.box.com/api/oauth2/authorize?response_type=code&client_id='${CLIENT_ID} \
//		    -X POST \
//		    --dump-header header.txt \
//		    --cookie cookies.txt --cookie-jar cookies.txt \
//		    -v \
//		    -H 'Host: ucmerced.app.box.com' \
//		    -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0' \
//		    -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8' \
//		    -H 'Accept-Language: en-US,en;q=0.5' \
//		    -H 'Referer: https://app.box.com/api/oauth2/authorize?response_type=code&client_id='${CLIENT_ID} \
//		    -H 'Connection: keep-alive' \
//		    -H 'Content-Type: application/x-www-form-urlencoded' \
//		    --data 'login='${USERNAME}'&password='${USERPASS}'&login_submit=Authorizing...&dologin=1&client_id='${CLIENT_ID}'&response_type=code&redirect_uri=https%3A%2F%2Fwww.google.com&scope=root_readwrite+manage_enterprise&folder_id=&file_id=&state=&reg_step=&submit1=1&folder=&login_or_register_mode=login&new_login_or_register_mode=&__login=1&_redirect_url=%2Fapi%2Foauth2%2Fauthorize%3Fresponse_type%3Dcode%26client_id%3D'${CLIENT_ID}'&request_token='${REQUEST_TOKEN}'&_pw_sql=' > response.html

		Exception e1 = null;
		HttpsURLConnection con = null;
		String http_url = "https://app.box.com/api/oauth2/authorize";
		ic_token = null;

		Map<String, String> parameters = init_parameters();
		parameters.put("response_type", "code");
		parameters.put("client_id", client_id);

//		URLEncoder.encode(param1, charset),
		Map<String, String> _data = init_parameters();
		_data.put("login", username);
		_data.put("password", userpass);
		_data.put("login_submit", "Authorizing...");
		_data.put("dologin", "1");
		_data.put("client_id", client_id);
		_data.put("response_type", "code");
		_data.put("redirect_uri", "https://www.google.com");
		_data.put("scope", "root_readwrite manage_enterprise");
		_data.put("folder_id", "");
		_data.put("file_id", "");
		_data.put("state", "");
		_data.put("reg_step", "");
		_data.put("submit1", "1");
		_data.put("folder", "");
		_data.put("login_or_register_mode", "login");
		_data.put("new_login_or_register_mode", "");
		_data.put("__login", "1");
		_data.put("_redirect_url", "/api/oauth2/authorize?response_type=code&client_id=" + client_id);
		_data.put("request_token", request_token);
		_data.put("_pw_sql", "");

		try {
//			con = open_connection(http_url);
			URL url = new URL(String.format("%s%s", http_url, create_query(parameters)));
			con = (HttpsURLConnection)url.openConnection();
//			con = (HttpURLConnection)url.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", URLEncoder.encode("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0"));
			con.setRequestProperty("Accept-Charset", charset());
			con.setRequestProperty("Accept-Language", URLEncoder.encode("en-US,en;q=0.5"));
			con.setRequestProperty("Host", "ucmerced.app.box.com");
			con.setRequestProperty("Accept", URLEncoder.encode("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", charset()));
			con.setRequestProperty("Referer", URLEncoder.encode(String.format("https://app.box.com/api/oauth2/authorize?response_type=code&client_id=%s", client_id), charset()));
			con.setRequestProperty("Connection", "keep-alive");
			con.setRequestProperty("Content-Type", URLEncoder.encode("application/x-www-form-urlencoded", charset()));

//			List <HttpCookie> _cookies = cookieJar.getCookies();
//			Map<String, String> cookies = new HashMap<String,String>();
//			for(HttpCookie cookie : _cookies){
//				con.setRequestProperty("Cookie", String.format("%s=%s", cookie.getName(), cookie.getValue()));
//			}

			con.setDoInput(true);
			con.setDoOutput(true);

//			there has to be a non-null value for the body, even if it is an
//			empty string or there will be a 411 error reported
//			BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
//			bf.write(query.getBytes(charset));
//			bf.flush();

			String data = create_query(_data);
			try (OutputStream output = con.getOutputStream()) {
				output.write(data.getBytes(charset()));
			}

			//dump response info
			logger().debug("Request Method: {}", con.getRequestMethod());
			log_cookies(manager, con);
			log_headers(con);
			log_https_cert(con);

			String response_content = get_content(con);
			write_string_to_file("ic_token.txt", response_content);

			ic_token = parse_ic_token(response_content);

			logger().debug(String.format("ic_token has been cached (%s)", get_token_partial(ic_token)));

		} catch (Exception e) {
			e1 = e;
		} finally {
			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}

		return ic_token;
	}

	protected String create_query(Map<String, String> parameters) throws BoxServiceClientException {
		String query = "";
		Exception e1 = null;

		try {
			if(parameters != null && parameters.size() > 0){
				StringBuffer sb_parameters = new StringBuffer("?");
				for(String parameter_name : parameters.keySet()){
					String parameter_value = URLEncoder.encode(parameters.get(parameter_name), charset());
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

	protected String parse_ic_token(String response_content) {
		String ic_token = null;
		Pattern pattern = Pattern.compile(".*<input type=\"hidden\" name=\"ic\" value=\"([^\"]*)\" />.*");
		Matcher m = pattern.matcher(response_content);

		if (m.find( )) {
			ic_token = m.group(1);
		}

		return ic_token;
	}

	protected String request_token() throws BoxServiceClientException{
//		  curl 'https://app.box.com/api/oauth2/authorize?response_type=code&client_id='${CLIENT_ID} \
//		    --dump-header header.txt \
//		    --cookie cookies.txt --cookie-jar cookies.txt \
//		    -v \
//		    -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8' \
//		    -H 'Accept-Language: en-US,en;q=0.5' \
//		    -H 'Cache-Control: no-cache' \
//		    -H 'Connection: keep-alive' \
//		    -H 'Host: app.box.com' \
//		    -H 'Pragma: no-cache' \
//		    -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0' > response.html

		Exception e1 = null;

		HttpURLConnection con = null;
		String http_url = "https://app.box.com/api/oauth2/authorize";
		request_token = null;

		reset_cookies();

		Map<String, String> parameters = init_parameters();
		parameters.put("response_type", "code");
		parameters.put("client_id", client_id);

		try {
			con = open_connection(http_url, parameters);
			con.setRequestProperty("Accept-Charset", charset());
			con.setRequestProperty("Accept", URLEncoder.encode("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
			con.setRequestProperty("Accept-Language", URLEncoder.encode("en-US,en;q=0.5"));
			con.setRequestProperty("User-Agent", URLEncoder.encode("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0"));
			con.setRequestProperty("Host", "app.box.com");
			con.setRequestProperty("Accept", URLEncoder.encode("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", charset()));
			con.setRequestProperty("Connection", "keep-alive");

			String response_content = get_content(con);
			write_string_to_file("request_token.txt", response_content);

			//dump response info
			logger().debug("Request Method: {}", con.getRequestMethod());
			log_cookies(manager, con);
			log_headers(con);

			request_token = parse_request_token(response_content);

			logger().debug(String.format("request_token has been cached (%s)", get_request_token_partial()));
		} catch (Exception e) {
			e1 = e;
		} finally {
			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}

		return request_token;
	}

	protected String get_request_token_partial(){
		return get_token_partial(request_token, DEFAULT_TOKEN_PARTIAL_LENGTH);
	}

	protected String get_token_partial(String token){
		return get_token_partial(token, DEFAULT_TOKEN_PARTIAL_LENGTH);
	}

	protected String get_token_partial(String token, int length){
		String request_token_partial = null;
		if(token != null && token.length() > 0){
			int endIndex = token.length();
			int beginIndex = endIndex - length;
			request_token_partial = String.format("...%s", token.substring(beginIndex, endIndex));
		}
		return request_token_partial;
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

	protected URL create_url(String http_url, Map<String, String> parameters) throws BoxServiceClientException {
		URL url = null;
		Exception e1 = null;

		String query = create_query(parameters);

		try {
			url = new URL(http_url + query);

		} catch (Exception e) {
			e1 = e;
		} finally {
			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}

		return url;
	}

	protected Logger logger(){
		if (logger == null)
			logger=LoggerFactory.getLogger("edu.ucmerced.box.client.BoxServiceClient");

		return logger;
	}

	protected BufferedReader get_buffered_reader(InputStream is){
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		return br;
	}

	protected String get_content(HttpURLConnection con) throws BoxServiceClientException{
		String res = null;

		if(con!=null){
			try {

				BufferedReader br = get_buffered_reader(con.getInputStream());
				String input;
				StringBuffer sb_response = new StringBuffer();

				while ((input = br.readLine()) != null){
					sb_response.append(input);
				}
				br.close();

				res = sb_response.toString();
				if(logger().isDebugEnabled() && res != null && res.length() > 100){
					int beginIndex = 0;
					int endIndex = 100;
					logger().debug(String.format("response content recieved (%s...)", res.substring(beginIndex, endIndex)));
				}

			} catch (Exception e) {
				throw new BoxServiceClientException("Unable to get content from the response", e);
			}
		}

		return res;
	}

	protected String charset(){
		return CHARSET;
	}

	protected HttpURLConnection open_connection(String http_url) throws BoxServiceClientException{
		HttpURLConnection con = null;
		Exception e1 = null;

		try {
			URL url = new URL(http_url);
			con = (HttpURLConnection)url.openConnection();

		} catch (IOException e) {
			e1 = e;
		} finally {
			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}

		return con;
	}

	protected HttpURLConnection open_connection(String http_url, Map<String, String> parameters) throws BoxServiceClientException{
		HttpURLConnection con = null;
		Exception e1 = null;

		try {
			URL url = create_url(http_url, parameters);
			con = (HttpURLConnection)url.openConnection();

		} catch (BoxServiceClientException e) {
			e1 = e;
		} catch (IOException e) {
			e1 = e;
		} finally {
			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}

		return con;
	}

	protected Map<String, String> init_parameters(){
		Map<String, String> parameters = new HashMap<String,String>();
		return parameters;
	}

	@Deprecated
	private void write_string_to_file(String response_content) {
		write_string_to_file("chk.txt", response_content);
	}

	private void write_string_to_file(String filename, String response_content) {
		try {
			PrintWriter out = new PrintWriter(filename);
			out.write(response_content);
			out.flush();
			out.close();

			logger().debug(String.format("%d bytes were written to file, %s", response_content.length(), filename));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void log_cookies(CookieManager manager, HttpURLConnection con) {
		if(logger().isDebugEnabled()){
			// get cookies from underlying
			// CookieStore
			CookieStore cookieJar =  manager.getCookieStore();
			List <HttpCookie> cookies = cookieJar.getCookies();
			for (HttpCookie cookie: cookies) {
				logger().debug(String.format("CookieHandler retrieved cookie: %s", cookie));
			}
		}
	}

	private void log_headers(HttpURLConnection con) {
		if(logger().isDebugEnabled()){
			//get all headers
			Map<String, List<String>> map = con.getHeaderFields();
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				logger().debug(String.format("Key : %s, Value: %s", entry.getKey(), entry.getValue()));
			}

			//get header by 'key'
			String server = con.getHeaderField("Server");
			logger().debug("[HEADER] Server: {}", server);
		}
	}

	private void log_https_cert(HttpsURLConnection con){

		if(con!=null && logger().isDebugEnabled()){

			try {

				logger().debug("Response Code: {}", con.getResponseCode());
				logger().debug("Cipher Suite: {}", con.getCipherSuite());

				Certificate[] certs = con.getServerCertificates();
				for(Certificate cert : certs){
					logger().debug("Cert Type: {}", cert.getType());
					logger().debug("Cert Hash Code: {}", cert.hashCode());
					logger().debug("Cert Public Key Algorithm: {}", cert.getPublicKey().getAlgorithm());
					logger().debug("Cert Public Key Format: {}", cert.getPublicKey().getFormat());
				}

			} catch (SSLPeerUnverifiedException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}

		}

	}

}
