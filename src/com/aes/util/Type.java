package com.aes.util;

/**
 * Created by arsi on 05/04/17.
 */

public enum Type {
    ENCRYPT ("Encrypt"),
    DECRYPT ("Decrypt");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    public String toString() {
        return this.type;
    }
}