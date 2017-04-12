package com.aes;

/**
 * Created by arsi on 05/04/17.
 */

import com.aes.spec.CalculatorSpec;
import com.aes.util.StringUtil;
import com.aes.util.Type;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

class Aes {
    static String start(CalculatorSpec spec) throws Exception {
        Path inputPath = Paths.get(spec.getInputFile().getAbsolutePath());
        Path keyPath = Paths.get(spec.getKeyFile().getAbsolutePath());

        String keyHex = Files.readAllLines(keyPath).get(0);
        byte[] keyBytes = StringUtil.hexStringToByteArray(keyHex);
        byte[] input = Files.readAllBytes(inputPath);

        if (spec.getType() == Type.ENCRYPT){
            return encrypt(spec.getInputFile().getName(), input, keyBytes);
        } else {
            return decrypt(input, keyBytes);
        }
    }

    private static String encrypt(String fileName, byte[] input, byte[] keyBytes) throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");

        String hexFileName = StringUtil.toHexString(fileName + ";");
        byte[] byteFileName = StringUtil.hexStringToByteArray(hexFileName);
        input = ArrayUtils.addAll(byteFileName, input);
        byte[] iv = StringUtil.generateIV();

        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input);
        CipherInputStream cipherInputStream = new CipherInputStream(byteArrayInputStream, cipher);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ch;
        while ((ch = cipherInputStream.read()) >= 0) {
            byteArrayOutputStream.write(ch);
        }

        byte[] cipherText = byteArrayOutputStream.toByteArray();

        Path cipherPath = Paths.get("cipher");

        byte[] ivPlusDelim = ArrayUtils.addAll(iv, StringUtil.hexStringToByteArray(StringUtil.toHexString("?")));
        Files.write(cipherPath, ArrayUtils.addAll(ivPlusDelim, cipherText));

        return cipherPath.toAbsolutePath().toString();
    }

    private static String decrypt(byte[] input, byte[] keyBytes) throws Exception{
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");

        ByteArrayOutputStream byteArrayOutputStream;

        int ivDelimiterPos = StringUtil.findIvDelimiter(input);
        input = ArrayUtils.removeElement(input, (byte) 0x3F);

        byte[] ivByte = new byte[ivDelimiterPos];
        System.arraycopy(input, 0, ivByte, 0, ivDelimiterPos);
        input = Arrays.copyOfRange(input, ivDelimiterPos, input.length);

        System.out.print(StringUtil.toHexString(ivByte));
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivByte));
        byteArrayOutputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(byteArrayOutputStream, cipher);
        cipherOutputStream.write(input);
        cipherOutputStream.close();

        byte[] decryptedByte = byteArrayOutputStream.toByteArray();
        int nameDelimiterPos = StringUtil.findNameDelimiter(decryptedByte);
        decryptedByte = ArrayUtils.removeElement(decryptedByte, (byte) 0x3B); // removing ";"

        byte[] byteFileName = new byte[nameDelimiterPos];
        System.arraycopy(decryptedByte, 0, byteFileName, 0, nameDelimiterPos);
        String fileName = StringUtil.fromHexString(StringUtil.toHexString(byteFileName));
        byte[] fileBytes = Arrays.copyOfRange(decryptedByte, nameDelimiterPos, decryptedByte.length);

        Path decryptedPath = Paths.get("[decrypted] " + fileName);
        Files.write(decryptedPath, fileBytes);

        return decryptedPath.toAbsolutePath().toString();
    }
}