package org.example;

public class Main {
    public static void main(String[] args) {
        try {
            new TorrentClient("src/main/resources/fight_club.torrent").start();
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}