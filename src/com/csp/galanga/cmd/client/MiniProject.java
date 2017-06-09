package com.csp.galanga.cmd.client;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.util.QuillPen;

public class MiniProject implements Command{

	@Override
	public void doIt() {
		final String dir = "c:/GalangaData/MiniProject";
		JFileChooser chooser = new JFileChooser(new File(dir));
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    
	    if (JFileChooser.ERROR_OPTION != returnVal) {
	    	File miniFile = chooser.getSelectedFile();
		    try {
		    	File template = new File(dir+"/template.htm");
				String content = getFileContent(template, miniFile);
				QuillPen q = new QuillPen();
				q.saveFile(content, dir+"/progress.htm");
				Desktop.getDesktop().open(new File(dir+"/progress.htm"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
	    }
	    
	}

	private String getFileContent(File template, File miniFile) throws IOException {
		final String dcTag = "#{dc}";
		final String nomeTag = "#{nome}";
		
		StringBuffer resp = new StringBuffer();		
		try(BufferedReader reader = new BufferedReader(new FileReader(template));){
			String s = reader.readLine();
			while (s != null){			
				resp.append(s);
				s = reader.readLine();
			}	
		}
		
		ArrayList<String> eventos = new ArrayList<String>();
		try(BufferedReader reader = new BufferedReader(new FileReader(miniFile))){
			String s = reader.readLine();
			while (s != null){
				eventos.add(s);
				s = reader.readLine();
			}	
		}
		
		StringBuffer f = new StringBuffer("dc('id1', [");
		int size = eventos.size();
		
		String[] quebraLinha = null;
		String dataHora = null;
		String[] dayMonthYear = null;
		String[] hourMinute = null;
		String month = null;
		for (int i = 0; i < size; i++){
			int monthInt = 0;
			month = Integer.toString(--monthInt);
			String atual = eventos.get(i);
			String prox = "";
			String virgula = "";
			String proxData = "new Date()";
			if (i+1 < size){
				prox = eventos.get(i + 1);
				virgula = ",";
				quebraLinha = prox.split("#");				
				dataHora = quebraLinha[0];				
				dayMonthYear = dataHora.split(" ")[0].split("/");
				hourMinute = dataHora.split(" ")[1].split(":");
				month = dayMonthYear[1];
				monthInt = Integer.parseInt(month);
				month = Integer.toString(--monthInt);
				proxData = String.format("new Date(%s,%s,%s,%s,%s,0)", dayMonthYear[2], month, dayMonthYear[0], hourMinute[0], hourMinute[1]); 
			}
			
			quebraLinha = atual.split("#");
			dataHora = quebraLinha[0];
			dayMonthYear = dataHora.split(" ")[0].split("/");
			month = dayMonthYear[1];
			monthInt = Integer.parseInt(month);
			month = Integer.toString(--monthInt);
			hourMinute = dataHora.split(" ")[1].split(":");
			
			String line = String.format("['%s','%s', new Date(%s,%s,%s,%s,%s,0), %s ]%s", 
					Integer.toString(i+1), 
					quebraLinha[1], 
					dayMonthYear[2], 
					month, 
					dayMonthYear[0], 
					hourMinute[0],
					hourMinute[1],
					proxData, 
					virgula);
			f.append(line);
		}	
		f.append("]);");
		int begin = resp.indexOf(dcTag);
		int end = begin + dcTag.length();
		resp.replace(begin, end, f.toString());
		
		begin = resp.indexOf(nomeTag);
		end = begin + nomeTag.length();
		resp.replace(begin, end, miniFile.getName());
		
		return resp.toString();
	}

}
