package com.csp.galanga.cmd;

public interface Command {
	public final String SHOW_ACCESS_REPORT = "Show Access Report";
	public final String SHOW_ORG_CHART = "Show Organizational Chart";
	public final String SHOW_RUNNING_PROCESSES = "Show Running Processes";
	public final String SELECT_ACCESS_REPORT = "Select Users Activity in IFS";
	public final String MIGRATE = "Migrate ZPE to SAP";
	public final String INSERT_RULES = "Insert Rules";
	public final String MINI_PROJECT = "Mini Project";
	public final String SELECT_ORG_CHART = "Select Organizational Chart";
	public final String PRE_ACCOUNTING_SQL = "Create Pre-accounting SQL";
	public final String UPGRADE_ZPE_DATA = "Upgrade ZPE data";
	public final String SAVE_SOFTWAY = "SAVE SOFTWAY";
	public final String CSP_SYS_MIGRATION = "CSPsys Migration";
	public final String FIND_PIS_COFINS = "Find PIS Cofins";
	public final String READ_EXCEL = "Read Excel";
	public final String DB_WALKER = "DB Walker";
	public void doIt();

}
