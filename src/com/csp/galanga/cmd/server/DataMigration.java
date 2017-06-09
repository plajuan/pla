package com.csp.galanga.cmd.server;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.dto.Contabilizacao;
import com.csp.galanga.dto.Lancamento;
import com.csp.galanga.util.QuillPen;
import com.csp.galanga.util.ReadProps;
import com.csp.galanga.util.Warehouse;

public class DataMigration implements Command {

	private HashMap<Integer, String> impostos;
	private HashMap<Integer, String> contas;
	private Warehouse w = new Warehouse();

	public DataMigration() {
		impostos = new HashMap<Integer, String>();
		impostos.put(1, "PIS");
		impostos.put(2, "PIS_IMPORTACAO");
		impostos.put(3, "COFINS");
		impostos.put(4, "COFINS_IMPORTACAO");
		impostos.put(5, "IMPOSTO_IMPORTACAO");
		impostos.put(6, "AFRMM");
		impostos.put(7, "ICMS_DIFERIDO");
		impostos.put(8, "IPI");

		contas = new HashMap<Integer, String>();
		contas.put(1, "CALCULADO");
		contas.put(2, "SUSPENSO");
		contas.put(3, "DEVIDO");
		contas.put(4, "EXTINTO");
		contas.put(5, "DIFERIDO");
	}

	@Override
	public void doIt() {
		HashMap<Integer, Lancamento> lancamentos = getListaLancamentos();
		StringBuilder log = new StringBuilder();
		
		try (Connection c = w.connectIFS()) {
			String sql = "SELECT A.fiscal_reg_id, A.fiscal_note_id, A.contract FROM IFSCSP.FISCAL_REGISTRY A";
			try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int fiscalReg = rs.getInt(1);
					int fiscalNote = rs.getInt(2);
					String contract = rs.getString(3);
					Lancamento lancamento = lancamentos.get(fiscalNote);
					if (lancamento != null) {
						lancamento.setFiscalReg(fiscalReg);
						lancamento.contrato = contract;
					}

				}
			}

			Set<Integer> keys = lancamentos.keySet();
			ArrayList<Integer> removeKeys = new ArrayList<Integer>();

