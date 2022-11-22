package com.mshoppers.admin.user.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mshoppers.admin.AbstractExporter;
import com.mshoppers.common.entity.User;

public class UserExcelExporter extends AbstractExporter {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserCsvExporter.class);

	private HSSFWorkbook workbook;
	private HSSFSheet sheet;
	private String headerValuesString; // This is a property value

	public UserExcelExporter(String headerValuesString) {
		workbook = new HSSFWorkbook();
		sheet = workbook.createSheet("Users of Mumbai Shoppers");
		this.headerValuesString = headerValuesString;
	}

	public void export(List<User> users, HttpServletResponse response) throws IOException {
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			// Calling parent class AbstractExporter method to set headers for file download
			super.setResponseHeaderForDownload(response, "application/octet-stream", ".xls", "Mshoppers_USERS_Export_");

			// Create the Header
			createHeader();
			createDataLines(users);

			// Write the excel

			this.workbook.write(outputStream);
			this.workbook.close();

		} catch (IOException e) {
			LOGGER.error("ERROR occurred ::: " + e);
			e.printStackTrace();
		} finally {
			this.workbook.close();
		}

	}

	// ********* HELPER FUNCTIONS STARTS **********

	private void createHeader() {
		HSSFRow row = sheet.createRow(0);

		// Set Cell Style starts
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontName("Arial Rounded MT Bold");
		font.setFontHeightInPoints((short) 18);
		cellStyle.setFont(font);
		// Set Cell Style ends

		// Create the cell for the headers
		String[] headerValues = this.headerValuesString.split(",");
		for (int i = 0; i < headerValues.length; i++) {
			createCell(row, i, headerValues[i], cellStyle);
		}

	}

	private void createCell(HSSFRow row, int columnIndex, Object value, HSSFCellStyle cellStyle) {
		HSSFCell cell = row.createCell(columnIndex);
		sheet.autoSizeColumn(columnIndex);

		// set each cell value type -- which can be boolean, integer or string etc
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else {
			cell.setCellValue((String) value);
		}

		cell.setCellStyle(cellStyle);
	}

	private void createDataLines(List<User> users) {
		int rowIndex = 1;

		// Set Cell Style starts
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 12);
		cellStyle.setFont(font);
		// Set Cell Style ends

		// Create Each line in excel
		for (User user : users) {
			HSSFRow row = sheet.createRow(rowIndex++);
			int columnIndex = 0;

			createCell(row, columnIndex++, user.getId(), cellStyle);
			createCell(row, columnIndex++, user.getFirstName(), cellStyle);
			createCell(row, columnIndex++, user.getLastName(), cellStyle);
			createCell(row, columnIndex++, user.getEmail(), cellStyle);
			createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
			createCell(row, columnIndex++, user.isEnabled(), cellStyle);

		}
	}

	// ********* HELPER FUNCTIONS ENDS **********

}
