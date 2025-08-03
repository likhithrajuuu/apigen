package com.likhithraju.apigen.coder;

import java.util.List;
import java.util.stream.Collectors;

import com.likhithraju.apigen.configs.Column;
import com.likhithraju.apigen.configs.ApiConfigModel;

public class ControllerCoder extends AbstractCoder {

	private String entityName = "";
	private String entityModel = "";
	private String entityService = "";
	private List<Column> columns = null;

	public ControllerCoder() {
		super();
	}

	@Override
	public String toCode(ApiConfigModel configuration) throws CoderException {
		initialize(configuration);
		this.columns = configuration.getDatabase().getModels().get(0).getColumns();
		String baseName = configuration.getDatabase().getModels().get(0).getEntityName();
		String BasePackage = configuration.getBasePackage();
		entityName = baseName + "Controller";
		entityModel = baseName + "Model";
		entityService = baseName + "Service";

		String code = addPackageName().addDefaultImport().addServiceImport(BasePackage).addRequestMapping()
				.addServiceObject().addLogger().addGetAll().addGetById().addAdd().addCount().addCountBy().addDelete()
				.addfindableFunctions().addDeletBy().addDeleteById().addUpdate().addClassend().format();

		return code;
	}

	private ControllerCoder addDefaultImport() {
		builder.append("import java.util.List;").append(System.lineSeparator());
		builder.append("import java.util.Optional;").append(System.lineSeparator());
		builder.append("import org.apache.logging.log4j.LogManager;").append(System.lineSeparator());
		builder.append("import org.apache.logging.log4j.Logger;").append(System.lineSeparator());
		builder.append("import org.springframework.beans.factory.annotation.Autowired;").append(System.lineSeparator());
		builder.append("import org.springframework.http.HttpStatus;").append(System.lineSeparator());
		builder.append("import org.springframework.http.ResponseEntity;").append(System.lineSeparator());
		builder.append("import org.springframework.web.bind.annotation.*;").append(System.lineSeparator());
		builder.append("import ").append(configuration.getBasePackage()).append(".service.CrudOperationException;")
				.append(System.lineSeparator());
		builder.append("import ").append(configuration.getBasePackage()).append(".service.CrudValidationException;")
				.append(System.lineSeparator());
		return this;
	}

	private ControllerCoder addServiceImport(String BasePackage) {
		builder.append("import ").append(BasePackage).append(".service.").append(entityService).append(";")
				.append(System.lineSeparator());
		builder.append("import ").append(BasePackage).append(".model.").append(entityModel).append(";")
				.append(System.lineSeparator());
		return this;
	}

	private ControllerCoder addRequestMapping() {
		builder.append("@RestController").append(System.lineSeparator());
		builder.append("@RequestMapping(\"").append(configuration.getApiRequestMapping()).append("\")")
				.append(System.lineSeparator());
		builder.append("public class ").append(entityName).append(" {").append(System.lineSeparator());
		return this;
	}

	private ControllerCoder addPackageName() {
		builder.append("package " + configuration.getBasePackage() + ".controller;").append(System.lineSeparator());
		return this;
	}

	private ControllerCoder addClassend() {
		builder.append("}");
		return this;
	}

	private ControllerCoder addServiceObject() {
		builder.append("@Autowired").append(System.lineSeparator());
		builder.append("private ").append(entityService).append(" service;").append(System.lineSeparator());
		return this;
	}

	private ControllerCoder addLogger() {
		builder.append("private final Logger log = LogManager.getLogger(getClass());").append(System.lineSeparator());
		return this;
	}

	private ControllerCoder addUpdate() {
		builder.append(getAnnotation("put", "/update")).append(System.lineSeparator());
		builder.append("public ResponseEntity<?> update(@RequestBody ").append(entityModel).append(" model) {")
				.append(System.lineSeparator());
		builder.append("try {").append(System.lineSeparator());
		builder.append("return ResponseEntity.ok(service.update(model));").append(System.lineSeparator());
		builder.append(addCatchStatements(true, true));
		builder.append("}").append(System.lineSeparator());
		return this;
	}

	private String getAnnotation(String mapping, String path) {
		switch (mapping.toLowerCase()) {
			case "get":
				return "@GetMapping(\"" + path + "\")";
			case "post":
				return "@PostMapping(\"" + path + "\")";
			case "put":
				return "@PutMapping(\"" + path + "\")";
			case "delete":
				return "@DeleteMapping(\"" + path + "\")";
			default:
				throw new IllegalArgumentException("Unsupported mapping type: " + mapping);
		}
	}

