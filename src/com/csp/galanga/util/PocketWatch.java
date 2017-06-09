package com.csp.galanga.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PocketWatch {
	
	public static Date todayIs(String text) throws ParseException{
		if ("".equals(text) || text == null){
			return null;
		}
		DateFormat format = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a", Locale.ENGLISH);
		text = text.replaceAll("\\.[0-9]{6}", "");
		Date date = format.parse(text);
		return date;
	}
	
}
