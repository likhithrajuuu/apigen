package com.likhithraju.apigen.coder;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.github.javafaker.Faker;
import com.likhithraju.apigen.configs.ApiConfigModel;
import com.likhithraju.apigen.configs.Column;
import com.likhithraju.apigen.util.PostgresToJavaTypeMapper;

public class TestCoder extends AbstractCoder {

        private String entityName;
        private String modelName;
        private String controllerName;
        private String basePackage;
        private List<Column> columns = null;
        static final String TEST_ANNOTATION = "@Test";
        ApiConfigModel configuration = null;
        Faker faker = new Faker();

        public TestCoder() {
                super();
        }

        @Override
        public String toCode(ApiConfigModel configuration) throws CoderException {
                this.configuration = configuration;
                this.basePackage = configuration.getBasePackage();
                this.controllerName = configuration.getDatabase().getModels().get(0).getEntityName() + "Controller";
                this.entityName = configuration.getDatabase().getModels().get(0).getEntityName() + "ControllerTests";
                this.modelName = configuration.getDatabase().getModels().get(0).getEntityName() + "Model";
                this.columns = configuration.getDatabase().getModels().get(0).getColumns();
                String code = addPackage()
                                .addDefaultImports()
                                .addModelAndControllerImports()
                                .addTestAnnotation()
                                .addClassName()
                                .addClassStartBlock()
                                .addControllerObject()
                                .addContextLoadTest()
                                .addPurgeFunction()
                                .addEmptyConstructor()
                                .addAppendingNewDataTest()
                                .addGetCountTests()
                                // .addGetByIdTest()
                                .addUpdateTest()
                                .addGetAllTest()
                                // .addDeleteByIdTest()
                                .addDeleteByColumnIdTest()
                                .addGetTestForFindableColumns()
                                .addGetTestForFindableColumnsList()
                                .addClassEndBlock()
                                .format();
                return code;
        }

        private TestCoder addPackage() {
                builder.append("package " + this.configuration.getBasePackage() + ";").append(System.lineSeparator());
                return this;
        }

        private TestCoder addDefaultImports() {
                builder.append("import static org.junit.jupiter.api.Assertions.assertEquals;")
                                .append(System.lineSeparator());
                builder.append("import static org.junit.jupiter.api.Assertions.assertNotEquals;")
                                .append(System.lineSeparator());
                builder.append("import static org.junit.jupiter.api.Assertions.assertNotNull;")
                                .append(System.lineSeparator());
                builder.append("import static org.junit.jupiter.api.Assertions.assertTrue;")
                                .append(System.lineSeparator());
                builder.append("import java.time.LocalDateTime;").append(System.lineSeparator());
                builder.append("import java.util.List;").append(System.lineSeparator());
                builder.append("import org.junit.jupiter.api.BeforeEach;").append(System.lineSeparator());
                builder.append("import org.junit.jupiter.api.Test;").append(System.lineSeparator());
                builder.append("import org.springframework.beans.factory.annotation.Autowired;")
                                .append(System.lineSeparator());
                builder.append("import org.springframework.boot.test.context.SpringBootTest;")
                                .append(System.lineSeparator());
                builder.append("import org.springframework.http.HttpStatus;").append(System.lineSeparator());
                builder.append("import org.springframework.http.ResponseEntity;").append(System.lineSeparator());
                builder.append("import com.fasterxml.jackson.databind.JsonNode;").append(System.lineSeparator());
                builder.append("import com.fasterxml.jackson.databind.ObjectMapper;").append(System.lineSeparator());
                builder.append("import java.time.LocalDate;").append(System.lineSeparator());

                return this;
        }

        private TestCoder addModelAndControllerImports() {
                builder.append("import ").append(basePackage).append(".controller.").append(controllerName).append(";")
                                .append(System.lineSeparator());
                builder.append("import ").append(basePackage).append(".model.").append(modelName).append(";")
                                .append(System.lineSeparator());
                return this;
        }

        private TestCoder addTestAnnotation() {
                builder.append("@SpringBootTest").append(System.lineSeparator());
                return this;
        }

        private TestCoder addClassName() {
                builder.append("public class ").append(entityName).append(System.lineSeparator());
                return this;
        }

        private TestCoder addClassStartBlock() {
                builder.append("{").append(System.lineSeparator());
                return this;
        }

        private TestCoder addClassEndBlock() {
                builder.append("}").append(System.lineSeparator());
                return this;
        }

        private TestCoder addPurgeFunction() {
                builder.append("@BeforeEach").append(System.lineSeparator());
                builder.append("void purgeTableBeforeEachTest() {").append(System.lineSeparator());
                builder.append("controller.deleteAll();").append(System.lineSeparator());
                builder.append("}").append(System.lineSeparator());

                return this;
        }

