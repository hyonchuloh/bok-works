package com.bok.iso.util.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CompareUGMappingMain {
	
	public static final String BASE_DIR = "src/main/resources/static";
	public static final String ORIGINAL_FILE_PATH = BASE_DIR + "/input/20231004/(붙임)한은금융망 서버접속 전문설명서_v1.3.7_표준전문개발반송부용_매핑포함.xlsx";
	public static final String FILEMAP_FILE_PATH = BASE_DIR + "/input/20231004";
	public static final String FILEMAP_FILE_NAME = "mapping_20230830 ver.2.xlsx";
	public static final String OUTPUT_FILE_PATH = BASE_DIR + "/output/20231004";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		FileOutputStream fos = null;
		
		try {
			
			/* 데이터 초기화 */
			CompareUGMappingFileMap fileMap = new CompareUGMappingFileMap(FILEMAP_FILE_PATH, FILEMAP_FILE_NAME);
			
			/* 1번 시트 생성 */
			XSSFSheet titleHeaderSheet = workbook.createSheet("목차");
			titleHeaderSheet.setColumnWidth(0, 4000);
			titleHeaderSheet.setColumnWidth(1, 6000);
			
			XSSFRow titleHeaderRow = null;
			int headerRowIndex = 0;
			XSSFCell titleHeaderCell = null;
			
			titleHeaderRow = titleHeaderSheet.createRow(headerRowIndex++);
			titleHeaderCell = titleHeaderRow.createCell(0); titleHeaderCell.setCellValue("거래구분코드"); 	titleHeaderCell.setCellStyle(CompareUGMappingUtil.getTitleStyle(workbook));
			titleHeaderCell = titleHeaderRow.createCell(1); titleHeaderCell.setCellValue("UG파일명"); 		titleHeaderCell.setCellStyle(CompareUGMappingUtil.getTitleStyle(workbook));
			CreationHelper createHelper = workbook.getCreationHelper();
			Hyperlink hyperlinkTx, hyperlinkHome = null;

			for ( String txCode : fileMap.keySet() ) {
				
				/* 요청부 */
				String ugFileName = fileMap.getReqFileName(txCode);
				System.out.println("--- 진행중 ["+txCode+"_요청]["+ugFileName+"]");
				
				CompareUGMappingOriMap originalMap = new CompareUGMappingOriMap(ORIGINAL_FILE_PATH, txCode, true);
				CompareUGMappingUGMap ugMap = new CompareUGMappingUGMap(FILEMAP_FILE_PATH + "/" + ugFileName, txCode);
				
				titleHeaderRow = titleHeaderSheet.createRow(headerRowIndex++);
				
				titleHeaderCell = titleHeaderRow.createCell(0); 
								  titleHeaderCell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
								  titleHeaderCell.setCellValue(txCode);
								  hyperlinkTx = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
								  hyperlinkTx.setAddress("'"+txCode+"_요청'!A1");
								  titleHeaderCell.setHyperlink(hyperlinkTx);
				titleHeaderCell = titleHeaderRow.createCell(1);
								  titleHeaderCell.setCellValue(ugFileName); 
								  titleHeaderCell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
				
				/* (반복부) 거래별 시트 생성 */
				sheet = workbook.createSheet(txCode+"_요청");
				sheet.setColumnWidth(0, 4000);
				sheet.setColumnWidth(1, 6000);
				sheet.setColumnWidth(3, 10000);
				sheet.setColumnWidth(5, 10000);
				
				/* (반복부) 거래별 시트 내 제목 행 */
				row = sheet.createRow(0);
				cell = row.createCell(0);	cell.setCellValue(txCode+"_요청");		cell.setCellStyle(CompareUGMappingUtil.getTitleStyle(workbook));
											hyperlinkHome = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
											hyperlinkHome.setAddress("'목차'!A1");
											cell.setHyperlink(hyperlinkHome);
				cell = row.createCell(1);	cell.setCellValue(ugFileName);	cell.setCellStyle(CompareUGMappingUtil.getTitleStyle(workbook));
				
				/* (반복부) 거래별 시트 내 테이블 헤드 행 (고정값) */
				row = sheet.createRow(1);
				cell = row.createCell(0);	cell.setCellValue("순서");			cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(1);	cell.setCellValue("한은망전문항목");cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(2);	cell.setCellValue("필수");			cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(3);	cell.setCellValue("UG_매핑현황");	cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(4);	cell.setCellValue("UG_MinMand");	cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(5);	cell.setCellValue("UG매핑패스");	cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				
				int colIndex = 1, rowIndex=2, excelColIndex=0, loopCnt = 0;
				for ( String key : originalMap.keySet() ) {
					
					/* (반복부의 반복부) 거래별 시트 내 본문 행 */
					if ( loopCnt == 0 ) {
						row = sheet.createRow(rowIndex++);
					}
					loopCnt = 0;
					excelColIndex = 0;
					cell = row.createCell(excelColIndex++);	cell.setCellValue(colIndex);			cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++);	cell.setCellValue(key);					cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++);	cell.setCellValue(originalMap.get(key));	cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					
					int startCellIndex = 0;

					if ( key.indexOf("(") > 0)
						key = key.substring(0, key.indexOf("("));
					
					for ( String path : ugMap.pathMapKeySet() ) {
						
						startCellIndex = excelColIndex;
						
						if ( path.contains(colIndex+key) ) {
							
							String b = path.substring(path.indexOf(colIndex+key) + (colIndex+key).length());
							if (  !CompareUGMappingUtil.isNumeric(b.substring(0,1)) )
								continue;
							
							cell = row.createCell(startCellIndex++);	cell.setCellValue(path);	cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
							String temp = ugMap.getPathMap(path);
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
				
				for ( String path : ugMap.pathMapKeySet() ) {
					row = sheet.createRow(rowIndex++);
					excelColIndex = 3;
					String temp = ugMap.getPathMap(path);
					cell = row.createCell(excelColIndex++);	cell.setCellValue(path);									cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++);	cell.setCellValue(temp.substring(0, temp.indexOf(";")));	cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++);	cell.setCellValue(temp.substring(temp.indexOf(";")+1));		cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++); cell.setCellValue(ugMap.getAddInfoMap(path));					cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
				}
				
				
				
				
				
				/* 응답부 */
				ugFileName = fileMap.getResFileName(txCode);
				System.out.println("--- 진행중 ["+txCode+"_응답]["+ugFileName+"]");
				
				originalMap = new CompareUGMappingOriMap(ORIGINAL_FILE_PATH, txCode, false);
				ugMap = new CompareUGMappingUGMap(FILEMAP_FILE_PATH + "/" + ugFileName, txCode);
				
				//titleHeaderRow = titleHeaderSheet.createRow(headerRowIndex++);
				
				titleHeaderCell = titleHeaderRow.createCell(2); 
								  titleHeaderCell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
								  titleHeaderCell.setCellValue(txCode);
								  hyperlinkTx = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
								  hyperlinkTx.setAddress("'"+txCode+"_응답'!A1");
								  titleHeaderCell.setHyperlink(hyperlinkTx);
				titleHeaderCell = titleHeaderRow.createCell(3);
								  titleHeaderCell.setCellValue(ugFileName); 
								  titleHeaderCell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
				
				/* (반복부) 거래별 시트 생성 */
				sheet = workbook.createSheet(txCode+"_응답");
				sheet.setColumnWidth(0, 4000);
				sheet.setColumnWidth(1, 6000);
				sheet.setColumnWidth(3, 10000);
				sheet.setColumnWidth(5, 10000);
				
				/* (반복부) 거래별 시트 내 제목 행 */
				row = sheet.createRow(0); 
				cell = row.createCell(0);	cell.setCellValue(txCode+"_응답");		cell.setCellStyle(CompareUGMappingUtil.getTitleStyle(workbook));
											hyperlinkHome = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
											hyperlinkHome.setAddress("'목차'!A1");
											cell.setHyperlink(hyperlinkHome);
				cell = row.createCell(1);	cell.setCellValue(ugFileName);	cell.setCellStyle(CompareUGMappingUtil.getTitleStyle(workbook));
				
				/* (반복부) 거래별 시트 내 테이블 헤드 행 (고정값) */
				row = sheet.createRow(1);
				cell = row.createCell(0);	cell.setCellValue("순서");			cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(1);	cell.setCellValue("한은망전문항목");cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(2);	cell.setCellValue("필수");			cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(3);	cell.setCellValue("UG_매핑현황");	cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(4);	cell.setCellValue("UG_MinMand");	cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				cell = row.createCell(5);	cell.setCellValue("UG매핑패스");	cell.setCellStyle(CompareUGMappingUtil.getHeadStyle(workbook));
				
				colIndex = 1; rowIndex=2; excelColIndex=0; loopCnt = 0;
				for ( String key : originalMap.keySet() ) {
					
					/* (반복부의 반복부) 거래별 시트 내 본문 행 */
					if ( loopCnt == 0 ) {
						row = sheet.createRow(rowIndex++);
					}
					loopCnt = 0;
					excelColIndex = 0;
					cell = row.createCell(excelColIndex++);	cell.setCellValue(colIndex);			cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++);	cell.setCellValue(key);					cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++);	cell.setCellValue(originalMap.get(key));	cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					
					int startCellIndex = 0;

					if ( key.indexOf("(") > 0)
						key = key.substring(0, key.indexOf("("));
					
					for ( String path : ugMap.pathMapKeySet() ) {
						
						startCellIndex = excelColIndex;
						
						if ( path.contains(colIndex+key) ) {
							
							String b = path.substring(path.indexOf(colIndex+key) + (colIndex+key).length());
							if (  !CompareUGMappingUtil.isNumeric(b.substring(0,1)) )
								continue;
							
							cell = row.createCell(startCellIndex++);	cell.setCellValue(path);	cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
							String temp = ugMap.getPathMap(path);
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
				
				for ( String path : ugMap.pathMapKeySet() ) {
					row = sheet.createRow(rowIndex++);
					excelColIndex = 3;
					String temp = ugMap.getPathMap(path);
					cell = row.createCell(excelColIndex++);	cell.setCellValue(path);									cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++);	cell.setCellValue(temp.substring(0, temp.indexOf(";")));	cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++);	cell.setCellValue(temp.substring(temp.indexOf(";")+1));		cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
					cell = row.createCell(excelColIndex++); cell.setCellValue(ugMap.getAddInfoMap(path));					cell.setCellStyle(CompareUGMappingUtil.getCommStyle(workbook));
				}
				
			}
			
			/* 작성된 결과물을 엑셀파일에 저장한다 */
			File file = new File(OUTPUT_FILE_PATH + "/BOK_Phase1_CorePayment_v_1_2_매핑현황_" + (new Date().getTime()) +".xlsx");
			fos = new FileOutputStream(file);
			workbook.write(fos);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( fos!= null ) fos.close();
		}
	}

}
