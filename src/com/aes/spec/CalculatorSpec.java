package com.aes.spec;

import com.aes.util.Type;
import java.io.File;

/**
 * Created by arsi on 07-Apr-17.
 */
public class CalculatorSpec {
    private File inputFile;
    private File keyFile;
    private Type type;

    public CalculatorSpec() {
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public File getKeyFile() {
        return keyFile;
    }

    public void setKeyFile(File keyFile) {
        this.keyFile = keyFile;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}