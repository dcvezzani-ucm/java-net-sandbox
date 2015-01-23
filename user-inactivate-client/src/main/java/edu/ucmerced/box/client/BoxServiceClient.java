package edu.ucmerced.box.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class BoxServiceClient.
 *
 * Use this to authenticate with Box, establish a session and obtaining an access
 * token and then making a RESTful Box Api request.
 *
 * At this point, the only action desired is to inactivate a user.
 */
public class BoxServiceClient {

	/** The Constant DEFAULT_TOKEN_PARTIAL_LENGTH.
	 * For debugging purposes only.
	 */
	private static final int DEFAULT_TOKEN_PARTIAL_LENGTH = 5;

	/** The Constant CHARSET.
	 * 	When encoding/decoding, the charset should be specified.
	 */
	private static final String CHARSET = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()

	/** The logger. */
	private Logger logger = null;

	/** The httpclient.
	 * 	Entity used to make requests to Box as if the back-end were doing so through a web
	 * 	browser, going through the "User Experience"
	 *
	 * 	See <a href="https://developers.box.com/oauth/">https://developers.box.com/oauth/</a>
	 */
	private CloseableHttpClient httpclient = null;

	/**
	 * The <code>client_id</code> and <code>client_secret</code> attributes are generated/provided when a
	 * Box application is created.
	 * <p>
	 * See <a href="https://ucmerced.app.box.com/developers/services">https://ucmerced.app.box.com/developers/services</a>
	 * <p>
	 * The <code>username</code> and <code>userpass</code> attributes are the credentials for the administrator
	 * account that is being used to manage the enterprise users.  These are established
	 * when you are given or create your Box user account and are the same values
	 * used during a standard login to Box.
	 * <p>
	 * There is a set of handshakes that takes place to authenticate and obtain an
	 * access_token.
	 * <p>
	 * <ol>
	 * <li>ask for <code>request_token</code>, supplying only the client_id</li>
	 * <li>authenticate using admin account credentials (<code>ic_token</code>); check response for "IC" token</li>
	 * <li>authorize access to the admin account (<code>auth_token</code>); check response for "AUTH" token</li>
	 * <li>request <code>access_token</code></li>
	 * </ol>
	 * <p>
	 * Once the <code>access_token</code> is obtained, RESTful Box Api calls may be made.
	 */
	String[] authentication_attributes = new String[]{"client_id", "client_secret", "username", "userpass", "request_token", "ic_token", "auth_token", "access_token"};

	/** The client_id. */
	String client_id;

	/** The client_secret. */
	String client_secret;

	/** The username. */
	String username;

	/** The userpass. */
	String userpass;

	/** Used to initiate the authentication and authorization process to obtain an
	 * access_token, necessary for making RESTful Box Api requests.
	 */
	String request_token;

	/** Part of the credentials form used for authenticating the Box user.  This
	 * is necessary in order for authentication to succeed and move on to the
	 * authorization phase. */
	String ic_token;

	/** This is obtained once the specified user has granted authorization to
	 * make RESTful Box Api requests of the associated Box enterprise system. */
	String auth_token;

	/** One all authentication and authorization handshaking is complete, the end
	 * result is an access token.  This token must be included in the header of
	 * every RESTful Box Api call that is made. */
	String access_token;

	/**
	 * Instantiates a new box service client.
	 *
	 * @param client_id the client_id
	 * @param client_secret the client_secret
	 */
	@Deprecated
	public BoxServiceClient(String client_id, String client_secret){
		this.client_id = client_id;
		this.client_secret = client_secret;
		username = null;
		userpass = null;
	}

	/**
	 * Instantiates a new box service client.
	 *
	 * @param client_id the client_id
	 * @param client_secret the client_secret
	 * @param username the username
	 * @param userpass the userpass
	 */
	public BoxServiceClient(String client_id, String client_secret, String username, String userpass){
		this.client_id = client_id;
		this.client_secret = client_secret;
		this.username = username;
		this.userpass = userpass;
	}

