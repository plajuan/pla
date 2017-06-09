package com.csp.galanga.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebScout {

	public ArrayList<String> getIfsUsers(){
		ArrayList<String> lista = new ArrayList<>();
		
		try {
			String pageContent = "";
			URL usuIfs = new URL("http://srv-for-csp01:9191/CSPRequests/wiki/UsuariosIfs");
			try(BufferedReader in = new BufferedReader(new InputStreamReader(usuIfs.openStream(), StandardCharsets.UTF_8))){
				String line = ""; 
				while ((line = in.readLine()) != null){
					pageContent += line;
				}
				
			}
			
			pageContent = pageContent.split("<hr />")[3];
			
			Pattern pattern = Pattern.compile("CSP_[A-Z]{3,}_[A-Z]{1,}|CSP_[A-Z]{3,}");
			Matcher matcher = pattern.matcher(pageContent);
			while (matcher.find()){
				lista.add(matcher.group(0));
			}
		} catch (Exception e) {
			System.out.println("Lista usuários do IFS não está disponível");
		}
		
		return lista;
	}
}
