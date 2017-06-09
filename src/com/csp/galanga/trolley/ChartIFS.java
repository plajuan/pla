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
import com.csp.galanga.util.FileCabinet;
import com.csp.galanga.util.ReadProps;

public class ChartIFS extends Chart{

	@Override
	public void readAndDisplay() {
		ArrayList<Employee> employees = new ArrayList<Employee>();
		StringBuilder content = new StringBuilder();
		@SuppressWarnings("unchecked")
		ArrayList<Employee> result = (ArrayList<Employee>) new FileCabinet().read(ReadProps.FILE.getKey("FILE.CLIENT.PATH") + ReadProps.FILE.getKey("FILE.EMPLOYEE"));
		
		for(Employee line: result){
			employees.add(line);
		}
				
		try(Scanner scan = new Scanner(Paths.get("c:/GalangaData/OrganizationalChart/chart.jsp"), StandardCharsets.UTF_8.name())){
			while(scan.hasNextLine()){
				String line = scan.nextLine();
				if (line.equals("${chart_data}")){					
					line = getChartData(employees);
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
