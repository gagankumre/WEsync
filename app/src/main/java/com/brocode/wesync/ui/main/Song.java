package com.brocode.wesync.ui.main;

public class Song {

    private String songName;
    private String artistName;
    private String songLocation;

    public Song(String songName, String artistName, String songLocation) {
        this.songName = songName;
        this.artistName = artistName;
        this.songLocation = songLocation;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongLocation() {
        return songLocation;
    }

    public void setSongLocation(String songLocation) {
        this.songLocation = songLocation;
    }
}
