package com.csp.galanga.cmd.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.util.Warehouse;

public class DbWalker implements Command{

	@Override
	public void doIt() {
		Warehouse w = new Warehouse();
		ArrayList<String> tableList = new ArrayList<>();
		try(Connection c = w.connectIFS();) {
			System.out.println("execute query");
			
			try(ResultSet tables = c.getMetaData().getTables(null, null, "%", null)){
				while(tables.next()){
					String schema = tables.getString(2);
					String tName = tables.getString(3);
					if (schema.equalsIgnoreCase("ifscsp")){
						tableList.add(tName);
					}
				}
			}
			
			for(String table: tableList){
				try(Connection c1 = w.connectIFS(); Statement stmt = c1.createStatement(); 
						ResultSet rs = stmt.executeQuery("select * from ifscsp." + table);
						){
					ResultSetMetaData metaData = rs.getMetaData();
					if (rs.next()){
						if (metaData.getColumnType(1) == Types.INTEGER || metaData.getColumnType(1) == Types.VARCHAR){
							System.out.println(":: " + table + " : " + rs.getString(1));	
						}
					}
				} catch (SQLException e1){
					System.out.println(":> " + e1.getErrorCode() + " " + e1.getMessage());
				}
			}
		} catch (SQLException e) {
			System.out.println(":> " + e.getErrorCode() + " " + e.getMessage());
			//e.printStackTrace();
		}
	}
	
}
