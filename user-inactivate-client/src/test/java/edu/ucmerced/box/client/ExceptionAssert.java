package edu.ucmerced.box.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;

public class ExceptionAssert extends Assert {
	static ExceptionAssert ea;

	public ExceptionAssert() {
	}

	static{
		ea = new ExceptionAssert();
	}

	public static void assertBothNullOrBothNotNull(Object first, Object second) {
		if (first == null || second == null) {
			assertSame("first and second not both null", first, second);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void assertThrowsException(Throwable expected, Class actual) {
		assertNotNull("Expected exception to be thrown and it wasn't", expected);

		assertTrue(String.format("Cause of exception should have been %s, but was %s",
				actual.getCanonicalName(),
				expected.getCause().getClass().getCanonicalName()),
				actual.isInstance(expected.getCause()));
	}

	public static void assertThrowsException(Throwable expected) {
		assertNotNull("Expected exception to be thrown and it wasn't", expected);
	}

	public static void assertNotThrowsException(Throwable expected) {
		assertNull("Expected no exception to be thrown and there was one thrown", expected);
	}

	public static void assertParametersMatch(String[] expectedParameters, URL actualUrl) {
		String actualQuery = actualUrl.toString().split("\\?")[1];
		String[] _actualParameters = actualQuery.split("\\&");

		String[] _expectedParameters = ea.create_ordered_list_of_strings(expectedParameters);
		String[] actualParameters = ea.create_ordered_list_of_strings(_actualParameters);

		assertArrayEquals(_expectedParameters, actualParameters);
	}

	private String[] create_ordered_list_of_strings(String[] array) {
		List<String> orderedList = new ArrayList<String>();
		for(String item : array){
			orderedList.add(item);
		}
		Collections.sort(orderedList);
		return orderedList.toArray(new String[]{});
	}

	public static void assertHostAndPathMatch(String expectedHostAndPath, URL actual) {
		String actualHostAndPath = String.format("%s://%s:%d%s", actual.getProtocol(), actual.getHost(), actual.getPort(), actual.getPath());
		assertEquals(expectedHostAndPath, actualHostAndPath);
	}


}
