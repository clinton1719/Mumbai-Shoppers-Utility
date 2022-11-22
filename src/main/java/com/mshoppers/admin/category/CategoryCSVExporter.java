/**
 * 
 */
package com.mshoppers.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.mshoppers.admin.AbstractExporter;
import com.mshoppers.admin.user.export.UserCsvExporter;
import com.mshoppers.common.entity.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Clinton
 *
 */
public class CategoryCSVExporter extends AbstractExporter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserCsvExporter.class);

	public void export(List<Category> categories, HttpServletResponse response) throws IOException {
		try (ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {
			// Calling parent class AbstractExporter method to set headers for file download
			super.setResponseHeaderForDownload(response, "text/csv", ".csv", "Mshoppers_USERS_Export_");

			String[] csvHeader = { "User ID", "Name", "Alias", "IsEnabled"};
			String[] csvFieldMapping = { "id", "nameInDropDown", "alias", "isEnabled"};

			csvWriter.writeHeader(csvHeader);

			for (Category category : categories) {
				csvWriter.write(category, csvFieldMapping);
			}

		} catch (IOException e) {
			LOGGER.error("ERROR occurred ::: " + e);
			e.printStackTrace();
		}
	}

}
