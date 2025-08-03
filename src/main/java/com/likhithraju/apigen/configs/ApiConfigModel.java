package com.likhithraju.apigen.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ApiConfigModel {
	private String basePackage = "";
	private Database database;
	private String apiRequestMapping = "";
	
	public ApiConfigModel() {
		
	}

	public ApiConfigModel(String basePackage) {
		super();

		this.basePackage = basePackage;
	}

	public ApiConfigModel(String basePackage, String apiRequestMapping) {
		super();

		this.basePackage = basePackage;
		this.apiRequestMapping = apiRequestMapping;
	}

	public String getApiRequestMapping() {
		return apiRequestMapping;
	}

	public void setApiRequestMapping(String apiRequestMapping) {
		this.apiRequestMapping = apiRequestMapping;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public Database getDatabase() {
		return database;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			return mapper.writeValueAsString(this);
		} catch(JsonProcessingException exception) {
			return "{" + exception.getMessage() + "}";
		}
	}
}
