package org.example.tracker;

import org.example.bencode.BenCodeDecoder;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class TrackerClient {

    public static Map<String, Object> getTrackerResponse(String urlStr) throws Exception {

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        byte[] response = conn.getInputStream().readAllBytes();

        BenCodeDecoder decoder = new BenCodeDecoder(response);
        return (Map<String, Object>) decoder.decode();
    }
}