	/**
	 * Instantiates a new box service client.
	 * <p>
	 * Properties supplied in the argument typically come from a properties file named
	 * "application.properties" in the classpath.
	 *
	 * @param properties the properties
	 */
	public BoxServiceClient(Properties properties){
		client_id = properties.getProperty("client_id");
		client_secret = properties.getProperty("client_secret");
		username = properties.getProperty("username");
		userpass = properties.getProperty("userpass");
	}

	/**
	 * Inactivate the user specified by their ucmnetid.
	 *
	 * @param user_id the staff user's ucmnetid
	 * @return the results (in JSON format)
	 */
	public String inactivate(String user_id){
		StringBuffer json = new StringBuffer();

		return json.toString();
	}

	/**
	 * Extract the {@link #ic_token} from the response (credentials form).
	 *
	 * @return the <code>ic_token</code>
	 * @throws BoxServiceClientException the box service client exception
	 */
	protected String ic_token() throws BoxServiceClientException{
		/* Apache's HttpClient will be used to mimic the behavior shown in the follow curl call
		 *
		  curl 'https://ucmerced.app.box.com/api/oauth2/authorize?response_type=code&client_id='${CLIENT_ID} \
		    -X POST \
		    --dump-header header.txt \
		    --cookie cookies.txt --cookie-jar cookies.txt \
		    -v \
		    -H 'Host: ucmerced.app.box.com' \
		    -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0' \
		    -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*''/*;q=0.8' \
		    -H 'Accept-Language: en-US,en;q=0.5' \
		    -H 'Referer: https://app.box.com/api/oauth2/authorize?response_type=code&client_id='${CLIENT_ID} \
		    -H 'Connection: keep-alive' \
		    -H 'Content-Type: application/x-www-form-urlencoded' \
		    --data 'login='${USERNAME}'&password='${USERPASS}'&login_submit=Authorizing...&dologin=1&client_id='${CLIENT_ID}'&response_type=code&redirect_uri=https%3A%2F%2Fwww.google.com&scope=root_readwrite+manage_enterprise&folder_id=&file_id=&state=&reg_step=&submit1=1&folder=&login_or_register_mode=login&new_login_or_register_mode=&__login=1&_redirect_url=%2Fapi%2Foauth2%2Fauthorize%3Fresponse_type%3Dcode%26client_id%3D'${CLIENT_ID}'&request_token='${REQUEST_TOKEN}'&_pw_sql=' > response.html
		 */

		Exception e1 = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		ic_token = null;

		// base url
		String http_url = "https://app.box.com/api/oauth2/authorize";

		// set parameters
		Map<String, String> parameters = init_parameters();
		parameters.put("response_type", "code");
		parameters.put("client_id", client_id);
		String query = create_query(parameters);

		// full url (with inline parameters)
		HttpPost httpPost = new HttpPost(String.format("%s%s", http_url, query));

		// prepare form parameters
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

		try {

			// set form parameters
			httpPost.setEntity(new UrlEncodedFormEntity((List<? extends org.apache.http.NameValuePair>) nvps));

			// set headers
			httpPost.setHeader("User-Agent", URLEncoder.encode("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0", charset()));
			httpPost.setHeader("Accept-Charset", charset());
			httpPost.setHeader("Accept-Language", URLEncoder.encode("en-US,en;q=0.5", charset()));
			httpPost.setHeader("Host", "ucmerced.app.box.com");
			httpPost.setHeader("Accept", URLEncoder.encode("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", charset()));
			httpPost.setHeader("Referer", URLEncoder.encode(String.format("https://app.box.com/api/oauth2/authorize?response_type=code&client_id=%s", client_id), charset()));
			httpPost.setHeader("Connection", "keep-alive");

			// send request; get response
			response = http_client().execute(httpPost);
			String response_content = get_content(response);

			// dump response info
			logger().debug("Request Method: {}", httpPost.getMethod());
			log_headers(response);

			if(logger().isDebugEnabled()){
				write_string_to_file("ic_token.txt", response_content);
			}

			// extract "IC" token
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

	/**
	 * Extract the {@link #auth_token} from the response (authorization form).
	 *
	 * @return the <code>auth_token</code>
	 * @throws BoxServiceClientException the box service client exception
	 */
	protected String auth_token() throws BoxServiceClientException{
		/* Apache's HttpClient will be used to mimic the behavior shown in the follow curl call
		 *
	  curl 'https://app.box.com/api/oauth2/authorize?response_type=code&client_id='${CLIENT_ID}'' \
	    -X POST \
	    --dump-header header.txt \
	    --cookie cookies.txt --cookie-jar cookies.txt \
	    -v \
	    -H 'Host: app.box.com' \
	    -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0' \
	    -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*''/*;q=0.8' \
	    -H 'Accept-Language: en-US,en;q=0.5' \
	    -H 'Referer: https://ucmerced.app.box.com/api/oauth2/authorize?response_type=code&client_id='${CLIENT_ID}'' \
	    -H 'Connection: keep-alive' \
	    -H 'Content-Type: application/x-www-form-urlencoded' \
	    --data 'client_id='${CLIENT_ID}'&response_type=code&redirect_uri=https%3A%2F%2Fwww.google.com&scope=root_readwrite+manage_enterprise&folder_id=&file_id=&state=&doconsent=doconsent&ic='${IC_TOKEN}'&consent_accept=Grant+access+to+Box&request_token='${REQUEST_TOKEN}'' > response.html
		 */

		Exception e1 = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		auth_token = null;

		// base url
		String http_url = "https://app.box.com/api/oauth2/authorize";

		// set parameters
		Map<String, String> parameters = init_parameters();
		parameters.put("response_type", "code");
		parameters.put("client_id", client_id);
		String query = create_query(parameters);

		// full url (with inline parameters)
		HttpPost httpPost = new HttpPost(String.format("%s%s", http_url, query));

		// prepare form parameters
		List <BasicNameValuePair> nvps = new ArrayList <BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("client_id", client_id));
		nvps.add(new BasicNameValuePair("response_type", "code"));
		nvps.add(new BasicNameValuePair("redirect_uri", "https://www.google.com"));
		nvps.add(new BasicNameValuePair("scope", "root_readwrite manage_enterprise"));
		nvps.add(new BasicNameValuePair("folder_id", ""));
		nvps.add(new BasicNameValuePair("file_id", ""));
		nvps.add(new BasicNameValuePair("state", ""));
		nvps.add(new BasicNameValuePair("doconsent", "doconsent"));
		nvps.add(new BasicNameValuePair("ic", ic_token));
		nvps.add(new BasicNameValuePair("consent_accept", "Grant access to Box"));
		nvps.add(new BasicNameValuePair("request_token", request_token));

		try {
			// set form parameters
			httpPost.setEntity(new UrlEncodedFormEntity((List<? extends org.apache.http.NameValuePair>) nvps));

			// set headers
			httpPost.setHeader("User-Agent", URLEncoder.encode("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0", charset()));
			httpPost.setHeader("Accept-Charset", charset());
			httpPost.setHeader("Accept-Language", URLEncoder.encode("en-US,en;q=0.5", charset()));
			httpPost.setHeader("Host", "app.box.com");
			httpPost.setHeader("Accept", URLEncoder.encode("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", charset()));
			httpPost.setHeader("Referer", URLEncoder.encode(String.format("https://app.box.com/api/oauth2/authorize?response_type=code&client_id=%s", client_id), charset()));
			httpPost.setHeader("Connection", "keep-alive");

			// send request; get response
			response = http_client().execute(httpPost);
			String response_content = get_content(response);

			//dump response info
			logger().debug("Request Method: {}", httpPost.getMethod());
			log_headers(response);

			if(logger().isDebugEnabled()){
				write_string_to_file("auth_token.txt", response_content);
			}

			// extract "AUTH" token
			Header[] _header_location = response.getHeaders("Location");
			String header_location = null;
			if(_header_location != null){
				header_location = _header_location[0].getValue();
			}

			auth_token = parse_auth_token(header_location);
			logger().debug(String.format("auth_token has been cached (%s)", get_token_partial(auth_token)));

		} catch (Exception e) {
			e1 = e;
		} finally {
			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}

		return ic_token;
	}

	/**
	 * Extract the {@link #access_token} from the response.
	 *
	 * @return the <code>access_token</code>
	 * @throws BoxServiceClientException the box service client exception
	 */
	protected String access_token() throws BoxServiceClientException{
		/* Apache's HttpClient will be used to mimic the behavior shown in the follow curl call
		 *
	  export ACCESS_TOKEN=$( curl https://api.box.com/oauth2/token \
		    -X POST \
		    --dump-header header.txt \
		    --cookie cookies.txt --cookie-jar cookies.txt \
		    -v \
		    -d "grant_type=authorization_code&code=${AUTH_TOKEN}&client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}" | jq '. | .access_token' | sed -n -e 's/\"\([^\"]*\)\"/\1/p' )
		 */

		Exception e1 = null;
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		access_token = null;

		// base url
		String http_url = "https/api.box.com/oauth2/token";

		// full url (with inline parameters)
		HttpPost httpPost = new HttpPost(String.format(http_url));

		// prepare form parameters
		List <BasicNameValuePair> nvps = new ArrayList <BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));
		nvps.add(new BasicNameValuePair("code", auth_token));
		nvps.add(new BasicNameValuePair("client_id", client_id));
		nvps.add(new BasicNameValuePair("client_secret", client_secret));

