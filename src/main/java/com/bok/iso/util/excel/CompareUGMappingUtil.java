package com.bok.iso.util.excel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;

public class CompareUGMappingUtil {
	
	/**
	 * 타이틀 스타일
	 * @param workbook
	 * @return
	 */
	public static CellStyle getTitleStyle(Workbook workbook) {
		
		Font titleFont = workbook.createFont();
		titleFont.setFontName("한컴 고딕");
		titleFont.setFontHeight((short)250);
		titleFont.setBoldweight((short)700);
		
		CellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setFont(titleFont);
		
		return titleStyle;
	}
	
	/**
	 * 테이블 헤드 스타일
	 * @param workbook
	 * @return
	 */
	public static CellStyle getHeadStyle(Workbook workbook) {
		
		Font headFont = workbook.createFont();
		headFont.setFontName("한컴 고딕");
		headFont.setFontHeight((short)200);
		headFont.setBoldweight((short)700);
		
		CellStyle headStyle = workbook.createCellStyle();
		headStyle.setFont(headFont);
		
		return headStyle;
	}
	
	/**
	 * 본문 스타일
	 * @param workbook
	 * @return
	 */
	public static CellStyle getCommStyle(Workbook workbook) {
		
		Font commFont = workbook.createFont();
		commFont.setFontName("한컴 고딕");
		commFont.setFontHeight((short)200);
		
		CellStyle commStyle = workbook.createCellStyle();
		commStyle.setFont(commFont);
		
		return commStyle;
	}
	
	public static String getCellValue(XSSFCell cell) {
		String value = "";
		if (cell == null) { // 빈 셀 체크
			return "";
		} else {
			// 타입 별로 내용을 읽는다
			switch (cell.getCellType()) {
			case XSSFCell.CELL_TYPE_FORMULA:
				value = cell.getCellFormula();
				break;
			case XSSFCell.CELL_TYPE_NUMERIC:
				value = cell.getNumericCellValue() + "";
				break;
			case XSSFCell.CELL_TYPE_STRING:
				value = cell.getStringCellValue() + "";
				break;
			case XSSFCell.CELL_TYPE_BLANK:
				value =  "";
				break;
			case XSSFCell.CELL_TYPE_ERROR:
				value = cell.getErrorCellValue() + "";
				break;
			}
		}
		return value;
	}
	
	/**
	 * 한글만 추출
	 * @param str
	 * @return
	 */
	public static String getOnlyHangle(String str){

		StringBuffer sb=new StringBuffer();
		if(str!=null && str.length()!=0){
			Pattern p = Pattern.compile("[가-힣|0-9|()]");
			Matcher m = p.matcher(str);
			while(m.find()){
				sb.append(m.group());
			}
		}
		return sb.toString();

	}
	
	/**
	 * 패턴 매칭
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String in){

		if(in!=null && in.length()!=0){
			Pattern p = Pattern.compile("[_|0-9]");
			Matcher m = p.matcher(in);
			while(m.find()){
				return true;
			}
		}
		return false;

	}
	
	

}