	private String addCrudValidationException() {
		return "} catch (CrudValidationException e) {\n" + "log.error(e.getMessage(), e);\n"
				+ "return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());\n";
	}

	private String addCrudOperationException() {
		return "} catch (CrudOperationException e) {\n" + "log.error(e.getMessage(), e);\n"
				+ "return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());\n";
	}

	private String addCatchStatements(boolean validation, boolean operation) {
		StringBuilder sb = new StringBuilder();
		if (validation)
			sb.append(addCrudValidationException());
		if (operation)
			sb.append(addCrudOperationException());
		sb.append("}").append(System.lineSeparator());
		return sb.toString();
	}

	private String getReturnStatement(String status, String message) {
		switch (status) {
			case "notfound":
				return "return ResponseEntity.status(HttpStatus.NOT_FOUND).body(\"" + message + "\");";
			case "badrequest":
				return "return ResponseEntity.badRequest().body(\"" + message + "\");";
			case "ok":
				return "return ResponseEntity.ok(service." + message + ");";
			default:
				return "";
		}
	}

	private ControllerCoder addGetAll() {
		builder.append(getAnnotation("get", "/get/all/page/{page}/size/{size}")).append(System.lineSeparator());
		builder.append(
				"public ResponseEntity<?> getAll(@RequestParam(defaultValue = \"0\") int page, @RequestParam(defaultValue = \"10\") int size) {")
				.append(System.lineSeparator());
		builder.append("try {").append(System.lineSeparator());
		builder.append("List<").append(entityModel).append("> items = service.getAll(page, size);")
				.append(System.lineSeparator());
		builder.append("if (items.isEmpty()) ").append(System.lineSeparator());
		builder.append(getReturnStatement("notfound", "No records found.")).append(System.lineSeparator());
		builder.append("return ResponseEntity.ok(items);").append(System.lineSeparator());
		builder.append(addCatchStatements(true, true));
		builder.append("}").append(System.lineSeparator());
		return this;
	}

	private ControllerCoder addGetById() {

		Column primaryKey = columns.stream()
				.filter(column -> column.isFindable() && column.isPrimaryKey())
				.findFirst()
				.orElse(null);

		if (primaryKey != null) {
			builder.append(getAnnotation("get", "/get/by/id/{id}")).append(System.lineSeparator());
			builder.append("public ResponseEntity<?> getById(@PathVariable long id) {").append(System.lineSeparator());
			builder.append("if (id <= 0) ").append(System.lineSeparator());
			builder.append("return ResponseEntity.badRequest().body(\"Invalid ID\");").append(System.lineSeparator());
			builder.append("try {").append(System.lineSeparator());
			builder.append("return ResponseEntity.ok(service.getById(id));").append(System.lineSeparator());
			builder.append(addCatchStatements(true, true));
			builder.append("}").append(System.lineSeparator());
		}
		return this;
	}

	private String getByService(Column column) {
		return (column.isFindable() ? "getBy" : "getAllBy") + toSentenceCase(column.getName()) + "("
				+ toCamelCase(column.getName())
				+ ")";
	}

	private ControllerCoder addfindableFunctions() {
		List<Column> findableColumns = columns.stream()
				.filter(column -> (column.isFindable() || column.isFindableAsList()) && !column.isPrimaryKey())
				.collect(Collectors.toList());

		for (Column column : findableColumns) {
			builder.append(getAnnotation("get",
					"/get/by/" + toCamelCase(column.getName()) + "/{" + toCamelCase(column.getName()) + "}"))
					.append(System.lineSeparator());
			builder.append("public ResponseEntity<?> ").append(column.isFindable() ? "getBy" : "getAllBy")
					.append(toSentenceCase(column.getName())).append("(@PathVariable ").append(column.getJavaDataType())
					.append(" ").append(toCamelCase(column.getName())).append(") {").append(System.lineSeparator());
			builder.append("try {").append(System.lineSeparator());
			builder.append(getReturnStatement("ok", getByService(column))).append(System.lineSeparator());
			builder.append(addCatchStatements(true, true));
			builder.append("}").append(System.lineSeparator());
		}
		return this;
	}

	private ControllerCoder addAdd() {
		builder.append(getAnnotation("post", "/add")).append(System.lineSeparator());
		builder.append("public ResponseEntity<?> add(@RequestBody ").append(entityModel + " ")
				.append(toCamelCase(entityModel) + " ) {")
				.append(System.lineSeparator());
		builder.append("try {").append(System.lineSeparator());
		builder.append("return ResponseEntity.ok(service.add(" + toCamelCase(entityModel) + "));")
				.append(System.lineSeparator());
		builder.append(addCatchStatements(true, true));
		builder.append("}").append(System.lineSeparator());
		return this;
	}

