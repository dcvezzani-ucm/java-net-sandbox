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
	String client_id;
	String client_secret;
	String _charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
	String request_token;
	Logger logger = null;

//	Logger logger = LoggerFactory.getLogger(BoxServiceClient.class);

	public BoxServiceClient(String client_id, String client_secret){
		this.client_id = client_id;
		this.client_secret = client_secret;
	}

	public BoxServiceClient(Properties properties){
		client_id = properties.getProperty("client_id");
		client_secret = properties.getProperty("client_secret");
	}

	public String inactivate(String user_id){
		StringBuffer json = new StringBuffer();

		return json.toString();
	}

	protected String request_token() throws BoxServiceClientException{
		//	  'https://app.box.com/api/oauth2/authorize?response_type=code&client_id='${CLIENT_ID}
		HttpURLConnection con = null;
		String http_url = "https://app.box.com/api/oauth2/authorize";
		request_token = null;

		Map<String, String> parameters = init_parameters();
		parameters.put("response_type", "code");
		parameters.put("client_id", client_id);

		con = open_connection(http_url, parameters);
		con.setRequestProperty("Accept-Charset", charset());

		String response_content = get_content(con);
//		write_string_to_file(response_content);

		request_token = parse_request_token(response_content);

		logger().debug(String.format("request_token has been cached (%s)", get_request_token_partial()));

		return request_token;
	}

	protected String get_request_token_partial(){
		String request_token_partial = null;
		if(request_token != null && request_token.length() > 0){
			int endIndex = request_token.length() - 1;
			int beginIndex = endIndex - 5;
			request_token_partial = String.format("...%s", request_token.substring(beginIndex, endIndex));
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
		return _charset;
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


	private void write_string_to_file(String response_content) {
		try {
			PrintWriter out = new PrintWriter("chk.txt");
			out.write(response_content);
			out.flush();
			out.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
