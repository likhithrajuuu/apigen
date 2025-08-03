package com.likhithraju.apigen.coder;

import java.util.List;
import java.util.stream.Collectors;

import com.likhithraju.apigen.configs.Column;
import com.likhithraju.apigen.configs.ApiConfigModel;
import com.likhithraju.apigen.util.PostgresToJavaTypeMapper;

public class EntityCoder extends AbstractCoder {
	private List<Column> columns = null;
	private String entityName = "";
	private String entityNameInCamelCase = "";

	public EntityCoder() {
		super();
	}

	@Override
	public String toCode(ApiConfigModel configuration) throws CoderException {
		initialize(configuration);

		this.columns = configuration.getDatabase().getModels().get(0).getColumns();
		entityName = configuration.getDatabase().getModels().get(0).getEntityName() + ("Model");
		entityNameInCamelCase = toCamelCase(entityName);

		String code = addPackage().addImports().addEntityAnnotation().addTableAnnotation().addClassName()
				.addClassStartBlock().addPropertiesBlock().addDefaultConstructorBlock().addConstructorUsingFieldsBlock()
				.addGettersAndSettersBlock().addToStringBlock().addUpdateFromBlock().addDateTime().addClassEndBlock().format();

		return code;
	}

	private EntityCoder addPackage() {
		builder.append("package " + configuration.getBasePackage() + ".model;").append(System.lineSeparator());

		return this;
	}

	private EntityCoder addImports() {
		builder.append("import jakarta.persistence.Column;").append(System.lineSeparator());
		builder.append("import jakarta.persistence.Entity;").append(System.lineSeparator());
		builder.append("import jakarta.persistence.GeneratedValue;").append(System.lineSeparator());
		builder.append("import jakarta.persistence.GenerationType;").append(System.lineSeparator());
		builder.append("import jakarta.persistence.Id;").append(System.lineSeparator());
		builder.append("import jakarta.persistence.Table;").append(System.lineSeparator());
		builder.append("import org.hibernate.annotations.JdbcTypeCode;").append(System.lineSeparator());
		builder.append("import jakarta.validation.constraints.NotNull;").append(System.lineSeparator());
		builder.append("import java.time.LocalDateTime;").append(System.lineSeparator());
		builder.append("import com.fasterxml.jackson.databind.JsonNode;").append(System.lineSeparator());
		builder.append("import java.time.LocalDate;").append(System.lineSeparator());

		return this;
	}

	private EntityCoder addEntityAnnotation() {
		builder.append("@Entity").append(System.lineSeparator());

		return this;
	}

	public EntityCoder addTableAnnotation() {
		String schemaName = configuration.getDatabase().getModels().get(0).getSchemaName();
		String tableName = configuration.getDatabase().getModels().get(0).getTableName();

		builder.append("@Table(name = \"").append(tableName).append("\", ").append("schema = \"").append(schemaName)
				.append("\")").append(System.lineSeparator());

		return this;
	}

	private EntityCoder addClassName() {
		builder.append("public class ").append(entityName).append(System.lineSeparator());

		return this;
	}

	private EntityCoder addClassStartBlock() {
		builder.append("{").append(System.lineSeparator());

		return this;
	}

	private EntityCoder addPropertiesBlock() {
		String fields = columns.stream().map(field -> getFieldCode(field)).collect(Collectors.joining());

		builder.append(fields);

		return this;
	}

	private String getFieldCode(Column column) {
		FieldCoder fieldCoder = new FieldCoder();

		return fieldCoder.toCode(column);
	}

	private EntityCoder addDefaultConstructorBlock() {
		builder.append("public ").append(entityName).append("()").append(System.lineSeparator());
		builder.append("{").append(System.lineSeparator()).append("}").append(System.lineSeparator());

		return this;
	}