			for (Integer key : keys) {
				Lancamento l = lancamentos.get(key);
				if (l.getFiscalRegValue() == 0) {
					removeKeys.add(key);
				}
			}
			for (Integer key : removeKeys) {
				lancamentos.remove(key);
			}
			keys = null;
			removeKeys = null;

			
			for(Lancamento lan: lancamentos.values()){
				log.append(lan.getNotaFiscal());
				log.append("\n");
				/*
				 * 1 lsResult o 2 objid o 3 objversion o 4 attr i o 5 action i
				 */
				sql = "{call IFSCSP.C_ZPE_VOUCHER_API.NEW__(?,?,?,?,?)}";
				try (CallableStatement cs = c.prepareCall(sql)) {
					String value = QuillPen.parseValues(new String[] { "FISCAL_REG_ID", lan.getFiscalReg() });

					cs.registerOutParameter(1, java.sql.Types.VARCHAR);
					cs.registerOutParameter(2, java.sql.Types.VARCHAR);
					cs.registerOutParameter(3, java.sql.Types.VARCHAR);
					cs.setString(4, value);
					cs.registerOutParameter(4, java.sql.Types.VARCHAR);
					cs.setString(5, "PREPARE");

					cs.executeUpdate();

					cs.clearParameters();
					value = QuillPen.parseValues(new String[] { 
							"FISCAL_REG_ID", lan.getFiscalReg(), 
							"OPERATION_DATE",lan.getDataOperacao(), 
							"OPERATION_TYPE_ID", lan.getTipoOperacao(),
							"FISCAL_NOTE_ID_IN", lan.getNotaFiscal(), 
							"USER_ID", lan.getUsuario().toUpperCase() });
					// FISCAL_REG_ID20OPERATION_DATE2013-09-20-13.53.44OPERATION_TYPE_ID1FISCAL_NOTE_ID40USER_IDCSP_FSALVES

					// value =
					// "FISCAL_REG_ID20OPERATION_DATE2014-09-01-00.00.00OPERATION_TYPE_ID1FISCAL_NOTE_ID_IN40USER_IDCSP_JNETO";
					// change values inside value Character c.hashCode {31,30}
					cs.setString(4, value);
					cs.setString(5, "DO");
					cs.executeUpdate();
				}

				sql = "{call IFSCSP.FISCAL_REGISTRY_FN_ITEM_API.Update_Contract(?,?)}";
				try (CallableStatement cs = c.prepareCall(sql)) {
					cs.setString(1, lan.getFiscalReg());
					cs.setString(2, lan.contrato);
					cs.executeUpdate();
				}

				sql = "{call IFSCSP.FISCAL_REGISTRY_UTIL_API.Grouping_Data(?)}";
				try (CallableStatement cs = c.prepareCall(sql)) {
					cs.setString(1, lan.getFiscalReg());
					cs.executeUpdate();
				}
				
				for (Contabilizacao con: lan.contabilizacoes){
					log.append("\t" + impostos.get(con.imposto) + " " + con.valor + " " + contas.get(con.debito) + " "
							+ contas.get(con.credito));
					log.append("\n");
					
					/*
					 * 1 lsresult o 2 objId o 3 objversion o 4 attr i o 5 action i
					 */
					sql = "{call IFSCSP.C_ZPE_ACCOUNTING_API.NEW__(?,?,?,?,?)}";
					try (CallableStatement cs = c.prepareCall(sql)) {
						String value = QuillPen.parseValues(new String[] { "FISCAL_REG_ID", lan.getFiscalReg() });

						cs.registerOutParameter(1, java.sql.Types.VARCHAR);
						cs.registerOutParameter(2, java.sql.Types.VARCHAR);
						cs.registerOutParameter(3, java.sql.Types.VARCHAR);
						// cs.setString(4, "FISCAL_REG_ID20");
						cs.setString(4, value);
						cs.registerOutParameter(4, java.sql.Types.VARCHAR);
						cs.setString(5, "PREPARE");

						cs.executeUpdate();
						cs.clearParameters();
						// aqui
						value = QuillPen.parseValues(new String[] { 
								"FISCAL_REG_ID", lan.getFiscalReg(), 
								"ZPE_VOUCHER_ID", "1", 
								"VALUE", con.valor, 
								"TAX_LIST_ID",  impostos.get(con.imposto), 
								"CREDIT_ACC_LIST", contas.get(con.credito),
								"DEBIT_ACC_LIST", contas.get(con.debito) });
						// "FISCAL_REG_ID20ZPE_VOUCHER_ID1VALUE1TAX_LIST_IDPIS_IMPORTACAOCREDIT_ACC_LISTCALCULADODEBIT_ACC_LISTSUSPENSO");
						cs.setString(4, value);
						cs.setString(5, "DO");

						cs.executeUpdate();
					}

					sql = "{call IFSCSP.FISCAL_REGISTRY_FN_ITEM_API.Update_Contract(?,?)}";
					try (CallableStatement cs = c.prepareCall(sql)) {
						cs.setString(1, lan.getFiscalReg());
						cs.setString(2, lan.contrato);
						cs.executeUpdate();
					}

					sql = "{call IFSCSP.FISCAL_REGISTRY_UTIL_API.Grouping_Data(?)}";
					try (CallableStatement cs = c.prepareCall(sql)) {
						cs.setString(1, lan.getFiscalReg());
						cs.executeUpdate();
					}		
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		QuillPen pen = new QuillPen();
		try {
			pen.saveFile(log.toString(), ReadProps.FILE.getKey("FILE.SERVER.PATH") + "NotasIFS.txt");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("Migration OK");
	}

	// Fazer as consultas uma-a-uma
	private HashMap<Integer, Lancamento> getListaLancamentos() {
		ArrayList<Integer> nfs = new ArrayList<Integer>();
		HashMap<Integer, Lancamento> lancamentos = new HashMap<Integer, Lancamento>();

		try (Connection c = w.connectCspSys()) {
			String sql = "select a.id from nota_fiscal a";
			try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					nfs.add(rs.getInt(1));
				}
			}

			sql = "select TO_CHAR(a.data_operacao, 'yyyy-MM-dd-HH.mm.ss'), a.nota_fiscal_id, a.cod_usuario, a.tipo_operacao from lancamento a "
					+ "where a.nota_fiscal_id = ?";
			try (PreparedStatement ps = c.prepareStatement(sql);) {
				for (Integer id : nfs) {
					ps.setInt(1, id);
					try (ResultSet rs = ps.executeQuery();) {
						Lancamento lancamento = new Lancamento();
						if (rs.next()) {
							lancamento.setDataOperacao(rs.getString(1));
							lancamento.setNotaFiscal(rs.getInt(2));
							lancamento.setUsuario(rs.getString(3));
							lancamento.setTipoOperacao(rs.getInt(4));
							lancamentos.put(lancamento.getNotaFiscalValue(), lancamento);
						}
					}
				}

			}
			
			sql = "select a.nota_fiscal_id, listagg(a.id, ',') WITHIN GROUP (ORDER BY a.id) ids " + 
					"FROM lancamento a " + 
					"GROUP BY a.nota_fiscal_id";
			try(PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
				while (rs.next()){
					Integer nfId = rs.getInt(1);
					String[] codigos = rs.getString(2).split(",");
					Lancamento l = lancamentos.get(nfId);
					l.codLancamentos[0] = Integer.parseInt(codigos[0]);
					l.codLancamentos[1] = Integer.parseInt(codigos[1]);
				}
			}

			sql = "select c.imposto, c.valor, c.debito, c.credito from contabilizacao c "
					+ "where c.lancamento_id in (?,?)";
			try (PreparedStatement ps = c.prepareStatement(sql)) {
				for (Lancamento l : lancamentos.values()) {
					ps.setInt(1, l.codLancamentos[0]);
					ps.setInt(2, l.codLancamentos[1]);
					
					l.contabilizacoes = new ArrayList<Contabilizacao>();
					try (ResultSet rs = ps.executeQuery()) {
						while (rs.next()) {
							Contabilizacao value = new Contabilizacao();
							value.imposto = rs.getInt(1);
							value.valor = rs.getString(2);
							value.debito = rs.getInt(3);
							value.credito = rs.getInt(4);
							l.contabilizacoes.add(value);
						}
					}
				}
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		logLancamentos(lancamentos);

		return lancamentos;
	}

	private void logLancamentos(HashMap<Integer, Lancamento> lancamentos) {
		StringBuilder sb = new StringBuilder();
		for (Lancamento l : lancamentos.values()) {
			sb.append(l.getNotaFiscal());
			sb.append("\n");
			for (Contabilizacao c : l.contabilizacoes) {
				sb.append("\t" + impostos.get(c.imposto) + " " + c.valor + " " + contas.get(c.debito) + " "
						+ contas.get(c.credito));
				sb.append("\n");
			}
		}
		QuillPen pen = new QuillPen();
		try {
			pen.saveFile(sb.toString(), ReadProps.FILE.getKey("FILE.SERVER.PATH") + "NotasCSPsys.txt");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
