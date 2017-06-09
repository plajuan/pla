package com.csp.galanga.trolley;

import java.util.ArrayList;

import com.csp.galanga.dto.Employee;

public abstract class Chart {

	public abstract void readAndDisplay();
	
	protected String getChartData(ArrayList<Employee> users) {
		StringBuilder sb = new StringBuilder();
		for (Employee ee: users){
			String toAppend = "[{v:'name', f:'name<div style=\"color:red; font-style:italic\">job</div>'}, 'manager', 'job'],";
			toAppend = toAppend.replaceAll("name", ee.getName());
			toAppend = toAppend.replaceAll("job", ee.getJob());
			toAppend = toAppend.replaceAll("manager", ee.getManager());
			sb.append(toAppend);
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		return sb.toString();
	}
		
}
