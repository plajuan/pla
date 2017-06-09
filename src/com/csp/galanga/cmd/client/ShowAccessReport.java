package com.csp.galanga.cmd.client;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.dao.UserDAO;
import com.csp.galanga.dto.Usuario;
import com.csp.galanga.util.FileCabinet;
import com.csp.galanga.util.ReadProps;

public class ShowAccessReport implements Command {

	private final String path = ReadProps.FILE.getKey("FILE.CLIENT.PATH");
	private final String reportFile = path + ReadProps.FILE.getKey("FILE.REPORT");
	
	@SuppressWarnings("unchecked")
	@Override
	public void doIt() {
		System.out.println("Show Access Report");
		
		HashMap<String, Usuario> usuarios = null;
		FileCabinet cabinet = new FileCabinet();
		Object obj = cabinet.read(ReadProps.FILE.getKey("FILE.CLIENT.PATH") + ReadProps.FILE.getKey("FILE.USERS"));
		String softwayLog = (String) cabinet.read(ReadProps.FILE.getKey("FILE.CLIENT.PATH") + ReadProps.FILE.getKey("FILE.SOFTWAY"));
		if (obj instanceof HashMap){
			usuarios = (HashMap<String, Usuario>) obj;
		}
		HashMap<String,Integer> userLog = null;
		UserDAO dao = new UserDAO(); 
		try {
			userLog = dao.insert(usuarios);
			System.out.println(userLog.get("logLines") + " New Log Line(s) - " + userLog.get("users") + " New Users");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			String[] log = dao.selectReportTables(usuarios);
			htmlReport(userLog, log, softwayLog);
			Desktop.getDesktop().open(new File(reportFile));
		} catch (IOException | SQLException e2) {
			e2.printStackTrace();
		} 
	}
	
	/**
	 * @deprecated
	 * @param file
	 * @param nListName
	 * @param elems
	 * @return
	 */
	@SuppressWarnings("unused")
	private ArrayList<String[]> parseFile(String file, String nListName, String[] elems) {
		ArrayList<String[]> r = new ArrayList<>();

		try {
			File fXmlFile = new File(file);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName(nListName);

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String[] ls = new String[elems.length];
					for(int i = 0; i < elems.length; i++){
						ls[i] = eElement.getElementsByTagName(elems[i]).item(0).getTextContent();
					}					
					r.add(ls);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}
		
	public void htmlReport(HashMap<String, Integer> hash, String[] tables, String softwayLog) throws IOException, SQLException{	
		StringBuilder content = new StringBuilder();		
		final String fileReport = ReadProps.FILE.getKey("FILE.CLIENT.PATH") + ReadProps.FILE.getKey("FILE.REPORT.TEMPLATE");
				
		Path baseFile = Paths.get(fileReport);
		try(Scanner scanner = new Scanner(baseFile, StandardCharsets.ISO_8859_1.name())){
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				if (line.equals("${tb0}")) {
					content.append(tables[0]);
				} else if (line.equals("${tb1}")) {
					content.append(tables[1]);
				} else if (line.equals("${tb2}")) {
					content.append(tables[2]);
				} else if (line.equals("${tb3}")) {
					content.append(softwayLog);
				} else if (line.equals("${info}")) {
					content.append(getInfo(hash));
				} else{
					content.append(line);	
				}
				
			}
		}
		
		Path report = Paths.get(ReadProps.FILE.getKey("FILE.CLIENT.PATH") + ReadProps.FILE.getKey("FILE.REPORT"));
		
		try(BufferedWriter writer = Files.newBufferedWriter(report, StandardCharsets.ISO_8859_1)){
			writer.write(content.toString());
		}
		
	}

	private String getInfo(HashMap<String, Integer> hash) {
		String resp = ReadProps.FILE.getKey("IFS.USERS") + " licenses acquired<br />"; 
		resp += hash.get("users") + " users recorded<br />";
		resp += hash.get("activeUsers") + " active users <br />";
		resp += hash.get("inactiveUsers") + " inactive users <br />";
		return resp;
	}
}