        private TestCoder addControllerObject() {
                builder.append("@Autowired").append(System.lineSeparator());
                builder.append("private ").append(this.controllerName).append(" controller;")
                                .append(System.lineSeparator());
                return this;
        }

        private TestCoder addContextLoadTest() {
                builder.append(TEST_ANNOTATION).append(System.lineSeparator());
                builder.append("void contextLoads() {").append(System.lineSeparator());
                builder.append(
                                "assertNotNull(controller, \"Controller cannot be null. A valid controller is required to test.\");")
                                .append(System.lineSeparator());
                builder.append("}").append(System.lineSeparator());
                return this;
        }

        private TestCoder addEmptyConstructor() {
                builder.append("public ").append(entityName).append("(){}");
                return this;
        }

        private TestCoder addAppendingNewDataTest() {
                builder.append(TEST_ANNOTATION).append(System.lineSeparator());
                builder.append("void addShouldSuccessfullyAdd").append(toSentenceCase(modelName))
                                .append("() throws Exception {").append(System.lineSeparator());
                addCreateModel();
                builder.append("ResponseEntity<?> response = controller.add(model);").append(System.lineSeparator());
                builder.append("assertNotNull(response, \"Add model controller returned a null response\");")
                                .append(System.lineSeparator());
                builder.append("assertTrue(response.getBody() instanceof ").append(modelName)
                                .append(", \"Add " + controllerName + " did not save the " + modelName + "\");")
                                .append(System.lineSeparator());
                builder.append(modelName).append(" savedModel = (").append(modelName).append(") response.getBody();")
                                .append(System.lineSeparator());
                builder.append("assertTrue(savedModel.getId() > 0, \" " + modelName
                                + " was not saved properly (ID <= 0)\");")
                                .append(System.lineSeparator());
                builder.append("}").append(System.lineSeparator());
                return this;
        }

        private TestCoder addGetCountTests() {
                builder.append(TEST_ANNOTATION).append(System.lineSeparator());
                builder.append("void getCountShouldReturnCorrectCountOf").append(toSentenceCase(modelName))
                                .append("s() throws Exception {")
                                .append(System.lineSeparator());

                for (int i = 1; i <= 3; i++) {
                        if (i == 1) {
                                addCreateModel();
                        } else {
                                addCreateModel(true);
                        }
                        builder.append("controller.add(model);").append(System.lineSeparator())
                                        .append(System.lineSeparator());
                }

                builder.append("ResponseEntity<?> response = controller.count();").append(System.lineSeparator());
                builder.append("assertNotNull(response, \"Get count returned a null response\");")
                                .append(System.lineSeparator());
                builder.append(
                                "assertTrue(response.getBody() instanceof Long, \"Get count did not return an numaric value\");")
                                .append(System.lineSeparator());
                builder.append("long count = (Long) response.getBody();").append(System.lineSeparator());
                builder.append("assertEquals(3, count, \"Get count did not return the expected number of ")
                                .append(modelName)
                                .append("s\");")
                                .append(System.lineSeparator());
                builder.append("}").append(System.lineSeparator());
                return this;
        }

        private TestCoder addGetByIdTest() {
                builder.append(TEST_ANNOTATION).append(System.lineSeparator());
                builder.append("void getByIdShouldReturn").append(toSentenceCase(modelName))
                                .append("() throws Exception {")
                                .append(System.lineSeparator());
                addCreateModel();
                builder.append("ResponseEntity<?> response = controller.add(model);").append(System.lineSeparator());
                builder.append(modelName).append(" savedModel = (").append(modelName).append(") response.getBody();")
                                .append(System.lineSeparator());
                builder.append("response = controller.getById(savedModel.getId());").append(System.lineSeparator());
                builder.append("assertNotNull(response, \"Get by ID returned a null response\");")
                                .append(System.lineSeparator());
                builder.append("assertTrue(response.getBody() instanceof ").append(modelName)
                                .append(", \"Add " + controllerName + " controller did not save the " + modelName
                                                + "\");")
                                .append(System.lineSeparator());
                builder.append(modelName).append(" foundModel = (").append(modelName).append(") response.getBody();")
                                .append(System.lineSeparator());
                builder.append(
                                "assertEquals(savedModel.getId(), foundModel.getId(), \"Get by ID returned wrong "
                                                + modelName + "\");")
                                .append(System.lineSeparator());
                builder.append("}").append(System.lineSeparator());
                return this;
        }

