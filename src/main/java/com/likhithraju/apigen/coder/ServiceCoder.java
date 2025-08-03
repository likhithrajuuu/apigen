package com.likhithraju.apigen.coder;

import java.util.List;
import java.util.stream.Collectors;

import com.likhithraju.apigen.configs.Column;
import com.likhithraju.apigen.configs.PrimaryKey;
import com.likhithraju.apigen.configs.ApiConfigModel;

public class ServiceCoder extends AbstractCoder {
	private String entityName = "";
	private String entityModel = "";
	private String entityRepository = "";
	private String idDataType = "";
	private List<Column> columns = null;

	public ServiceCoder() {

	}

	@Override
	public String toCode(ApiConfigModel configuration) throws CoderException {
		this.configuration = configuration;
		initialize(configuration);

		entityName = configuration.getDatabase().getModels().get(0).getEntityName() + "Service";
		entityModel = configuration.getDatabase().getModels().get(0).getEntityName() + "Model";
		entityRepository = configuration.getDatabase().getModels().get(0).getEntityName() + "Repository";
		this.columns = configuration.getDatabase().getModels().get(0).getColumns();
		idDataType = configuration.getDatabase().getModels().get(0).getColumns().get(0).getJavaDataType();

		String code = addPackage()
				.addImports()
				.addServiceAnnotation()
				.addClassName()
				.addClassStartBlock()
				.addAutoWiredStatements()
				.addLoggerStatement()
				.addEmptyConstructor()
				.addCheckForNull()
				.addValidate()
				.addCheckId()
				.addSave()
				.addGetAll()
				.addGetById()
				.addFindableServices()
				.addService()
				.addUpdate()
				.addGetCount()
				.addFindableGetCount()
				.addDelete()
				.addDeleteById()
				.addDeleteFindableServices()
				.addClassEndBlock().format();

		return code;
	}

