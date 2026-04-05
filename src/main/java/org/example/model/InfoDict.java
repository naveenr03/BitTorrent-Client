package org.example.model;

public class InfoDict {
    private String name;
    private int pieceLength;
    private byte[] pieces;
    private long length;

    @Override
    public String toString() {
        return "InfoDict {\n" +
                "  name='" + name + "',\n" +
                "  pieceLength=" + pieceLength + ",\n" +
                "  piecesLength=" + (pieces != null ? pieces.length : 0) + ",\n" +
                "  length=" + length + "\n" +
                "}";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPieceLength() {
        return pieceLength;
    }

    public void setPieceLength(int pieceLength) {
        this.pieceLength = pieceLength;
    }

    public byte[] getPieces() {
        return pieces;
    }

    public void setPieces(byte[] pieces) {
        this.pieces = pieces;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
