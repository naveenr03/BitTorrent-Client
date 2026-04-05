package org.example.bencode;

import java.util.List;
import java.util.Map;

public interface BenCodeType {
    enum Type {
        INTEGER, STRING, LIST, DICTIONARY
    }
    Type getType();
    Object getValue();

    public static class BenCodeInteger implements BenCodeType {
        private final long value;
        public BenCodeInteger(long value) { this.value = value; }
        public Type getType() { return Type.INTEGER; }
        public Object getValue() { return value; }
        public long longValue() { return value; }
    }

    public static class BenCodeString implements BenCodeType {
        private final byte[] value;
        public BenCodeString(byte[] value) { this.value = value; }
        public Type getType() { return Type.STRING; }
        public Object getValue() { return value; }
        public byte[] bytesValue() { return value; }
        public String stringValue() { return new String(value); }
    }

    public static class BenCodeList implements BenCodeType {
        private final List<BenCodeType> value;
        public BenCodeList(List<BenCodeType> value) { this.value = value; }
        public Type getType() { return Type.LIST; }
        public Object getValue() { return value; }
        public List<BenCodeType> listValue() { return value; }
    }

    public static class BenCodeDictionary implements BenCodeType {
        private final Map<String, BenCodeType> value;
        public BenCodeDictionary(Map<String, BenCodeType> value) { this.value = value; }
        public Type getType() { return Type.DICTIONARY; }
        public Object getValue() { return value; }
        public Map<String, BenCodeType> mapValue() { return value; }
    }
}