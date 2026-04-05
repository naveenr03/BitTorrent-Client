package org.example.tracker;

import org.example.model.Peer;
import java.util.ArrayList;
import java.util.List;

public class PeerParser {

    public static List<Peer> parse(byte[] peersBytes) {

        List<Peer> peers = new ArrayList<>();

        for (int i = 0; i < peersBytes.length; i += 6) {

            String ip = (peersBytes[i] & 0xFF) + "." +
                    (peersBytes[i+1] & 0xFF) + "." +
                    (peersBytes[i+2] & 0xFF) + "." +
                    (peersBytes[i+3] & 0xFF);

            int port = ((peersBytes[i+4] & 0xFF) << 8) |
                    (peersBytes[i+5] & 0xFF);

            peers.add(new Peer(ip, port));
        }

        return peers;
    }
}