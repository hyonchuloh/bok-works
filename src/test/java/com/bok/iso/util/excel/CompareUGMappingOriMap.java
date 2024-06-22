package com.bok.iso.util.excel;

import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * TX_CODE를 가지고 기존 한은금융망 전문 레이아웃을 가져온다
 * @author ohhyonchul
 *
 */
public class CompareUGMappingOriMap {
	
	private Map<String, String> ORI_COL_MAP;
	
	public CompareUGMappingOriMap(String filePath, String txCode, boolean isReq) {
		
		FileInputStream file = null;
		ORI_COL_MAP = new LinkedHashMap<String, String>();
		
		try {
			
			file = new FileInputStream(filePath);
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			
			XSSFSheet sheetTxCode = workbook.getSheet(txCode);
			int rows = sheetTxCode.getPhysicalNumberOfRows();
			int rowNo = 0;
			for (rowNo = 0; rowNo < rows; rowNo++) {
				XSSFRow row = sheetTxCode.getRow(rowNo);
				if (row != null) {
					XSSFCell cell1 = row.getCell(1); // 셀의 값을 가져온다
					XSSFCell cell5 = row.getCell(5); // 셀의 값을 가져온다
					XSSFCell cell6 = row.getCell(6); // 셀의 값을 가져온다
					if ( isReq == false ) 
						cell6 = row.getCell(7); // 셀의 값을 가져온다
					if (cell1 == null || cell6 == null)
						continue;
					String colName = cell1.toString().trim();
					if (colName.length() == 0)
						continue;
					if (colName.equals("항목") || colName.equals("공통부"))
						continue;
					this.ORI_COL_MAP.put(CompareUGMappingUtil.getOnlyHangle(colName), cell5.toString() +"_"+ cell6.toString());
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			try {
				if (file != null)
					file.close();
			} catch (Exception e) {
				//
			}
		}
	}
	
	public Set<String> keySet() {
		return ORI_COL_MAP.keySet();
	}
	
	public String get(String key) {
		return ORI_COL_MAP.get(key);
	}
	
	
	
	

}
