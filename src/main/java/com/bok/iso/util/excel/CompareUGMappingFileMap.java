package com.bok.iso.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * TX_CODE와 UG 파일명을 매핑한다.
 * @author ohhyonchul
 *
 */
public class CompareUGMappingFileMap {
	
	private Map<String, String> TX_MAP_REQ;
	private Map<String, String> TX_MAP_RES;
	private Map<String, String> TX_MAP_NAME;
	
	public CompareUGMappingFileMap(String filePath, String fileName) {
		
		FileInputStream file = null;
		TX_MAP_REQ = new HashMap<String, String>();
		TX_MAP_RES = new HashMap<String, String>();
		TX_MAP_NAME = new HashMap<String, String>();
		
		try {
			file = new FileInputStream(filePath + "/" + fileName);
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheet("매핑_표준전문개발반");
			int rowNo = 0;
			int rows = sheet.getPhysicalNumberOfRows();
			int readCnt = 0;
			for (rowNo = 0; rowNo < rows; rowNo++) {
				XSSFRow row = sheet.getRow(rowNo);
				if (row != null) {
					XSSFCell cell1 = row.getCell(1); // TX_CODE
					XSSFCell cell2 = row.getCell(2); // TX_NAME
					XSSFCell cell3 = row.getCell(3); // UG_CODE_REQ
					XSSFCell cell3_1 = row.getCell(3+2); // UG_CODE_REQ_PATTERN
					XSSFCell cell4 = row.getCell(8); // UG_CODE_RES
					XSSFCell cell4_1 = row.getCell(8+2); // UG_CODE_RES_PATTERN
					if (cell1 == null || cell2 == null)
						continue;
					if (!CompareUGMappingUtil.getCellValue(cell1).startsWith("B")) // BKS~ 패턴이 아닌건 패스
						continue;
					if ( CompareUGMappingUtil.getCellValue(cell3).startsWith("폐지"))	// 폐지전문 패스
						continue;
					//System.out.println(CompareUGMappingUtil.getCellValue(cell1) +", "+ CompareUGMappingUtil.getCellValue(cell2) +", "+ CompareUGMappingUtil.getCellValue(cell3) +", "+ CompareUGMappingUtil.getCellValue(cell4));
					TX_MAP_REQ.put(CompareUGMappingUtil.getCellValue(cell1), CompareUGMappingUtil.getCellValue(cell3).substring(0,8) + "_" + CompareUGMappingUtil.getCellValue(cell3_1));
					TX_MAP_RES.put(CompareUGMappingUtil.getCellValue(cell1), CompareUGMappingUtil.getCellValue(cell4).substring(0,8) + "_" + CompareUGMappingUtil.getCellValue(cell4_1));
					TX_MAP_NAME.put(CompareUGMappingUtil.getCellValue(cell1), CompareUGMappingUtil.getCellValue(cell2));
					readCnt++;
					
				}
			}
			
			System.out.println("--- 신규/폐지 제외 총 "+readCnt+" 전문 매핑 현황 READ완료");
			
			/* 실제 파일명으로 변환 */
			File [] files = new File(filePath).listFiles();
			int exCnt = 0;
			
			/* 요청 */
			for ( String txCode : this.TX_MAP_REQ.keySet() ) {
				
				String [] name = this.TX_MAP_REQ.get(txCode).split("\\_");
				
				for ( File f : files ) {
					if ( f.getName().contains(name[0].replace(".", "_"))) {
						if ( f.getName().contains(name[0].replace(".", "_"))) {
							if ( f.getName().contains("CLS") || f.getName().contains("LINKED") || f.getName().contains("Intra")) {
								if ( name[1].endsWith("2") ) {
//									System.out.println("요청_" +  txCode + "_" + name[0] + ":" + name[1] + " - " + f.getName());
									this.TX_MAP_REQ.put(txCode, f.getName());
									exCnt++;
									break;
								}
							} else if ( name[1].endsWith("1") ) {
//								System.out.println("요청_" + txCode + "_" + name[0] + ":" + name[1] + " - " + f.getName());
								this.TX_MAP_REQ.put(txCode, f.getName());
								exCnt++;
								break;
							}
						}
					}
				}
			}
			
			System.out.println("--- 실물 파일명으로 변환 완료 (요청) " + exCnt);
			exCnt = 0;
			
			/* 응답 */
			for ( String txCode : this.TX_MAP_RES.keySet() ) {
				
				String [] name = this.TX_MAP_RES.get(txCode).split("\\_");
				
				for ( File f : files ) {
					if ( f.getName().contains(name[0].replace(".", "_"))) {
						if ( f.getName().contains("CLS") || f.getName().contains("LINKED") || f.getName().contains("Intra")) {
							if ( name[1].endsWith("2") ) {
//								System.out.println("응답_" +  txCode + "_" + name[0] + ":" + name[1] + " - " + f.getName());
								this.TX_MAP_RES.put(txCode, f.getName());
								exCnt++;
								break;
							}
						} else if ( name[1].endsWith("1") ) {
//							System.out.println("응답_" + txCode + "_" + name[0] + ":" + name[1] + " - " + f.getName());
							this.TX_MAP_RES.put(txCode, f.getName());
							exCnt++;
							break;
						}
					}
				}
			}
			
			System.out.println("--- 실물 파일명으로 변환 완료 (응답) " + exCnt);

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
	
	public Set<String> keySet() {
		return this.TX_MAP_REQ.keySet();
	}
	
	public String getReqFileName(String TX_CODE) {
		return this.TX_MAP_REQ.get(TX_CODE);
	}
	
	public String getResFileName(String TX_CODE) {
		return this.TX_MAP_RES.get(TX_CODE);
	}
	
	public String getTXName(String TX_CODE) {
		return this.TX_MAP_NAME.get(TX_CODE);
	}
	
	public static void main(String [] args) {
		CompareUGMappingFileMap map = new CompareUGMappingFileMap("src/main/resources/static/input/20231004","mapping_20230830 ver.2.xlsx");
		for ( String key : map.TX_MAP_REQ.keySet() ) { 
			System.out.println(key + "/" + map.getReqFileName(key));
		}
		
		for ( String key : map.TX_MAP_RES.keySet() ) { 
			System.out.println(key + "/" + map.getResFileName(key));
		}
	}

}
