package com.bok.iso.util.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReSortUsageGuideMain {
	
	static final String BASE_DIR = "src/main/resources/static";
	static final String UG_PATH = BASE_DIR + "/input/20231004/";
	static final String OUTPUT_FILE_PATH = BASE_DIR + "/output/20231004";
	

	public static void main(String [] args) throws IOException {
		
		String outputFilePath = OUTPUT_FILE_PATH + "/BOK_Phase1_CorePayment_v_1_2_FullView_통합본_" + (new SimpleDateFormat("yyyyMMddHmm").format(new Date())) +".xlsx";
		
		FileOutputStream fos = null;
		XSSFWorkbook outputWorkbook = new XSSFWorkbook();
		XSSFSheet outputSheet = null;
		
		/* 1번 시트 생성 */
		XSSFSheet titleHeaderSheet = outputWorkbook.createSheet("목차");
		titleHeaderSheet.setColumnWidth(0, 4000);
		titleHeaderSheet.setColumnWidth(1, 6000);
		
		XSSFRow titleHeaderRow = null;
		int headerRowIndex = 0;
		XSSFCell titleHeaderCell = null;
		
		titleHeaderRow = titleHeaderSheet.createRow(headerRowIndex++);
		titleHeaderCell = titleHeaderRow.createCell(0); titleHeaderCell.setCellValue("거래구분코드"); 	titleHeaderCell.setCellStyle(CompareUGMappingUtil.getTitleStyle(outputWorkbook));
		titleHeaderCell = titleHeaderRow.createCell(1); titleHeaderCell.setCellValue("UG파일명"); 		titleHeaderCell.setCellStyle(CompareUGMappingUtil.getTitleStyle(outputWorkbook));
		CreationHelper createHelper = outputWorkbook.getCreationHelper();
		Hyperlink hyperlinkTx, hyperlinkHome = null;
		
		
		try {
			
			/* 파일데이터 수집*/
			Map<String, String> fileMap = getFileList(UG_PATH);
			
			for ( String msgCd : fileMap.keySet() ) {
				
				titleHeaderRow = titleHeaderSheet.createRow(headerRowIndex++);
				
				titleHeaderCell = titleHeaderRow.createCell(0); 
								  titleHeaderCell.setCellStyle(CompareUGMappingUtil.getCommStyle(outputWorkbook));
								  titleHeaderCell.setCellValue(msgCd);
								  hyperlinkTx = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
								  hyperlinkTx.setAddress("'"+msgCd+"'!A1");
								  titleHeaderCell.setHyperlink(hyperlinkTx);
				titleHeaderCell = titleHeaderRow.createCell(1);
								  titleHeaderCell.setCellValue(fileMap.get(msgCd)); 
								  titleHeaderCell.setCellStyle(CompareUGMappingUtil.getCommStyle(outputWorkbook));
				
				outputSheet = outputWorkbook.createSheet(msgCd);
				System.out.println(msgCd);
				
				/* (반복부) 실제 UG 값을 가져온다 */
				OPCPackage ugPkg = OPCPackage.open(new File(UG_PATH + fileMap.get(msgCd)));
				XSSFWorkbook UGworkbook = new XSSFWorkbook(ugPkg);
				XSSFSheet FullViewsheet = UGworkbook.getSheet("Full_View");
				
				Util.copySheets(outputSheet, FullViewsheet,true);
				
				XSSFRow firstRow = outputSheet.getRow(0);
				XSSFCell firstCell = firstRow.getCell(0);
				hyperlinkHome = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
				hyperlinkHome.setAddress("'목차'!A1");
				firstCell.setHyperlink(hyperlinkHome);
				
				
			}
			
			/* 작성된 결과물을 엑셀파일에 저장한다*/
			fos = new FileOutputStream(new File(outputFilePath));
			outputWorkbook.write(fos); 
			fos.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( fos!= null ) {
				fos.close();
			}
		}
	}
	
	
	/**
	 * UG 파일들을 전문이름(8):파일명 으로 수집
	 * @param path
	 * @return
	 */
	public static Map<String, String> getFileList(String path) {
		
		Map<String, String> retValue = new TreeMap<String, String>();
		
		File [] files = new File(path).listFiles();
		
		String key;
		
		for ( File f : files ) {
			if ( !f.getName().startsWith("BOK") )
				continue;
			if ( !f.getName().endsWith("xlsx"))
				continue;
			// BOK_Phase1_CorePayment_v_1_2_BOK_acmt_023_001_03_IdentificationVerificationRequest_20231004_1307.xlsx
			
			String [] words = f.getName().split("_");
			
			key = words[7] + "." + words[8];
			if ( !words[12].startsWith("20") ) {
				key += "_" + words[12];
			}
			retValue.put(key, f.getName());
		}
		
		
		return retValue;
	}
}
