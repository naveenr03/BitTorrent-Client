package org.example;

import org.example.bencode.BenCodeParser;
import org.example.model.Peer;
import org.example.model.TorrentMeta;
import org.example.peer.PeerConnection;
import org.example.tracker.PeerParser;
import org.example.tracker.TrackerClient;
import org.example.tracker.TrackerRequestBuilder;
import org.example.utils.HashUtils;

import java.util.List;
import java.util.Map;

/**
 * High-level orchestrator for the BitTorrent client.
 * Handles parsing, tracker communication, and peer handshakes.
 */
public class TorrentClient {

    private final String torrentPath;

    public TorrentClient(String torrentPath) {
        this.torrentPath = torrentPath;
    }

    public void start() throws Exception {
        TorrentMeta meta = parseTorrent();
        byte[] infoHash = computeInfoHash();
        printInfoHash(infoHash);

        List<Peer> peers = fetchPeers(meta, infoHash);
        printPeers(peers);

        byte[] peerId = TrackerRequestBuilder.generatePeerId();
        connectToPeers(peers, infoHash, peerId);
    }

    // ── Step 1 ──────────────────────────────────────────────────────────────

    private TorrentMeta parseTorrent() throws Exception {
        TorrentMeta meta = BenCodeParser.parse(torrentPath);
        System.out.println(meta);
        return meta;
    }

    // ── Step 2 ──────────────────────────────────────────────────────────────

    private byte[] computeInfoHash() throws Exception {
        byte[] rawInfo = BenCodeParser.getRawInfo();
        return HashUtils.sha1(rawInfo);
    }

    private void printInfoHash(byte[] infoHash) {
        byte[] rawInfo = BenCodeParser.getRawInfo();
        StringBuilder hex = new StringBuilder();
        for (byte b : infoHash) hex.append(String.format("%02x", b));
        System.out.println("Raw info bytes length : " + rawInfo.length);
        System.out.println("Info hash (hex)       : " + hex);
        System.out.println("Info hash generated ✅");
    }

    // ── Step 3 & 4 ──────────────────────────────────────────────────────────

    private List<Peer> fetchPeers(TorrentMeta meta, byte[] infoHash) throws Exception {
        String trackerUrl = resolveTrackerUrl(meta, infoHash);
        System.out.println("Tracker URL: " + trackerUrl);

        Map<String, Object> trackerResponse = TrackerClient.getTrackerResponse(trackerUrl);
        System.out.println("Tracker response received ✅");

        byte[] peersBytes = (byte[]) trackerResponse.get("peers");
        return PeerParser.parse(peersBytes);
    }

    private String resolveTrackerUrl(TorrentMeta meta, byte[] infoHash) {
        long length = meta.getInfo().getLength();

        // Prefer an HTTP/HTTPS tracker from the announce-list
        if (meta.getAnnounceList() != null) {
            for (String url : meta.getAnnounceList()) {
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return TrackerRequestBuilder.build(url, infoHash, length);
                }
            }
        }
        return TrackerRequestBuilder.build(meta.getAnnounce(), infoHash, length);
    }

    // ── Step 5 ──────────────────────────────────────────────────────────────

    private void printPeers(List<Peer> peers) {
        System.out.println("Peers found (" + peers.size() + "):");
        peers.forEach(System.out::println);
    }

    private void connectToPeers(List<Peer> peers, byte[] infoHash, byte[] peerId) {
        System.out.println("\nAttempting peer handshakes...");
        for (Peer peer : peers) {
            System.out.println("\nTrying peer: " + peer);
            try {
                PeerConnection connection = new PeerConnection(peer.getIp(), peer.getPort());
                if (connection.connectAndHandShake(infoHash, peerId)) {
                    System.out.println("Connection established ✅");
                    return; // stop after first successful handshake
                }
            } catch (Exception e) {
                System.out.println("Failed (" + e.getClass().getSimpleName() + "): " + e.getMessage());
            }
        }
        System.out.println("Could not complete a handshake with any peer.");
    }
}

