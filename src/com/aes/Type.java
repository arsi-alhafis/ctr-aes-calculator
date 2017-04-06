package com.aes;

/**
 * Created by arsi on 05/04/17.
 */
public enum Type {
    ENCRYPT("encrypt"),
    DECRYPT("decrypt");

    private final String text;

    Type(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}