package com.csp.galanga.cmd.server;

import java.sql.SQLException;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.dao.EmployeeDAO;
import com.csp.galanga.util.FileCabinet;
import com.csp.galanga.util.ReadProps;

public class SelectOrgChart implements Command{

	@Override
	public void doIt() {
		EmployeeDAO dao = new EmployeeDAO();
		try {
			FileCabinet cabinet = new FileCabinet();
			cabinet.keep(ReadProps.FILE.getKey("FILE.SERVER.PATH") + ReadProps.FILE.getKey("FILE.EMPLOYEE"), dao.selectAll());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
