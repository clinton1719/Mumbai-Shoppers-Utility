package com.mshoppers.admin.user.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.mshoppers.admin.AbstractExporter;
import com.mshoppers.common.entity.User;

public class UserCsvExporter extends AbstractExporter {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserCsvExporter.class);

	public void export(List<User> users, HttpServletResponse response) throws IOException {
		try (ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {
			// Calling parent class AbstractExporter method to set headers for file download
			super.setResponseHeaderForDownload(response, "text/csv", ".csv", "Mshoppers_USERS_Export_");

			String[] csvHeader = { "User ID", "E-mail", "First Name", "Last Name", "Enabled", "Roles" };
			String[] csvFieldMapping = { "id", "email", "firstName", "lastName", "enabled", "roles" };

			csvWriter.writeHeader(csvHeader);

			for (User user : users) {
				csvWriter.write(user, csvFieldMapping);
			}

		} catch (IOException e) {
			LOGGER.error("ERROR occurred ::: " + e);
			e.printStackTrace();
		}
	}

}
