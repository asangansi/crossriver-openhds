package org.openhds.specialstudy.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.openhds.specialstudy.domain.DatabaseConfig;
import org.openhds.specialstudy.domain.EndUser;
import org.openhds.specialstudy.domain.Individual;
import org.openhds.specialstudy.domain.Location;
import org.openhds.specialstudy.domain.SocialGroup;
import org.openhds.specialstudy.domain.Visit;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/database-configuration")
public class DatabaseConfigurationController {
	private static final Logger logger = Logger.getLogger(DatabaseConfigurationController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String index(ModelMap modelMap, SessionStatus sessionStatus) {
		DatabaseConfig config = getCurrentConfig();
		modelMap.put("databaseConfig", config);
		return "database-configuration/update";
	}

	private DatabaseConfig getCurrentConfig() {
		ClassPathResource resource = new ClassPathResource("application.properties");
		Properties props = new Properties();
		try {
			props.load(resource.getInputStream());
		} catch (IOException e) {

		}
		DatabaseConfig config = new DatabaseConfig();
		config.setDatabaseName(props.getProperty("database.name"));
		config.setDatabasePassword(props.getProperty("database.password"));
		config.setDatabaseUser(props.getProperty("database.username"));
		config.setOpenhdsDatabaseName(props.getProperty("openhds.database.name"));
		config.setOpenHdsWebServiceUrl(props.getProperty("openhds.applicationUrl"));

		return config;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String updateConfig(@Valid DatabaseConfig config, BindingResult result, ModelMap modelMap) {
		try {
			new URL(config.getOpenHdsWebServiceUrl());
		} catch (MalformedURLException e) {
			result.rejectValue("openHdsWebServiceUrl", null, "Invalid URL");
			return "database-configuration/update";
		}
		writeApplicationProperties(config);
		if (config.isCreateDatabase()) {
			createDatabase(config);
		}
		modelMap.addAttribute("configUpdated",
				"Updated Successfully, please restart the application for settings to take effect");
		return "database-configuration/update";
	}

	private void createDatabase(DatabaseConfig config) {
		Ejb3Configuration ejb3Cfg = new Ejb3Configuration();
		Properties props = new Properties();
		props.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		props.put("hibernate.connection.username", config.getDatabaseUser());
		props.put("hibernate.connection.url", getJdbcUrl(config.getDatabaseName()));
		props.put("hibernate.connection.password", config.getDatabasePassword());
		props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		ejb3Cfg.addProperties(props);

		ejb3Cfg.setNamingStrategy(ImprovedNamingStrategy.INSTANCE);

		// add annotated classes
		ejb3Cfg.addAnnotatedClass(EndUser.class);
		ejb3Cfg.addAnnotatedClass(Individual.class);
		ejb3Cfg.addAnnotatedClass(Location.class);
		ejb3Cfg.addAnnotatedClass(SocialGroup.class);
		ejb3Cfg.addAnnotatedClass(Visit.class);

		Configuration cfg = ejb3Cfg.getHibernateConfiguration();

		SchemaExport se = new SchemaExport(cfg);
		se.execute(false, true, false, true);
	}

	private void writeApplicationProperties(DatabaseConfig config) {
		Properties props = new Properties();
		props.setProperty("database.username", config.getDatabaseUser());
		props.setProperty("database.password", config.getDatabasePassword());
		props.setProperty("database.name", config.getDatabaseName());
		props.setProperty("database.url", getJdbcUrl(config.getDatabaseName()));
		props.setProperty("database.driverClassName", "com.mysql.jdbc.Driver");
		props.setProperty("database.dialect", "org.hibernate.dialect.MySQLDialect");
		props.setProperty("hibernate.hbm2ddl", "update");

		props.setProperty("openhds.database.name", config.getOpenhdsDatabaseName());
		props.setProperty("openhds.database.url", getJdbcUrl(config.getOpenhdsDatabaseName()));
		props.setProperty("openhds.database.driverClassName", "org.hibernate.dialect.MySQLDialect");

		props.setProperty("openhds.applicationUrl", config.getOpenHdsWebServiceUrl());
		props.setProperty("openhds.specialstudyBaseAddress", getOpenHdsWebService(config.getOpenHdsWebServiceUrl()));
		writePropsToFile(props);
	}

	private void writePropsToFile(Properties props) {
		ClassPathResource resource = new ClassPathResource("application.properties");
		try {
			File file = resource.getFile();
			FileOutputStream output = new FileOutputStream(file);
			props.store(output, null);
			output.close();
		} catch (IOException e) {
			logger.error("Could not write to application.properties file");
		}
	}

	private String getOpenHdsWebService(String openHdsWebServiceUrl) {
		return openHdsWebServiceUrl + "/api/rest/specialstudywebservice";
	}

	private String getJdbcUrl(String databaseName) {
		return "jdbc:mysql://localhost:3306/" + databaseName;
	}
}
