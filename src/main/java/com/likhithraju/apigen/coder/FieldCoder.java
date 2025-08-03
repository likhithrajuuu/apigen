package com.likhithraju.apigen.coder;

import com.likhithraju.apigen.configs.Column;
import com.likhithraju.apigen.util.PostgresToJavaTypeMapper;

public class FieldCoder {
	private Column column = null;
	private StringBuilder builder = new StringBuilder();

	public FieldCoder() {

	}

	public String toCode(Column column) {
		this.column = column;

		String code = addColumnAnnotation().addAccessModifier().addDataType().addFieldName().build();

		return code;
	}
	
	private FieldCoder addColumnAnnotation() {
		if(column.isGeneratedId()) {
			builder.append("@Id").append(System.lineSeparator());
			builder.append("@GeneratedValue(strategy = GenerationType.IDENTITY)").append(System.lineSeparator());
		} else {
			builder.append("@Column(name = \"").append(column.getName()).append("\"");
			if(column.getCharacterLength() > 0) {
				builder.append(", length = ").append(column.getCharacterLength());
			}
			if(!column.isNullable()) {
				builder.append(", nullable = false");
			}
			if(column.isUnique()) {
				builder.append(", unique = true");
			}
			builder.append(")").append(System.lineSeparator());
		}
		
		return this;
	}

	private FieldCoder addAccessModifier() {
		builder.append("private").append(" ");

		return this;
	}
	
	private FieldCoder addDataType() {
		String dataType = column.getDataType();
		
		builder.append(PostgresToJavaTypeMapper.getJavaTypeOrDefault(dataType, dataType)).append(" ");
		
		return this;
	}

	private FieldCoder addFieldName() {
		builder.append(toCamelCase(column.getName())).append(";").append(System.lineSeparator());

		return this;
	}

	private String toCamelCase(String snakeCaseString) {
		if (snakeCaseString == null || snakeCaseString.isEmpty()) {
			return snakeCaseString;
		}

		StringBuilder camelCaseBuilder = new StringBuilder();
		String[] parts = snakeCaseString.split("_");

		boolean firstPart = true;
		for (String part: parts) {
			if (part.isEmpty()) {
				continue;
			}
			if (firstPart) {
				camelCaseBuilder.append(part);
				firstPart = false;
			} else {
				camelCaseBuilder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
			}
		}

		return camelCaseBuilder.toString();
	}

	private String build() {
		return builder.toString();
	}
}
