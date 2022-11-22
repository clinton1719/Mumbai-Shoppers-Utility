package com.mshoppers.admin.user.export;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mshoppers.admin.AbstractExporter;
import com.mshoppers.common.entity.User;

public class UserPDFExporter extends AbstractExporter {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserCsvExporter.class);

	String headerValuesString;

	public UserPDFExporter(String headerValues) {
		this.headerValuesString = headerValues;
	}

	public void export(List<User> users, HttpServletResponse response) throws IOException {
		try (Document document = new Document(PageSize.A4)) {
			super.setResponseHeaderForDownload(response, "application/pdf", ".pdf", "Mshoppers_USERS_Export_");

			PdfWriter.getInstance(document, response.getOutputStream());

			document.open();

			// Set styles starts
			Font font = FontFactory.getFont(FontFactory.TIMES_BOLD);
			font.setSize(18);
			font.setColor(Color.BLUE.darker());
			// Set styles ends

			// Header
			Paragraph paragraph = new Paragraph("List Of Users of Mumbai Shoppers", font);
			paragraph.setAlignment(Paragraph.ALIGN_CENTER);

			// Table
			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100f);
			table.setSpacingBefore(10);
			table.setWidths(new float[] { 1.5f, 3.5f, 3.0f, 3.0f, 3.0f, 1.8f });

			writeTableHeader(table);
			writeTableData(table, users);

			document.add(paragraph);
			document.add(table);
		} catch (DocumentException e) {
			LOGGER.error("ERROR occurred ::: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.error("ERROR occurred ::: " + e);
			e.printStackTrace();
		}
	}

	// ********* HELPER FUNCTIONS STARTS **********

	private void writeTableHeader(PdfPTable table) {
		// Create Cell
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.BLUE.darker());
		cell.setPadding(5);

		// Get Font
		Font font = FontFactory.getFont(FontFactory.TIMES_BOLD);
		font.setColor(Color.WHITE.brighter());

		String[] headers = headerValuesString.split(",");
		for (String header : headers) {
			// Add it to Table
			cell.setPhrase(new Phrase(header, font));
			table.addCell(cell);
		}

	}

	private void writeTableData(PdfPTable table, List<User> users) {
		for (User user : users) {
			table.addCell(String.valueOf(user.getId()));
			table.addCell(String.valueOf(user.getFirstName()));
			table.addCell(String.valueOf(user.getLastName()));
			table.addCell(String.valueOf(user.getEmail()));
			table.addCell(String.valueOf(user.getRoles().toString()));
			table.addCell(String.valueOf(user.isEnabled()));
		}
	}

	// ********* HELPER FUNCTIONS ENDS **********

}
