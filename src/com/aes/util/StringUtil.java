package com.aes.util;

/**
 * Created by arsi on 07-Apr-17.
 */

import org.apache.commons.lang3.ArrayUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Arrays;

public class StringUtil {
    public static String toHexString(byte[] ba) {
        StringBuilder str = new StringBuilder();
        for (byte aBa : ba) str.append(String.format("%x", aBa));
        return str.toString();
    }

    public static String toHexString(String text) throws UnsupportedEncodingException {
        byte[] myBytes = text.getBytes("UTF-8");
        return DatatypeConverter.printHexBinary(myBytes);
    }

    public static String fromHexString(String hex) {
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

    public static int findNameDelimiter(byte[] bytes) {
        int count = 0;
        while (true) {
            if (bytes[count] == (byte) 0x3B) { // 0x3B == ";"
                return count;
            }
            count++;
        }
    }

    public static byte[] generateIV() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] newSeed = secureRandom.generateSeed(16);
        secureRandom.setSeed(newSeed);

        byte[] byteIV = new byte[16];
        secureRandom.nextBytes(byteIV);

        return byteIV;
    }

    public static byte[] addPadding(byte[] input) {
        int res = 16 - input.length % 16;
        byte[] result = new byte[res];

        Arrays.fill(result,(byte) res);
        return ArrayUtils.addAll(input, result);
    }

    public static byte[] removePadding(byte[] input) {
        int res = input[input.length - 1];
        byte[] result = new byte[input.length - res];

        System.arraycopy(input, 0, result, 0, result.length);
        return result;
    }
}