package com.likhithraju.apigen.configs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.likhithraju.apigen.util.PostgresToJavaTypeMapper;

public class Column {
	private String name = "";
	private String dataType = "";
	private int characterLength = 0;
	private boolean generatedId = false;
	private boolean primaryKey = false;
	private boolean nullable = false;
	private boolean unique = false;
	private boolean findable = false;
	private boolean findableAsList = false;
	private boolean updatable = false;
	
	public Column() {
		
	}

	public Column(String name, String dataType, int characterLength, boolean generatedId,
			boolean primaryKey, boolean nullable, boolean unique, boolean findable, boolean findableAsList, boolean updatable) {
		super();
		this.name = name;
		this.dataType = dataType;
		this.characterLength = characterLength;
		this.generatedId = generatedId;
		this.primaryKey = primaryKey;
		this.nullable = nullable;
		this.unique = unique;
		this.findable = findable;
		this.findableAsList = findableAsList;	
		this.updatable = updatable;	
	}

	public boolean isFindable() {
		return findable;
	}

	public void setFindable(boolean findable) {
		this.findable = findable;
	}

	public boolean isFindableAsList() {
		return findableAsList;
	}

	public void setFindableAsList(boolean findableAsList) {
		this.findableAsList = findableAsList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getCharacterLength() {
		return characterLength;
	}

	public void setCharacterLength(int characterLength) {
		this.characterLength = characterLength;
	}

	public boolean isGeneratedId() {
		return generatedId;
	}

	public void setGeneratedId(boolean generatedId) {
		this.generatedId = generatedId;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
	public boolean isUpdatable() {
		return updatable;
	}

	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}

	@Override
	public String toString() {
		return "Column [name=" + name + ", dataType=" + dataType + ", characterLength=" + characterLength
				+ ", generatedId=" + generatedId + ", primaryKey=" + primaryKey + ", nullable=" + nullable + ", unique="
				+ unique + ", findable=" + findable + ", findableAsList=" + findableAsList + "]";
	}

	public String toFieldString(boolean multiPrimaryKey) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(name).append(" ");
		if(dataType.equalsIgnoreCase("varchar")) {
			builder.append("VARCHAR(").append(characterLength).append(")").append(" ");
		} else {
			builder.append(dataType).append(" ");
		}
		
		builder.append(isNullable() ? "NULL " : "NOT NULL ");
		builder.append(isPrimaryKey() && multiPrimaryKey == false ? "PRIMARY KEY " : "");
		builder.append(isUnique() ? "UNIQUE" : "");
		
		return builder.toString().trim();
	}
	
	@JsonIgnore
	public String getJavaDataType() {
		return PostgresToJavaTypeMapper.getJavaTypeOrDefault(dataType, dataType);
	}
}
