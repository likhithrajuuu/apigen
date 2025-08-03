package com.likhithraju.apigen.configs;

public class CodeRequest {
    private String filename;
    private String code;

    public CodeRequest() {
    }

    public CodeRequest(String filename, String code) {
        this.filename = filename;
        this.code = code;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
