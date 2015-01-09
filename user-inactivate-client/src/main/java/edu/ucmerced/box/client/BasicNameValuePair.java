package edu.ucmerced.box.client;

import org.apache.http.NameValuePair;

public class BasicNameValuePair implements NameValuePair {

	private String name;
	private String value;

	public BasicNameValuePair(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getValue() {
		return this.value;
	}
}
