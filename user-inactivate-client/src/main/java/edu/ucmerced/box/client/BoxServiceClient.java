package edu.ucmerced.box.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoxServiceClient {
	private static final int DEFAULT_TOKEN_PARTIAL_LENGTH = 5;
	private static final String CHARSET = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()

	Logger logger = null;

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

		HttpURLConnection con = null;
		String http_url = "https://app.box.com/api/oauth2/authorize";
		ic_token = null;

		Map<String, String> parameters = init_parameters();
		parameters.put("login", username);
		parameters.put("password", userpass);
		parameters.put("login_submit", "Authorizing...");
		parameters.put("dologin", "1");
		parameters.put("client_id", client_id);
		parameters.put("response_type", "code");
		parameters.put("redirect_uri", "https%3A%2F%2Fwww.google.com");
		parameters.put("scope", "root_readwrite+manage_enterprise");
		parameters.put("folder_id", "");
		parameters.put("file_id", "");
		parameters.put("state", "");
		parameters.put("reg_step", "");
		parameters.put("submit1", "1");
		parameters.put("folder", "");
		parameters.put("login_or_register_mode", "login");
		parameters.put("new_login_or_register_mode", "");
		parameters.put("__login", "1");
		parameters.put("_redirect_url", "%2Fapi%2Foauth2%2Fauthorize%3Fresponse_type%3Dcode%26client_id%3D" + client_id);
		parameters.put("request_token", request_token);
		parameters.put("_pw_sql", "");

		con = open_connection(http_url, parameters);
		con.setRequestProperty("Accept-Charset", charset());

		String response_content = get_content(con);
		write_string_to_file("ic_token.txt", response_content);

		ic_token = parse_ic_token(response_content);

		logger().debug(String.format("ic_token has been cached (%s)", get_token_partial(ic_token)));

		return ic_token;
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

		HttpURLConnection con = null;
		String http_url = "https://app.box.com/api/oauth2/authorize";
		request_token = null;

		Map<String, String> parameters = init_parameters();
		parameters.put("response_type", "code");
		parameters.put("client_id", client_id);

		con = open_connection(http_url, parameters);
		con.setRequestProperty("Accept-Charset", charset());

		String response_content = get_content(con);
		write_string_to_file("request_token.txt", response_content);

		request_token = parse_request_token(response_content);

		logger().debug(String.format("request_token has been cached (%s)", get_request_token_partial()));

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

		try {
			String query = "";

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

}
