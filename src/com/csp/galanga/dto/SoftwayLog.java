package com.csp.galanga.dto;


public class SoftwayLog {
	private String username = "";
	private String nome = "";
	private String depto = "";
	private String lastLog = "";
	private String tempo = "0:00";
	private int accesses = 0;
	
	public SoftwayLog(String username, String nome, String depto, String lastlog, String tempo) {
		this.username = username;
		this.nome = nome;
		this.depto = depto;
		this.lastLog = lastlog;
		updateTime(tempo);
	}

	/**
	 * This class was originally designed for recording all time the user logged on but it will only record
	 * the times user entered the system. Real time amount will be recorded on a second development cycle
	 * @param time
	 */
	public void updateTime(String time) {
		this.accesses++;
	}
	
	@Override
	/**
	 * Username, nome, depto, lastlog, tempo and accesses separated by ::
	 */
	public String toString() {
		return username + "::" + nome + "::" + depto + "::" + lastLog + "::" + tempo + "::" + accesses;
	}
	
	/*public static void main(String[] args) {
		String txt = "Ultimo Acesso: 26/12/2013 15:07:39 -> 11/02/2014 11:13:17";
		System.out.println(txt.substring(15, 17));
		System.out.println(txt.substring(18,20));
		System.out.println(txt.substring(21,25));
		System.out.println(txt.substring(26,28));
		System.out.println(txt.substring(29,31));
		System.out.println(txt.substring(32,34));
		
		System.out.println(txt.substring(38,40));
		System.out.println(txt.substring(41,43));
		System.out.println(txt.substring(44,48));
		System.out.println(txt.substring(49, 51));
		System.out.println(txt.substring(52, 54));
		System.out.println(txt.substring(55, 57));
		
		Calendar inicio = Calendar.getInstance();
		inicio.set(Calendar.DAY_OF_MONTH, Integer.parseInt(txt.substring(15, 17)));
		inicio.set(Calendar.MONTH, Integer.parseInt(txt.substring(18, 20))-1);
		inicio.set(Calendar.YEAR, Integer.parseInt(txt.substring(21, 25)));
		inicio.set(Calendar.HOUR_OF_DAY, Integer.parseInt(txt.substring(26, 28)));
		inicio.set(Calendar.MINUTE, Integer.parseInt(txt.substring(29, 31)));
		inicio.set(Calendar.SECOND, Integer.parseInt(txt.substring(32, 34)));
		System.out.println(inicio);
		
		Calendar fim = Calendar.getInstance();
		fim.set(Calendar.DAY_OF_MONTH, Integer.parseInt(txt.substring(38, 40)));
		fim.set(Calendar.MONTH, Integer.parseInt(txt.substring(41, 43))-1);
		fim.set(Calendar.YEAR, Integer.parseInt(txt.substring(44, 48)));
		fim.set(Calendar.HOUR_OF_DAY, Integer.parseInt(txt.substring(49, 51)));
		fim.set(Calendar.MINUTE, Integer.parseInt(txt.substring(52, 54)));
		fim.set(Calendar.SECOND, Integer.parseInt(txt.substring(55, 57)));
		System.out.println(fim);
		
		System.out.println("----");
		System.out.println( (((fim.getTimeInMillis() - inicio.getTimeInMillis())/1000)/60)/60  );
		
	}*/
	
}
