package com.likhithraju.apigen;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentsProcessor {
	private static final Logger logger = LogManager.getLogger(ArgumentsProcessor.class);
	private Options options = new Options();
	private CommandLineParser parser = new DefaultParser();
	private HelpFormatter formatter = new HelpFormatter();
	private boolean logEnabled = true;
	private String configFile = "";
	private String projectFolder = "";
	private boolean verbose = false;

	public ArgumentsProcessor() {
		options.addOption(Option.builder("c").longOpt("config").hasArg().argName("CONFIG_FILE_NAME").desc(
				"Full path and file name of the configuration file containing the apigen configuration in JSON format")
				.required(true).build());

		options.addOption(Option.builder("p").longOpt("project").hasArg().argName("PROJECT_FOLDER")
				.desc("Full path to the folder containing a valid Java project created and built using maven")
				.required(false).build());

		options.addOption(Option.builder("l").longOpt("log").hasArg().argName("LOG_ENABLED")
				.desc("True to enable logging. False to disable logging. Logging is enabled by default.")
				.required(false).build());

		options.addOption(Option.builder("v").longOpt("verbose").argName("VERBOSE")
				.desc("True to display the generated code on console. Code is not displayed by default.")
				.required(false).build());

		options.addOption(
				Option.builder("h").longOpt("help").argName("HELP").desc("Show help message").required(false).build());
	}

	public boolean isLogEnabled() {
		return logEnabled;
	}

	public void setLogEnabled(boolean logEnabled) {
		this.logEnabled = logEnabled;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public String getProjectFolder() {
		return projectFolder;
	}

	public void setProjectFolder(String projectFolder) {
		this.projectFolder = projectFolder;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean process(String[] args) {
		try {
			CommandLine commandLine = parser.parse(options, args);

			processConfig(commandLine);
		} catch (ParseException exception) {
			logger.error(exception.getMessage(), exception);
			System.err.println(exception);
			formatter.printHelp("App", options);
		}

		return true;
	}

	private void processConfig(CommandLine commandLine) {
		if (commandLine.hasOption("config")) {
			setConfigFile(commandLine.getOptionValue("config"));
		}

		processProject(commandLine);
	}

	private void processProject(CommandLine commandLine) {
		if (commandLine.hasOption("project")) {
			setProjectFolder(commandLine.getOptionValue("project"));
		}

		processLog(commandLine);
	}

	private void processLog(CommandLine commandLine) {
		if (commandLine.hasOption("log")) {
			String value = commandLine.getOptionValue("log").trim().toLowerCase();

			logEnabled = value.equals("true");
		}

		processVerbose(commandLine);
	}

	private void processVerbose(CommandLine commandLine) {
		if (commandLine.hasOption("verbose")) {
			verbose = true;
		}
	}
}
