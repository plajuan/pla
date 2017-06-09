package com.csp.galanga.cmd.server;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.util.QuillPen;
import com.csp.galanga.util.Warehouse;

public class InsertRules implements Command{
	
	/*
	 delete from pur_approval_templ_tab
	 where description 'values'
	 or template_id 'ativa' 
	 */
	
	private HashMap<String, ArrayList<String[]>> values;
	private HashSet<String> regrasMedicao;
	
	@Override
	public void doIt() {
		boolean templates = true;
		boolean createRegrasAtivacao = true;
		boolean createAprovadores = true;
		boolean createRulesAtivar = true;
		
		Warehouse w = new Warehouse();
		try(Connection c = w.connectIFS()){
			if (createAprovadores){
				criarAprovadores(c);
			}
			
			if (templates){
				values = getTemplates(c);
			}
			
			if (createRegrasAtivacao){
				regrasMedicao = doCreateRegrasAprovacao(c);
			}
			
			if (createRulesAtivar){
				doCreateRulesAtivar(c);
			}
			
		} catch (SQLException e) {
			System.out.println("Erro método doIt()");
			e.printStackTrace();
		}
		
	}
	

	private void doCreateRulesAtivar(Connection c) throws SQLException {
		String[][] valoresMedicao = {
				{"0", "245100"}, 
				{"245100.01", "1225500"},
				{"1225500.01", "2451000"},
				{"2451000.01","19608000"}
		};
		String[][] valoresAtivacao = {
				{"0","245100"},
				{"245100.01","735300"},
				{"735300.01","2451000"},
				{"2451000.01","19608000"}
		};
		
		String sql = "{call IFSCSP.C_SUB_APP_RULE_API.NEW__(?,?,?,?,?)}";
		try(CallableStatement cs = c.prepareCall(sql);){
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, java.sql.Types.VARCHAR);
			cs.registerOutParameter(3, java.sql.Types.VARCHAR);
			cs.registerOutParameter(4, java.sql.Types.VARCHAR);
			
			String[] line = {
					"APPROVAL_RULE", "", //0,1
					"CONTRACT", "", //2,3
					"TEMP_PRIORITY", "", //4,5
					"VENDOR_NO", "%", //6,7
					"SUPP_GRP", "%", //8,9
					"MIN_AMOUNT", "0", //10,11
					"MAX_AMOUNT", "0", //12,13
					"AUTHORIZE_CODE", "CONTRATO", //14,15
					"C_SUB_APP_RULE_TYPE", "Ativação de Contrato", //16,17
					"TEMPLATE_ID", "", //18,19
					"ALLOW_CHANGES", "TRUE",
					"USE_AMOUNTS_INCL_TAX_DB", "FALSE"
				};
			
			line[17] = "Ativação de Contrato";
						
			for(int i = 1; i <= 4; i++){
				line[3] = "CSP";
				line[1] = "ATI" + line[3] + i;				
				line[5] = Integer.toString(i);
				line[11] = valoresAtivacao[i-1][0];
				line[13] = valoresAtivacao[i-1][1];
				line[19] = "ATIVA" + i;
				stdPrepareCheckDo(cs, QuillPen.parseValues(line));
				
				line[3] = "FOR";
				line[1] = "ATI" + line[3] + i;				
				line[5] = Integer.toString(i);
				line[11] = valoresAtivacao[i-1][0];
				line[13] = valoresAtivacao[i-1][1];
				//line[19] = line[1];
				stdPrepareCheckDo(cs, QuillPen.parseValues(line));
			}			
			
			line[17] = "Aprovação de Contrato";
			Set<String> keys = values.keySet();
			int i = 0;
			for(String key: keys){
				line[19] = key;
				
				int index = -1;
				if (key.contains("1")){
					index = 0;
				} else if (key.contains("2")){
					index = 1;
				} else if (key.contains("3")){
					index = 2;
				} else if (key.contains("4")){
					index = 3;
				} else if (key.contains("5")){
					ArrayList<String[]> obj = values.get(key);
					if (obj.size() != 5){
						continue;
					} else {
						index = 4;
					}
				}
				line[11] = valoresAtivacao[index][0];
				line[13] = valoresAtivacao[index][1];
				
				i++;
				line[3] = "CSP";
				line[1] = "APR" + line[3] + i;
				line[5] = Integer.toString(i);
				stdPrepareCheckDo(cs, QuillPen.parseValues(line));
				
				i++;
				line[3] = "FOR";
				line[1] = "APR" + line[3] + i;
				line[5] = Integer.toString(i);
				stdPrepareCheckDo(cs, QuillPen.parseValues(line));
			}
			
			i = 0;
			line[17] = "Certificação de Medição";
			if (regrasMedicao == null || regrasMedicao.size() == 0){
				regrasMedicao = getRegrasMedicao(c);
			}
			
			for(String key: regrasMedicao){
				line[19] = key;
				
				int index = -1;
				if (key.contains("1")){
					index = 0;
				} else if (key.contains("2")){
					index = 1;
				} else if (key.contains("3")){
					index = 2;
				} else if (key.contains("4")){
					index = 3;
				} else if (key.contains("5")){
					index = 4;
				}
				line[11] = valoresMedicao[index][0];
				line[13] = valoresMedicao[index][1];
				
				i++;
				line[3] = "CSP";
				line[1] = "CER" + line[3] + i;
				line[5] = Integer.toString(i);
				stdPrepareCheckDo(cs, QuillPen.parseValues(line));
				
				i++;
				line[3] = "FOR";
				line[1] = "CER" + line[3] + i;
				line[5] = Integer.toString(i);
				stdPrepareCheckDo(cs, QuillPen.parseValues(line));
				
			}
			
		} catch (Exception e){
			System.out.println("doCreateRulesAtivar");
		}
		
	}


	private HashSet<String> getRegrasMedicao(Connection c) {
		HashSet<String> resp = new HashSet<String>();;
		String sql = "select TEMPLATE_ID from IFSCSP.PUR_APPROVAL_TEMPL " + 
				"WHERE TEMPLATE_ID LIKE 'M%' " + 
				"AND TEMPLATE_ID NOT LIKE '%.%'";
		try(PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
			while(rs.next()){
				resp.add(rs.getString(1));
			}
		} catch (SQLException e) {
			System.out.println("getRegrasMedicao");
		}
		
		return resp;
	}


	private void stdPrepareCheckDo(CallableStatement cs, String value) throws SQLException {
		cs.setNull(4, java.sql.Types.VARCHAR);
		cs.setString(5, "PREPARE");
		cs.executeUpdate();
		cs.clearParameters();
		cs.setString(4, value);
		cs.setString(5, "CHECK");
		cs.executeUpdate();
		cs.setString(4, value);
		cs.setString(5, "DO");
		cs.executeUpdate();
	}

	private void criarAprovadores(Connection c) {
		String[] aprovadoresContabil = {"CSP_NCCOSTA", "CSP_RQUEIROZ", "CSP_RRJUNIOR", "CSP_SRODRIGUES"};
		String[] aprovadoresFiscal = {"CSP_AFSILVA", "CSP_FSALVES", "CSP_JCSILVA", "CSP_KQUEIROZ", "CSP_NCCOSTA", "CSP_TSGUIMARAES"};
		HashSet<String> aprovadores = new HashSet<String>();
		String sql = "select USERID from IFSCSP.PURCHASE_AUTHORIZER where USERID like ?";
		try(PreparedStatement ps = c.prepareStatement(sql)){
			for(String key: aprovadoresContabil){
				ps.setString(1, key);
				try(ResultSet rs = ps.executeQuery()){
					if (!rs.next()){
						aprovadores.add(key);
					}
				}
			}
			for(String key: aprovadoresFiscal){
				ps.setString(1, key);
				try(ResultSet rs = ps.executeQuery()){
					if (!rs.next()){
						aprovadores.add(key);
					}
				}
			}
		} catch (SQLException e){
			System.out.println("criarAprovadores 1");
		}
		
		if (!aprovadores.isEmpty()){
			System.out.println("===Aprovadores===");
			sql = "{call IFSCSP.PURCHASE_AUTHORIZER_API.NEW__(?,?,?,?,?)}"; //o, o, o, i o, i
			try(CallableStatement cs = c.prepareCall(sql)){
				for (String key: aprovadores){
					System.out.println(key);
					cs.registerOutParameter(1, java.sql.Types.VARCHAR);
					cs.registerOutParameter(2, java.sql.Types.VARCHAR);
					cs.registerOutParameter(3, java.sql.Types.VARCHAR);
					cs.registerOutParameter(4, java.sql.Types.VARCHAR);
					String value = QuillPen.parseValues(new String[]{"AUTHORIZE_ID", key});
					cs.setString(4, value);
					cs.setString(5, "DO");
					cs.executeUpdate();
				}
			} catch (SQLException e){
				System.out.println("criarAprovadores 2");
			}
		}
		
		sql = "{call IFSCSP.PURCH_AUTHORIZE_GROUP_API.NEW__(?,?,?,?,?)}";
		String anaCont = "ANACONT";
		String anaFisc = "ANAFISC";
		
		try(CallableStatement cs = c.prepareCall(sql)){			
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, java.sql.Types.VARCHAR);
			cs.registerOutParameter(3, java.sql.Types.VARCHAR);
			cs.registerOutParameter(4, java.sql.Types.VARCHAR);
			cs.setNull(4, java.sql.Types.VARCHAR);
			cs.setString(5, "PREPARE");
			cs.executeUpdate();
			
			String value = QuillPen.parseValues(new String[]{"AUTHORIZE_GROUP_ID", anaCont, "DESCRIPTION", "ANALISE CONTABIL"});
			cs.setString(4, value);
			cs.setString(5, "CHECK");
			cs.executeUpdate();
			
			cs.setString(4, value);
			cs.setString(5, "DO");
			cs.executeUpdate();
			
			cs.setNull(4, java.sql.Types.VARCHAR);
			cs.setString(5, "PREPARE");
			cs.executeUpdate();
			
			value = QuillPen.parseValues(new String[]{"AUTHORIZE_GROUP_ID", anaFisc, "DESCRIPTION", "ANALISE FISCAL"});
			cs.setString(4, value);
			cs.setString(5, "CHECK");
			cs.executeUpdate();
			
			cs.setString(4, value);
			cs.setString(5, "DO");
			cs.executeUpdate();
		} catch (SQLException e){
			System.out.println("criarAprovadores 3");
		}
		//TODO TIRAR ESSA LINHA
		sql = "{call IFSCSP.PURCH_AUTHORIZE_GROUP_LINE_API.NEW__(?,?,?,?,?)}";
		try(CallableStatement cs = c.prepareCall(sql)){
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, java.sql.Types.VARCHAR);
			cs.registerOutParameter(3, java.sql.Types.VARCHAR);
			cs.registerOutParameter(4, java.sql.Types.VARCHAR);
			
			for(String key: aprovadoresContabil){
				cs.setNull(4, java.sql.Types.VARCHAR);
				cs.setString(5, "PREPARE");
				cs.executeUpdate();
				
				cs.setString(4, QuillPen.parseValues(new String[]{"AUTHORIZE_GROUP_ID", anaCont, "AUTHORIZE_ID", key}));
				cs.setString(5, "DO");
				cs.executeUpdate();	
			}
			
			for(String key: aprovadoresFiscal){
				cs.setNull(4, java.sql.Types.VARCHAR);
				cs.setString(5, "PREPARE");
				cs.executeUpdate();
				
				cs.setString(4, QuillPen.parseValues(new String[]{"AUTHORIZE_GROUP_ID", anaFisc, "AUTHORIZE_ID", key}));
				cs.setString(5, "DO");
				cs.executeUpdate();	
			}
			
		} catch (SQLException e){
			System.out.println("criarAprovadores 4");
		}

	}
	
	/**
	 * Create rules only for procurement, fiscal, account analysis
	 * The set of rules created will be applied to 'Ativação de Contrato'
	 * @param c
	 * @throws SQLException 
	 */
	private HashSet<String> doCreateRegrasAprovacao(Connection c)  {
		HashSet<String> resp = new HashSet<String>();
		HashMap<String, String> hashApprov = new HashMap<String, String>();
		
		String sql = "{call IFSCSP.PUR_APPROVAL_TEMPL_API.NEW__(?,?,?,?,?)}";
		
		try(CallableStatement cs = c.prepareCall(sql)){
			String[] regrasMedicao = {"ATE 245.100,00", 
					"245.100,01 A 1.225.500,00", 
					"1.225.500,01 A 2.451.000,00", 
					"2.451.000,01 A 19.608.000,00"
			};
			String[] regrasAtivacao = {"ATE 245.100,00", 
					"245.100,01 A 735.300,00",
					"735.300,01 A 2.451.000,00",
					"2.451.000,01 A 19.608.000,00"
			};
			String[] line = {"TEMPLATE_ID", "", "DESCRIPTION", ""};
			
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, java.sql.Types.VARCHAR);
			cs.registerOutParameter(3, java.sql.Types.VARCHAR);
			cs.registerOutParameter(4, java.sql.Types.VARCHAR);
			
			for(int i = 1; i <= 4; i++){
				cs.setNull(4, java.sql.Types.VARCHAR);
				cs.setString(5, "PREPARE");
				cs.executeUpdate();
				line[1] = "ATIVA"+i;
				line[3] = regrasAtivacao[i-1];
				String value = QuillPen.parseValues(line);
				System.out.println(value);
				cs.setString(4, value);				
				cs.setString(5, "DO");
				cs.executeUpdate();
			}
			
			Set<String> keys = values.keySet();
			for(String key: keys){				
				String novo = "M" + key;
				novo = novo.replaceAll(" ", "");
				while(novo.length() > 10){
					novo = novo.substring(0, novo.length()-2) + novo.charAt(novo.length()-1);
				}
				cs.setNull(4, java.sql.Types.VARCHAR);
				cs.setString(5, "PREPARE");
				cs.executeUpdate();
				
				resp.add(novo);
				hashApprov.put(novo, key);
				line[1] = novo;
				
				if (novo.contains("1")){
					line[3] = regrasMedicao[0];
				} else if (novo.contains("2")){
					line[3] = regrasMedicao[1];
				} else if (novo.contains("3")){
					line[3] = regrasMedicao[2];
				} else if (novo.contains("4")){
					line[3] = regrasMedicao[3];
				} else if (novo.contains("5")){
					//line[3] = desc[4];
					System.out.println(novo);
				}
				cs.setString(4, QuillPen.parseValues(line));				
				cs.setString(5, "DO");
				cs.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("doCreateRegrasAprovacao 1");
		}
		
		String anaCont = "ANACONT";
		String anaFisc = "ANAFISC";	
		sql = "{call IFSCSP.PUR_APPROVAL_TEMPL_LINE_API.NEW__(?,?,?,?,?)}";
		try(CallableStatement cs = c.prepareCall(sql)){
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, java.sql.Types.VARCHAR);
			cs.registerOutParameter(3, java.sql.Types.VARCHAR);
			cs.registerOutParameter(4, java.sql.Types.VARCHAR);
			
			
			String[] grupoSup = {"CRDGENPURCH", "MNGGENPURCH", "GMNGENPURCH", "DIRGENPURCH", "CEO"};
			String[] line = {
					"TEMPLATE_ID", "ATIVA", 
					"ROUTE", "", 
					"APPROVAL_LEVEL", "Grupo", 
					"AUTHORIZE_ID", "", 
					"AUTHORIZE_GROUP_ID", ""};
			
			int nextStep = 0;
			for(int i = 1; i <= 4; i++){
				line[1] = "ATIVA"+i;
				for(int j = 1; j <= i; j++){					
					line[3] = Integer.toString(j*10);
					line[9] = grupoSup[j-1];
					cs.setNull(4, java.sql.Types.VARCHAR);
					cs.setString(5, "PREPARE");
					cs.executeUpdate();
					cs.setString(4, QuillPen.parseValues(line));				
					cs.setString(5, "DO");
					cs.executeUpdate();
					nextStep = j;
				}
				++nextStep;
				line[3] = Integer.toString(nextStep*10);
				line[9] = anaCont;
				cs.setNull(4, java.sql.Types.VARCHAR);
				cs.setString(5, "PREPARE");
				cs.executeUpdate();
				cs.setString(4, QuillPen.parseValues(line));				
				cs.setString(5, "DO");
				cs.executeUpdate();
				
				++nextStep;
				line[3] = Integer.toString(nextStep*10);
				line[9] = anaFisc;
				cs.setNull(4, java.sql.Types.VARCHAR);
				cs.setString(5, "PREPARE");
				cs.executeUpdate();
				cs.setString(4, QuillPen.parseValues(line));				
				cs.setString(5, "DO");
				cs.executeUpdate();
				
			}			
			
			Set<String> chaves = hashApprov.keySet();
			for(String key: chaves){
				String original = hashApprov.get(key);
				ArrayList<String[]> aprovacoes = values.get(original);
				for(String[] aprovacao: aprovacoes){
					line[1] = key;
					line[3] = aprovacao[3];
					line[9] = aprovacao[6];
					cs.setNull(4, java.sql.Types.VARCHAR);
					cs.setString(5, "PREPARE");
					cs.executeUpdate();
					cs.setString(4, QuillPen.parseValues(line));				
					cs.setString(5, "DO");
					cs.executeUpdate();
				}
			}
		} catch (SQLException e) {
			System.out.println("doCreateRegrasAprovacao 2");
		}
		return resp;
	}
	

	/**
	 * Read all templates 
	 * @param c
	 * @return
	 * @throws SQLException
	 */
	private HashMap<String,ArrayList<String[]>> getTemplates(Connection c) throws SQLException {
		String sql = "select trim(REGEXP_REPLACE(a.template_id, '[A-Z]', '')), " + 
				" a.template_id, b.line_no, b.route, b.approval_level, b.approval_level_db, b.authorize_group_id " +
				" from IFSCSP.pur_approval_templ a " + 
				" join IFSCSP.pur_approval_templ_line b on a.template_id = b.template_id " + 
				" where b.authorize_group_id not like 'BDG%'" +
				" and a.template_id not like 'CARTAO%' " + 
				" and a.template_id not like 'DI%' " + 
				" and a.template_id not like 'PECIMP%' " + 
				" and a.template_id not like 'PECNAC%' " + 
				" and a.template_id not like 'ZPE%' " +
				" and a.template_id not like 'ACCOUNTS%'" +
				" and a.template_id not like 'OC %'" +
				" and a.template_id not like 'OCPURCH%'" +
				" and a.template_id not like 'ATIVA%'" +
				" and ( " + 
				" trim(REGEXP_REPLACE(a.template_id, '[A-Z]', '')) like '1' " + 
				" or trim(REGEXP_REPLACE(a.template_id, '[A-Z]', '')) like '2' " + 
				" or trim(REGEXP_REPLACE(a.template_id, '[A-Z]', '')) like '3' " + 
				" or trim(REGEXP_REPLACE(a.template_id, '[A-Z]', '')) like '4' " +				 
				" ) " + 
				" order by a.template_id, b.line_no";
		
		HashMap<String, ArrayList<String[]>> values = new HashMap<String, ArrayList<String[]>>();
		try(PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
			while(rs.next()){
				String[] line = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)};
				ArrayList<String[]> approvers = values.get(line[1]);
				if (approvers != null){
					approvers.add(line);
				} else {
					approvers = new ArrayList<String[]>();
					approvers.add(line);
					values.put(line[1], approvers);
				}
			}
		} catch (Exception e){
			System.out.println("getTemplates");
		}
		
		return values;
	}

	/**
	 PROCEDURE New__ (
   info_       OUT    VARCHAR2,
   objid_      OUT    VARCHAR2,
   objversion_ OUT    VARCHAR2,
   attr_       IN OUT VARCHAR2,
   action_     IN     VARCHAR2 )
	 * @param c 
	 * @throws SQLException 
	 */
	private void createContractRules(Connection c) throws SQLException {
		String[][] amounts = {
				{"0", "18800"}, 
				{"18800.01", "188000"},
				{"188000.01", "940000"},
				{"940000.01","7520000"},
				{"7520000.01", "15040000"}
		};
		
		
				
		/*
		 * se houver somente 2 aprovadores no nível 5 então não fazer a regra - nesse caso a aprovação irá somente até
		 * o diretor (não teremos CEO)
		 */
		/*
		 * copiar as regras 1 a cinco para medição (sem o grupo de budget)
		 */
		/*
		 * quais regras são para suprimentos, fiscal e contabil? (regra de ativação)
		 * elas existem no GED - usá-las? ou criar uma nova?
		 */
		/*
		 * temp_priority não tem nenhuma ação no IFS - se as regras forem iguais o IFS pega a que foi inserida primeiro
		 */
		/*
		 * fazer somente CSP e FOR
		 */
		/*
		 * SE uma das regras contiver CEO... salvar somente CEO
		 * SE contiver BDG... salvar somente BDG
		 */
		
		String sql = "{call IFSCSP.C_SUB_APP_RULE_API.NEW__(?,?,?,?,?)}";
		try(CallableStatement cs = c.prepareCall(sql);){
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, java.sql.Types.VARCHAR);
			cs.registerOutParameter(3, java.sql.Types.VARCHAR);
			cs.registerOutParameter(4, java.sql.Types.VARCHAR);
			cs.setNull(4, java.sql.Types.VARCHAR);
			cs.setString(5, "PREPARE");
			cs.executeUpdate();
			cs.clearParameters();
			
			String value = QuillPen.parseValues(new String[]{
				"APPROVAL_RULE", "PURCHASE_5",
				"CONTRACT", "CSP",
				"TEMP_PRIORITY", "5",
				"VENDOR_NO", "%",
				"SUPP_GRP", "%",
				"MIN_AMOUNT", "7520000.01",
				"MAX_AMOUNT", "15040000",
				"AUTHORIZE_CODE", "CONTRATO",
				"C_SUB_APP_RULE_TYPE", "Aprovação de Contrato",
				"TEMPLATE_ID", "PURCHASE 8",
				"ALLOW_CHANGES", "TRUE",
				"USE_AMOUNTS_INCL_TAX_DB", "FALSE"
			});
			cs.setString(4, value);
			cs.setString(5, "CHECK");
			cs.executeUpdate();
			
			cs.setString(4, value);
			cs.setString(5, "DO");
			cs.executeUpdate();
			
		} catch (SQLException e){
			System.out.println("createContractRules");
		}
	}
	
}