		try {
			// set form parameters
			httpPost.setEntity(new UrlEncodedFormEntity((List<? extends org.apache.http.NameValuePair>) nvps));

			// send request; get response
			response = http_client().execute(httpPost);
			String response_content = get_content(response);

			//dump response info
			logger().debug("Request Method: {}", httpPost.getMethod());
			log_headers(response);

			if(logger().isDebugEnabled()){
				write_string_to_file("access_token.txt", response_content);
			}

			// extract "ACCESS" token
			access_token = parse_access_token(response_content);
			logger().debug(String.format("access_token has been cached (%s)", get_token_partial(access_token)));

		} catch (Exception e) {
			e1 = e;
		} finally {
			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}

		return access_token;
	}

	/**
	 * Show Box user profile for the currently authenticated and authorized user.
	 *
	 * @return the user profile (in JSON format)
	 * @throws BoxServiceClientException the box service client exception
	 */
	protected String my_box_profile() throws BoxServiceClientException{
		/* Apache's HttpClient will be used to mimic the behavior shown in the follow curl call
		 *
	  curl https://api.box.com/2.0/users/me \
		    --dump-header header.txt \
		    --cookie cookies.txt --cookie-jar cookies.txt \
		    -v \
		    -H "Authorization: Bearer ${ACCESS_TOKEN}" | jq '.'
		 */

		Exception e1 = null;
		CloseableHttpResponse response = null;
		String my_box_profile = null;

		// base url
		String http_url = "https://api.box.com/2.0/users/me";

		// full url (with inline parameters)
		HttpGet httpGet = new HttpGet(String.format(http_url));

		try {
			// set headers
			httpGet.setHeader("Authorization", String.format("Bearer %s", access_token));

			// send request; get response
			response = http_client().execute(httpGet);
			String response_content = get_content(response);

			//dump response info
			logger().debug("Request Method: {}", httpGet.getMethod());
			log_headers(response);

			if(logger().isDebugEnabled()){
				write_string_to_file("my_box_profile.txt", response_content);
			}

			// return user profile
			my_box_profile = response_content;

		} catch (Exception e) {
			e1 = e;
		} finally {
			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}

		return my_box_profile;
	}

	/**
	 * Creates a String representing a full URL, complete with query clause.
	 *
	 * @param parameters the parameters
	 * @return the full URL
	 * @throws BoxServiceClientException the box service client exception
	 */
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

	/**
	 * Following successful authentication and authorization, the request to get an
	 * active <code>access_token</code> returns JSON.  This method will parse the JSON response
	 * and return the <code>access_token</code> value only.
	 *
	 * @param response_content the response_content
	 * @return the string
	 */
	protected String parse_access_token(String response_content) {
		/* Sample JSON containing the access token value
		 *
		{
		  "access_token": "2kFUcXylxR5c7wjx2iz51SrZuSbg02a4",
		  "expires_in": 4202,
		  "restricted_to": [],
		  "refresh_token": "M1DFeKPmoa0lnZ4nyclWZCSpFnphueH8WbZ2X84QkvIfJXpmXPw6kuclqCWA2rkb",
		  "token_type": "bearer"
		}
		 */

		JSONObject obj = new JSONObject(response_content);
		String access_token = obj.getString("access_token");

		return access_token;
	}

	/**
	 * Following the request to initiate authentication, an html response is returned
	 * with an html form that includes a hidden field with an <code>ic_token</code>.
	 *
	 * @param response_content from the initial request
	 * @return the <code>ic_token</code>
	 */
	protected String parse_ic_token(String response_content) {
		String ic_token = null;
		Pattern pattern = Pattern.compile(".*<input type=\"hidden\" name=\"ic\" value=\"([^\"]*)\" />.*");
		Matcher m = pattern.matcher(response_content);

		if (m.find( )) {
			ic_token = m.group(1);
		}

		return ic_token;
	}

	/**
	 * Following successful authentication and authorization, the
	 * <code>auth_token</code> is returned in the response header.  This method
	 * extracts the <code>auth_token</code>.
	 *
	 * @param header_location the header_location
	 * @return the string
	 */
	protected String parse_auth_token(String header_location) {
		String auth_token = null;
		Pattern pattern = Pattern.compile(".*code=([^&[:space:]]*).*");
		Matcher m = pattern.matcher(header_location);

		if (m.find( )) {
			auth_token = m.group(1);
		}

		return auth_token;
	}

	/**
	 * This is the first of many steps to authenticate, authorize and obtain an
	 * <code>access_token</code> that is then used to make RESTful Box Api requests.  The
	 * result is a {@link #request_token} that is then used to start the
	 * authentication step.
	 *
	 * @return the <code>request_token</code>
	 * @throws BoxServiceClientException the box service client exception
	 */
	protected String request_token() throws BoxServiceClientException{
		/* Apache's HttpClient will be used to mimic the behavior shown in the follow curl call
		 *
	  curl 'https://app.box.com/api/oauth2/authorize?response_type=code&client_id='${CLIENT_ID} \
	    --dump-header header.txt \
	    --cookie cookies.txt --cookie-jar cookies.txt \
	    -v \
	    -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*''/*;q=0.8' \
	    -H 'Accept-Language: en-US,en;q=0.5' \
	    -H 'Cache-Control: no-cache' \
	    -H 'Connection: keep-alive' \
	    -H 'Host: app.box.com' \
	    -H 'Pragma: no-cache' \
	    -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0' > response.html
		 */

		Exception e1 = null;

		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		ic_token = null;

		// base url
		String http_url = "https://app.box.com/api/oauth2/authorize";

		// set parameters
		Map<String, String> parameters = init_parameters();
		parameters.put("response_type", "code");
		parameters.put("client_id", client_id);
		String query = create_query(parameters);

		// full url (with inline parameters)
		HttpGet httpGet = new HttpGet(String.format("%s%s", http_url, query));

		try {

			// set headers
			httpGet.setHeader("Accept-Charset", charset());
			httpGet.setHeader("Accept", URLEncoder.encode("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", charset()));
			httpGet.setHeader("Accept-Language", URLEncoder.encode("en-US,en;q=0.5", charset()));
			httpGet.setHeader("User-Agent", URLEncoder.encode("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0", charset()));
			httpGet.setHeader("Host", "app.box.com");
			httpGet.setHeader("Accept", URLEncoder.encode("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", charset()));
			httpGet.setHeader("Connection", "keep-alive");

			// send request; get response
			response = http_client().execute(httpGet);
			String response_content = get_content(response);

			if(logger().isDebugEnabled()){
				write_string_to_file("request_token.txt", response_content);
			}

			//dump response info
			logger().debug("Request Method: {}", httpGet.getMethod());
			log_headers(response);

			// extract "REQUEST" token
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

	/**
	 * Show the last part of the <code>request_token</code>.
	 *
	 * @return part of the <code>request_token</code>
	 */
	protected String get_request_token_partial(){
		return get_token_partial(request_token, DEFAULT_TOKEN_PARTIAL_LENGTH);
	}

	/**
	 * Logging tokens may feel like a breach of security.  In order to provide
	 * useful logging, but guard against disclosing too much, just show the
	 * last few characters of the token.
	 *
	 * @param token the token
	 * @return part of the <code>request_token</code>
	 */
	protected String get_token_partial(String token){
		return get_token_partial(token, DEFAULT_TOKEN_PARTIAL_LENGTH);
	}

	/**
	 * Logging tokens may feel like a breach of security.  In order to provide
	 * useful logging, but guard against disclosing too much, just show the
	 * last <code>length</code> characters of the token.
	 *
	 * @param token the token
	 * @param length the number of characters to reveal (from the end of the token)
	 * @return part of the <code>request_token</code>
	 */
	protected String get_token_partial(String token, int length){
		String request_token_partial = null;
		if(token != null && token.length() > 0){
			int endIndex = token.length();
			int beginIndex = endIndex - length;
			request_token_partial = String.format("...%s", token.substring(beginIndex, endIndex));
		}
		return request_token_partial;
	}

	/**
	 * Given the content body of an html response, scrape the page for the
	 * <code>request_token</code>.  This should be located in a hidden input field in the
	 * html form.
	 *
	 * @param response_content the response_content
	 * @return the <code>request_token</code>
	 */
	protected String parse_request_token(String response_content) {
		String request_token = null;
		Pattern pattern = Pattern.compile(".*<input type=\"hidden\" name=\"request_token\" value=\"([^\"]*)\">.*");
		Matcher m = pattern.matcher(response_content);

		if (m.find( )) {
			request_token = m.group(1);
		}

		return request_token;
	}

	/**
	 * Given a base url and a map of parameters, create a full url, complete
	 * with a query string.  If parameters don't exist, then there shouldn't
	 * be any query string in the url.
	 *
	 * @param http_url the http_url
	 * @param parameters the parameters
	 * @return the complete url, with query string if applicable
	 * @throws BoxServiceClientException the box service client exception
	 */
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

	/**
	 * Get handle to the logger.
	 * <p></p>
	 * Encapsulating the logger this way allows for some unit testing
	 * and validation that would not otherwise be so easy.
	 *
	 * @return the logger
	 */
	protected Logger logger(){
		if (logger == null)
			logger=LoggerFactory.getLogger("edu.ucmerced.box.client.BoxServiceClient");

		return logger;
	}

	/**
	 * Gets a buffered reader.
	 * <p></p>
	 * Encapsulating the logger this way allows for some unit testing
	 * and validation that would not otherwise be so easy.
	 *
	 * @param is the is
	 * @return the _buffered_reader
	 */
	protected BufferedReader get_buffered_reader(InputStream is){
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		return br;
	}

	/**
	 * Gets the response body content (for Apache HttpClient).
	 *
	 * @param response the response
	 * @return the body content
	 * @throws BoxServiceClientException the box service client exception
	 */
	protected String get_content(CloseableHttpResponse response) throws BoxServiceClientException{
		String response_content = null;

		if(response!=null){

			try {
				logger().debug("Status: {}", response.getStatusLine());
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();

				BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
				String input;
				StringBuffer sb_response = new StringBuffer();

				while ((input = br.readLine()) != null){
					sb_response.append(input);
				}
				br.close();

				response_content = sb_response.toString();
				logger().debug("Response content: {}", response_content);

			} catch (Exception e) {
				throw new BoxServiceClientException("Unable to get content from the response", e);
			}
		}

		return response_content;
	}

	/**
	 * Gets the response body content (for java.net).
	 *
	 * @param con the http connection
	 * @return the body content
	 * @throws BoxServiceClientException the box service client exception
	 */
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

	/**
	 * Gets the charset (used for encoding and decoding).
	 * <p></p>
	 * Encapsulating the logger this way allows for some unit testing
	 * and validation that would not otherwise be so easy.
	 *
	 * @return the string
	 */
	protected String charset(){
		return CHARSET;
	}

	/**
	 * Opens a http(s) connection (for java.net).
	 *
	 * @param http_url the http_url
	 * @return the http url connection
	 * @throws BoxServiceClientException the box service client exception
	 */
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

	/**
	 * Opens a http(s) connection (for java.net).
	 *
	 * @param http_url the http_url
	 * @param parameters the parameters
	 * @return the http url connection
	 * @throws BoxServiceClientException the box service client exception
	 */
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

	/**
	 * Init map for query parameters.
	 * <p></p>
	 * Encapsulating the logger this way allows for some unit testing
	 * and validation that would not otherwise be so easy.
	 *
	 * @return the map
	 */
	protected Map<String, String> init_parameters(){
		Map<String, String> parameters = new HashMap<String,String>();
		return parameters;
	}

	/**
	 * Write_string_to_file.
	 *
	 * @param response_content the response_content
	 * @deprecated Use {@link #write_string_to_file(String, String)} instead
	 */
	@Deprecated
	private void write_string_to_file(String response_content) {
		write_string_to_file("chk.txt", response_content);
	}

	/**
	 * Write the provided string to a file with the specified path and name.  In
   * this file, the string will typically be the body content from an http(s)
   * response.
	 *
	 * @param filename the filename
	 * @param response_content the response_content
	 */
	private void write_string_to_file(String filename, String response_content) {
		try {
			PrintWriter out = new PrintWriter(filename);
			out.write(response_content);
			out.flush();
			out.close();

			logger().debug(String.format("%d bytes were written to file, %s", response_content.length(), filename));

		} catch (FileNotFoundException e) {

			String response_content_sample = "<null>";
			if (response_content != null){
				int sample_length = (response_content.length() > 100) ? 100 : response_content.length();
				response_content_sample =  response_content.substring(0,  sample_length);
			}

			logger().warn(String.format("Unable to write string to file: %s (%s)", filename, response_content_sample));
			e.printStackTrace();
		}
	}

	/**
	 * Log cookies (for java.net).
   * <p></p>
   * It appears that cookies are automatically handled for Apache's HttpClient.
	 *
	 * @param manager the cookie manager
	 * @param con the http(s) connection
	 */
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

	/**
	 * Log headers (for Apache HttpClient).
	 *
	 * @param response the response
	 */
	private void log_headers(CloseableHttpResponse response) {
		if(logger().isDebugEnabled()){
			for(Header header : response.getAllHeaders()){
				logger().debug(String.format("Name : %s, Value: %s", header.getName(), header.getValue()));
			}
			//get header by 'key'
			//			String server = con.getHeaderField("Server");
			//			logger().debug("[HEADER] Server: {}", server);
		}
	}

	/**
	 * Log headers (for java.net).
	 *
	 * @param con the http(s) connection
	 */
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

	/**
	 * Log https certificate attributes.
	 *
	 * @param con the http(s) connection
	 */
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

	/**
	 * Http client (for Apache HttpClient).
	 * <p></p>
	 * Encapsulating the logger this way allows for some unit testing
	 * and validation that would not otherwise be so easy.
	 *
	 * @return the closeable http client
	 */
	protected CloseableHttpClient http_client(){
		if(httpclient == null){
			httpclient = HttpClients.createDefault();
		}
		return httpclient;
	}

}
