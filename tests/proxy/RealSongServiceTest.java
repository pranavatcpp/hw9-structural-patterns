package proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Dependency-free service checks. Searches retain the real server delay. */
public class RealSongServiceTest {
    public static void main(String[] args) {
        Song first = new Song(1, "Sunrise", "Maya", "Morning Light", 210);
        Song second = new Song(2, "Sunrise", "Leo", "New Day", 240);
        Song third = new Song(3, "Open Road", "Maya", "Morning Light", 185);
        List<Song> library = new ArrayList<>(Arrays.asList(first, second, third));
        SongService service = new RealSongService(library);
        library.clear();

        long start = System.nanoTime();
        check(service.searchById(1) == first, "ID lookup must find the matching song.");
        check(System.nanoTime() - start >= 1_000_000_000L, "Real lookup must simulate delay.");
        check(first.getArtist().equals("Maya") && first.getDuration() == 210,
                "Song must preserve metadata.");
        List<Song> titles = service.searchByTitle("Sunrise");
        check(titles.equals(Arrays.asList(first, second)), "Return every matching title.");
        titles.clear();
        check(service.searchByTitle("Sunrise").size() == 2,
                "Changing results must not change the server library.");
        check(service.searchByAlbum("Morning Light").equals(Arrays.asList(first, third)),
                "Return every song in the matching album.");
        check(service.searchById(99) == null, "Missing IDs return null.");
        check(service.searchByTitle("sunrise").isEmpty(), "Title matching is case-sensitive.");
        check(service.searchByAlbum("Missing").isEmpty(), "Missing albums return an empty list.");
        try {
            new RealSongService(Arrays.asList(first, first));
            throw new AssertionError("Duplicate IDs must be rejected.");
        } catch (IllegalArgumentException expected) {
            // Expected: an ID uniquely identifies one song.
        }
        Thread.currentThread().interrupt();
        try {
            service.searchById(1);
            throw new AssertionError("Interrupted searches must fail.");
        } catch (IllegalStateException expected) {
            check(Thread.currentThread().isInterrupted(), "Preserve interrupt status.");
        } finally {
            Thread.interrupted();
        }
        System.out.println("RealSongService checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
