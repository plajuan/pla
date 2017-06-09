package com.csp.galanga.trolley;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import com.csp.galanga.dto.Employee;

public class ChartAD extends Chart{

	@Override
	public void readAndDisplay() {
		ArrayList<Employee> users = new ArrayList<Employee>();
		StringBuilder content = new StringBuilder();
		try(Scanner scan = new Scanner(Paths.get("c:/GalangaData/OrganizationalChart/Users.txt"), StandardCharsets.UTF_8.name())){
			String name = "";
			String manager = "";
			
			while (scan.hasNextLine()){
				String line = scan.nextLine();

				if (line.indexOf("CN=") != -1){					
					line = line.substring(1);					
					name = line.substring(0, line.indexOf("\""));
					line = line.split("CN=")[1];
					manager = line.substring(0, line.indexOf(","));
					users.add(new Employee(name, manager, "", "", ""));
				} else if (line.indexOf("CEO") != -1){
					line = line.substring(1);
					name = line.substring(0, line.indexOf("\""));
					users.add(new Employee(name, manager, "", "", ""));
				}
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try(Scanner scan = new Scanner(Paths.get("c:/GalangaData/OrganizationalChart/chart.jsp"), StandardCharsets.UTF_8.name())){
			while(scan.hasNextLine()){
				String line = scan.nextLine();
				if (line.equals("${chart_data}")){					
					line = getChartData(users);
				} 
				content.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Path baseFile = Paths.get("c:/GalangaData/OrganizationalChart/chartTest.html");
		try(BufferedWriter writer = Files.newBufferedWriter(baseFile, StandardCharsets.UTF_8)){
			writer.write(content.toString());
			Desktop.getDesktop().open(new File(baseFile.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
