package org.example.tracker;

import org.example.utils.UrlUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class TrackerRequestBuilder {

    /**
     * Generates a random 20-byte peer ID: "-NV0001-" + 12 random digits.
     */
    public static byte[] generatePeerId() {
        byte[] peerIdBytes = new byte[20];
        byte[] prefix = "-NV0001-".getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(prefix, 0, peerIdBytes, 0, prefix.length);
        for (int i = prefix.length; i < 20; i++) {
            peerIdBytes[i] = (byte) ('0' + ThreadLocalRandom.current().nextInt(10));
        }
        return peerIdBytes;
    }

    public static String build(String announce, byte[] infoHash, long left) {
        byte[] peerIdBytes = generatePeerId();

        return announce +
                "?info_hash=" + UrlUtils.urlEncode(infoHash) +
                "&peer_id=" + UrlUtils.urlEncode(peerIdBytes) +
                "&port=6881" +
                "&uploaded=0" +
                "&downloaded=0" +
                "&left=" + left +
                "&compact=1" +
                "&event=started";
    }
}

