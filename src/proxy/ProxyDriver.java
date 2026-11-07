package proxy;

import java.util.Arrays;

/** Entry point for the Proxy song metadata demonstration. */
public class ProxyDriver {
    public static void main(String[] args) {
        SongService service = new RealSongService(Arrays.asList(
                new Song(1, "Sunrise", "Maya", "Morning Light", 210),
                new Song(2, "Open Road", "Maya", "Morning Light", 185),
                new Song(3, "Sunrise", "Leo", "New Day", 240),
                new Song(4, "City Lights", "Leo", "New Day", 198),
                new Song(5, "Ocean", "Nora", "Blue", 225),
                new Song(6, "Waves", "Nora", "Blue", 202)));

        System.out.println("Real server searches (one-second delay each):");
        System.out.println("ID 3: " + service.searchById(3));
        System.out.println("Title Sunrise: " + service.searchByTitle("Sunrise"));
        System.out.println("Album Blue: " + service.searchByAlbum("Blue"));
        System.out.println("Missing ID: " + service.searchById(99));
        System.out.println("Caching proxy will be added next.");
    }
}
