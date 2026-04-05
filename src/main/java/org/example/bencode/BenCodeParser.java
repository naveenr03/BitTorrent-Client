package org.example.bencode;

import org.example.model.InfoDict;
import org.example.model.TorrentMeta;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BenCodeParser {

    private static byte[] lastRawInfo;

    public static TorrentMeta parse(String filePath) throws Exception {
        // Read entire file into a byte array so we can track raw offsets
        byte[] fileBytes;
        try (FileInputStream fis = new FileInputStream(filePath)) {
            fileBytes = fis.readAllBytes();
        }

        BenCodeDecoder decoder = new BenCodeDecoder(fileBytes);
        Map<String, Object> root = (Map<String, Object>) decoder.decode();

        // Capture raw info bytes for correct info_hash computation
        lastRawInfo = decoder.getRawInfo();

        TorrentMeta meta = new TorrentMeta();
        meta.setAnnounce(new String((byte[]) root.get("announce")));

        // Parse announce-list (list of lists of tracker URLs)
        if (root.containsKey("announce-list")) {
            List<String> allTrackers = new ArrayList<>();
            List<Object> tierList = (List<Object>) root.get("announce-list");
            for (Object tier : tierList) {
                for (Object url : (List<Object>) tier) {
                    allTrackers.add(new String((byte[]) url));
                }
            }
            meta.setAnnounceList(allTrackers);
        }

        Map<String, Object> infoMap = (Map<String, Object>) root.get("info");

        InfoDict info = new InfoDict();
        info.setName(new String((byte[]) infoMap.get("name")));
        info.setPieceLength(((Long) infoMap.get("piece length")).intValue());
        info.setPieces((byte[]) infoMap.get("pieces"));

        if (infoMap.containsKey("length")) {
            info.setLength((Long) infoMap.get("length"));
        } else if (infoMap.containsKey("files")) {
            java.util.List<Object> files = (java.util.List<Object>) infoMap.get("files");
            long totalLength = 0;
            for (Object fileObj : files) {
                Map<String, Object> fileMap = (Map<String, Object>) fileObj;
                if (fileMap.containsKey("length")) {
                    totalLength += (Long) fileMap.get("length");
                }
            }
            info.setLength(totalLength);
        }

        meta.setInfo(info);
        return meta;
    }

    /** Returns the exact raw bytes of the info dictionary for SHA-1 hashing. */
    public static byte[] getRawInfo() {
        return lastRawInfo;
    }


}
