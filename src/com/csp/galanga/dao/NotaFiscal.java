package com.csp.galanga.dao;

import java.util.ArrayList;
import java.util.Date;

public class NotaFiscal {
	private Date date;
	private Double cofins;
	private Integer codCofins;
	private Double pis;	
	private Integer codPis;
	private Double ipi;
	private Integer codIpi;
	private Double icms;
	private Integer codIcms;
	private Double ii;
	private Integer codII;
	private Double afrmm;
	private Integer codAfrmm;
	private Double nf_total;
	private String num;
	private Integer id;
	private boolean isNacional;
	private int operacao;
	
	public NotaFiscal(String tipo) {
		String temp = tipo.trim();
		if (temp.endsWith("Importada") || temp.endsWith("Importado")){
			isNacional = false;
			codPis = 2;
			codCofins = 4;
			operacao = 1;
		} else{
			isNacional = true;
			codPis = 1;
			codCofins = 3;
			operacao = 2;
		}
		codII = 5;
		codAfrmm = 6;
		codIcms = 7;
		codIpi = 8;
	}
	
	public ArrayList<Object[]> getContabilizacoes(){
		ArrayList<Object[]> r = new ArrayList<>();
		
		if(pis > 0.0){
			r.add(new Object[]{codPis, pis});
		}
		
		if (cofins > 0.0){
			r.add(new Object[]{codCofins, cofins});
		}
		
		if (ipi > 0.0){
			r.add(new Object[]{codIpi, ipi});
		}
		
		if (icms > 0.0){
			r.add(new Object[]{codIcms, icms});
		}
		
		if (ii > 0.0){
			r.add(new Object[]{codII, ii});
		}
		
		if (afrmm > 0.0){
			r.add(new Object[]{codAfrmm, afrmm});
		}
		
		return r;		
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setCofins(double cofins) {
		this.cofins = cofins;
	}

	public void setPis(double pis) {
		this.pis = pis;
	}

	public void setIpi(double ipi) {
		this.ipi = ipi;
	}

	public void setIcms(double icms) {
		this.icms = icms;
	}

	public void setIi(double ii) {
		this.ii = ii;
	}

	public void setAfrmm(double afrmm) {
		this.afrmm = afrmm;
	}

	public double getNf_total() {
		return nf_total;
	}

	public void setNf_total(double nf_total) {
		this.nf_total = nf_total;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String isNacional() {
		String res = "F";
		if(isNacional){
			res = "T";
		}
		return res;
	}

	public int getCodCofins() {
		return codCofins;
	}

	public int getCodPis() {
		return codPis;
	}

	public int getCodIpi() {
		return codIpi;
	}

	public int getCodIcms() {
		return codIcms;
	}

	public int getCodII() {
		return codII;
	}

	public int getCodAfrmm() {
		return codAfrmm;
	}

	public int getOperacao() {
		return operacao;
	}
}
