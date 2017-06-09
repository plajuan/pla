package com.csp.galanga.dto;

public class Contabilizacao2 {
	private Integer id;
	private Integer lancamentoId; 
	private Integer imposto;
	private Double valor;
	private Integer debito;
	private Integer credito;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getLancamentoId() {
		return lancamentoId;
	}
	public void setLancamentoId(Integer lancamentoId) {
		this.lancamentoId = lancamentoId;
	}
	public Integer getImposto() {
		return imposto;
	}
	public void setImposto(Integer imposto) {
		this.imposto = imposto;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public Integer getDebito() {
		return debito;
	}
	public void setDebito(Integer debito) {
		this.debito = debito;
	}
	public Integer getCredito() {
		return credito;
	}
	public void setCredito(Integer credito) {
		this.credito = credito;
	}
	
}
