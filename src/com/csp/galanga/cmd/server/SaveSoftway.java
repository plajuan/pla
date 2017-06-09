package com.csp.galanga.cmd.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.util.QuillPen;
import com.csp.galanga.util.ReadProps;
import com.csp.galanga.util.Warehouse;

public class SaveSoftway implements Command {

	@Override
	public void doIt() {
		String sql = "select owner, table_name from all_tables "
				+ "WHERE OWNER NOT IN ('SYS', 'SYSTEM', 'CTXSYS', 'XDB', 'EXFSYS', 'MDSYS', 'OLAPSYS', 'APEX_030200') "
				+ "AND table_name NOT IN ('LOG_IMPORTACAO')";
		
		
		sql = "select owner, table_name from all_tables "
				+ "WHERE OWNER NOT IN ('SYS', 'SYSTEM', 'CTXSYS', 'XDB', 'EXFSYS', 'MDSYS', 'OLAPSYS', 'APEX_030200') "
				+ "AND UPPER(table_name) like '%PRODUTO%'";
		
		String other_sql = "select * from ";
		ArrayList<String> tables = new ArrayList<>();
		final String path = ReadProps.FILE.getKey("FILE.SERVER.PATH") + "softway\\";

		Warehouse w = new Warehouse();
		try (Connection c = w.connectSoftway()) {

			try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
				while (rs.next()) {
					String tmp = other_sql + rs.getString(1) + "." + rs.getString(2);
					tables.add(tmp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(">> Lista: " + tables.size());

			final QuillPen pen = new QuillPen();
			int numberOfTables = 0;
			for (String s : tables) {
				pen.saveStringNIO(s, "Q:\\temp\\softway\\log.txt");
				try (Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(s)) {
					ResultSetMetaData metaData = rs.getMetaData();
					int column_count = metaData.getColumnCount();
					final String table_name = s.split("from")[1].trim().replace('.', '_');
					final ArrayList<String[]> table = new ArrayList<>();
					String[] columns = new String[column_count];

					for (int i = 0; i < column_count; i++) {
						columns[i] = metaData.getColumnName(i + 1);
					}
					table.add(columns);

					boolean shallSave = false;
					while (rs.next()) {
						shallSave = true;
						numberOfTables++;
						columns = new String[column_count];
						for (int i = 0; i < column_count; i++) {
							String ss = rs.getString(i + 1);
							if (ss == null) {
								ss = "";
							}
							columns[i] = ss;
						}
						table.add(columns);
					}

					if (shallSave) {
						pen.saveStringNIO(pen.writeHTML(table), path + table_name + ".htm");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Tables saved: " + numberOfTables);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
