package org.example.utils;

public class UrlUtils {

    public static String urlEncode(byte[] data) {
        StringBuilder sb = new StringBuilder();

        for (byte b : data) {
            int unsigned = b & 0xFF;
            // Unreserved characters per RFC 3986: A-Z a-z 0-9 - _ . ~
            if ((unsigned >= 'A' && unsigned <= 'Z') ||
                (unsigned >= 'a' && unsigned <= 'z') ||
                (unsigned >= '0' && unsigned <= '9') ||
                unsigned == '-' || unsigned == '_' ||
                unsigned == '.' || unsigned == '~') {
                sb.append((char) unsigned);
            } else {
                sb.append('%');
                sb.append(String.format("%02X", unsigned));
            }
        }

        return sb.toString();
    }


}