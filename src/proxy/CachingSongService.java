package proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Caches exact queries for a fixed song library for this proxy's lifetime. */
public final class CachingSongService implements SongService {
    private final SongService server;
    private final Map<Integer, Song> ids = new HashMap<>();
    private final Map<String, List<Song>> titles = new HashMap<>();
    private final Map<String, List<Song>> albums = new HashMap<>();

    public CachingSongService(SongService server) {
        this.server = Objects.requireNonNull(server, "Server must not be null.");
    }

    @Override
    public Song searchById(Integer songID) {
        // containsKey also recognizes a cached missing-song result (null).
        if (!ids.containsKey(songID)) {
            ids.put(songID, server.searchById(songID));
        }
        return ids.get(songID);
    }

    @Override
    public List<Song> searchByTitle(String title) {
        if (!titles.containsKey(title)) {
            titles.put(title, new ArrayList<>(server.searchByTitle(title)));
        }
        return new ArrayList<>(titles.get(title));
    }

    @Override
    public List<Song> searchByAlbum(String album) {
        if (!albums.containsKey(album)) {
            albums.put(album, new ArrayList<>(server.searchByAlbum(album)));
        }
        return new ArrayList<>(albums.get(album));
    }
}
