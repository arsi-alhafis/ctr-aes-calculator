package com.aes; /**
 * Created by arsi on 05/04/17.
 */

import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import java.io.*;
import java.io.File;
import java.lang.Integer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Aes {
    private static byte[] ivBytes = new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d,
            0x0e, 0x0f};

    public static String start(File inputFile, File keyFile, Type type) throws Exception {
        Path inputPath = Paths.get(inputFile.getAbsolutePath());
        Path keyPath = Paths.get(keyFile.getAbsolutePath());

        String keyHex = Files.readAllLines(keyPath).get(0);
        byte[] keyBytes = hexStringToByteArray(keyHex);

        byte[] input = Files.readAllBytes(inputPath);

        if (type.equals(Type.ENCRYPT)){
            return encrypt(inputFile.getName(), input, keyBytes, ivBytes);
        } else {
            return decrypt(input, keyBytes, ivBytes);
        }
    }

    private static String encrypt(String fileName, byte[] input, byte[] keyBytes, byte[] ivBytes) throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");

        String hexFileName = toHexString(fileName + ";");
        byte[] byteFileName = hexStringToByteArray(hexFileName);
        input = ArrayUtils.addAll(byteFileName, input);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input);
        CipherInputStream cipherInputStream = new CipherInputStream(byteArrayInputStream, cipher);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ch;
        while ((ch = cipherInputStream.read()) >= 0) {
            byteArrayOutputStream.write(ch);
        }

        byte[] cipherText = byteArrayOutputStream.toByteArray();

        Path cipherPath = Paths.get("cipher");
        Files.write(cipherPath, cipherText);

        return cipherPath.toAbsolutePath().toString();
    }

    private static String decrypt(byte[] input, byte[] keyBytes, byte[] ivBytes) throws Exception{
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");

        ByteArrayOutputStream byteArrayOutputStream;

        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byteArrayOutputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(byteArrayOutputStream, cipher);
        cipherOutputStream.write(input);
        cipherOutputStream.close();

        byte[] decryptedByte = byteArrayOutputStream.toByteArray();
        int count = 0;
        while (true) {
            if (decryptedByte[count] == (byte) 0x3B) {
                decryptedByte = ArrayUtils.removeElement(decryptedByte, (byte) 0x3B);
                break;
            }
            count++;
        }

        byte[] byteFileName = new byte[count];
        for(int i = 0; i < count; i++) {
            byteFileName[i] = decryptedByte[i];
        }

        byte[] fileBytes = Arrays.copyOfRange(decryptedByte, count, decryptedByte.length);
        String fileName = fromHexString(toHexString(byteFileName));

        Path decryptedPath = Paths.get("[decrypted] " + fileName);
        Files.write(decryptedPath, fileBytes);

        return decryptedPath.toAbsolutePath().toString();
    }

    private static String toHexString(byte[] ba) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < ba.length; i++)
            str.append(String.format("%x", ba[i]));
        return str.toString();
    }

    private static String toHexString(String text) throws UnsupportedEncodingException {
        byte[] myBytes = text.getBytes("UTF-8");
        return DatatypeConverter.printHexBinary(myBytes);
    }

    private static String fromHexString(String hex) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
        }
        return str.toString();
    }

    public static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }
}
