package com.csp.galanga.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class QuillPen {

	public void saveFile(String content, String pathAndname) throws FileNotFoundException, UnsupportedEncodingException {
		String encoding = ReadProps.FILE.getKey("FILE.ENCODING");
		try (PrintWriter w = new PrintWriter( new File(pathAndname), encoding );) {
			w.println(content);
			w.close();
		}
	}
	
	public void saveFile(String content, String pathAndname, String encoding) throws FileNotFoundException, UnsupportedEncodingException {		
		try (PrintWriter w = new PrintWriter( new File(pathAndname), encoding );) {
			w.println(content);
			w.close();
		}
	}
	
	public void saveStringNIO(String content, String pathAndname){
		File file = new File(pathAndname);
		try {
			java.nio.file.Files.write(Paths.get(file.toURI()), content.getBytes("utf-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static String parseValues(String[] values) {
		String sep = new Character((char) 31).toString();
		String end = new Character((char) 30).toString();
		String r = null;
		for (int i = 0; i < values.length; i++) {
			if (r != null) {
				r += values[i] + sep + values[++i] + end;
			} else {
				r = values[i] + sep + values[++i] + end;
			}
		}

		return r;
	}
	
	/**
	 * This method is only invoked for debug purposes
	 * @param a
	 * @return
	 */
	public String writeHTML(ArrayList<String[]> a) {
		StringBuilder html = new StringBuilder();
		html.append("<html>");
		html.append("<head></head>");
		html.append("<body>");
		html.append("<table border='1'>");
		for (String[] s : a) {
			html.append("<tr>");
			for(int i=0; i < s.length; i++){
				html.append("<td>" + s[i] + "</td>");
			}
			html.append("</tr>");
		}
		html.append("</table>");
		html.append("</body>");
		html.append("</html>");
		return html.toString();
	}

	/**
	 * Generates an XML file with users login to Oracle DB
	 * @param a
	 * @return
	 */
	public String writeXML(ArrayList<String[]> a, String estrutura) {
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>");
		String[] keys = estrutura.split(" ");
		for (String[] s: a){
			xml.append("<" + keys[0] + ">");
			
			for (int i=0; i < s.length; i++){
				xml.append(String.format("<%1$s>%2$s</%1$s>", keys[i+1], s[i])); 
			}
			xml.append("</" + keys[0] + ">");
		}
		xml.append("</root>");
		return xml.toString();
	}
	
	public String writeTXT(ArrayList<String[]> a){		
		StringBuilder txt = new StringBuilder();
		for(String[] s: a){
			for(String it: s){
				txt.append(it + ",");
			}
			txt.deleteCharAt(txt.lastIndexOf(","));
			txt.append("\n");
		}
		
		return txt.toString();
	}
	
	public String writeTxtSingleValue(ArrayList<String> a){
		StringBuilder txt = new StringBuilder();
		for(String s: a){
			txt.append(s + "\n");
		}
		return txt.toString();
	}
	
	/**
	 * JSON format will not be used for data transfer
	 * @param a
	 * @return
	 * @deprecated
	 */
	public String writeJSON(ArrayList<String[]> a) {		
		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append("\"item\":[");
		for(String[] s: a){
			String line = String.format("{ \"id\":\"%s\" , \"user\":\"%s\" , \"begin\":\"%s\" , \"end\":\"%s\" }", s[0], s[1], s[2], s[3]);
			json.append(line);
		}
		json.append("]}");
		return json.toString();
	}
	
}
