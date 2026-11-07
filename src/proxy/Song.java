package proxy;

import java.util.Objects;

/** Immutable song metadata; duration is measured in seconds. */
public final class Song {
    private final Integer id;
    private final String title;
    private final String artist;
    private final String album;
    private final int duration;

    public Song(Integer id, String title, String artist, String album, int duration) {
        this.id = Objects.requireNonNull(id, "Song ID must not be null.");
        this.title = Objects.requireNonNull(title, "Title must not be null.");
        this.artist = Objects.requireNonNull(artist, "Artist must not be null.");
        this.album = Objects.requireNonNull(album, "Album must not be null.");
        if (duration < 0) {
            throw new IllegalArgumentException("Duration must not be negative.");
        }
        this.duration = duration;
    }

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public int getDuration() { return duration; }

    @Override
    public String toString() {
        return id + ": " + title + " by " + artist + " | " + album + " | " + duration + " seconds";
    }
}
