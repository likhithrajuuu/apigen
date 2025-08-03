package com.likhithraju.apigen.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.javafaker.Faker;

public class PostgresToJavaTypeMapper {
    private static final Map<String, String> postgresToJavaTypeMap = new HashMap<>();
    
    static {
    	postgresToJavaTypeMap.put("smallint", "short");
    	postgresToJavaTypeMap.put("integer", "int");
    	postgresToJavaTypeMap.put("bigint", "long");
    	postgresToJavaTypeMap.put("decimal", "double");
    	postgresToJavaTypeMap.put("numeric", "double");
    	postgresToJavaTypeMap.put("real", "float");
    	postgresToJavaTypeMap.put("double precision", "double");
    	postgresToJavaTypeMap.put("serial", "int");
    	postgresToJavaTypeMap.put("bigserial", "long");

        // Character/String Types
    	postgresToJavaTypeMap.put("varchar", "String");
        postgresToJavaTypeMap.put("character varying", "String");
        postgresToJavaTypeMap.put("char", "char");
        postgresToJavaTypeMap.put("character", "char");
        postgresToJavaTypeMap.put("text", "String");
        postgresToJavaTypeMap.put("json", "jsonb");
        postgresToJavaTypeMap.put("jsonb", "josnb");
        postgresToJavaTypeMap.put("uuid", "java.util.UUID");
        postgresToJavaTypeMap.put("xml", "String");
        
        // Date/Time Types
        postgresToJavaTypeMap.put("date", "java.time.LocalDate");
        postgresToJavaTypeMap.put("time", "java.time.LocalTime");
        postgresToJavaTypeMap.put("timestamp", "java.time.LocalDateTime");
        postgresToJavaTypeMap.put("interval", "java.time.Duration");

        // Boolean Type
        postgresToJavaTypeMap.put("boolean", "boolean");
        postgresToJavaTypeMap.put("bool", "boolean");
    }

    public PostgresToJavaTypeMapper() {
    	
    }
    
    public static Optional<String> getJavaType(String postgresType) {
        if (postgresType == null) {
            return Optional.empty();
        }

        String javaType = postgresToJavaTypeMap.get(postgresType.toLowerCase());
        return Optional.ofNullable(javaType);
    }

    public static String getJavaTypeOrDefault(String postgresType, String defaultValue) {
        return getJavaType(postgresType).orElse(defaultValue);
    }

    public static Object getExampleValue(String javaType) {

        Faker faker = new Faker();

        switch (javaType) {
            case "short":
                return (short) faker.number().numberBetween(1, 100);
            case "int":
                return faker.number().numberBetween(1, 1000);
            case "long":
                return faker.number().numberBetween(1,10) + "";
            case "float":
                return faker.number().randomDouble(2, 1, 100);
            case "double":
                return faker.number().randomDouble(2, 1, 1000);
            case "boolean":
                return faker.bool().bool();
            case "char":
                return faker.lorem().character();
            case "String":
                return faker.lorem().word();
            case "java.util.UUID":
                return "UUID.randomUUID()";
            case "java.time.LocalDate":
                return "LocalDate.now()";
            case "java.time.LocalTime":
                return "LocalTime.now()";
            case "java.time.LocalDateTime":
                return "LocalDateTime.now()";
            case "jsonb":
                return "{\"sampleKey\": \"" + faker.lorem().word() + "\"}";
            default:
                return null;
        }
    }

}