	private ControllerCoder addCount() {
		builder.append(getAnnotation("get", "/count")).append(System.lineSeparator());
		builder.append("public ResponseEntity<?> count() {").append(System.lineSeparator());
		builder.append("try {").append(System.lineSeparator());
		builder.append("return ResponseEntity.ok(service.count());").append(System.lineSeparator());
		builder.append(addCatchStatements(false, true));
		builder.append("}").append(System.lineSeparator());
		return this;
	}

	private ControllerCoder addCountBy() {
		List<Column> countableColumns = columns.stream()
				.filter(column -> (column.isFindable() || column.isFindableAsList()) && !column.isPrimaryKey())
				.collect(Collectors.toList());

		for (Column column : countableColumns) {
			builder.append(getAnnotation("get",
					"/count/by/" + toCamelCase(column.getName()) + "/{" + toCamelCase(column.getName()) + "}"))
					.append(System.lineSeparator());
			builder.append("public ResponseEntity<?> countBy").append(toSentenceCase(column.getName()))
					.append("(@PathVariable ").append(column.getJavaDataType()).append(" ")
					.append(toCamelCase(column.getName()))
					.append(") {").append(System.lineSeparator());
			builder.append("try {").append(System.lineSeparator());
			builder.append("return ResponseEntity.ok(service.countBy").append(toSentenceCase(column.getName()))
					.append("(").append(toCamelCase(column.getName())).append("));").append(System.lineSeparator());
			builder.append(addCatchStatements(false, true));
			builder.append("}").append(System.lineSeparator());
		}
		return this;
	}

	private ControllerCoder addDelete() {
		builder.append(getAnnotation("delete", "/delete/all")).append(System.lineSeparator());
		builder.append("public ResponseEntity<?> deleteAll() {").append(System.lineSeparator());
		builder.append("try {").append(System.lineSeparator());
		builder.append("service.deleteAll();").append(System.lineSeparator());
		builder.append("return ResponseEntity.ok(\"Deleted successfully.\");").append(System.lineSeparator());
		builder.append(addCatchStatements(true, true));
		builder.append("}").append(System.lineSeparator());
		return this;
	}

	private ControllerCoder addDeleteById() {

		Column primaryKey = columns.stream()
				.filter(column -> column.isFindable() && column.isPrimaryKey())
				.findFirst()
				.orElse(null);

		if (primaryKey != null) {
			builder.append(getAnnotation("delete", "/delete/by/id/{id}")).append(System.lineSeparator());
			builder.append("public ResponseEntity<?> deleteById(@PathVariable Long id) {")
					.append(System.lineSeparator());
			builder.append("try {").append(System.lineSeparator());
			builder.append("service.deleteById(id);").append(System.lineSeparator());
			builder.append("return ResponseEntity.ok(\"Deleted entry with id .\");").append(System.lineSeparator());
			builder.append(addCatchStatements(true, true));
			builder.append("}").append(System.lineSeparator());
		}
		return this;
	}

	private ControllerCoder addDeletBy() {
		List<Column> deletableColumns = columns.stream()
				.filter(column -> (column.isFindable() || column.isFindableAsList()) && !column.isPrimaryKey())
				.collect(Collectors.toList());

		for (Column column : deletableColumns) {
			builder.append(getAnnotation("delete",
					"/delete/by/" + toCamelCase(column.getName()) + "/{" + toCamelCase(column.getName()) + "}"))
					.append(System.lineSeparator());
			builder.append("public ResponseEntity<?> deleteBy").append(toSentenceCase(column.getName()))
					.append("(@PathVariable ").append(column.getJavaDataType()).append(" ")
					.append(toCamelCase(column.getName()))
					.append(") {").append(System.lineSeparator());
			builder.append("try {").append(System.lineSeparator());
			builder.append("long count = service.deleteBy").append(toSentenceCase(column.getName())).append("(")
					.append(toCamelCase(column.getName())).append(");").append(System.lineSeparator());
			builder.append("return ResponseEntity.ok(\"Deleted \" + count + \" entries \" );").append(System.lineSeparator());
			builder.append(addCatchStatements(false, true));
			builder.append("}").append(System.lineSeparator());
		}
		return this;
	}

}
