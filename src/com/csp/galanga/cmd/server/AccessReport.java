package com.csp.galanga.cmd.server;

import java.util.HashMap;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.dao.AccessReportDAO;
import com.csp.galanga.dao.SoftwayLogDAO;
import com.csp.galanga.dto.Usuario;
import com.csp.galanga.util.FileCabinet;
import com.csp.galanga.util.ReadProps;

public class AccessReport implements Command {

	@Override
	public void doIt() {		
		FileCabinet cabinet = new FileCabinet();
		try {
			HashMap<String, Usuario> usersList = new AccessReportDAO().selectUsuarios(); 
			String softway = new SoftwayLogDAO().select(); 
			
			cabinet.keep(ReadProps.FILE.getKey("FILE.SERVER.PATH") + ReadProps.FILE.getKey("FILE.USERS"), usersList);
			cabinet.keep(ReadProps.FILE.getKey("FILE.SERVER.PATH") + ReadProps.FILE.getKey("FILE.SOFTWAY"), softway);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Access Report");
	}

}
