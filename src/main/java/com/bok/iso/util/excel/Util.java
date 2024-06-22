package com.bok.iso.util.excel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;


public final class Util {

	/**
	 * @param newSheet the sheet to create from the copy.
	 * @param sheet    the sheet to copy.
	 */
	public static void copySheets(Sheet newSheet, Sheet sheet) {
		copySheets(newSheet, sheet, true);
	}

	/**
	 * @param newSheet  the sheet to create from the copy.
	 * @param sheet     the sheet to copy.
	 * @param copyStyle true copy the style.
	 */

	public static void copySheets(Sheet newSheet, Sheet sheet, boolean copyStyle) {
		int maxColumnNum = 0;
		Map<Integer, CellStyle> styleMap = (copyStyle) ? new HashMap<Integer, CellStyle>() : null;
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			Row srcRow = sheet.getRow(i);
			Row destRow = newSheet.createRow(i);
			if (srcRow != null) {
				Util.copyRow(sheet, newSheet, srcRow, destRow, styleMap);
				if (srcRow.getLastCellNum() > maxColumnNum) {
					maxColumnNum = srcRow.getLastCellNum();
				}
			}
		}

		for (int i = 0; i <= maxColumnNum; i++) {
			newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
		}

	}

	/**
	 * @param srcSheet  the sheet to copy.
	 * @param destSheet the sheet to create.
	 * 
	 * @param srcRow    the row to copy.
	 * 
	 * @param destRow   the row to create.
	 * 
	 * @param styleMap  -
	 * 
	 */

	public static void copyRow(Sheet srcSheet, Sheet destSheet, Row srcRow, Row destRow,
			Map<Integer, CellStyle> styleMap) {
		// manage a list of merged zone in order to not insert two times a merged zone
		Set<CellRangeAddressWrapper> mergedRegions = new TreeSet<CellRangeAddressWrapper>();
		destRow.setHeight(srcRow.getHeight());
		// pour chaque row
		for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
			Cell oldCell = srcRow.getCell(j); // ancienne cell
			Cell newCell = destRow.getCell(j); // new cell
			if (oldCell != null) {
				if (newCell == null) {
					newCell = destRow.createCell(j);
				}

				// copy chaque cell
				copyCell(oldCell, newCell, styleMap);
				// copy les informations de fusion entre les cellules
				// System.out.println("row num: " + srcRow.getRowNum() + " , col: " +
				// (short)oldCell.getColumnIndex());
				CellRangeAddress mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(),
						(short) oldCell.getColumnIndex());

				if (mergedRegion != null) {
					// System.out.println("Selected merged region: " + mergedRegion.toString());
					CellRangeAddress newMergedRegion = new CellRangeAddress(mergedRegion.getFirstRow(),
							mergedRegion.getLastRow(), mergedRegion.getFirstColumn(), mergedRegion.getLastColumn());
					// System.out.println("New merged region: " + newMergedRegion.toString());
					CellRangeAddressWrapper wrapper = new CellRangeAddressWrapper(newMergedRegion);
					if (isNewMergedRegion(wrapper, mergedRegions)) {
						for (int i = 0; i < destSheet.getNumMergedRegions(); ++i)
						{
							CellRangeAddress range = destSheet.getMergedRegion(i);
							if (range.equals(newMergedRegion)) {
								destSheet.removeMergedRegion(i);
							}
						}
						mergedRegions.add(wrapper);
						destSheet.addMergedRegion(wrapper.range);
					}
				}
			}
		}
	}

	
	/**
	 * 
	 * @param oldCell
	 * 
	 * @param newCell
	 * 
	 * @param styleMap
	 * 
	 */

	public static void copyCell(Cell oldCell, Cell newCell, Map<Integer, CellStyle> styleMap) {
		if (styleMap != null) {
			if (oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()) {
				newCell.setCellStyle(oldCell.getCellStyle());
			} else {
				int stHashCode = oldCell.getCellStyle().hashCode();
				CellStyle newCellStyle = styleMap.get(stHashCode);
				if (newCellStyle == null) {
					newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
					newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
					styleMap.put(stHashCode, newCellStyle);
				}
				newCell.setCellStyle(newCellStyle);
			}
		}
//		if (oldCell.getCellTypeEnum() == CellType.BLANK) {
//
//			newCell.setCellValue(oldCell.getStringCellValue());
//
//		} else if (oldCell.getCellTypeEnum() == CellType.BOOLEAN) {
//
//			newCell.setCellValue(oldCell.getBooleanCellValue());
//
//		} else if (oldCell.getCellTypeEnum() == CellType.ERROR) {
//
//			newCell.setCellErrorValue(oldCell.getErrorCellValue());
//
//		} else if (oldCell.getCellTypeEnum() == CellType.FORMULA) {
//
//			newCell.setCellFormula(oldCell.getCellFormula());
//
//		} else if (oldCell.getCellTypeEnum() == CellType.NUMERIC) {
//
//			newCell.setCellValue(oldCell.getNumericCellValue());
//
//		} else if (oldCell.getCellTypeEnum() == CellType.STRING) {
//
//			newCell.setCellValue(oldCell.getRichStringCellValue());
//
//		}

// 위 셀타입 방법은 poi-3.17.jar 버전 4.0부터는 아래 방법으로.

		switch (oldCell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			newCell.setCellValue(oldCell.getStringCellValue());
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			newCell.setCellValue(oldCell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			newCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			newCell.setCellValue(oldCell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			newCell.setCellErrorValue(oldCell.getErrorCellValue());
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			newCell.setCellFormula(oldCell.getCellFormula());
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * Récupère les informations de fusion des cellules dans la sheet source pour
	 * les appliquer
	 * 
	 * à la sheet destination...
	 * 
	 * Récupère toutes les zones merged dans la sheet source et regarde pour chacune
	 * d'elle si
	 * 
	 * elle se trouve dans la current row que nous traitons.
	 * 
	 * Si oui, retourne l'objet CellRangeAddress.
	 * 
	 * 
	 * 
	 * @param sheet   the sheet containing the data.
	 * 
	 * @param rowNum  the num of the row to copy.
	 * 
	 * @param cellNum the num of the cell to copy.
	 * 
	 * @return the CellRangeAddress created.
	 * 
	 */

	public static CellRangeAddress getMergedRegion(Sheet sheet, int rowNum, short cellNum) {
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress merged = sheet.getMergedRegion(i);
			if (merged.isInRange(rowNum, cellNum)) {
				return merged;
			}
		}
		return null;
	}

	/**
	 * 
	 * Check that the merged region has been created in the destination sheet.
	 * 
	 * @param newMergedRegion the merged region to copy or not in the destination
	 *                        sheet.
	 * 
	 * @param mergedRegions   the list containing all the merged region.
	 * 
	 * @return true if the merged region is already in the list or not.
	 * 
	 */

	private static boolean isNewMergedRegion(CellRangeAddressWrapper newMergedRegion,
			Set<CellRangeAddressWrapper> mergedRegions) {
		return !mergedRegions.contains(newMergedRegion);
	}

	public static class CellRangeAddressWrapper implements Comparable<CellRangeAddressWrapper> {

		public CellRangeAddress range;

		/**
		 * 
		 * @param theRange the CellRangeAddress object to wrap.
		 * 
		 */

		public CellRangeAddressWrapper(CellRangeAddress theRange) {
			this.range = theRange;
		}

		/**
		 * 
		 * @param o the object to compare.
		 * 
		 * @return -1 the current instance is prior to the object in parameter, 0:
		 *         equal, 1: after...
		 * 
		 */

		public int compareTo(CellRangeAddressWrapper o) {
			if (range.getFirstColumn() < o.range.getFirstColumn()
					&& range.getFirstRow() < o.range.getFirstRow()) {
				return -1;
			} else if (range.getFirstColumn() == o.range.getFirstColumn()
					&& range.getFirstRow() == o.range.getFirstRow()) {
				return 0;
			} else {
				return 1;
			}
		}
	}
}