        private TestCoder addUpdateTest() {
                builder.append(TEST_ANNOTATION).append(System.lineSeparator());
                builder.append("void updateShouldSuccessfullyUpdate").append(toSentenceCase(modelName))
                                .append("() throws Exception {").append(System.lineSeparator());
                addCreateModel();
                builder.append("ResponseEntity<?> response = controller.add(model);").append(System.lineSeparator());
                builder.append(modelName).append(" savedModel = (").append(modelName).append(") response.getBody();")
                                .append(System.lineSeparator());

                for (Column col : columns) {
                        if (col.isUpdatable()) {
                                Object exampleValue = PostgresToJavaTypeMapper.getExampleValue(col.getJavaDataType());
                                String value = "String".equalsIgnoreCase(col.getJavaDataType())
                                                ? "\"" + exampleValue + "\""
                                                : exampleValue.toString();

                                builder.append("savedModel.set").append(toSentenceCase(col.getName())).append("(")
                                                .append(value)
                                                .append(");").append(System.lineSeparator());
                        }
                }

                builder.append("response = controller.update(savedModel);").append(System.lineSeparator());
                builder.append("assertNotNull(response, \"Update returned a null response\");")
                                .append(System.lineSeparator());
                builder.append("assertTrue(response.getBody() instanceof ").append(modelName)
                                .append(", \"Add " + controllerName + " did not save the " + modelName + "\");")
                                .append(System.lineSeparator());

                builder.append(modelName).append(" updatedModel = (").append(modelName).append(") response.getBody();")
                                .append(System.lineSeparator());
                builder.append(
                                "assertEquals(savedModel.getId(), updatedModel.getId(), \"Update did not return the same model\");")
                                .append(System.lineSeparator());

                for (Column col : columns) {
                        if (col.isUpdatable()) {
                                String methodName = toSentenceCase(col.getName());
                                String javaType = PostgresToJavaTypeMapper.getExampleValue(col.getJavaDataType())
                                                .toString();

                                if ("LocalDateTime.now()".equalsIgnoreCase(javaType)) {
                                        builder.append("assertNotNull(updatedModel.get").append(methodName)
                                                        .append("(), \"Updated model ").append(col.getName())
                                                        .append(" should not be null\");")
                                                        .append(System.lineSeparator());

                                        builder.append("assertNotEquals(savedModel.get").append(methodName)
                                                        .append("(), updatedModel.get").append(methodName)
                                                        .append("(), \"").append(col.getName())
                                                        .append(" should have been updated\");")
                                                        .append(System.lineSeparator());
                                } else {
                                        if ("Boolean".equalsIgnoreCase(col.getJavaDataType())) {
                                                builder.append("assertEquals(savedModel.is").append(methodName)
                                                                .append("(), updatedModel.is").append(methodName)
                                                                .append("(), \"Update did not update the model "
                                                                                + methodName
                                                                                + " correctly\");")
                                                                .append(System.lineSeparator());
                                        } else {
                                                builder.append("assertEquals(savedModel.get").append(methodName)
                                                                .append("(), updatedModel.get").append(methodName)
                                                                .append("(), \"Update did not update the model "
                                                                                + methodName
                                                                                + " correctly\");")
                                                                .append(System.lineSeparator());
                                        }
                                }
                        }
                }

                builder.append("}").append(System.lineSeparator());
                return this;
        }

        private TestCoder addGetAllTest() {
                builder.append(TEST_ANNOTATION).append(System.lineSeparator());
                builder.append("void getAllShouldReturnListOf").append(toSentenceCase(modelName))
                                .append("s() throws Exception {").append(System.lineSeparator());

                for (int i = 1; i <= 3; i++) {
                        if (i == 1) {
                                addCreateModel();
                        } else {
                                addCreateModel(true);
                        }
                        builder.append("controller.add(model);").append(System.lineSeparator())
                                        .append(System.lineSeparator());
                }

                builder.append("ResponseEntity<?> response = controller.getAll(0, 10);").append(System.lineSeparator());
                builder.append("assertNotNull(response, \"Get all returned a null response\");")
                                .append(System.lineSeparator());
                builder.append("List<").append(modelName).append("> items = (List<").append(modelName)
                                .append(">) response.getBody();").append(System.lineSeparator());
                builder.append("assertEquals(3, items.size(), \"Get all did not return expected number of ")
                                .append(modelName)
                                .append("s\");").append(System.lineSeparator());
                builder.append("}").append(System.lineSeparator());
                return this;
        }

