package org.example.utils;

import java.security.MessageDigest;

public class HashUtils {

    public static byte[] sha1(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        return digest.digest(data);
    }
}