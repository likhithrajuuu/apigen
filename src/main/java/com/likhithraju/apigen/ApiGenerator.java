package com.likhithraju.apigen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.likhithraju.apigen.coder.CoderException;
import com.likhithraju.apigen.coder.ControllerCoder;
import com.likhithraju.apigen.coder.EntityCoder;
import com.likhithraju.apigen.coder.RepositoryCoder;
import com.likhithraju.apigen.coder.ServiceCoder;
import com.likhithraju.apigen.coder.TestCoder;
import com.likhithraju.apigen.configs.ApiConfigModel;

@Service
public class ApiGenerator {

	private String entityName;
	private ApiConfigModel configuration;

	public ApiGenerator(){
		
	}

	public String generateAll(ApiConfigModel config) throws CoderException
	{	
		try {
			generateController(true);
			generateEntity(true);
			generateRepository(true);
			generateService(true);
			genrateTests(true);
			System.out.println(configuration.getDatabase().getModels().get(0).getCreateTableQuery());
		} catch (CoderException exception) {
			throw exception;
		}
		return "All services have been created";
	}

	public String generateForPreview(ApiConfigModel config, String file, int index) throws CoderException {
		this.configuration = config;
		this.entityName = configuration.getDatabase().getModels().get(index).getEntityName();
		String code = "";
		switch (file) {
			case "controller":
				try {
					code = generateController(false);
				} catch (CoderException e) {
					throw e;
				}
				break;
			case "entity":
				try {
					code = generateEntity(false);
				} catch (CoderException e) {
					throw e;
				}
				break;
			case "repository":
				try {
					code = generateRepository(false);
				} catch (CoderException e) {
					throw e;
				}
				break;
			case "service":
				try {
					code = generateService(false);
				} catch (CoderException e) {
					throw e;
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid file type: " + file);
		}
		return code;
	}

	public Map<String, String> generateAllCodeForPreview(ApiConfigModel config, int index) throws CoderException {
		
		this.configuration = config;
		this.entityName = configuration.getDatabase().getModels().get(index).getEntityName();
		Map<String, String> codeMap = new HashMap<>();
		try {
			codeMap.put("controller", generateController(false));
			codeMap.put("entity", generateEntity(false));
			codeMap.put("repository", generateRepository(false));
			codeMap.put("service", generateService(false));
		} catch (CoderException exception) {
			throw exception;
		}
		return codeMap;
	}

	public void writeAllToJavaFile(String fileNames, Map<String,String> codes)
	{
		System.out.println("still working on this");
	}

	public Boolean writeCodeToJavaFile(String fileName, String code)
	{
		if(writeToJavaFile(fileName, code))
		{
			return true;
		}
		
		return false;
	}

	private String generateEntity(boolean writeToFile) throws CoderException {
		try {
			EntityCoder entityCoder = new EntityCoder();
			String entityCode = entityCoder.toCode(this.configuration);
			if (writeToFile)
				writeToJavaFile(entityName + "Model", entityCode);
			return entityCode;
		} catch (CoderException exception) {
			exception.printStackTrace();
			throw exception;
		}
	}

	private String generateRepository(boolean writeToFile) throws CoderException {
		try {
			RepositoryCoder repositoryCoder = new RepositoryCoder();
			String repositoryCode = repositoryCoder.toCode(this.configuration);
			if (writeToFile)
				writeToJavaFile(entityName + "Repository", repositoryCode);
			return repositoryCode;
		} catch (CoderException exception) {
			exception.printStackTrace();
			throw exception;
		}
	}

	private String generateService(boolean writeToFile) throws CoderException {
		try {
			ServiceCoder serviceCoder = new ServiceCoder();
			String serviceCode = serviceCoder.toCode(this.configuration);
			if (writeToFile)
				writeToJavaFile(entityName + "Service", serviceCode);
			return serviceCode;
		} catch (CoderException exception) {
			exception.printStackTrace();
			throw exception;
		}
	}

	private String generateController(boolean writeToFile) throws CoderException {
		try {
			ControllerCoder controllerCoder = new ControllerCoder();
			String controllerCode = controllerCoder.toCode(this.configuration);
			if (writeToFile)
				writeToJavaFile(entityName + "Controller", controllerCode);
			return controllerCode;
		} catch (CoderException exception) {
			exception.printStackTrace();
			throw exception;
		}
	}

	private String genrateTests(boolean writeToFile) throws CoderException {
		try {
			TestCoder testCoder = new TestCoder();
			String testCode = testCoder.toCode(this.configuration);
			if (writeToFile)
				writeToJavaFile(entityName + "ControllerTests", testCode);
			return testCode;
		} catch (CoderException exception) {
			exception.printStackTrace();
			throw exception;
		}
	}



	private static boolean writeToJavaFile(String fileName, String code) {
		if ((fileName != null) && (!fileName.isBlank())) {
			String updatedFileName = updateFileExtension(fileName, "java");

			try {
				Files.write(Paths.get(updatedFileName), code.getBytes());
				return true;
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}

		return false;
	}

	private static String updateFileExtension(String fileName, String extension) {
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex == -1) {
			return fileName + "." + extension;
		} else {
			return fileName.substring(0, dotIndex + 1) + extension;
		}
	}
}
