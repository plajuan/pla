package com.csp.galanga.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Warehouse {
	
	public Connection connectIFS() throws SQLException {
		String key = ReadProps.FILE.getKey("ENVIRONMENT") + ".IFS.DB.URL";
		final Properties ifsProps = new Properties();
		ifsProps.setProperty("user", System.getProperty("user.name"));
		final String ifsUrl = ReadProps.FILE.getKey(key);

		return DriverManager.getConnection(ifsUrl, ifsProps);
	}
	
	public Connection connectPostgre() throws SQLException{
		String key = ReadProps.FILE.getKey("ENVIRONMENT");
		System.out.println("=========================================");
		System.out.println("Environment: " + key);
		System.out.println("=========================================");
		String url = ReadProps.FILE.getKey(key + ".POSTGRES.URL");
		String user = ReadProps.FILE.getKey(key + ".POSTGRES.ID");
		String password = ReadProps.FILE.getKey(key + ".POSTGRES.PASSWORD");
		final Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", password);
		
		return DriverManager.getConnection(url, props); 
	}
	
	public Connection connectCspSys() throws SQLException{
		String env = ReadProps.FILE.getKey("ENVIRONMENT");
		String url = ReadProps.FILE.getKey(env + ".CSPSYS.DB.URL");
		String user = ReadProps.FILE.getKey(env + ".CSPSYS.DB.USER");
		String pass = ReadProps.FILE.getKey(env + ".CSPSYS.DB.PASSWORD");
		
		return DriverManager.getConnection(url, user, pass);
	}
	
	public Connection connectSoftway() throws SQLException{
		String env = ReadProps.FILE.getKey("ENVIRONMENT");
		String url = ReadProps.FILE.getKey(env + ".SOFTWAY.DB.URL");
		String user = ReadProps.FILE.getKey(env + ".SOFTWAY.DB.USER");
		String password = ReadProps.FILE.getKey(env + ".SOFTWAY.DB.PASSWORD");
		final Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", password);
		
		return DriverManager.getConnection(url, props);
	}
}
