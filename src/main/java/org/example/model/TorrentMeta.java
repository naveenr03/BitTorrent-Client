package org.example.model;

import java.util.List;

public class TorrentMeta {

    private String announce;
    private InfoDict info;
    private List<String> announceList;

    @Override
    public String toString() {
        return "TorrentMeta {\n" +
                "  announce='" + announce + "',\n" +
                "  announceList=" + announceList + ",\n" +
                "  info=" + info + "\n" +
                "}";
    }

    public String getAnnounce() { return announce; }
    public void setAnnounce(String announce) { this.announce = announce; }

    public InfoDict getInfo() { return info; }
    public void setInfo(InfoDict info) { this.info = info; }

    public List<String> getAnnounceList() { return announceList; }
    public void setAnnounceList(List<String> announceList) { this.announceList = announceList; }
}
