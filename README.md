# BitTorrent (Java) — current scope

Educational BitTorrent **metadata + tracker + peer handshake** prototype. It parses `.torrent` files, talks to **HTTP/HTTPS** trackers, and attempts a **plain BitTorrent v1** TCP handshake with returned peers.

**Requirements:** Java 21, Maven.

## Build and run

From the project root (so `src/main/resources/...` paths resolve):

```bash
mvn compile
java -cp target/classes org.example.Main
```

By default, `Main` loads `src/main/resources/fight_club.torrent`. Change the path in `Main.java` to use another torrent under `src/main/resources/`.

## What is implemented

| Area | Details |
|------|---------|
| **Bencode** | Decoder for torrent files; encoder/types exist for round-trips. `BenCodeDecoder` records the **raw bytes** of the `info` dictionary as they appear in the file (required for a correct **info hash**). |
| **Metadata** | `TorrentMeta` / `InfoDict`: announce URL, flattened `announce-list` (HTTP/HTTPS URLs), name, piece length, pieces blob, total size (single-file `length` or sum of multi-file `files`). |
| **Info hash** | SHA-1 over the **exact** bencoded `info` dictionary bytes (`HashUtils.sha1` + `BenCodeParser.getRawInfo()`). |
| **HTTP(S) tracker** | `TrackerClient` issues a **GET** and decodes the bencoded response. `TrackerRequestBuilder` adds `info_hash`, `peer_id`, `port`, `uploaded` / `downloaded` / `left`, `compact=1`, `event=started`. |
| **Tracker selection** | If `announce-list` is present, the **first** HTTP or HTTPS URL in that list is used; otherwise `announce` is used. Non-HTTP(S) announces (e.g. `udp://`) are not supported by this client. |
| **Compact peers** | `PeerParser` decodes the 6-byte IPv4 compact peer list from the tracker response. |
| **Peer handshake** | `PeerConnection` connects with timeouts, sends the 68-byte handshake (`BitTorrent protocol`, zero reserved, info hash, peer id), reads 68 bytes back, checks protocol string and matching info hash. |
| **Peer ID** | Random 20-byte id with prefix `-NV0001-` plus 12 decimal digits (`TrackerRequestBuilder.generatePeerId()`). |

## What is not implemented

- Downloading or uploading **pieces** (no disk I/O, no `request` / `piece` messages).
- **UDP tracker** (BEP 15), **DHT**, **PEX**.
- **Message Stream Encryption** (many peers never complete a plain handshake first).
- **BitTorrent v2** / hybrid torrents.
- Listening on a port, **choking**, **bitfield**, **keepalive**, endgame, etc.
- Robust tracker handling (no `failure reason` display, no `numwant`, no merging multiple announces).

## Project layout (main pieces)

```
src/main/java/org/example/
├── Main.java                 # Entry; passes a .torrent path to TorrentClient
├── TorrentClient.java        # Orchestrates parse → hash → tracker → handshake
├── bencode/                  # Bencode decode/encode
├── model/                    # TorrentMeta, InfoDict, Peer
├── peer/PeerConnection.java # TCP + BitTorrent handshake
├── tracker/                  # HTTP tracker, announce URL builder, compact peers
└── utils/                    # SHA-1, URL-encoding for tracker params
```

Sample `.torrent` files live under `src/main/resources/`.

## Known limitations

1. **`peer_id` consistency:** `TrackerRequestBuilder.build()` generates one random `peer_id` for the announce URL, while `TorrentClient.start()` generates **another** for the handshake. Trackers and peers normally expect the same id; fixing this means building the tracker URL with the same `peer_id` you use in `PeerConnection`.

2. **Network reality:** Most tracker addresses are unreachable from a given network (NAT, firewalls, stale peers). Corporate proxies (e.g. Zscaler) often interfere with **direct TCP** to arbitrary peers. Failures are often environmental, not proof that the handshake layout is wrong.

3. **Handshake string encoding:** The outgoing protocol name uses the JVM default charset; using `US_ASCII` for those 19 bytes avoids any platform ambiguity.

## References

- [BitTorrent specification](https://wiki.theory.org/BitTorrentSpecification) (wiki.theory.org)
- BEP index: [bittorrent.org/beps](http://bittorrent.org/beps/)

## License

This repository does not declare a license in the POM; treat as private/educational unless you add one.
