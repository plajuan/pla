package com.csp.galanga.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadProps {
	public static volatile ReadProps FILE = new ReadProps();
	private Properties prop = new Properties();

	private ReadProps() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream stream = classLoader.getResourceAsStream("common.properties");) {
			prop.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getKey(String key) {
		return prop.getProperty(key);
	}

}
