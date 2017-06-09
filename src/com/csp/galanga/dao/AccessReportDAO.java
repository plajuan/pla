package com.csp.galanga.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.csp.galanga.dto.Usuario;
import com.csp.galanga.util.Warehouse;

public class AccessReportDAO{

	public HashMap<String,Usuario> selectUsuarios() throws SQLException {
		String sqlUsers = "select U.IDENTITY, U.DESCRIPTION, U.ACTIVE from IFSCSP.FND_USER U order by U.ACTIVE, U.DESCRIPTION";
		String sqlLog = "select a.log_id, a.identity usuario,a.date_created dt_entrada, "
				+ "a.date_finished dt_saida from IFSCSP.server_log_tab a "
				+ "where a.identity like 'CSP%' " 
				+ "and sysdate-a.date_created <= 31 "
				+ "order by a.identity ";
		
		String sqlFuncionario = "select PI.user_id, " + 
				"A.emp_no, A.pos_code, A.valid_from, A.valid_to, " + 
				"B.person_id, B.employee_name, " + 
				"C.position_title, C.sup_pos_code " + 
				"from IFSCSP.COMPANY_PERS_ASSIGN A " + 
				"JOIN IFSCSP.COMPANY_PERSON_ALL B ON A.emp_no = B.emp_no " + 
				"JOIN IFSCSP.PERSON_INFO PI ON B.person_id = PI.person_id " + 
				"JOIN IFSCSP.COMPANY_POSITION6 C ON C.pos_code = A.pos_code " + 
				"where B.employee_status like 'ATIVO' " + 
				"and b.person_id in ( " + 
				"  select aa.person_id from ifscsp.person_info aa " + 
				"  join ifscsp.fnd_user bb on aa.user_id = bb.identity " + 
				"  where bb.active like 'TRUE' " + 
				") " + 
				"and c.position_title not like '%EXPATRIATE%%' " + 
				"and sysdate between c.struct_valid_from and c.struct_valid_to " + 
				"order by b.employee_name";
		
		String sqlAcoes = "select * from ( "
				+ "select 'Requisições' action, b.user_id usuario, count(*) total from IFSCSP.purchase_requisition a "
				+ "join IFSCSP.person_info b on a.requisitioner_code = b.person_id "
				+ "where a.requisition_date > to_date('&dt','dd/mm/yyyy') " + "group by b.user_id " + "union "
				+ "select 'Aprovação Requisição' action, b.signature_id, count(*) from IFSCSP.purch_req_line_approval  b "
				+ "where b.date_approved > to_date('&dt','dd/mm/yyyy') " + "and b.signature_id is not null "
				+ "group by b.signature_id " + "union "
				+ "select 'Reembolso' action, d.user_id, count(*) from IFSCSP.expense_header c "
				+ "join IFSCSP.person_info d on  c.confirmed_by = d.person_id "
				+ "where c.start_stamp > to_date('&dt','dd/mm/yyyy') " + "and c.confirmed_by is not null "
				+ "group by d.user_id " + "union "
				+ "select 'Aprovação Reembolso' action, d.user_id, count(*) from IFSCSP.expense_header c "
				+ "join IFSCSP.person_info d on  c.authorized_by = d.person_id "
				+ "where c.start_stamp > to_date('&dt','dd/mm/yyyy') " + "and c.confirmed_by is not null "
				+ "group by d.user_id " + "union "
				+ "select 'Contratos' action, b.user_id, count(*) from IFSCSP.sub_contract a "
				+ "join IFSCSP.person_info b on a.sub_contract_manager = b.person_id " + "where a.objstate = 'Active' "
				+ "and b.user_id is not null " + "group by b.user_id " + "union "
				+ "select 'Medições' action, a.created_by, count(*) from IFSCSP.SUB_CON_AFP_VALUATION a "
				+ "where a.dt_cre > to_date('&dt','dd/mm/yyyy') " + "and a.objstate != 'Cancelled' "
				+ "group by a.created_by " + "union "
				+ "select 'aprovações medições e contratos' action, b.user_id, count(*) from IFSCSP.approval_routing a "
				+ "join IFSCSP.person_info b on a.app_sign = b.person_id "
				+ "where a.app_date > to_date('31/12/2013','dd/mm/yyyy') " + "and a.approval_status = 'Aprovado' "
				+ "group by b.user_id " + ") aa order by aa.usuario";
		String strDate = "31/12/" + (Calendar.getInstance().get(Calendar.YEAR) - 1);
		sqlAcoes = sqlAcoes.replaceAll("&dt", strDate);
		HashMap<String, Usuario> usuarios = new HashMap<String, Usuario>();
		
		Warehouse w = new Warehouse();		
		try(Connection c = w.connectIFS();){
			
			try(PreparedStatement ps = c.prepareStatement(sqlUsers); ResultSet rs = ps.executeQuery();){
				while (rs.next()){
					Usuario u = new Usuario();
					u.id = rs.getString(1);
					u.name = rs.getString(2).replaceAll("[&<>'\"]", "");
					u.active = rs.getString(3).equalsIgnoreCase("false") ? false : true;
					usuarios.put(u.id, u);
				}
			}
			
			try(PreparedStatement ps = c.prepareStatement(sqlFuncionario); ResultSet rs = ps.executeQuery();){
				ArrayList<String[]> lista = new ArrayList<String[]>();
				SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy"); 
				while (rs.next()){
					String userId = rs.getString(1);
					String empNo = rs.getString(2);
					String posCode = rs.getString(3);
					Date validFrom = rs.getDate(4);
					Date validTo = rs.getDate(5);
					String personId = rs.getString(6);
					String employeeName = rs.getString(7);
					String positionTitle = rs.getString(8);
					String supPosCode = rs.getString(9);
					
					lista.add(new String[]{userId,
							empNo,
							posCode,
							format.format(validFrom),
							format.format(validTo),
							personId,
							employeeName,
							positionTitle,
							supPosCode
							});
				}
				
				//String s = new QuillPen().writeHTML(lista); 
				/*try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(ReadProps.FILE.getKey("FILE.SERVER.PATH") +"employees.htm"), 
						StandardCharsets.ISO_8859_1)){
					writer.write(s);
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				//FileCabinet cabinet = new FileCabinet();
				//cabinet.keep(ReadProps.FILE.getKey("FILE.SERVER.PATH")+"employees.dat", lista);
			}
			
			try(PreparedStatement ps = c.prepareStatement(sqlLog); ResultSet rs = ps.executeQuery();){
				String key = null;
				Usuario u = null;
				while(rs.next()){
					String newKey = rs.getString(2); 
					if (key == null || !key.equals(newKey)){
						key = newKey;
						u = usuarios.get(key); 
					}
					if (u != null){
						String[] log = {rs.getString(1), key, rs.getString(3), rs.getString(4)};
						u.ifsLog.add(log);	
					}
				}
			}

			try(PreparedStatement ps = c.prepareStatement(sqlAcoes); ResultSet rs = ps.executeQuery();){
				String key = null;
				Usuario u = null;
				while(rs.next()){
					String newKey = rs.getString(2);
					if (key == null || !key.equals(newKey)){
						key = newKey;
						u = usuarios.get(key);
					}
					if (u != null){
						u.acoes.put(rs.getString(1), rs.getInt(3));	
					}
				}
			}
		}
		
		return usuarios;
	}

}
