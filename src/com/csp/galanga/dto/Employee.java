package com.csp.galanga.dto;

import java.io.Serializable;

public class Employee implements Serializable{
	
	private static final long serialVersionUID = 7840379642377354632L;
	private String name;
	private String manager;
	private String job;
	private String id;
	
	public Employee(String name, String manager, String job, String id, String active) {
		this.name = name;
		this.manager = manager == null ? "" : manager;
		this.job = job;
		if (id == null || active.equals("FALSE")){
			this.id = "";
		} else {
			this.id = id;
		}
	}

	public String getName() {
		return name;
	}

	public String getManager() {
		return manager;
	}

	public String getJob() {
		return job;
	}

	public String getId() {
		return id;
	}
	
	
}
