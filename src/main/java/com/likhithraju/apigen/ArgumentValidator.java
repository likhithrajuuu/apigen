package com.likhithraju.apigen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likhithraju.apigen.configs.ApiConfigModel;

public class ArgumentValidator {
	private ArgumentsProcessor processor = null;
	
	public ArgumentValidator() {
		
	}
	
	public ArgumentValidator(ArgumentsProcessor processor) {
		this.processor = processor;
	}
	
	public ApiConfigModel validate() throws ArgumentValidationException {
		if(processor != null) {
			validateConfigurationFile();
			validateProjectFolder();
			
			return createConfiguration();
		} else {
			throw ArgumentValidationException.asMissingProcessorException();
		}
	}
	
	private boolean validateConfigurationFile() throws ArgumentValidationException {
		String file = processor.getConfigFile();
		
		if((file == null) || ((file != null) && (file.isBlank()))) {
			throw ArgumentValidationException.asMissingConfigurationFileException();
		}
		
		File configurationFile = new File(file);
		if(!configurationFile.exists()) {
			throw ArgumentValidationException.asFileNotFoundException(file);
		}
		
		if(configurationFile.canRead() == false) {
			throw ArgumentValidationException.asFileNotReadableException(file);
		}
		
		return validateProjectFolder();
	}
	
	private boolean validateProjectFolder() throws ArgumentValidationException {
		String folder = processor.getProjectFolder();
		
		if((folder != null) && (folder.isBlank() == false)) {
			Path path = Path.of(processor.getProjectFolder());
			if(!Files.exists(path)) {
				throw ArgumentValidationException.asProjectFolderNotFoundException(folder);
			}
		}

		return true;
	}
	
	private ApiConfigModel createConfiguration() throws ArgumentValidationException {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			String json = new String(Files.readAllBytes(Paths.get(processor.getConfigFile())));
			return mapper.readValue(json, ApiConfigModel.class);
		} catch(JsonProcessingException exception) {
			throw ArgumentValidationException.asInvalidConfigurationException(processor.getConfigFile(), exception);
		} catch(IOException exception) {
			throw ArgumentValidationException.asIoException("Exception processing configuration file", exception);
		}
	}
}
