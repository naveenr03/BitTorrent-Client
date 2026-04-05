package org.example.bencode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BenCodeDecoder {

    private final byte[] buf;
    private int pos;

    // Stores the start/end positions of the raw "info" value bytes
    private int infoStart = -1;
    private int infoEnd   = -1;

    public BenCodeDecoder(byte[] buf) {
        this.buf = buf;
        this.pos = 0;
    }

    public Object decode() throws Exception {
        int prefix = buf[pos++] & 0xFF;

        if (prefix == 'i') return decodeInteger();
        if (prefix == 'l') return decodeList();
        if (prefix == 'd') return decodeDictionary();
        if (Character.isDigit(prefix)) return decodeString(prefix);

        throw new RuntimeException("Invalid BenCode prefix: " + (char) prefix);
    }

    /** Returns the exact raw bytes of the "info" dictionary as it appeared in the file. */
    public byte[] getRawInfo() {
        if (infoStart == -1) return null;
        byte[] raw = new byte[infoEnd - infoStart];
        System.arraycopy(buf, infoStart, raw, 0, raw.length);
        return raw;
    }

    private long decodeInteger() throws Exception {
        int start = pos;
        while (buf[pos] != 'e') pos++;
        long value = Long.parseLong(new String(buf, start, pos - start));
        pos++; // consume 'e'
        return value;
    }

    private byte[] decodeString(int firstDigit) throws Exception {
        StringBuilder lenBuilder = new StringBuilder();
        lenBuilder.append((char) firstDigit);
        while (buf[pos] != ':') {
            lenBuilder.append((char) (buf[pos++] & 0xFF));
        }
        pos++; // consume ':'
        int length = Integer.parseInt(lenBuilder.toString());
        byte[] data = new byte[length];
        System.arraycopy(buf, pos, data, 0, length);
        pos += length;
        return data;
    }

    private List<Object> decodeList() throws Exception {
        List<Object> list = new ArrayList<>();
        while ((buf[pos] & 0xFF) != 'e') {
            list.add(decode());
        }
        pos++; // consume 'e'
        return list;
    }

    private Map<String, Object> decodeDictionary() throws Exception {
        Map<String, Object> map = new HashMap<>();
        while ((buf[pos] & 0xFF) != 'e') {
            // Decode key (always a string in bencode)
            int keyPrefix = buf[pos++] & 0xFF;
            String key = new String(decodeString(keyPrefix));

            // Track raw bytes of the "info" value
            if ("info".equals(key)) {
                infoStart = pos;
                Object value = decode();
                infoEnd = pos;
                map.put(key, value);
            } else {
                map.put(key, decode());
            }
        }
        pos++; // consume 'e'
        return map;
    }
}
