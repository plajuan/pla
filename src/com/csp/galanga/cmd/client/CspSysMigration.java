package com.csp.galanga.cmd.client;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.util.FileCabinet;
import com.csp.galanga.util.PocketWatch;
import com.csp.galanga.util.Warehouse;

public class CspSysMigration implements Command {

	 //nextval('contabilizacao_id_seq'::regclass)
	//nextval('lancamento_id_seq'::regclass)
	
	@Override
	public void doIt() {
		FileCabinet fc = new FileCabinet();
		try {
			insertNotaFiscal(fc.readXML(new File("D:\\data\\CSPsys\\nota_fiscal.xml")));
			insertLancamento(fc.readXML(new File("D:\\data\\CSPsys\\lancamento.xml")));
			insertContabilizacao(fc.readXML(new File("D:\\data\\CSPsys\\contabilizacao.xml")));
		} catch (ParserConfigurationException | SAXException | IOException | DOMException | SQLException | ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	private void insertNotaFiscal(Document doc) throws SQLException, DOMException, ParseException{
		String sql = "INSERT INTO NOTA_FISCAL(ID, NUM_NOTA_FISCAL, DATA_RECEBIMENTO, VALOR_TOTAL, IS_NACIONAL, DATA_ENTRADA_SISTEMA) "
				+ "VALUES (?,?,?,?,?,?)";
		NodeList nodeList = doc.getElementsByTagName("ROW");
		int end = nodeList.getLength();
		Element element = null;
		Warehouse w = new Warehouse();
		try(Connection c = w.connectPostgre(); PreparedStatement stmt = c.prepareStatement(sql) ){
			for(int i = 0; i < end; i++){
				element = (Element) nodeList.item(i);
				Integer ID =  Integer.valueOf(element.getElementsByTagName("ID").item(0).getTextContent());
				Integer NUM_NOTA_FISCAL = Integer.valueOf(element.getElementsByTagName("NUM_NOTA_FISCAL").item(0).getTextContent());
				Calendar DATA_RECEBIMENTO = Calendar.getInstance();
				DATA_RECEBIMENTO.setTime(PocketWatch.todayIs(element.getElementsByTagName("DATA_RECEBIMENTO").item(0).getTextContent()));
				Double VALOR_TOTAL = Double.valueOf(element.getElementsByTagName("VALOR_TOTAL").item(0).getTextContent().replace(',', '.'));
				boolean IS_NACIONAL = "F".equals(element.getElementsByTagName("IS_NACIONAL").item(0).getTextContent()) ? false : true;
				Calendar DATA_ENTRADA_SISTEMA = Calendar.getInstance();
				DATA_ENTRADA_SISTEMA.setTime(PocketWatch.todayIs(element.getElementsByTagName("DATA_ENTRADA_SISTEMA").item(0).getTextContent()));
				
				stmt.setInt(1, ID);
				stmt.setInt(2, NUM_NOTA_FISCAL);
				stmt.setTimestamp(3, new java.sql.Timestamp(DATA_RECEBIMENTO.getTimeInMillis()));
				stmt.setDouble(4, VALOR_TOTAL);
				stmt.setBoolean(5, IS_NACIONAL);
				stmt.setTimestamp(6, new java.sql.Timestamp(DATA_ENTRADA_SISTEMA.getTimeInMillis()));
				stmt.execute();
			}			
		}
		System.out.println("insertNotaFiscal");
	}
	
	private void insertLancamento(Document doc) throws SQLException, DOMException, ParseException{
		String lanctoSql = "INSERT INTO LANCAMENTO(ID, DATA_OPERACAO, NOTA_FISCAL_ID, NOTA_FISCAL_SAIDA_ID, NOTA_FISCAL_SAIDA_NUM, COD_USUARIO, TIPO_OPERACAO) "
				+ "VALUES (?,?,?,?,?,?,?)";
		NodeList nodeList = doc.getElementsByTagName("ROW");
		int end = nodeList.getLength();
		Element element = null;
		Warehouse w = new Warehouse();
		try(Connection c = w.connectPostgre(); PreparedStatement stmt = c.prepareStatement(lanctoSql) ){
			for(int i = 0; i < end; i++){
				element = (Element) nodeList.item(i);
				Integer ID =  Integer.valueOf(element.getElementsByTagName("ID").item(0).getTextContent());
				
				Calendar DATA_OPERACAO = Calendar.getInstance();
				DATA_OPERACAO.setTime(PocketWatch.todayIs(element.getElementsByTagName("DATA_OPERACAO").item(0).getTextContent()));
				
				Integer NOTA_FISCAL_ID =  Integer.valueOf(element.getElementsByTagName("NOTA_FISCAL_ID").item(0).getTextContent());
				Integer NOTA_FISCAL_SAIDA_ID =  Integer.valueOf(element.getElementsByTagName("NOTA_FISCAL_SAIDA_ID").item(0).getTextContent());
				Integer NOTA_FISCAL_SAIDA_NUM =  Integer.valueOf(element.getElementsByTagName("NOTA_FISCAL_SAIDA_NUM").item(0).getTextContent());
				String COD_USUARIO = element.getElementsByTagName("COD_USUARIO").item(0).getTextContent();
				Integer TIPO_OPERACAO =  Integer.valueOf(element.getElementsByTagName("TIPO_OPERACAO").item(0).getTextContent());
				
				stmt.setInt(1, ID);
				stmt.setTimestamp(2, new java.sql.Timestamp(DATA_OPERACAO.getTimeInMillis()));
				stmt.setInt(3, NOTA_FISCAL_ID);
				stmt.setInt(4, NOTA_FISCAL_SAIDA_ID);
				stmt.setInt(5, NOTA_FISCAL_SAIDA_NUM);
				stmt.setString(6, COD_USUARIO);
				stmt.setInt(7, TIPO_OPERACAO);				
				stmt.execute();
			}			
		}
		System.out.println("insertLancamento");
	}
	
	private void insertContabilizacao(Document doc) throws SQLException{
		String contabSql = "INSERT INTO CONTABILIZACAO (ID, LANCAMENTO_ID, IMPOSTO, VALOR, DEBITO, CREDITO) "
				+ "VALUES(?,?,?,?,?,?)";
		NodeList nodeList = doc.getElementsByTagName("ROW");
		int end = nodeList.getLength();
		Element element = null;
		Warehouse w = new Warehouse();
		try(Connection c = w.connectPostgre(); PreparedStatement stmt = c.prepareStatement(contabSql) ){
			for(int i = 0; i < end; i++){
				element = (Element) nodeList.item(i);
				Integer ID =  Integer.valueOf(element.getElementsByTagName("ID").item(0).getTextContent());
				Integer LANCAMENTO_ID =  Integer.valueOf(element.getElementsByTagName("LANCAMENTO_ID").item(0).getTextContent());
				Integer IMPOSTO =  Integer.valueOf(element.getElementsByTagName("IMPOSTO").item(0).getTextContent());
				Double VALOR = Double.valueOf(element.getElementsByTagName("VALOR").item(0).getTextContent().replace(',', '.'));
				Integer DEBITO =  Integer.valueOf(element.getElementsByTagName("DEBITO").item(0).getTextContent());
				Integer CREDITO =  Integer.valueOf(element.getElementsByTagName("CREDITO").item(0).getTextContent());
				
				stmt.setInt(1, ID);
				stmt.setInt(2, LANCAMENTO_ID);
				stmt.setInt(3, IMPOSTO);
				stmt.setDouble(4, VALOR);
				stmt.setInt(5, DEBITO);
				stmt.setInt(6, CREDITO);
				stmt.execute();
			}
		}
		System.out.println("insertContabilizacao");
	}
	
}
