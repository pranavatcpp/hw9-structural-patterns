package proxy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Simulated server with a one-second delay on every search. */
public final class RealSongService implements SongService {
    private final Map<Integer, Song> songs = new LinkedHashMap<>();

    public RealSongService(List<Song> library) {
        Objects.requireNonNull(library, "Library must not be null.");
        for (Song song : library) {
            Objects.requireNonNull(song, "Song must not be null.");
            if (songs.put(song.getId(), song) != null) {
                throw new IllegalArgumentException("Duplicate song ID: " + song.getId());
            }
        }
    }

    @Override
    public Song searchById(Integer songID) {
        delay();
        return songs.get(songID);
    }

    @Override
    public List<Song> searchByTitle(String title) {
        delay();
        List<Song> matches = new ArrayList<>();
        for (Song song : songs.values()) {
            if (song.getTitle().equals(title)) {
                matches.add(song);
            }
        }
        return matches;
    }

    @Override
    public List<Song> searchByAlbum(String album) {
        delay();
        List<Song> matches = new ArrayList<>();
        for (Song song : songs.values()) {
            if (song.getAlbum().equals(album)) {
                matches.add(song);
            }
        }
        return matches;
    }

    private void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Song search interrupted.", interrupted);
        }
    }
}
