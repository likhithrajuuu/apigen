package com.likhithraju.apigen.configs;

import java.util.ArrayList;
import java.util.List;

public class Database {
	private String hostname = "";
	private int portNumber = 0;
	private String userName = "";
	private String password = "";
	private String databaseName = "";
	private boolean createIfNotExistsing = false;
	private List<Model> models = new ArrayList<Model>();

	public Database() {
		
	}

	public Database(String hostname, int portNumber, String userName, String password,
			String databaseName, boolean createIfNotExistsing) {
		super();
		this.hostname = hostname;
		this.portNumber = portNumber;
		this.userName = userName;
		this.password = password;
		this.databaseName = databaseName;
		this.createIfNotExistsing = createIfNotExistsing;
	}

	public boolean isCreateIfNotExistsing() {
		return createIfNotExistsing;
	}

	public void setCreateIfNotExistsing(boolean createIfNotExistsing) {
		this.createIfNotExistsing = createIfNotExistsing;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String databaseHostname) {
		this.hostname = databaseHostname;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int databasePortNumber) {
		this.portNumber = databasePortNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}

	@Override
	public String toString() {
		return "Database [hostname=" + hostname + ", portNumber="
				+ portNumber + ", userName=" + userName + ", password=" + password + ", databaseName="
				+ databaseName + ", createIfNotExistsing=" + createIfNotExistsing + "]";
	}
	
	public void addModel(Model model) {
		if(model != null) {
			models.add(model);
		}
	}
	
	public void removeModels() {
		models.clear();
	}
}
