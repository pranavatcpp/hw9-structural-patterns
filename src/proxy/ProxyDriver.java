package proxy;

import java.util.Arrays;
import java.util.function.Supplier;

/** Entry point for the Proxy song metadata demonstration. */
public class ProxyDriver {
    public static void main(String[] args) {
        SongService server = new RealSongService(Arrays.asList(
                new Song(1, "Sunrise", "Maya", "Morning Light", 210),
                new Song(2, "Open Road", "Maya", "Morning Light", 185),
                new Song(3, "Sunrise", "Leo", "New Day", 240),
                new Song(4, "City Lights", "Leo", "New Day", 198),
                new Song(5, "Ocean", "Nora", "Blue", 225),
                new Song(6, "Waves", "Nora", "Blue", 202)));

        SongService service = new CachingSongService(server);
        System.out.println("First requests reach the server; repeated requests use the cache.");
        demonstrate("ID 3", () -> service.searchById(3));
        demonstrate("Title Sunrise", () -> service.searchByTitle("Sunrise"));
        demonstrate("Album Blue", () -> service.searchByAlbum("Blue"));
        demonstrate("Missing ID 99", () -> service.searchById(99));
    }

    private static void demonstrate(String label, Supplier<?> search) {
        for (int attempt = 1; attempt <= 2; attempt++) {
            long start = System.nanoTime();
            Object result = search.get();
            double elapsedMillis = (System.nanoTime() - start) / 1_000_000.0;
            System.out.printf("%s (%s): %s [%.2f ms]%n", label,
                    attempt == 1 ? "server" : "cached", result, elapsedMillis);
        }
    }
}
