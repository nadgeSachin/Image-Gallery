package com.example.imagegallery;
import java.io.Serializable;
import java.util.ArrayList;

public class AlbumModel implements Serializable {
    private String albumName;
    private ArrayList<String> photoPaths;

    public AlbumModel(String albumName, ArrayList<String> photoPaths) {
        this.albumName = albumName;
        this.photoPaths = photoPaths;
    }

    public String getAlbumName() {
        return albumName;
    }

    public ArrayList<String> getPhotoPaths() {
        return photoPaths;
    }
}
