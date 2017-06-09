package com.csp.galanga.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

import com.csp.galanga.dto.SoftwayLog;
import com.csp.galanga.util.Warehouse;

public class SoftwayLogDAO{
	public String select() throws SQLException{
		
		String sqlUsers = "SELECT a.username, a.nome, a.departamento, a.last_login, B.DESC_ALTERACAO " + 
				"FROM SFWCSP.SFW_USUARIO a " + 
				"JOIN SFWCSP.SFW_USUARIO_LOG_ALTERACAO B ON A.USERNAME = B.USERNAME " + 
				"where B.MOMENTO > TO_DATE('01/01/2014','dd/mm/yyyy') " + 
				"AND a.ativo = 'S' " + 
				"and upper(a.username) not like 'ADM%' " +
				"order by a.username";
		
		StringBuilder text = new StringBuilder();
		String trtd = "<tr><td>";
		String tdtd = "</td><td>";
		String tdtr = "</td></tr>";
		
		HashMap<String, SoftwayLog> data = new HashMap<String, SoftwayLog>(); 
		Warehouse w = new Warehouse();
		
		try(Connection c = w.connectSoftway(); PreparedStatement ps = c.prepareStatement(sqlUsers); ResultSet rs = ps.executeQuery()){
			while (rs.next()){
				String key = rs.getString(1).replaceAll("[&<>'\"]", "");
				SoftwayLog value = data.get(key);
				if (value == null){
					String depto = rs.getString(3);
					if (depto == null){
						depto = "blank";
					} else {
						depto = depto.replaceAll("[&<>'\"]", "");
					}
					value = new SoftwayLog(key, 
							rs.getString(2).replaceAll("[&<>'\"]", ""),
							depto,
							rs.getString(4),
							rs.getString(5));
					data.put(key, value);
				} else {
					value.updateTime(rs.getString(5));
				}
			}
			
		}
		
		Set<String> keys = data.keySet();
		for(String k: keys){
			text.append(trtd + data.get(k).toString().replaceAll("::", tdtd) + tdtr);
		}
		return text.toString();
	}
	
}
