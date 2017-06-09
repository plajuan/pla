package com.csp.galanga.cmd.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.csp.galanga.cmd.Command;

public class ReadExcel implements Command{

		
	@Override
	public void doIt() {
		ArrayList<String> sqlList = new ArrayList<>();
		String sqlPIS = "select c.id from contabilizacao c join lancamento l on l.id = c.lancamento_id join nota_fiscal n on n.id = l.nota_fiscal_id where c.imposto = 1 and n.id = 5647 ";
		String sqlCOFINS = "select c.id from contabilizacao c join lancamento l on l.id = c.lancamento_id join nota_fiscal n on n.id = l.nota_fiscal_id where c.imposto = 3 and n.id = 5647 ";
		
		//final JFileChooser chooser = new JFileChooser();
		//int returnVal = chooser.showOpenDialog(null);
		File excel = new File("C:/GalangaData/inserir.xlsx");
		
		try {
			FileInputStream input = new FileInputStream(excel);
			XSSFWorkbook wb = new XSSFWorkbook(input);
			XSSFSheet sheet = wb.getSheet("SUSPENSO CSPSYS");
			for(int i = 9; i <= 3305; i++){
				XSSFRow row = sheet.getRow(i);
				//XSSFCell cell = row.getCell(1);
				//double value = cell.getNumericCellValue();
				
				double pis = row.getCell(8).getNumericCellValue();
				double cofins = row.getCell(9).getNumericCellValue();
				double ipi = row.getCell(10).getNumericCellValue();
				double icms = row.getCell(11).getNumericCellValue();
				double ii = row.getCell(12).getNumericCellValue();
				double afrmm = row.getCell(13).getNumericCellValue();
				System.out.println(pis  + " " + cofins + " " +  ipi + " " + icms+ " " + ii + " " + afrmm);
				
				if (row.getCell(1).getNumericCellValue() > 0.0 && i == 9 || i == 3305){
					//System.out.println(row.getCell(1).getNumericCellValue() + " " + row.getCell(2).getNumericCellValue() + " " +row.getCell(3).getDateCellValue() + " " +row.getCell(4).getNumericCellValue() + " " +row.getCell(5).getNumericCellValue() + " " +row.getCell(6).getNumericCellValue());
					System.out.println(row.getCell(1).getRawValue() + " " + row.getCell(2).getRawValue());
				}
								
			}
			System.out.println(wb);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