        private TestCoder addDeleteByIdTest() {
                builder.append(TEST_ANNOTATION).append(System.lineSeparator());
                builder.append("void deleteByIdShouldRemove").append(toSentenceCase(modelName))
                                .append("() throws Exception {").append(System.lineSeparator());

                addCreateModel();
                builder.append("controller.add(model);").append(System.lineSeparator());

                builder.append(modelName).append(" createdModel = model;").append(System.lineSeparator());
                builder.append("Long idToDelete = createdModel.getId();").append(System.lineSeparator());

                builder.append("ResponseEntity<?> deleteResponse = controller.deleteById(idToDelete);")
                                .append(System.lineSeparator());
                builder.append("assertEquals(HttpStatus.OK, deleteResponse.getStatusCode(), ")
                                .append("\"Delete by ID did not return OK status\");").append(System.lineSeparator());

                builder.append("ResponseEntity<?> getResponse = controller.getById(idToDelete);")
                                .append(System.lineSeparator());
                builder.append("assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getResponse.getStatusCode(), ")
                                .append("\"Deleted ").append(modelName).append(" was still found by ID\");")
                                .append(System.lineSeparator());

                builder.append("}").append(System.lineSeparator());
                return this;
        }

        private TestCoder addDeleteByColumnIdTest() {

                for (Column col : this.columns) {
                        if (col.isFindable()) {
                                String columnName = col.getName();
                                String methodSuffix = toSentenceCase(columnName);

                                builder.append(TEST_ANNOTATION).append(System.lineSeparator());
                                builder.append("void deleteBy").append(methodSuffix)
                                                .append("ShouldRemove").append(toSentenceCase(modelName))
                                                .append("() throws Exception {")
                                                .append(System.lineSeparator());

                                addCreateModel();
                                builder.append("controller.add(model);").append(System.lineSeparator());

                                builder.append(col.getJavaDataType() + " ").append(toCamelCase(col.getName()))
                                                .append(" = model.get")
                                                .append(toSentenceCase(columnName)).append("();")
                                                .append(System.lineSeparator());

                                builder.append("ResponseEntity<?> deleteResponse = controller.deleteBy")
                                                .append(methodSuffix).append("(").append(toCamelCase(col.getName()))
                                                .append(");")
                                                .append(System.lineSeparator());
                                builder.append("assertEquals(HttpStatus.OK, deleteResponse.getStatusCode(), ")
                                                .append("\"Delete by ").append(columnName)
                                                .append(" ID did not return OK status\");")
                                                .append(System.lineSeparator());

                                builder.append("ResponseEntity<?> getResponse = controller.getBy")
                                                .append(methodSuffix).append("(").append(toCamelCase(col.getName()))
                                                .append(");")
                                                .append(System.lineSeparator());
                                builder.append(
                                                "assertTrue(getResponse.getBody() == null || getResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR, ")
                                                .append("\"Deleted ").append(modelName).append(" still found by ")
                                                .append(columnName)
                                                .append(" ID\");")
                                                .append(System.lineSeparator());

                                builder.append("}").append(System.lineSeparator());
                        }
                }
                return this;
        }

        private TestCoder addGetTestForFindableColumns() {
                for (Column col : this.columns) {
                        if (col.isFindable()) {
                                builder.append(TEST_ANNOTATION).append(System.lineSeparator());
                                builder.append("void getBy").append(toSentenceCase(col.getName()))
                                                .append("ShouldReturn")
                                                .append(toSentenceCase(modelName)).append("() throws Exception {")
                                                .append(System.lineSeparator());
                                addCreateModel();
                                builder.append("ResponseEntity<?> response = controller.add(model);")
                                                .append(System.lineSeparator());
                                builder.append(modelName).append(" savedModel = (").append(modelName)
                                                .append(") response.getBody();")
                                                .append(System.lineSeparator());
                                builder.append("response = controller.getBy").append(toSentenceCase(col.getName()))
                                                .append("(savedModel.get").append(toSentenceCase(col.getName()))
                                                .append("());")
                                                .append(System.lineSeparator());
                                builder.append("assertNotNull(response, \"Get by ").append(col.getName())
                                                .append(" returned a null response\");").append(System.lineSeparator());
                                builder.append("assertTrue(response.getBody() instanceof ").append(modelName)
                                                .append(", \"Get by ")
                                                .append(col.getName()).append(" did not return the expected model\");")
                                                .append(System.lineSeparator());
                                builder.append(modelName).append(" foundModel = (").append(modelName)
                                                .append(") response.getBody();")
                                                .append(System.lineSeparator());
                                builder.append("assertEquals(savedModel.get").append(toSentenceCase(col.getName()))
                                                .append("(), foundModel.get").append(toSentenceCase(col.getName()))
                                                .append("(), \"Get by ")
                                                .append(col.getName()).append(" returned incorrect model\");")
                                                .append(System.lineSeparator());
                                builder.append("}").append(System.lineSeparator());
                        }
                }
                return this;
        }

