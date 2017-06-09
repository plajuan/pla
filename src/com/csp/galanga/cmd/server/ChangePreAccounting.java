package com.csp.galanga.cmd.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.util.QuillPen;
import com.csp.galanga.util.ReadProps;
import com.csp.galanga.util.Warehouse;

public class ChangePreAccounting implements Command {

	@Override
	public void doIt() {
		final String dir = ReadProps.FILE.getKey(ReadProps.FILE.getKey("ENVIRONMENT") + ".SQL.FILE.DIR");
		JFileChooser chooser = new JFileChooser(new File(dir));
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    
	    if (JFileChooser.ERROR_OPTION != returnVal){
	    	File sqlFile = chooser.getSelectedFile();		    
		    String sql = getSQLString(sqlFile);
		    QuillPen pen = new QuillPen();
		    try {
				pen.saveFile(sql, dir + "/pre_accounting.sql", "UTF-8");
				System.out.println("arquivo pre_accounting.sql gravado");
				//pen.saveStringNIO(sql);
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				System.out.println(e.toString());
				e.printStackTrace();
			}
	    }
		
	}
	
	private String getSQLString(File sqlFile) {
		StringBuffer res = new StringBuffer();
		try(BufferedReader reader = new BufferedReader(new FileReader(sqlFile))){
			String s = reader.readLine();
			Warehouse w = new Warehouse();
			while(s != null){
				try {
					Integer i = Integer.valueOf(s);
					String checkSQL = "select t.pre_accounting_id from ifscsp.PURCHASE_REQ_LINE_PART t " + 
							"where t.requisition_no = ? " + 
							"and t.objstate = 'Planned'";
					
					try(Connection c = w.connectIFS(); PreparedStatement ps = c.prepareStatement(checkSQL);){
						ps.setString(1, i.toString());
						try(ResultSet rs = ps.executeQuery()){
							while(rs.next()){
								res.append("update ifscsp.pre_accounting_tab t set t.codeno_h = 'PRE OPEX' where t.pre_accounting_id = " + rs.getString(1) + ";\n");
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (NumberFormatException e) {
					
				}
				
				s = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		} 
		return res.toString();
	}

}
