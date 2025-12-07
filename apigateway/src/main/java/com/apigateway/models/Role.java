package com.apigateway.models;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
public class Role {

    @Id
    private String id;

    private EROLE name;

    public Role() {
    }

    public Role(EROLE name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EROLE getName() {
        return name;
    }

    public void setName(EROLE name) {
        this.name = name;
    }
}
