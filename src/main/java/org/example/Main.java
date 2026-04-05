package org.example;

import org.example.bencode.BenCodeParser;
import org.example.model.Peer;
import org.example.model.TorrentMeta;
import org.example.tracker.PeerParser;
import org.example.tracker.TrackerClient;
import org.example.tracker.TrackerRequestBuilder;
import org.example.utils.HashUtils;

import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        try {
            // 1. Parse torrent
            TorrentMeta meta = BenCodeParser.parse("src/main/resources/fight_club.torrent");
            System.out.println(meta);

            // 2. Generate info_hash from the raw info bytes (not re-encoded)
            byte[] rawInfo = BenCodeParser.getRawInfo();
            byte[] infoHash = HashUtils.sha1(rawInfo);

            // Diagnostic: print hex of info_hash and raw info length
            StringBuilder hex = new StringBuilder();
            for (byte b : infoHash) hex.append(String.format("%02x", b));
            System.out.println("Raw info bytes length : " + rawInfo.length);
            System.out.println("Info hash (hex)       : " + hex);

            System.out.println("Info hash generated ✅");

            // 3. Pick first HTTP tracker (announce may be UDP-only)
            String trackerUrl = null;
            if (meta.getAnnounceList() != null) {
                for (String url : meta.getAnnounceList()) {
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        trackerUrl = TrackerRequestBuilder.build(url, infoHash, meta.getInfo().getLength());
                        break;
                    }
                }
            }
            if (trackerUrl == null) {
                trackerUrl = TrackerRequestBuilder.build(meta.getAnnounce(), infoHash, meta.getInfo().getLength());
            }
            System.out.println("Tracker URL: " + trackerUrl);

            // 4. Call tracker
            Map<String, Object> trackerResponse =
                    TrackerClient.getTrackerResponse(trackerUrl);

            System.out.println("Tracker response received ✅");

            // 5. Parse peers
            byte[] peersBytes = (byte[]) trackerResponse.get("peers");

            List<Peer> peers = PeerParser.parse(peersBytes);

            System.out.println("Peers found:");
            for (Peer peer : peers) {
                System.out.println(peer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}