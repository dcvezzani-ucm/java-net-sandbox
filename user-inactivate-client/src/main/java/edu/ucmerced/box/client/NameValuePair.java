package edu.ucmerced.box.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.*;

public abstract class NameValuePair {
	public String name;
	public String value;
    Logger logger = LoggerFactory.getLogger(NameValuePair.class);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString(){
		String urlEncodedString = null;
		try {
			urlEncodedString = URLEncoder.encode(this.name, "UTF-8") + "=" + URLEncoder.encode(this.value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Unable to encode string > name: " + this.name + ", value: " + this.value);
		}

		return urlEncodedString;
	}
}
