package com.likhithraju.apigen.configs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiColumnSearch {
	private List<String> searchColumns = new ArrayList<String>();
	
	public MultiColumnSearch() {
		
	}
	
	public MultiColumnSearch(String...columnNames) {
		if(columnNames.length > 0) {
			for(String columnName: columnNames) {
				add(columnName);
			}
		}
	}

	public List<String> getSearchColumns() {
		return searchColumns;
	}

	public void setSearchColumns(List<String> searchColumns) {
		this.searchColumns = searchColumns;
	}
	
	public void add(String columnName) {
		searchColumns.add(columnName);
	}
	
	public void add(String...columnNames) {
		if(columnNames.length > 0) {
			for(String columnName: columnNames) {
				add(columnName);
			}
		}
	}
	
	public void clear() {
		searchColumns.clear();
	}

	@Override
	public String toString() {
		return "MultiColumnSearch [searchColumns=" + searchColumns.stream().collect(Collectors.joining(". ")) + "]";
	}	
}
