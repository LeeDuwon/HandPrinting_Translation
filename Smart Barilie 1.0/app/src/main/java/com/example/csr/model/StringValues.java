package com.example.csr.model;

public class StringValues {
    private String beforeTranslation;
    private String afterTranslation;

    public StringValues(String beforeTranslation, String afterTranslation) {
        this.beforeTranslation = beforeTranslation;
        this.afterTranslation = afterTranslation;
    }

    public String getBeforeTranslation() {
        return beforeTranslation;
    }

    public void setBeforeTranslation(String beforeTranslation) {
        this.beforeTranslation = beforeTranslation;
    }

    public String getAfterTranslation() {
        return afterTranslation;
    }

    public void setAfterTranslation(String afterTranslation) {
        this.afterTranslation = afterTranslation;
    }
}
