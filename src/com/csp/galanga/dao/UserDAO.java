package com.csp.galanga.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import com.csp.galanga.dto.Usuario;
import com.csp.galanga.util.Warehouse;
import com.csp.galanga.util.WebScout;

public class UserDAO {
	Warehouse warehouse = new Warehouse();	
	
	/**
	 * Save log information only if user disconnected ('end' field not equals
	 * 'null')
	 * 
	 * @param logData
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public HashMap<String, Integer> insert(HashMap<String, Usuario> usuarios)
			throws ClassNotFoundException, SQLException {
		HashMap<String, Integer> resp = new HashMap<>();

		int i = 0;
		int j = 0;
		int active = 0;
		int inactive = 0;

		String sqlLog = "INSERT INTO LOG VALUES(?,?,?,?)";
		String sqlInsertUsuario = "insert into usuario(cod,usuario,ativo) values(?,?,?)";
		String sqlUpdateUsuario = "update usuario set ativo=? where cod=?"; //update para true também
		String sqlBuscaUsuario = "select ativo from usuario where cod=?";
		String sqlUsuariosAtivos = "select count(*) from usuario where ativo = 'TRUE' and cod NOT like 'I%'";
		String sqlUsuariosInativos = "select count(*) from usuario where ativo = 'FALSE'";

		try (Connection conn = warehouse.connectPostgre();
				PreparedStatement logStmt = conn.prepareStatement(sqlLog);
				PreparedStatement insereUsuarioStmt = conn.prepareStatement(sqlInsertUsuario);
				PreparedStatement modificaUsuarioStmt = conn.prepareStatement(sqlUpdateUsuario);
				PreparedStatement buscaUsuarioStmt = conn.prepareStatement(sqlBuscaUsuario);
				Statement totalUsuarios = conn.createStatement();) {
			
			ArrayList<String[]> logData;
			for (Usuario usu: usuarios.values()){
				logData = usu.ifsLog;
				
				buscaUsuarioStmt.setString(1, usu.id);
				try (ResultSet usuarioRS = buscaUsuarioStmt.executeQuery()) {
					if (usuarioRS.next()) {
						modificaUsuarioStmt.setString(1, Boolean.toString(usu.active).toUpperCase());
						modificaUsuarioStmt.setString(2, usu.id);						
						modificaUsuarioStmt.executeUpdate();
					} else {
						insereUsuarioStmt.setString(1, usu.id);
						insereUsuarioStmt.setString(2, usu.name);
						insereUsuarioStmt.setString(3, Boolean.toString(usu.active).toUpperCase());
						j += insereUsuarioStmt.executeUpdate();
					}
				}
				
				for(String[] elem: logData){
					if ((elem[3] != null && !elem[3].equalsIgnoreCase("null")) && isNewRecord(conn, elem)) {
						logStmt.setInt(1, Integer.parseInt(elem[0]));
						logStmt.setString(2, elem[1]);
						logStmt.setTimestamp(3, Timestamp.valueOf(elem[2]));
						logStmt.setTimestamp(4, Timestamp.valueOf(elem[3]));
						i += logStmt.executeUpdate();
					}	
				}
				
			}
			
			try (ResultSet ativos = totalUsuarios.executeQuery(sqlUsuariosAtivos);) {
				if (ativos.next()) {
					active = ativos.getInt(1);
				}
			}

			try (ResultSet inativos = totalUsuarios.executeQuery(sqlUsuariosInativos);) {
				if (inativos.next()) {
					inactive = inativos.getInt(1);
				}
			}

		}

		resp.put("logLines", i);
		resp.put("users", j);
		resp.put("activeUsers", active);
		resp.put("inactiveUsers", inactive);
		return resp;
	}
	
	
	public String[] selectReportTables(HashMap<String, Usuario> usuarios) throws SQLException {		
		ArrayList<String> lista = new WebScout().getIfsUsers();			
		
		final String sqlAUNoLog = "select u.cod, u.usuario from usuario u where u.ativo like 'TRUE' and u.cod not in (select distinct l.usuario from log l)";
		final String sqlAULog = "select u.usuario, u.cod, count(l.cod), max(l.ini) from usuario u join log l on u.cod = l.usuario where u.ativo like 'TRUE' group by u.cod, u.usuario order by u.usuario";
		final String sqlIULog = "select u.usuario, u.cod, count(l.cod), max(l.ini) from usuario u join log l on u.cod = l.usuario where u.ativo like 'FALSE' group by u.cod, u.usuario order by u.usuario";

		final StringBuilder listAUNoLog = new StringBuilder();
		final StringBuilder listAULog = new StringBuilder();
		final StringBuilder listIULog = new StringBuilder();
		
		try (Connection conn = warehouse.connectPostgre();
				PreparedStatement stmtAUNoLog = conn.prepareStatement(sqlAUNoLog);
				PreparedStatement stmtAULog = conn.prepareStatement(sqlAULog);
				PreparedStatement stmtIULog = conn.prepareStatement(sqlIULog);) {
			ResultSet rsAUNoLog = stmtAUNoLog.executeQuery();
			ResultSet rsAULog = stmtAULog.executeQuery();
			ResultSet rsIULog = stmtIULog.executeQuery();

			final String trtd = "<tr><td>";
			final String preOpen = "<pre>";
			final String preEnd = "</pre>";
			final String tdtd = "</td><td>";
			final String tdtr = "</td></tr>";
			//String linkClose = "</a>";

			while (rsAUNoLog.next()) {
				String id = rsAUNoLog.getString(1);
				String name = rsAUNoLog.getString(2);
				//String link = String.format("<a href=\"javascript:sr('%s');\">", name.replaceAll(" ", "+"));
				
				String line = "";				
				if (lista.contains(id)){
					line = trtd + preOpen + id + preEnd + tdtd + name + tdtr;
				} else {
					line = trtd + id + tdtd + name + tdtr;
				}
				listAUNoLog.append(line);
			}
			
			while (rsAULog.next()) {
				String name = rsAULog.getString(1);
				String id = rsAULog.getString(2);
				Usuario usuario = usuarios.get(id);
				
				if (usuario == null){
					usuario = new Usuario();
				}				
				
				int requisicoes = usuario.acoes.get("Requisições") != null ? usuario.acoes.get("Requisições") :  0;
				int aprovReq = usuario.acoes.get("Aprovação Requisição") != null ? usuario.acoes.get("Aprovação Requisição") :  0;
				int reembolso = usuario.acoes.get("Reembolso") != null ? usuario.acoes.get("Reembolso") :  0;
				int aprovReemb = usuario.acoes.get("Aprovação Reembolso") != null ? usuario.acoes.get("Aprovação Reembolso") :  0;
				int contratos = usuario.acoes.get("Contratos") != null ? usuario.acoes.get("Contratos") :  0;
				int medicoes = usuario.acoes.get("Medições") != null ? usuario.acoes.get("Medições") :  0;
				int aprovMed = usuario.acoes.get("aprovações medições e contratos") != null ? usuario.acoes.get("aprovações medições e contratos") :  0;
				//String link = String.format("<a href=\"javascript:sr('%s');\">", name.replaceAll(" ", "+"));
				String line = "";
				if (lista.contains(id)){
					line = trtd + preOpen + id + preEnd + tdtd + name + tdtd
							+ rsAULog.getString(3) + tdtd + rsAULog.getString(4) + 
							"</td><td title='Requisições'>" + requisicoes + 
							"</td><td title='Aprovação Requisição'>" + aprovReq + 
							"</td><td title='Reembolso'>" + reembolso + 
							"</td><td title='Aprovação Reembolso'>" + aprovReemb + 
							"</td><td title='Contratos'>" + contratos + 
							"</td><td title='Medições'>" + medicoes + 
							"</td><td title='Aprovações contratos'>" + aprovMed + tdtr;
				} else {
					line = trtd + id + tdtd + name + tdtd
							+ rsAULog.getString(3) + tdtd + rsAULog.getString(4) + 
							"</td><td title='Requisições'>" + requisicoes + 
							"</td><td title='Aprovação Requisição'>" + aprovReq + 
							"</td><td title='Reembolso'>" + reembolso + 
							"</td><td title='Aprovação Reembolso'>" + aprovReemb + 
							"</td><td title='Contratos'>" + contratos + 
							"</td><td title='Medições'>" + medicoes + 
							"</td><td title='Aprovações contratos'>" + aprovMed + tdtr;
				}
				listAULog.append(line);
			}

			while (rsIULog.next()) {
				String name = rsIULog.getString(1);
				//String link = String.format("<a href=\"javascript:sr('%s');\">", name.replaceAll(" ", "+"));;
				listIULog.append(trtd + rsIULog.getString(2) + tdtd + name + tdtd
						+ rsIULog.getString(3) + tdtd + rsIULog.getString(4) + tdtr);
			}

		}

		return new String[] { listAUNoLog.toString(), listAULog.toString(), listIULog.toString() };
	}

	/**
	 * Checks if a log line already exists on database
	 * 
	 * @param c
	 * @param dados
	 * @return
	 * @throws SQLException
	 */
	private boolean isNewRecord(Connection c, String[] dados) throws SQLException {
		String sql = "select count(*) from log where cod = ? and usuario = ? and ini = ? and fim = ?";
		boolean r = true;
		try (PreparedStatement stmt = c.prepareStatement(sql);) {
			stmt.setInt(1, Integer.parseInt(dados[0]));
			stmt.setString(2, dados[1]);
			stmt.setTimestamp(3, Timestamp.valueOf(dados[2]));
			stmt.setTimestamp(4, Timestamp.valueOf(dados[3]));

			try (ResultSet rs = stmt.executeQuery();) {
				rs.next();
				if (rs.getInt(1) != 0) {
					r = false;
				}
			}
		}

		return r;
	}

}
