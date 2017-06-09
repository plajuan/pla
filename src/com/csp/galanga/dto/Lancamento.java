package com.csp.galanga.dto;

import java.util.ArrayList;

public class Lancamento {
	private String dataOperacao = null;	
	private int tipoOperacao = 0;	
	private String usuario = null;
	private int notaFiscal = 0;
	private int fiscalReg = 0;
	public ArrayList<Contabilizacao> contabilizacoes = new ArrayList<Contabilizacao>();
	public int[] codLancamentos = new int[2];
	public String contrato;
	
	public String getDataOperacao() {
		return dataOperacao;
	}
	
	public void setDataOperacao(String dataOperacao) {
		if (this.dataOperacao == null) this.dataOperacao = dataOperacao;	
	}
	
	public String getTipoOperacao() {
		return Integer.toString(tipoOperacao);
	}
	
	public void setTipoOperacao(int tipoOperacao) {
		if (this.tipoOperacao == 0){
			if (tipoOperacao == 11){
				this.tipoOperacao = 1;
			} else {
				this.tipoOperacao = tipoOperacao;
			}
		}
	}
	
	public String getUsuario() {
		return usuario;
	}
	
	public void setUsuario(String usuario) {
		if (this.usuario == null) this.usuario = usuario;
	}
	
	public String getNotaFiscal() {
		return Integer.toString(notaFiscal);
	}
	
	public int getNotaFiscalValue(){
		return notaFiscal;
	}
	
	public void setNotaFiscal(int notaFiscal) {
		if (this.notaFiscal == 0) this.notaFiscal = notaFiscal;
	}

	public String getFiscalReg() {
		return Integer.toString(fiscalReg) ;
	}
	
	public int getFiscalRegValue(){
		return fiscalReg;
	}
	
	public void setFiscalReg(int fiscalReg) {
		this.fiscalReg = fiscalReg;
	}
	
	
}
