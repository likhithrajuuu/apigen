package com.likhithraju.apigen;

import java.io.IOException;

public class ArgumentValidationException extends Throwable {
	private static final long serialVersionUID = 1L;

	public ArgumentValidationException() {
		super();
	}

	public ArgumentValidationException(String message) {
		super(message);
	}

	public ArgumentValidationException(Throwable throwable) {
		super(throwable);
	}

	public ArgumentValidationException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public static ArgumentValidationException asFileNotFoundException(String filename) {
		return new ArgumentValidationException("Configuration file not found: " + filename);
	}

	public static ArgumentValidationException asFileNotReadableException(String filename) {
		return new ArgumentValidationException("Configuration file cannot be read: " + filename);
	}
	
	public static ArgumentValidationException asInvalidConfigurationException(String filename, Throwable throwable) {
		return new ArgumentValidationException("Invalid configuration file not: " + filename, throwable);
	}
	
	public static ArgumentValidationException asProjectFolderNotFoundException(String folder) {
		return new ArgumentValidationException("Project folder not found: " + folder);
	}

	public static ArgumentValidationException asIoException(String file, IOException exception) {
		if((file == null) || ((file != null) && (file.isBlank()))) {
			return new ArgumentValidationException("IO Exception while processing: " + exception.getMessage());			
		}
		
		return new ArgumentValidationException("IO Exception while processing output file: " + file, exception);
	}
	
	public static ArgumentValidationException asMissingProcessorException() {
		return new ArgumentValidationException("Missing arguments processor");
	}
	
	public static ArgumentValidationException asMissingConfigurationFileException() {
		return new ArgumentValidationException("Configuration file to process not specified");
	}
}