	private ServiceCoder addPackage() {
		builder.append("package " + configuration.getBasePackage() + ".service;").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addImports() {
		String entityModel = configuration.getDatabase().getModels().get(0).getEntityName() + "Model";
		String entityRepository = configuration.getDatabase().getModels().get(0).getEntityName() + "Repository";

		builder.append("import org.apache.logging.log4j.LogManager;").append(System.lineSeparator());
		builder.append("import org.apache.logging.log4j.Logger;").append(System.lineSeparator());
		builder.append("import org.springframework.beans.factory.annotation.Autowired;").append(System.lineSeparator());
		builder.append("import org.springframework.data.domain.PageRequest;").append(System.lineSeparator());
		builder.append("import org.springframework.data.domain.Pageable;").append(System.lineSeparator());
		builder.append("import org.springframework.stereotype.Service;").append(System.lineSeparator());
		builder.append("import jakarta.transaction.Transactional;").append(System.lineSeparator());
		builder.append("import jakarta.validation.ConstraintViolation;").append(System.lineSeparator());
		builder.append("import jakarta.validation.Validator;").append(System.lineSeparator());
		builder.append("import ").append(configuration.getBasePackage()).append(".model.").append(entityModel)
				.append(";").append(System.lineSeparator());
		builder.append("import ").append(configuration.getBasePackage()).append(".repository.").append(entityRepository)
				.append(";").append(System.lineSeparator());
		builder.append("import java.util.List;").append(System.lineSeparator());
		builder.append("import java.util.Optional;").append(System.lineSeparator());
		builder.append("import java.util.Set;").append(System.lineSeparator());
		builder.append("import ").append(configuration.getBasePackage()).append(".service.CrudOperationException;")
				.append(System.lineSeparator());
		builder.append("import ").append(configuration.getBasePackage()).append(".service.CrudValidationException;")
				.append(System.lineSeparator());
		builder.append("import ").append(configuration.getBasePackage()).append(".util.ServiceConstants;")
				.append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addServiceAnnotation() {
		builder.append("@Service").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addClassName() {
		builder.append("public class ").append(entityName).append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addClassStartBlock() {
		builder.append("{").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addClassEndBlock() {
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addAutoWiredStatements() {
		builder.append("@Autowired").append(System.lineSeparator());
		builder.append("private ").append(entityRepository).append("  repository;").append(System.lineSeparator());

		builder.append("@Autowired").append(System.lineSeparator());
		builder.append("private Validator validator;").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addEmptyConstructor() {
		builder.append("public ").append(entityName).append("() {").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addLoggerStatement() {
		builder.append("private final Logger log = LogManager.getLogger(getClass());").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addCheckForNull() {
		builder.append("public void checkForNull(").append(entityModel)
				.append(" model) throws CrudOperationException {").append(System.lineSeparator());
		builder.append("if(model == null) {").append(System.lineSeparator());
		builder.append("throw CrudOperationException.asNullEntity(").append(entityModel).append(".class);")
				.append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addValidate() {
		builder.append("private void validate(").append(entityModel).append(" model) throws CrudValidationException {")
				.append(System.lineSeparator());
		builder.append("Set<ConstraintViolation<").append(entityModel)
				.append(">> violations = validator.validate(model);").append(System.lineSeparator());
		builder.append("if(!violations.isEmpty()) {").append(System.lineSeparator());
		builder.append("throw CrudValidationException.asFailedValidationOperation(").append(entityModel)
				.append(".class, violations);").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addCheckId() {
		builder.append("private void checkId(").append(idDataType).append(" id) throws CrudValidationException {")
				.append(System.lineSeparator());
		builder.append("if(id <= 0) {").append(System.lineSeparator());
		builder.append("throw CrudValidationException.asInvalidEntityId(").append(entityModel).append(".class); ")
				.append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addValidateUniqueFieldGroup() {
		List<PrimaryKey> primaryKeys = configuration.getDatabase().getModels().get(0).getPrimaryKeys();

		for (PrimaryKey columns : primaryKeys) {
			if (columns.getColumns().size() > 1) {
				builder.append("if(isNew) {").append(System.lineSeparator());
				String parameters = columns.getColumns().stream().map(keys -> toSentenceCase(keys))
						.collect(Collectors.joining("And")) + "IgnoreCase( ";
				String params = columns.getColumns().stream()
						.map(param -> "model.get" + toSentenceCase(param) + "()")
						.collect(Collectors.joining(", "));
				builder.append("if (repository.countBy").append(parameters).append(params).append(") > 0").append(") {")
						.append(System.lineSeparator());
				builder.append("throw new CrudOperationException(\"Duplicate entry for unique field group: \", ")
						.append(entityModel).append(".class);").append(System.lineSeparator());
				builder.append("}\n}").append(System.lineSeparator());

			}
		}
		return this;
	}

	private ServiceCoder addSave() {
		builder.append("public ").append(entityModel).append(" save(").append(entityModel)
				.append(" model) throws CrudOperationException {").append(System.lineSeparator());
		builder.append("try {").append(System.lineSeparator());
		builder.append("boolean isNew = model.getId() <= 0;").append(System.lineSeparator());
		addValidateUniqueFieldGroup();
		builder.append(entityModel).append(" savedModel = repository.save(model);").append(System.lineSeparator());
		builder.append("\tlog.info((isNew ? \"Added\" : \"Updated\") + \" ").append(entityModel)
				.append(" with ID: \" + savedModel.getId());").append(System.lineSeparator());
		builder.append("return savedModel;").append(System.lineSeparator());
		builder.append("} catch (Exception exception) {").append(System.lineSeparator());
		builder.append("throw CrudOperationException.asFailedAddOperation(").append(entityModel)
				.append(".class, exception);").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addFindableServices() {
		List<Column> findableColumns = columns.stream()
				.filter(column -> (column.isFindable() || column.isFindableAsList()) && !column.isPrimaryKey())
				.collect(Collectors.toList());
		for (Column column : findableColumns) {
			String returnType = column.isFindable() ? "Optional<" : "List<";
			String getType = column.isFindable() ? "getBy" : "getAllBy";
			String findType = column.isFindable() ? "findBy" : "findAllBy";

			builder.append("public ").append(column.isFindableAsList() ? "List<" + entityModel + ">" : entityModel)
					.append(" ").append(getType).append(toSentenceCase(column.getName()))
					.append("(").append(column.getJavaDataType()).append(" ").append(toCamelCase(column.getName()))
					.append(") throws CrudOperationException, CrudValidationException {")
					.append(System.lineSeparator());

			if (column.getName().equals("id")) {
				builder.append("checkId(").append(column.getName()).append(");").append(System.lineSeparator());
			}

			builder.append(returnType).append(entityModel).append("> result = ");
			builder.append("repository.").append(findType).append(toSentenceCase(column.getName())).append("(")
					.append(toCamelCase(column.getName())).append(");").append(System.lineSeparator());

			if (column.isFindable() &&  !column.isPrimaryKey()) {

				builder.append("if (result.isEmpty()) {").append(System.lineSeparator());
				builder.append("throw CrudOperationException.asNullEntity(" + entityModel + ".class);")
						.append(System.lineSeparator());
				builder.append("}").append(System.lineSeparator());

				builder.append("checkForNull(result.get());").append(System.lineSeparator());
				builder.append("return result.get();").append(System.lineSeparator());
			} else {
				builder.append("return result;").append(System.lineSeparator());
			}
			builder.append("}").append(System.lineSeparator());
		}
		return this;
	}

	private ServiceCoder addGetAll() {
		builder.append("public List<").append(entityModel).append("> getAll(int page, int size) {")
				.append(System.lineSeparator());
		builder.append("try {").append(System.lineSeparator());
		builder.append("Pageable pageable = PageRequest.of(").append(System.lineSeparator());
		builder.append("page < ServiceConstants.STARTING_PAGE_NUMBER ? ServiceConstants.STARTING_PAGE_NUMBER : page,")
				.append(System.lineSeparator());
		builder.append("size <= 0 ? ServiceConstants.DEFAULT_ITEMS_PER_PAGE : size);").append(System.lineSeparator());
		builder.append("return repository.findAll(pageable).getContent();").append(System.lineSeparator());
		builder.append("} catch (Exception exception) {").append(System.lineSeparator());
		builder.append("throw CrudOperationException.asFailedGetOperation(").append(entityModel)
				.append(".class, exception);").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addService() {
		builder.append("public ").append(entityModel).append(" add(").append(entityModel)
				.append(" model) throws CrudValidationException, CrudOperationException {")
				.append(System.lineSeparator());
		builder.append("checkForNull(model);").append(System.lineSeparator());
		builder.append("validate(model);").append(System.lineSeparator());
		for (Column column : columns) {
			if (column.getJavaDataType().equals("java.time.LocalDateTime")) {
				builder.append("model.addCurrentDatetime();").append(System.lineSeparator());
				break;
			}
		}
		builder.append("return save(model);").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addUpdate() {
		builder.append("public ").append(entityModel).append(" update(").append(entityModel)
				.append(" model) throws CrudValidationException, CrudOperationException {")
				.append(System.lineSeparator());
		builder.append("checkForNull(model);").append(System.lineSeparator());
		builder.append("Optional<").append(entityModel).append("> existing = repository.findById(model.getId());")
				.append(System.lineSeparator());
		builder.append("if(existing.isEmpty()) {").append(System.lineSeparator());
		builder.append("throw CrudOperationException.asEntityNotFound(").append(entityModel)
				.append(".class, model.getId());").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());
		builder.append(entityModel).append(" existingModel = existing.get();").append(System.lineSeparator());
		builder.append("validate(model);").append(System.lineSeparator());
		builder.append("existingModel.updateFrom(model);").append(System.lineSeparator());
		builder.append("return save(existingModel);").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addGetCount() {
		builder.append("public long count() throws CrudOperationException {");
		builder.append("try {").append(System.lineSeparator());
		builder.append("return repository.count();").append(System.lineSeparator());
		builder.append("} catch (Exception exception) {").append(System.lineSeparator());
		builder.append("throw CrudOperationException.asFailedGetOperation(").append(entityModel)
				.append(".class, exception);").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addFindableGetCount() {

		List<Column> findableColumns = columns.stream()
				.filter(column -> (column.isFindable() || column.isFindableAsList()) && !column.isPrimaryKey())
				.collect(Collectors.toList());
		for (Column column : findableColumns) {
			builder.append("public long countBy").append(toSentenceCase(column.getName())).append("(");
			builder.append(column.getJavaDataType()).append(" ").append(toCamelCase(column.getName()));
			builder.append(") throws CrudOperationException {").append(System.lineSeparator());
			builder.append("try {").append(System.lineSeparator());
			builder.append("return repository.").append("countBy").append(toSentenceCase(column.getName())).append("(");
			builder.append(toCamelCase(column.getName())).append(");").append(System.lineSeparator());
			builder.append("} catch (Exception exception) {").append(System.lineSeparator());
			builder.append("throw CrudOperationException.asFailedGetOperation(").append(entityModel)
					.append(".class, exception);").append(System.lineSeparator());
			builder.append("}").append(System.lineSeparator());
			builder.append("}").append(System.lineSeparator());
		}

		return this;
	}

	private ServiceCoder addDelete() {
		builder.append("public long deleteAll() {").append(System.lineSeparator());
		builder.append("try {").append(System.lineSeparator());
		builder.append("long count = repository.count();").append(System.lineSeparator());
		builder.append("repository.deleteAll();").append(System.lineSeparator());
		builder.append("log.info(\"deleted all\" + count + \" entires\");").append(System.lineSeparator());
		builder.append("return count;").append(System.lineSeparator());
		builder.append("} catch (Exception exception) {").append(System.lineSeparator());
		builder.append("throw CrudOperationException.asFailedDeleteOperation(").append(entityModel)
				.append(".class, exception);").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());
		builder.append("}").append(System.lineSeparator());

		return this;
	}

	private ServiceCoder addDeleteById() {
		Column primaryKey = columns.stream()
				.filter(column -> column.isFindable() && column.isPrimaryKey())
				.findFirst()
				.orElse(null);

		if (primaryKey != null) {
			builder.append("@Transactional").append(System.lineSeparator());
			builder.append("public long deleteById(").append("long id").append(") {").append(System.lineSeparator());
			builder.append("try {").append(System.lineSeparator());
			builder.append("Optional<").append(entityModel).append("> result = repository.findById(id);").append(System.lineSeparator());
			builder.append("if(result.isPresent()) {").append(System.lineSeparator());
			builder.append("repository.deleteById(id);\n}").append(System.lineSeparator());
			builder.append("log.info(\"Deleted \" + 1 + \" number of entries with id: \" + id);").append(System.lineSeparator());
			builder.append("return 1;");
			builder.append("} catch (Exception exception) {").append(System.lineSeparator());
			builder.append("throw CrudOperationException.asFailedDeleteOperation(").append(entityModel)
					.append(".class, exception);").append(System.lineSeparator());
			builder.append("}").append(System.lineSeparator());
			builder.append("}").append(System.lineSeparator());
		}
		return this;
	}

	private ServiceCoder addGetById() {
		Column primaryKey = columns.stream()
				.filter(column -> column.isFindable() && column.isPrimaryKey())
				.findFirst()
				.orElse(null);

		if (primaryKey != null) {
			builder.append("public ").append(entityModel).append(" getById(").append(idDataType)
					.append(" id) throws CrudOperationException, CrudValidationException {")
					.append(System.lineSeparator());
			builder.append("checkId(id);").append(System.lineSeparator());
			builder.append("try {").append(System.lineSeparator());
			builder.append("Optional<").append(entityModel).append("> result = repository.findById(id);")
					.append(System.lineSeparator());
			builder.append("if(result.isEmpty()) {").append(System.lineSeparator());
			builder.append("throw CrudOperationException.asEntityNotFound(").append(entityModel).append(".class, id);")
					.append(System.lineSeparator());
			builder.append("}").append(System.lineSeparator());
			builder.append("return result.get();").append(System.lineSeparator());
			builder.append("} catch (Exception exception) {").append(System.lineSeparator());
			builder.append("throw CrudOperationException.asFailedGetOperation(").append(entityModel)
					.append(".class, exception);").append(System.lineSeparator());
			builder.append("}").append(System.lineSeparator());
			builder.append("}").append(System.lineSeparator());
		}
		return this;
	}

	private ServiceCoder addDeleteFindableServices() {
		List<Column> findableColumns = columns.stream()
				.filter(column -> (column.isFindable() || column.isFindableAsList()) && !column.isPrimaryKey())
				.collect(Collectors.toList());
		for (Column column : findableColumns) {
			builder.append("@Transactional").append(System.lineSeparator());
			builder.append("public long deleteBy").append(toSentenceCase(column.getName())).append("(")
					.append(column.getJavaDataType()).append(" ").append(toCamelCase(column.getName())).append(") {")
					.append(System.lineSeparator());
			builder.append("try {").append(System.lineSeparator());
			builder.append("long count = repository.countBy").append(toSentenceCase(column.getName())).append("(")
					.append(toCamelCase(column.getName())).append(");").append(System.lineSeparator());
			builder.append("repository.deleteBy").append(toSentenceCase(column.getName())).append("(")
					.append(toCamelCase(column.getName())).append(");").append(System.lineSeparator());
			builder.append("log.info(\"Deleted \" + count + \" number of entries\");").append(System.lineSeparator());
			builder.append("return count;").append(System.lineSeparator());
			builder.append("} catch (Exception exception) {").append(System.lineSeparator());
			builder.append("throw CrudOperationException.asFailedDeleteOperation(").append(entityModel)
					.append(".class, exception);").append(System.lineSeparator());
			builder.append("}").append(System.lineSeparator());
			builder.append("}").append(System.lineSeparator());
		}
		return this;
	}
}
