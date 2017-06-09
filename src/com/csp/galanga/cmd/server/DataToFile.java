package com.csp.galanga.cmd.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.util.Warehouse;

public class DataToFile implements Command{
	
	public DataToFile() {
		
	}
	
	@Override
	public void doIt() {	
		ArrayList<Integer> listaIDs;
		HashMap<Integer, ArrayList<Double>> notasFiscais = new HashMap<>();
		ArrayList<String> sqlList = new ArrayList<>();
		Warehouse w = new Warehouse();
		try(Connection sysDB = w.connectCspSys();){
			listaIDs = getAllNotasFiscais(sysDB);
			
			try(Connection ifsDB = w.connectIFS();){
				for(Integer id: listaIDs){
					notasFiscais.put(id, getAllNotaFiscalItems(ifsDB, id));
				}	
			}
			
			ArrayList<Double> nota = null;
			for(Integer key: listaIDs){
				nota = notasFiscais.get(key);
				if(nota.size() == 1){
					sqlList.add("update contabilizacao set num_linha = 1 where lancamento_id in (select id from lancamento where nota_fiscal_id = 0)".replaceAll("0", key.toString()));
				} else {
					//FIXME
					
				}
			}
			
			for(String sql: sqlList){
				//FIXME
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	private ArrayList<Double> getAllNotaFiscalItems(Connection c, Integer id) throws SQLException{
		String sql = "select t.item_number, t.accounting_value"
				+ " from ifscsp.fiscal_note_item t where t.fiscal_note_id = ? order by t.item_number";
		ArrayList<Double> lista = new ArrayList<>();
		try(PreparedStatement stmt = c.prepareStatement(sql);){
			stmt.setInt(1, id);
			try(ResultSet rs = stmt.executeQuery();){
				while(rs.next()){
					lista.add(rs.getDouble(2));
				}	
			}
		}
		return lista;
	}

	
	private ArrayList<Integer> getAllNotasFiscais(Connection c) throws SQLException {
		String sql = "select distinct id from nota_fiscal";
		ArrayList<Integer> resp = new ArrayList<>();
		try(Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
			while(rs.next()){
				resp.add(rs.getInt(1));
			}
		}
		return resp;
	}
	

}
