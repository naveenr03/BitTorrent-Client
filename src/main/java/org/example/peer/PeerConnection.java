package org.example.peer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class PeerConnection {

    private static final int CONNECT_TIMEOUT_MS = 3_000;
    private static final int READ_TIMEOUT_MS    = 5_000;

    private final String ip;
    private final int port;

    public PeerConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Opens a TCP connection, performs the BitTorrent handshake and validates
     * the returned info-hash.
     *
     * @return true on a successful, validated handshake
     * @throws IOException if the connection fails, times out, or the peer
     *                     returns an invalid / mismatched handshake
     */
    public boolean connectAndHandShake(byte[] infoHash, byte[] peerId) throws IOException {

        try (Socket socket = new Socket()) {

            socket.connect(new InetSocketAddress(ip, port), CONNECT_TIMEOUT_MS);
            socket.setSoTimeout(READ_TIMEOUT_MS);

            System.out.println("Connected to " + ip + ":" + port);

            OutputStream out = socket.getOutputStream();
            InputStream  in  = socket.getInputStream();

            // 1. Send handshake
            byte[] handshake = createHandshake(infoHash, peerId);
            out.write(handshake);
            out.flush();
            System.out.println("Handshake sent");

            // 2. Read response (68 bytes)
            byte[] response = in.readNBytes(68);

            if (response.length < 68) {
                throw new IOException("Incomplete handshake response: got " + response.length + " bytes");
            }

            // 3. Validate pstrlen + pstr (first 20 bytes)
            if (response[0] != 19) {
                throw new IOException("Unexpected pstrlen: " + response[0]);
            }
            String pstr = new String(response, 1, 19);
            if (!pstr.equals("BitTorrent protocol")) {
                throw new IOException("Unexpected protocol string: " + pstr);
            }

            // 4. Validate info_hash (bytes 28-48)
            byte[] receivedInfoHash = Arrays.copyOfRange(response, 28, 48);
            if (!Arrays.equals(infoHash, receivedInfoHash)) {
                throw new IOException("Info-hash mismatch");
            }

            System.out.println("Handshake successful ✅");
            return true;
        }
    }

    private byte[] createHandshake(byte[] infoHash, byte[] peerId) {
        byte[] handshake = new byte[68];

        // pstrlen
        handshake[0] = 19;

        // pstr
        byte[] protocol = "BitTorrent protocol".getBytes();
        System.arraycopy(protocol, 0, handshake, 1, protocol.length);

        // reserved (8 bytes, already 0)

        // info_hash (bytes 28-47)
        System.arraycopy(infoHash, 0, handshake, 28, 20);

        // peer_id (bytes 48-67)
        System.arraycopy(peerId, 0, handshake, 48, 20);

        return handshake;
    }
}

