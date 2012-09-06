package org.openhds.specialstudy.domain;

public class DatabaseConfig {

	private String databaseName;
	private String databasePassword;
	private String databaseUser;
	private String openhdsDatabaseName;
	private String openHdsWebServiceUrl;
	private boolean createDatabase;

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	public String getDatabaseUser() {
		return databaseUser;
	}

	public void setDatabaseUser(String databaseUser) {
		this.databaseUser = databaseUser;
	}

	public String getOpenhdsDatabaseName() {
		return openhdsDatabaseName;
	}

	public void setOpenhdsDatabaseName(String openhdsDatabaseName) {
		this.openhdsDatabaseName = openhdsDatabaseName;
	}

	public String getOpenHdsWebServiceUrl() {
		return openHdsWebServiceUrl;
	}

	public void setOpenHdsWebServiceUrl(String openHdsWebServiceUrl) {
		this.openHdsWebServiceUrl = openHdsWebServiceUrl;
	}

	public boolean isCreateDatabase() {
		return createDatabase;
	}

	public void setCreateDatabase(boolean createDatabase) {
		this.createDatabase = createDatabase;
	}

}
