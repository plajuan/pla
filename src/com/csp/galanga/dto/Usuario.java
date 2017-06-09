package com.csp.galanga.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Usuario implements Serializable {
	private static final long serialVersionUID = -7289650512667998594L;

	public String id = "";
	public HashMap<String, Integer> acoes = new HashMap<String, Integer>();
	public String name;
	public boolean active;
	public ArrayList<String[]> ifsLog = new ArrayList<String[]>();
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ID: " + id + " \n");
		sb.append("Name: " + name + " \n");
		sb.append("active: " + active + " \n");
		return sb.toString();
	}
}
