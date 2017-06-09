package com.csp.galanga.cmd.client;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.trolley.Chart;
import com.csp.galanga.trolley.ChartIFS;

public class ShowOrgChart implements Command {

	@Override
	public void doIt() {
		//Chart c = new ChartAD();
		Chart c = new ChartIFS();
		//Chart c = new ChartExcel();
		c.readAndDisplay();
	}

}
