package com.csp.galanga.cmd;

import com.csp.galanga.cmd.client.*;
import com.csp.galanga.cmd.server.*;


public class CommandFactory {

	/**
	 * Return the corresponding command by the given string
	 * @exception Command cannot be null nor empty
	 * @exception Command must match a valid command constant	
	 */
	public static void executeCommand(String s){
		Command cmd = null;
				
		if (s == null || s.isEmpty()){
			throw new IllegalArgumentException("Command must have a value!");
		} else if (s.equals(Command.SELECT_ACCESS_REPORT)){
			cmd = new AccessReport();	
		} else if (s.equals(Command.SHOW_ACCESS_REPORT)){
			cmd = new ShowAccessReport();
		} else if (s.equals(Command.SHOW_ORG_CHART)){
			cmd = new ShowOrgChart();
		} else if (s.equals(Command.SHOW_RUNNING_PROCESSES)){
			cmd = new ShowRunningProcesses();		
		} else if (s.equals(Command.MIGRATE)){
			cmd = new DataMigration(); 
		} else if (s.equals(Command.INSERT_RULES)){
			cmd = new InsertRules();
		} else if (s.equals(Command.MINI_PROJECT)){
			cmd = new MiniProject();
		} else if (s.equals(Command.SELECT_ORG_CHART)){
			cmd = new SelectOrgChart();
		} else if (s.equals(Command.PRE_ACCOUNTING_SQL)){
			cmd = new ChangePreAccounting();
		} else if(s.equals(Command.UPGRADE_ZPE_DATA)){
			cmd = new DataToFile();
		} else if(s.equals(Command.SAVE_SOFTWAY)){
			cmd = new SaveSoftway();
		} else if(s.equals(Command.CSP_SYS_MIGRATION)){
			cmd = new CspSysMigration();
		} else if(s.equals(Command.FIND_PIS_COFINS)){
			cmd = new FindPisCofins();
		} else if(s.equals(Command.READ_EXCEL)){
			cmd = new ReadExcel();
		} else if(s.equals(Command.DB_WALKER)){
			cmd = new DbWalker();
		}
		
		cmd.doIt();
	}

}
