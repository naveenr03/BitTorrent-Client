package org.example.bencode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BenCodeEncoder {


    public static byte[] encode(Object obj) {
        if (obj instanceof Map) return encodeDictionary((Map<String, Object>) obj);
        if (obj instanceof List) return encodeList((List<Object>) obj);
        if (obj instanceof byte[]) return encodeString((byte[]) obj);
        if (obj instanceof Long) return encodeInteger((Long) obj);

        throw new RuntimeException("Unsupported type");
    }

    private static byte[] encodeInteger(Long value) {
        return ("i" + value + "e").getBytes();
    }
    private static byte[] encodeString(byte[] value) {
        byte[] prefix = (value.length + ":").getBytes();
        byte[] result = new byte[prefix.length + value.length];

        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(value, 0, result, prefix.length, value.length);

        return result;
    }


    private static byte[] encodeList(List<Object> list) {
        List<byte[]> parts = new ArrayList<>();
        parts.add("l".getBytes());

        for (Object item : list) {
            parts.add(encode(item));
        }

        parts.add("e".getBytes());
        return join(parts);
    }

    private static byte[] encodeDictionary(Map<String, Object> map) {
        List<byte[]> parts = new ArrayList<>();
        parts.add("d".getBytes());

        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);

        for (String key : keys) {
            parts.add(encode(key.getBytes()));
            parts.add(encode(map.get(key)));
        }

        parts.add("e".getBytes());
        return join(parts);
    }

    private static byte[] join(List<byte[]> parts) {
        int total = parts.stream().mapToInt(p -> p.length).sum();
        byte[] result = new byte[total];

        int pos = 0;
        for (byte[] part : parts) {
            System.arraycopy(part, 0, result, pos, part.length);
            pos += part.length;
        }

        return result;
    }


}
