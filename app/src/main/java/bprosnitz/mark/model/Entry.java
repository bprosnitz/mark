package bprosnitz.mark.model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.media.Image;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Entry {
    private int id;
    private String title;
    private List<Location> locations;
    private String notes;
    private String directions;
    private List<Image> images;
    private List<Tag> tags;

    private Entry() {
        this.title = "Title";
        this.locations = new ArrayList<>();
        this.notes = "Notes";
        this.directions = "Directions";
        this.images = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public static Entry newEntryFromLocation(Location loc) {
        Entry entry = new Entry();
        entry.locations = Arrays.asList(loc);
        return entry;
    }

    public static Entry newEntryFromId(int id) {
        // implement
        throw new RuntimeException("cannot yet lookup entry by id");
    }

    public String getTitle() {
        return this.title;
    }
    public List<Location> getLocations() {
        return this.locations;
    }
    public String getNotes(){
        return this.notes;
    }
    public String getDirections() {
        return this.directions;
    }
}