	private EntityCoder addConstructorUsingFieldsBlock() {
		List<String> parameters = columns.stream().map(field -> toCamelCase(field.getName()))
				.collect(Collectors.toList());
		String fields = columns.stream().map(field -> getParameterCode(field)).collect(Collectors.joining(", "));
		String prefix = "this.";

		builder.append("public ").append(entityName).append("(");
		builder.append(fields).append(")").append(System.lineSeparator());
		builder.append("{").append(System.lineSeparator());

		String initializers = parameters.stream()
				.map(field -> prefix + field + " = " + field + ";" + System.lineSeparator())
				.collect(Collectors.joining(""));
		builder.append(initializers).append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private String getParameterCode(Column column) {
		StringBuilder parameterBuilder = new StringBuilder();

		String dataType = column.getDataType();

		parameterBuilder.append(PostgresToJavaTypeMapper.getJavaTypeOrDefault(dataType, dataType)).append(" ");
		parameterBuilder.append(toCamelCase(column.getName()));

		return parameterBuilder.toString();
	}

	private EntityCoder addGettersAndSettersBlock() {
		for (Column column : columns) {
			String propertyNameInSentenceCase = toSentenceCase(column.getName());
			String propertyNameInCamelCase = toCamelCase(column.getName());
			String dataType = PostgresToJavaTypeMapper.getJavaTypeOrDefault(column.getDataType(), column.getDataType());

			createGetterBlock(dataType, propertyNameInCamelCase, propertyNameInSentenceCase);
			createSetterBlock(dataType, propertyNameInCamelCase, propertyNameInSentenceCase);
		}

		return this;
	}

	private void createGetterBlock(String dataType, String camelCase, String sentenceCase) {
		builder.append("public ").append(dataType).append(" ");
		if (dataType.equalsIgnoreCase("boolean")) {
			builder.append("is");
		} else {
			builder.append("get");
		}

		builder.append(sentenceCase).append("(){").append(System.lineSeparator());
		builder.append("return ").append(camelCase).append(";").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());
	}

	private void createSetterBlock(String dataType, String camelCase, String sentenceCase) {
		builder.append("public void ").append("set").append(sentenceCase).append("(");
		builder.append(dataType).append(" ").append(camelCase).append(")").append(System.lineSeparator());
		builder.append("{").append(System.lineSeparator());
		builder.append("this.").append(camelCase).append(" = ").append(camelCase).append(";")
				.append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());
	}

	private EntityCoder addToStringBlock() {
		builder.append("@Override").append(System.lineSeparator());
		builder.append("public String toString() {").append(System.lineSeparator());
		builder.append("return \"").append(entityName).append(" [\" + ");

		boolean firstColumn = true;
		for (Column column : columns) {
			String propertyNameInSentenceCase = toSentenceCase(column.getName());
			String propertyNameInCamelCase = toCamelCase(column.getName());

			if (!firstColumn) {
				builder.append(" + \", \" + ");
			}

			builder.append("\"").append(propertyNameInSentenceCase).append(" = \" + ").append(propertyNameInCamelCase);

			firstColumn = false;
		}
		builder.append(" + \"]\";").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private EntityCoder addUpdateFromBlock() {

		builder.append("public void updateFrom(").append(entityName).append(" ").append(toCamelCase(entityName));
		builder.append(") {").append(System.lineSeparator());

		for (Column column : columns) {
			if (column.isUpdatable()) {
				String propertyNameInSentenceCase = toSentenceCase(column.getName());
				builder.append("set").append(propertyNameInSentenceCase).append("(");
				
				String javaType = column.getJavaDataType();
				if ("java.time.LocalDate".equals(javaType) || "java.time.LocalDateTime".equals(javaType)) {
					builder.append("LocalDateTime.now()"); 
				} else {
					if ("boolean".equalsIgnoreCase(column.getJavaDataType())) {
						builder.append(entityNameInCamelCase).append(".is")
							   .append(propertyNameInSentenceCase).append("()");
					} else {
						builder.append(entityNameInCamelCase).append(".get")
							   .append(propertyNameInSentenceCase).append("()");
					}
				}
		
				builder.append(");").append(System.lineSeparator());
			}
		}
		

		builder.append("}");

		return this;
	}

	private EntityCoder addDateTime() {
		StringBuilder methodBuilder = new StringBuilder();
		boolean valid = false;
	
		methodBuilder.append("public void addCurrentDatetime() {").append(System.lineSeparator());
	
		for (Column column : columns) {
			String javaType = column.getJavaDataType();
			if ("java.time.LocalDate".equals(javaType) || "java.time.LocalDateTime".equals(javaType)) {
				valid = true;
				String propertyNameInSentenceCase = toSentenceCase(column.getName());
	
				methodBuilder.append("set")
							 .append(propertyNameInSentenceCase).append("(");
	
				if ("java.time.LocalDate".equals(javaType)) {
					methodBuilder.append("LocalDate.now());").append(System.lineSeparator());
				} else {
					methodBuilder.append("LocalDateTime.now());").append(System.lineSeparator());
				}
			}
		}
	
		methodBuilder.append("}").append(System.lineSeparator());
	
		if (valid) {
			builder.append(methodBuilder);
		}
	
		return this;
	}	

	private EntityCoder addClassEndBlock() {
		builder.append("}").append(System.lineSeparator());

		return this;
	}
}
