package com.csp.galanga.cmd.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.csp.galanga.cmd.Command;

public class ShowRunningProcesses implements Command{

	@Override
	public void doIt() {
		try {
	        String line;
	        Process p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
	        BufferedReader input =
	                new BufferedReader(new InputStreamReader(p.getInputStream()));
	        while ((line = input.readLine()) != null) {
	            System.out.println(line);
	        }	        
	        input.close();
	    } catch (Exception err) {
	        err.printStackTrace();
	    }
	}

}
