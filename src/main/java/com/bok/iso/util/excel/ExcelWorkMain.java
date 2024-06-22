package com.bok.iso.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelWorkMain {

	public static String TX_CODE[] = {"BKS20F030","BKS20F040","BKS10F060","BKS10E070","BKS20E090","BKS20E110","BKS10E150","BKS20E190","BKS10A011","BKS20A020","BKS20A030","BKS20A040","BKS10B011","BKS20B020","BKS20B030","BKS20B040","BKS20B050","BKS20B060","BKS20B070","BKS20B080","BKS10B021","BKS10B031","BKS20B360","BKS20B370","BKS10B091","BKS20B100","BKS20B110","BKS20B120","BKS20B130","BKS20B140","BKS10B081","BKS20B150","BKS20B160","BKS20B170","BKS10E300","BKS20E300","BKS10E310","BKS10E060","BKS20E130","BKS10B170","BKS20B180","BKS20B190","BKS20B200","BKS20E220","BKS10E120","BKF101011","BKS20G010","BKS20G020","BKF101021","BKS20G210","BKS20G220"};
	public static Map<String, String> COL_MAP = null;
	public static Map<String, String> PATH_MAP = null;
	public static Map<String, String> ADDINFO_MAP = null;

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		XSSFWorkbook workbook = null;
		XSSFSheet sheet = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		FileOutputStream fos = null;
		
		try {
			
			workbook = new XSSFWorkbook();
			
			/* 1번 시트 생성 */
			XSSFSheet 목차_headerSheet = workbook.createSheet("목차");
			목차_headerSheet.setColumnWidth(0, 4000);
			목차_headerSheet.setColumnWidth(1, 6000);
			
			XSSFRow 목차_headerRow = null;
			int headerRowIndex = 0;
			XSSFCell 목차_headerCell = null;
			
			목차_headerRow = 목차_headerSheet.createRow(headerRowIndex++);
			목차_headerCell = 목차_headerRow.createCell(0); 목차_headerCell.setCellValue("거래구분코드"); 	목차_headerCell.setCellStyle(CompareUGMappingUtil.getTitleStyle(workbook));
			목차_headerCell = 목차_headerRow.createCell(1); 목차_headerCell.setCellValue("UG파일명"); 		목차_headerCell.setCellStyle(CompareUGMappingUtil.getTitleStyle(workbook));
			CreationHelper createHelper = workbook.getCreationHelper();
			Hyperlink 목차_link = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
			
			
			for ( String txCode : TX_CODE ) {
				
				COL_MAP = new LinkedHashMap<String, String>();
				PATH_MAP = new TreeMap<String, String>();	
				ADDINFO_MAP = new TreeMap<String, String>();
				
				String fileName = getFileName(txCode);
				목차_headerRow = 목차_headerSheet.createRow(headerRowIndex++);
				
				목차_headerCell = 목차_headerRow.createCell(0); 
								  목차_headerCell.setCellValue(txCode); //headerCell.setCellStyle(commStyle);
								  목차_link.setAddress("'"+txCode+"'!A1");		
								  목차_headerCell.setHyperlink(목차_link);
				목차_headerCell = 목차_headerRow.createCell(1); 
								  목차_headerCell.setCellValue(fileName); 
								  목차_headerCell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
				
				getPath(fileName, txCode);
				
				/* (반복부) 거래별 시트 생성 */
				sheet = workbook.createSheet(txCode);
				sheet.setColumnWidth(0, 4000);
				sheet.setColumnWidth(1, 6000);
				sheet.setColumnWidth(3, 10000);
				sheet.setColumnWidth(5, 10000);
				
				/* (반복부) 거래별 시트 내 제목 행 */
				row = sheet.createRow(0);
				cell = row.createCell(0);	cell.setCellValue(txCode);		cell.setCellStyle(CompareUGMappingUtil.getTitleStyle(workbook));
				cell = row.createCell(1);	cell.setCellValue(fileName);	cell.setCellStyle(CompareUGMappingUtil.getTitleStyle(workbook));
				
				/* (반복부) 거래별 시트 내 테이블 헤드 행 (고정값) */
				row = sheet.createRow(1);
				cell = row.createCell(0);	cell.setCellValue("순서");				cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(1);	cell.setCellValue("한은망전문항목");	cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(2);	cell.setCellValue("필수");				cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(3);	cell.setCellValue("UG_매핑현황");		cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(4);	cell.setCellValue("UG_MinMand");		cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(5);	cell.setCellValue("UG매핑패스");		cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				
				int colIndex = 1, rowIndex=2, excelColIndex=0, loopCnt = 0;
				for ( String key : COL_MAP.keySet() ) {
					
					/* (반복부의 반복부) 거래별 시트 내 본문 행 */
					if ( loopCnt == 0 ) {		// 
						row = sheet.createRow(rowIndex++);
					}
					loopCnt = 0;
					excelColIndex = 0;
					cell = row.createCell(excelColIndex++);	cell.setCellValue(colIndex);			cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++);	cell.setCellValue(key);					cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++);	cell.setCellValue(COL_MAP.get(key));	cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					
					int startCellIndex = 0;
					

					if ( key.indexOf("(") > 0)
						key = key.substring(0, key.indexOf("("));
					
					for ( String path : PATH_MAP.keySet() ) {
						
						startCellIndex = excelColIndex;
						
						if ( path.contains(colIndex+key) ) {
							
							String b = path.substring(path.indexOf(colIndex+key) + (colIndex+key).length());
							
							if (  !CompareUGMappingUtil.isNumeric(b.substring(0,1)) )
								continue;
							
							cell = row.createCell(startCellIndex++);	cell.setCellValue(path);	cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
							String temp = PATH_MAP.get(path);
							String mm = temp.substring(0, temp.indexOf(";"));
							cell = row.createCell(startCellIndex++);	cell.setCellValue(mm);		cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
							cell = row.createCell(startCellIndex++);	cell.setCellValue(temp.substring(temp.indexOf(";")+1));	cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
							
							row = sheet.createRow(rowIndex++);	loopCnt++;
						}
						
					}
					colIndex++;
				}
				
				/* (반복부) 매핑 현황 조사 완료후 실제 UG 참조값을 그대로 옮겨둔다 */
				row = sheet.createRow(rowIndex++);
				row = sheet.createRow(rowIndex++);
				cell = row.createCell(0);	cell.setCellValue("<참고> UG에서 조사된 값의 목록(전체)");	cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(3);	cell.setCellValue("Annotation Field No");					cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(4);	cell.setCellValue("Min Mand");								cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(5);	cell.setCellValue("PATH");									cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(6);	cell.setCellValue("Annotation AddtionalInfomation");		cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				
				for ( String path : PATH_MAP.keySet() ) {
					row = sheet.createRow(rowIndex++);
					excelColIndex = 3;
					String temp = PATH_MAP.get(path);
					cell = row.createCell(excelColIndex++);	cell.setCellValue(path);									cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++);	cell.setCellValue(temp.substring(0, temp.indexOf(";")));	cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++);	cell.setCellValue(temp.substring(temp.indexOf(";")+1));		cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++); cell.setCellValue(ADDINFO_MAP.get(path));					cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
				}
			}
			
			/* 작성된 결과물을 엑셀파일에 저장한다 */
			File file = new File("files/BOK_Phase1_CorePayment_BOK_매핑현황(20230808))_"+(new Date().getTime())+".xlsx");
			fos = new FileOutputStream(file);
			workbook.write(fos);
			
		} catch ( Exception e ) {
			System.err.println(e.getMessage());
		} finally {
			if ( fos!= null ) fos.close();
		}
	}

	public static void getPath(String fileName, String txCode) {
		
		int minMandIndex = 0;
		int pathIndex = 0;
		FileInputStream file = null;
		try {
			// 경로에 있는 파일을 읽
			file = new FileInputStream("files/" + fileName);
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
			System.err.println(e.getMessage());
		} finally {
			try {
				if (file != null)
					file.close();
			} catch (Exception e) {
				//
			}
		}
	}

	/**
	 * 주어진 TX_CODE에 매핑되는 파일을 가져옵니다.
	 * @param txCode
	 * @return
	 */
	public static String getFileName(String txCode) {
		FileInputStream file = null;
		String retValue = "";
		try {
			// 경로에 있는 파일을 읽
			file = new FileInputStream("files/(붙임)한은금융망 서버접속 전문설명서_v1.3.7_표준전문개발반송부용_매핑포함.xlsx");
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			int rowNo = 0;
			XSSFSheet sheet = workbook.getSheet("목록");
			int rows = 83;
			for (rowNo = 0; rowNo < rows; rowNo++) {
				XSSFRow row = sheet.getRow(rowNo);
				if (row != null) {
					XSSFCell cell1 = row.getCell(4); // 셀의 값을 가져온다
					XSSFCell cell2 = row.getCell(10); // 셀의 값을 가져온다
					if (cell1 == null || cell2 == null)
						continue;
					if (CompareUGMappingUtil.getCellValue(cell1).equals(txCode))
						retValue = CompareUGMappingUtil.getCellValue(cell2);
				}
			}

			XSSFSheet sheetTxCode = workbook.getSheet(txCode);
			rows = sheetTxCode.getPhysicalNumberOfRows();
			for (rowNo = 0; rowNo < rows; rowNo++) {
				XSSFRow row = sheetTxCode.getRow(rowNo);
				if (row != null) {
					XSSFCell cell1 = row.getCell(1); // 셀의 값을 가져온다
					XSSFCell cell2 = row.getCell(6); // 셀의 값을 가져온다
					if (cell1 == null || cell2 == null)
						continue;
					String colName = cell1.toString().trim();
					if (colName.length() == 0)
						continue;
					if (colName.equals("항목") || colName.equals("공통부"))
						continue;
					COL_MAP.put(CompareUGMappingUtil.getOnlyHangle(colName), cell2.toString());
				}
			}

		} catch (Exception e) {
			retValue = e.getMessage();
		} finally {
			try {
				if (file != null)
					file.close();
			} catch (Exception e) {
				//
			}
		}
		return retValue;
	}

	
	
}