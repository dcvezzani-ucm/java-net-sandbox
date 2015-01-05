package edu.ucmerced.box.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoxServiceClient {
	String client_id;
	String client_secret;
	String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()

	Logger logger = LoggerFactory.getLogger(NameValuePair.class);

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
		URL url;
		HttpURLConnection con;
		String http_url = "https://app.box.com/api/oauth2/authorize";
		String res = null;

		try{
			Map<String, String> parameters = new HashMap<String,String>();
			parameters.put("response_type", "code");
			parameters.put("client_id", client_id);

			url = create_url(http_url, parameters);

			con = (HttpURLConnection)url.openConnection();
			con.setRequestProperty("Accept-Charset", charset);

			get_content(con);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return res;
	}

	protected URL create_url(String http_url, Map<String, String> parameters) throws BoxServiceClientException {
		URL url = null;
		Exception e1 = null;

		try {
			StringBuffer sb_parameters = new StringBuffer();
			for(String parameter_name : parameters.keySet()){
				String parameter_value = URLEncoder.encode(parameters.get(parameter_name), charset);
				sb_parameters.append(String.format("%s:%s&", parameter_name, parameter_value));
			}

			String query = sb_parameters.substring(0, sb_parameters.length()-1);

			url = new URL(http_url + "?" + query);
		} catch (UnsupportedEncodingException e) {
			e1 = e;
		} catch (MalformedURLException e) {
			e1 = e;
		} finally {
			if(e1 != null){
				throw new BoxServiceClientException(e1);
			}
		}

		return url;
	}

	protected Logger logger(){
		return logger;
	}

	protected BufferedReader get_buffered_reader(InputStream is){
		BufferedReader br =
				new BufferedReader(
						new InputStreamReader(is));
		return br;
	}

	protected String get_content(HttpURLConnection con) throws BoxServiceClientException{
		String res = null;

		if(con!=null){
			try {

				logger().debug("****** Content of the URL ********");
				BufferedReader br = get_buffered_reader(con.getInputStream());
				String input;
				StringBuffer sb_response = new StringBuffer();

				while ((input = br.readLine()) != null){
					sb_response.append(input);
				}
				br.close();

				res = sb_response.toString();
				logger().debug(String.format("response content: %s", res));

			} catch (Exception e) {
				throw new BoxServiceClientException("Unable to get content from the response", e);
			}
		}

		return res;
	}
}
