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
 * UG 정보를 읽어들인다.
 * @author ohhyonchul
 *
 */
public class CompareUGMappingUGMap {
	
	private Map<String, String> PATH_MAP;
	private Map<String, String> ADDINFO_MAP;
	
	public Set<String> pathMapKeySet() {
		return PATH_MAP.keySet();
	}
	public Set<String> AddInfoMapKeySet() {
		return ADDINFO_MAP.keySet();
	}
	
	public String getPathMap(String key) {
		return PATH_MAP.get(key);
	}
	
	public String getAddInfoMap(String key) {
		return ADDINFO_MAP.get(key);
	}
	
	public CompareUGMappingUGMap(String fileName, String txCode) {
		int minMandIndex = 0;
		int pathIndex = 0;
		FileInputStream file = null;
		
		this.PATH_MAP = new LinkedHashMap<String, String>();
		this.ADDINFO_MAP = new LinkedHashMap<String, String>();
		
		try {
			// 경로에 있는 파일을 읽
			file = new FileInputStream(fileName);
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			int rowNo = 0;
			int cellIndex = 0;
			XSSFSheet sheet = workbook.getSheet("Full_View");
			int rows = sheet.getPhysicalNumberOfRows();
			int annotationFieldNo = 0;
			int annotationAddInfo = 0;
			for (rowNo = 0; rowNo < rows; rowNo++) {
				XSSFRow row = sheet.getRow(rowNo);
				if (row != null) {
					int cells = row.getPhysicalNumberOfCells(); // 해당 Row에 사용자가 입력한 셀의 수를 가져온다
					if ( rowNo == 0 ) {
						for (cellIndex = 0; cellIndex <= cells; cellIndex++) {
							XSSFCell cell = row.getCell(cellIndex); // 셀의 값을 가져온다
							String value = CompareUGMappingUtil.getCellValue(cell);
							if ( value.contains(txCode)) {
								if ( value.contains("Field") ) {
									annotationFieldNo = cellIndex;
								} else if ( value.contains("Additional")) {
									annotationAddInfo = cellIndex;
								}
							} else if ( value.equals("Min Mand")) {
								minMandIndex = cellIndex;
							} else if ( value.contains("Path")) {
								pathIndex = cellIndex;
							}
						}
					} else {
						XSSFCell annoFieldCell = row.getCell(annotationFieldNo); // 셀의 값을 가져온다
						XSSFCell annoAddCell = row.getCell(annotationAddInfo); // /셀의 값을 가져온다
						XSSFCell pathCell = row.getCell(pathIndex); // path 가져오기
						XSSFCell minMandCell = row.getCell(minMandIndex); //MIN_MAND 가져오기
						String annoFieldCellStr = CompareUGMappingUtil.getCellValue(annoFieldCell);
						String annoAddCellStr = CompareUGMappingUtil.getCellValue(annoAddCell);
						String pathCellStr = CompareUGMappingUtil.getCellValue(pathCell);
						String minMandStr = CompareUGMappingUtil.getCellValue(minMandCell);
						if ( minMandStr.equals("false") ) minMandStr = "";
						if ( annoFieldCellStr != null && annoFieldCellStr.startsWith("false"))
							continue;
						PATH_MAP.put(CompareUGMappingUtil.getOnlyHangle(annoFieldCellStr)+"_"+rowNo, minMandStr +";"+pathCellStr);
						ADDINFO_MAP.put(CompareUGMappingUtil.getOnlyHangle(annoFieldCellStr)+"_"+rowNo, annoAddCellStr);
					}
				}
			}

		} catch (Exception e) {
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

}
