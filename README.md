# Structural Design Patterns Part 2

Java homework demonstrating Flyweight through a text editor and Proxy through
a song metadata service. The initial setup contains runnable placeholder drivers;
the assignment features below are still pending.

## Compile and run

Requires a JDK (Java 8 or newer). Run these commands from this project directory:

```sh
mkdir -p build
javac -d build src/flyweight/*.java src/proxy/*.java
java -cp build flyweight.FlyweightDriver
java -cp build proxy.ProxyDriver
```

## Project structure

- `src/flyweight/`: text editor and shared character properties.
- `src/proxy/`: song metadata service and caching proxy.
- `build/`: generated class files, excluded from Git.

## Requirements checklist

### Flyweight

- [ ] Share character property objects for matching font, size, and color.
- [ ] Support the supplied fonts (Arial, Calibri, Verdana), colors (Red, Blue,
      Black), and sizes (12, 14, 16).
- [ ] Allow document creation and editing with different character properties.
- [ ] Keep created character property objects; do not add property deletion.
- [ ] Save documents to a file and load them from a file.
- [ ] Save `HelloWorldCS5800` in the driver with at least four property variations.

### Proxy

- [ ] Model song title, artist, and album as strings and duration as an integer.
- [ ] Preserve the supplied `SongService` interface methods:
      `Song searchById(Integer songID)`,
      `List<Song> searchByTitle(String title)`, and
      `List<Song> searchByAlbum(String album)`.
- [ ] Fetch metadata on demand through a proxy and cache it in memory.
- [ ] Access the metadata through the `SongService` interface.
- [ ] Populate 5–10 songs and demonstrate all three search methods.
- [ ] Add the suggested one-second delay to each real-service search method,
      with no artificial delay in the proxy.
- [ ] Demonstrate repeated searches using the cache. Music playback is not needed.

## Implementation checkpoints

1. Project setup and runnable driver placeholders (current checkpoint).
2. Character property flyweight factory.
3. Document creation and editing.
4. Document save and load.
5. Flyweight demonstration and verification.
6. Song metadata model and real service.
7. Caching proxy and demonstration.
8. Final verification and run documentation.