        private TestCoder addGetTestForFindableColumnsList() {
                for (Column col : this.columns) {
                        if (col.isFindableAsList()) {
                                builder.append(TEST_ANNOTATION).append(System.lineSeparator());
                                builder.append("void getAllBy").append(toSentenceCase(col.getName()))
                                                .append("ShouldReturnListOf")
                                                .append(toSentenceCase(modelName)).append("s() throws Exception {")
                                                .append(System.lineSeparator());

                                Pair<String, Object> dataTypeExample = Pair.of(col.getName(),
                                                PostgresToJavaTypeMapper.getExampleValue(col.getJavaDataType()));
                                for (int i = 1; i <= 3; i++) {
                                        if (i == 1) {
                                                addCreateModel(false, dataTypeExample);
                                        } else {
                                                addCreateModel(true, dataTypeExample);
                                        }
                                        builder.append("controller.add(model);").append(System.lineSeparator())
                                                        .append(System.lineSeparator());
                                }
                                builder.append("ResponseEntity<?> response = controller.getAllBy")
                                                .append(toSentenceCase(col.getName()))
                                                .append("(model.get").append(toSentenceCase(col.getName()))
                                                .append("());")
                                                .append(System.lineSeparator());
                                builder.append("assertNotNull(response, \"Get by ").append(col.getName())
                                                .append(" returned a null response\");").append(System.lineSeparator());
                                builder.append("assertTrue(response.getBody() instanceof List, \"Get by ")
                                                .append(col.getName())
                                                .append(" did not return a list of models\");")
                                                .append(System.lineSeparator());
                                builder.append("List<").append(modelName).append("> foundModels = (List<")
                                                .append(modelName)
                                                .append(">) response.getBody();").append(System.lineSeparator());
                                builder.append("assertEquals(3, foundModels.size(), \"Get by ").append(col.getName())
                                                .append(" did not return the expected number of models\");")
                                                .append(System.lineSeparator());
                                builder.append("}").append(System.lineSeparator());
                        }
                }
                return this;
        }

        private TestCoder addCreateModel() {
                return addCreateModel(false);
        }

        private TestCoder addCreateModel(boolean ignoreDatatype, Pair<String, Object> datatype) {

                if (!ignoreDatatype) {
                        builder.append(modelName).append(" model = new ").append(modelName).append("(")
                                        .append(System.lineSeparator());
                } else {
                        builder.append("model = new ").append(modelName).append("(").append(System.lineSeparator());
                }

                String result = columns.stream()
                                .map(col -> {
                                        if (col.isPrimaryKey()) {
                                                return "0L";
                                        }
                                        if (col.getName().equals(datatype.getKey())) {
                                                return datatype.getValue();
                                        }
                                        Object exampleValue = PostgresToJavaTypeMapper
                                                        .getExampleValue(col.getJavaDataType());
                                        if ("String".equalsIgnoreCase(col.getJavaDataType())) {
                                                return "\"" + exampleValue + "\"";
                                        }
                                        if ("LocalDateTime".equalsIgnoreCase(col.getJavaDataType())) {
                                                return "LocalDateTime.now()";
                                        }
                                        return String.valueOf(exampleValue);
                                })
                                .map(String::valueOf)
                                .collect(Collectors.joining(", "));

                builder.append(result).append(");").append(System.lineSeparator());

                return this;

        }

        private TestCoder addCreateModel(boolean ignoreDatatype) {
                if (!ignoreDatatype) {
                        builder.append(modelName).append(" model = new ").append(modelName).append("(")
                                        .append(System.lineSeparator());
                } else {
                        builder.append("model = new ").append(modelName).append("(").append(System.lineSeparator());
                }

                String result = columns.stream()
                                .map(col -> {
                                        if (col.isPrimaryKey()) {
                                                return "0L";
                                        }
                                        Object exampleValue = PostgresToJavaTypeMapper
                                                        .getExampleValue(col.getJavaDataType());
                                        if ("String".equalsIgnoreCase(col.getJavaDataType())) {
                                                return "\"" + exampleValue + "\"";
                                        }
                                        if ("LocalDateTime".equalsIgnoreCase(col.getJavaDataType())) {
                                                return "LocalDateTime.now()";
                                        }
                                        return String.valueOf(exampleValue);
                                })
                                .collect(Collectors.joining(", "));

                builder.append(result).append(");").append(System.lineSeparator());
                return this;
        }
}
