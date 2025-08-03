package com.likhithraju.apigen.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likhithraju.apigen.ApiGenerator;
import com.likhithraju.apigen.coder.CoderException;
import com.likhithraju.apigen.configs.ApiConfigModel;
import com.likhithraju.apigen.configs.CodeRequest;

@RestController
@RequestMapping("/apigen/v1/")
public class ApiGenController {
    
    @Autowired
    private ApiGenerator apigen;

    @GetMapping("/test/connection")
    public ResponseEntity<String> testConnection()
    {
        return ResponseEntity.ok("connection is working");
    }

    @PostMapping("generate/all")
    public ResponseEntity<?> generateCode(@RequestBody ApiConfigModel config) {
        try {
            String message = apigen.generateAll(config);
            return ResponseEntity.ok(Map.of("success", true, "message", message));
        } catch (CoderException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "error", e.getMessage()
                    ));
        }
    }

    @PostMapping("generate/file")
    public ResponseEntity<?> generateJavaFile(@RequestBody CodeRequest code)
    {
        try{
            if(apigen.writeCodeToJavaFile(code.getFilename(),code.getCode()))
            {
                return ResponseEntity.ok(Map.of("message", code.getFilename() + " generated successfully"));

            }
            else{
                return ResponseEntity.internalServerError().body("Could not generate code");
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + e.getMessage());
        }
    }
    

    @PostMapping("preview/all")
    public ResponseEntity<?> generateAllPreviewCode(@RequestBody ApiConfigModel config) {
        try {
            Map<String,String> allCodePreview = apigen.generateAllCodeForPreview(config,0);
            return ResponseEntity.ok(allCodePreview);
        } catch (CoderException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
