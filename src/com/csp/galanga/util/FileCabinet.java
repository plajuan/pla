package com.csp.galanga.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FileCabinet {

	public void keep(String path, Object o){
		try(FileOutputStream fileOut = new FileOutputStream(path);
				BufferedOutputStream buffer = new BufferedOutputStream(fileOut);
				ObjectOutputStream out = new ObjectOutputStream(buffer);
				){
			out.writeObject(o);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Object read(String path){
		Object obj = null;
		
		try(FileInputStream file = new FileInputStream(path);
				BufferedInputStream buffer = new BufferedInputStream(file);
				ObjectInputStream input = new ObjectInputStream (buffer);){
			obj = input.readObject();
			System.out.println(obj);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
	public Document readXML(File f) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		Document doc = docBuilder.parse(f);
		doc.getDocumentElement().normalize();
		return doc;
	}

}
