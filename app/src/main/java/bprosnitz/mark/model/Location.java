package bprosnitz.mark.model;

public final class Location {
    private LocationType type;
    private android.location.Location location;

    public Location(LocationType type, android.location.Location location) {
        this.type = type;
        this.location = location;
    }

    public LocationType getType() {
        return type;
    }

    public android.location.Location getLocation() {
        return location;
    }
}
