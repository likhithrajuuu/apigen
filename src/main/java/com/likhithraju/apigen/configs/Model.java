package com.likhithraju.apigen.configs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Model {
	private String entityName = "";
	private String tableName = "";
	private String schemaName = "";
	private List<Column> columns = new ArrayList<Column>();
	private List<MultiColumnSearch> multiColumnSearches = new ArrayList<MultiColumnSearch>();
	private List<PrimaryKey> primaryKeys = new ArrayList<PrimaryKey>();


	public Model() {

	}

	public Model(String entityName, String tableName, String schemaName) {
		super();
		this.entityName = entityName;
		this.tableName = tableName;
		this.schemaName = schemaName;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public List<MultiColumnSearch> getMultiColumnSearches() {
		return multiColumnSearches;
	}

	public void setMultiColumnSearches(List<MultiColumnSearch> multiColumnSearches) {
		this.multiColumnSearches = multiColumnSearches;
	}
	
	public List<PrimaryKey> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(List<PrimaryKey> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	@Override
	public String toString() {
		return "Model [entityName=" + entityName + ", tableName=" + tableName + ", schemaName=" + schemaName
				+ ", columns=" + columns + ", multiColumnSearches=" + multiColumnSearches.toString() + "]";
	}
	
	public void addColumn(Column column) {
		if(column != null) {
			columns.add(column);
		}
	}
	
	public void removeColumns() {
		columns.clear();
	}
	
	public void addMultiColumnSearch(MultiColumnSearch search) {
		multiColumnSearches.add(search);
	}

	@JsonIgnore
public String getCreateTableQuery() {
    StringBuilder builder = new StringBuilder();

    builder.append("CREATE TABLE ").append(schemaName).append(".").append(tableName).append(" (");

	String fields = columns.stream()
			.map(column -> column.toFieldString(!primaryKeys.isEmpty()))
			.collect(Collectors.joining(", "));

    builder.append(fields);
	primaryKeys.forEach(pk -> System.out.println(pk));
    if (!primaryKeys.isEmpty()) {
        Set<String> uniqueKeys = primaryKeys.stream()
                .flatMap(pk -> pk.getKeyNames().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new)); // preserve order

        builder.append(",\nPRIMARY KEY(")
               .append(String.join(", ", uniqueKeys))
               .append(")");
    }

    builder.append(");");

    return builder.toString();
}

}
