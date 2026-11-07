package proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** Counts server calls to verify caching without relying on timing. */
public class CachingSongServiceTest {
    public static void main(String[] args) {
        CountingService server = new CountingService();
        SongService proxy = new CachingSongService(server);
        check(server.idCalls + server.titleCalls + server.albumCalls == 0,
                "Creating the proxy must not fetch metadata.");
        check(proxy.searchById(1) == server.first, "Return the server's song.");
        check(proxy.searchById(1) == server.first && server.idCalls == 1,
                "Repeated IDs must not call the server.");
        check(proxy.searchById(2) == server.second && server.idCalls == 2,
                "Different IDs must have separate cache entries.");
        for (Integer missing : Arrays.asList(99, null)) {
            check(proxy.searchById(missing) == null && proxy.searchById(missing) == null,
                    "Missing IDs remain missing.");
        }
        check(server.idCalls == 4, "Missing and null IDs must also be cached.");

        List<Song> results = proxy.searchByTitle("Shared");
        check(results.equals(Arrays.asList(server.first, server.second)), "Preserve all matches.");
        results.clear();
        server.titleResults.clear();
        check(proxy.searchByTitle("Shared").size() == 2 && server.titleCalls == 1,
                "Neither callers nor the server can mutate cached lists.");
        List<Song> albums = proxy.searchByAlbum("Shared");
        check(albums.equals(Arrays.asList(server.second)),
                "Title and album caches must remain separate for identical query strings.");
        albums.clear();
        server.albumResults.clear();
        check(proxy.searchByAlbum("Shared").size() == 1 && server.albumCalls == 1,
                "Album cache must be protected from list mutation.");
        for (String missing : Arrays.asList("Missing", null)) {
            check(proxy.searchByTitle(missing).isEmpty() && proxy.searchByTitle(missing).isEmpty(),
                    "Missing title results remain empty.");
            check(proxy.searchByAlbum(missing).isEmpty() && proxy.searchByAlbum(missing).isEmpty(),
                    "Missing album results remain empty.");
        }
        check(server.titleCalls == 3 && server.albumCalls == 3,
                "Empty lists and null queries must be cached independently.");

        server.failNext = true;
        try {
            proxy.searchById(7);
            throw new AssertionError("Server failure must propagate.");
        } catch (IllegalStateException expected) {
            // A failed lookup must not create a cache entry.
        }
        check(proxy.searchById(7) == null && server.idCalls == 6,
                "Retry a failed lookup through the server.");
        proxy.searchById(7);
        check(server.idCalls == 6, "Cache the successful retry.");
        System.out.println("CachingSongService checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class CountingService implements SongService {
        private final Song first = new Song(1, "Shared", "Maya", "Other", 210);
        private final Song second = new Song(2, "Shared", "Leo", "Shared", 240);
        private final List<Song> titleResults = new ArrayList<>(Arrays.asList(first, second));
        private final List<Song> albumResults = new ArrayList<>(Arrays.asList(second));
        private int idCalls;
        private int titleCalls;
        private int albumCalls;
        private boolean failNext;

        public Song searchById(Integer id) {
            idCalls++;
            if (failNext) {
                failNext = false;
                throw new IllegalStateException("Simulated server failure.");
            }
            return Objects.equals(id, 1) ? first : Objects.equals(id, 2) ? second : null;
        }

        public List<Song> searchByTitle(String title) {
            titleCalls++;
            return "Shared".equals(title) ? titleResults : new ArrayList<>();
        }

        public List<Song> searchByAlbum(String album) {
            albumCalls++;
            return "Shared".equals(album) ? albumResults : new ArrayList<>();
        }
    }
}
