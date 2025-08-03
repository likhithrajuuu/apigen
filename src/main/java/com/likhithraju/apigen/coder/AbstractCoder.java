package com.likhithraju.apigen.coder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.Document;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.text.edits.TextEdit;
import com.likhithraju.apigen.configs.ApiConfigModel;

public abstract class AbstractCoder implements Codable {
	protected ApiConfigModel configuration = null;
	protected StringBuilder builder = new StringBuilder();

	public AbstractCoder() {

	}

	protected void initialize(ApiConfigModel configuration) {
		this.configuration = configuration;
	}


	protected String format() {
		String unformattedJavaCode = builder.toString();
		String formattedJavaCode = unformattedJavaCode;

		try {

			Map<String, String> options = new HashMap<>();
			options.put(org.eclipse.jdt.core.JavaCore.COMPILER_SOURCE, "17");
			options.put(org.eclipse.jdt.core.JavaCore.COMPILER_COMPLIANCE, "17");
			options.put(org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, "17");

			CodeFormatter formatter = ToolFactory.createCodeFormatter(options);

			TextEdit edit = formatter.format(
					CodeFormatter.K_COMPILATION_UNIT,
					unformattedJavaCode,
					0,
					unformattedJavaCode.length(),
					0,
					System.getProperty("line.separator"));

			if (edit != null) {
				Document doc = new Document(unformattedJavaCode);
				edit.apply(doc);
				formattedJavaCode = doc.get();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return formattedJavaCode;
	}

	protected String toCamelCase(String snakeCaseString) {
		if (snakeCaseString == null || snakeCaseString.isEmpty()) {
			return snakeCaseString;
		}

		StringBuilder camelCaseBuilder = new StringBuilder();
		String[] parts = snakeCaseString.split("_");

		boolean firstPart = true;
		for (String part : parts) {
			if (part.isEmpty()) {
				continue;
			}
			if (firstPart) {
				camelCaseBuilder.append(Character.toLowerCase(part.charAt(0))).append(part.substring(1));
				firstPart = false;
			} else {
				camelCaseBuilder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
			}
		}

		return camelCaseBuilder.toString();
	}

	protected String toSentenceCase(String snakeCaseString) {
		if (snakeCaseString == null || snakeCaseString.isEmpty()) {
			return snakeCaseString;
		}

		StringBuilder sentenceCaseBuilder = new StringBuilder();
		String[] parts = snakeCaseString.split("_");

		for (String part : parts) {
			if (part.isEmpty()) {
				continue;
			}
			sentenceCaseBuilder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
		}

		return sentenceCaseBuilder.toString();
	}
}
