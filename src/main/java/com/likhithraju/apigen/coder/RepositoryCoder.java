package com.likhithraju.apigen.coder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.likhithraju.apigen.configs.Column;
import com.likhithraju.apigen.configs.ApiConfigModel;
import com.likhithraju.apigen.configs.MultiColumnSearch;
import com.likhithraju.apigen.configs.PrimaryKey;

public class RepositoryCoder extends AbstractCoder {
	private List<Column> columns = null;
	private String entityName = "";
	private String entityModel = "";

	public RepositoryCoder() {
		super();
	}

	@Override
	public String toCode(ApiConfigModel configuration) throws CoderException {
		initialize(configuration);

		this.columns = configuration.getDatabase().getModels().get(0).getColumns();
		entityName = configuration.getDatabase().getModels().get(0).getEntityName() + "Repository";
		entityModel = configuration.getDatabase().getModels().get(0).getEntityName() + "Model";

		addPackage().addImports().addRepositoryAnnotation().addClassName().addClassStartBlock()
				.addFinders().addMultiColumnSearchFinders().addCountFinders().addDeleteFinders().addValidateUniqueFieldGroup().addClassEndBlock();
		
		return format();
	}

	private RepositoryCoder addPackage() {
		builder.append("package " + configuration.getBasePackage() + ".repository;").append(System.lineSeparator());

		return this;
	}

	private RepositoryCoder addImports() {
		String entityModel = configuration.getDatabase().getModels().get(0).getEntityName() + "Model";

		builder.append("import org.springframework.data.jpa.repository.JpaRepository;").append(System.lineSeparator());
		builder.append("import org.springframework.stereotype.Repository;").append(System.lineSeparator());
		builder.append("import com.fasterxml.jackson.databind.JsonNode;").append(System.lineSeparator());
		builder.append("import java.util.List;\r\n" + //
						"import java.util.Optional;\r\n").append(System.lineSeparator());
		builder.append("import ").append(configuration.getBasePackage()).append(".model.").append(entityModel)
				.append(";").append(System.lineSeparator());


		return this;
	}

	private RepositoryCoder addRepositoryAnnotation() {
		builder.append("@Repository").append(System.lineSeparator());

		return this;
	}

	private RepositoryCoder addClassName() {
		builder.append("public interface ").append(entityName).append(" extends JpaRepository<").append(entityModel);
		builder.append(", Long>").append(System.lineSeparator());

		return this;
	}

	private RepositoryCoder addClassStartBlock() {
		builder.append("{").append(System.lineSeparator());

		return this;
	}

	private RepositoryCoder addClassEndBlock() {
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private RepositoryCoder addFinders() {
		List<Column> findableColumns = columns.stream()
				.filter(column -> (column.isFindable() || column.isFindableAsList()) && !column.isPrimaryKey()).collect(Collectors.toList());

		
				if (!findableColumns.isEmpty()) {
					for (Column column : findableColumns) {
						System.out.println("col 1: " + column.getName());
				
						String returnType;
						String methodPrefix;
				
						if (column.isFindable()) {
							returnType = "public Optional<" + entityModel + "> ";
							methodPrefix = "findBy";
						} else {
							returnType = "public List<" + entityModel + "> ";
							methodPrefix = "findAllBy";
						}
				
						builder.append(returnType)
							   .append(methodPrefix)
							   .append(toSentenceCase(column.getName()))
							   .append("(")
							   .append(column.getJavaDataType())
							   .append(" ")
							   .append(toCamelCase(column.getName()))
							   .append(");")
							   .append(System.lineSeparator());
					}
				}
		return this;				
	}

	private RepositoryCoder addDeleteFinders() {
		List<Column> findableColumns = columns.stream()
				.filter(column -> (column.isFindable() || column.isFindableAsList()) && !column.isPrimaryKey()).collect(Collectors.toList());
		for (Column column : findableColumns) {
			builder.append("public void deleteBy").append(toSentenceCase(column.getName())).append("(");
			builder.append(column.getJavaDataType()).append(" ").append(toCamelCase(column.getName()));
			builder.append(");").append(System.lineSeparator());
		}

		return this;
	}

	private RepositoryCoder addCountFinders() {
		List<Column> findableColumns = columns.stream()
				.filter(column -> (column.isFindable() || column.isFindableAsList())&& !column.isPrimaryKey()).collect(Collectors.toList());
		for (Column column : findableColumns) {
			builder.append("public long countBy").append(toSentenceCase(column.getName())).append("(");
			builder.append(column.getJavaDataType()).append(" ").append(toCamelCase(column.getName()));
			builder.append(");").append(System.lineSeparator());
		}

		return this;
	}

	
	private RepositoryCoder addMultiColumnSearchFinders() {
		List<MultiColumnSearch> multiColumnSearches = configuration.getDatabase().getModels().get(0).getMultiColumnSearches();
		
		for(MultiColumnSearch search: multiColumnSearches) {
			String functionBase = "public List<" + entityModel + "> findAllBy" + search.getSearchColumns().stream().map(field -> toSentenceCase(field)).collect(Collectors.joining("And"));

			builder.append(functionBase).append("(");
			String params = search.getSearchColumns().stream().map(param -> getParamString(param)).collect(Collectors.joining(", "));
			builder.append(params).append(");").append(System.lineSeparator());
		}
		
		return this;
	}

	private RepositoryCoder addValidateUniqueFieldGroup()
	{	
		List<PrimaryKey> primaryKeys = configuration.getDatabase().getModels().get(0).getPrimaryKeys();
		
		for(PrimaryKey columns : primaryKeys)
		{
			if(columns.getColumns().size() > 1)
			{
				String countValidationString = "public long countBy" + columns.getColumns().stream().map(keys -> toSentenceCase(keys)).collect(Collectors.joining("And")) + "IgnoreCase( ";
				String parameters = columns.getColumns().stream().map(param -> getParamString(param)).collect(Collectors.joining(", ")) + ");";
				builder.append(countValidationString).append(parameters);
			}
		}
		return this;
	}
	
	private String getParamString(String param) {
		Optional<Column> foundColumn = columns.stream().filter(column -> column.getName().equalsIgnoreCase(param)).findFirst();
		
		if(foundColumn.isPresent()) {
			Column column = foundColumn.get();
			
			return column.getJavaDataType() + " " + toCamelCase(column.getName());
		}
		
		return "";
	}
}
