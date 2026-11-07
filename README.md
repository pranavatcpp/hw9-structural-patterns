# Structural Design Patterns Part 2

Java homework demonstrating Flyweight through a text editor and Proxy through
a song metadata service. The Flyweight factory now shares immutable character
properties. Documents support text insertion, replacement, deletion, and formatting
changes, plus file save and load. The music example uses a caching proxy to avoid
repeated server requests for song metadata. Both assignment demonstrations are complete.

## Compile and run

Requires a JDK (Java 8 or newer). Run these commands from this project directory:

```sh
mkdir -p build
javac -d build src/flyweight/*.java src/proxy/*.java
java -cp build flyweight.FlyweightDriver
java -cp build proxy.ProxyDriver
```

The Flyweight driver demonstrates text and formatting edits, then saves exactly
`HelloWorldCS5800` to `build/hello-world.bin`. It reloads the file with a fresh
factory and prints the text, formatting, and identity-sharing checks. The 16
characters share four property objects:

| Text | Font | Size | Color |
| --- | --- | --- | --- |
| Hello | Arial | 12 | Red |
| World | Calibri | 14 | Blue |
| CS | Verdana | 16 | Black |
| 5800 | Arial | 16 | Blue |

An optional first argument selects a different output file (existing files are
overwritten):

```sh
java -cp build flyweight.FlyweightDriver build/custom-document.bin
```

Run the factory and document checks:

```sh
javac -cp build -d build tests/flyweight/*.java
java -cp build flyweight.CharacterPropertiesFactoryTest
java -cp build flyweight.DocumentTest
java -cp build flyweight.DocumentPersistenceTest
java -cp build flyweight.FlyweightDriverTest
```

Use one `CharacterPropertiesFactory` for characters that should share formatting.
Matching uses exact font and color strings plus size. The factory retains created
properties and exposes no deletion operation. Properties are immutable; changing
a character's formatting obtains another shared property object.

`Document` supports `append`, `insert`, `setCharacter`, `format`, and `delete`.
Range operations use an inclusive start and exclusive end. Indices count Java
`char` units. Deleting text does not remove formatting objects from the factory.

Save with `document.save(path)` and load with `Document.load(path, factory)`,
where `path` is a `java.nio.file.Path`. Saving replaces an existing file. The
binary format contains a version header, a character count, and each character's
value, font, size, and color. Loading uses the supplied factory to restore shared
formatting. These methods report file errors and malformed data as `IOException`.
The binary files are intended to be opened through `Document.load`, not a text editor.

The song driver creates six fictional songs and accesses `CachingSongService`
through the supplied `SongService` interface. The proxy wraps `RealSongService`.
For each ID, title, or album query, the first request takes approximately one second;
repeated requests use the cache without an artificial delay. The driver prints
both timings and also demonstrates caching a missing ID. Total runtime is about
four seconds; exact timings vary by machine.
Title and album searches use exact, case-sensitive matches and return all matches
in library order. Missing IDs return `null`; missing titles or albums return empty
lists. Null queries also return no matches. Durations are in seconds. The service
copies the input library and returns independent result lists.

`CachingSongService` maintains separate caches for ID, title, and album queries.
Missing results are cached too. Lists are copied on storage and return so callers
cannot change cached results. Failed requests are not cached. The demo assumes a
fixed library and a single thread; caches last for the proxy's lifetime and do not
expire. There is no music playback.

Run the real-service checks (approximately eight seconds):

```sh
javac -cp build -d build tests/proxy/*.java
java -cp build proxy.RealSongServiceTest
java -cp build proxy.CachingSongServiceTest
```

The proxy checks count backend calls directly to verify reuse, including empty
results, separate query types, protected result lists, and retries after failures.

## Design pattern roles

- **Flyweight:** `CharacterProperties` holds immutable shared formatting;
  `CharacterPropertiesFactory` reuses matching formats. `Document` holds each
  character's value and position separately and refers to the shared objects.
- **Proxy:** `SongService` is the common interface, `RealSongService` is the real
  service, and `CachingSongService` controls access by caching query results.
  `ProxyDriver` accesses the proxy through the interface.

## Full verification

Compile and run all six dependency-free checks from the project directory:

```sh
mkdir -p build
javac -d build src/flyweight/*.java src/proxy/*.java tests/flyweight/*.java tests/proxy/*.java
java -cp build flyweight.CharacterPropertiesFactoryTest
java -cp build flyweight.DocumentTest
java -cp build flyweight.DocumentPersistenceTest
java -cp build flyweight.FlyweightDriverTest
java -cp build proxy.RealSongServiceTest
java -cp build proxy.CachingSongServiceTest
```

Checks throw `AssertionError` on failure; no `-ea` flag or external test library
is needed. The persistence tests remove their temporary files. The driver writes
its default output under the Git-ignored `build/` directory.

## Project structure

- `src/flyweight/`: text editor and shared character properties.
- `src/proxy/`: song metadata service and caching proxy.
- `build/`: generated class files, excluded from Git.

## Requirements checklist

### Flyweight

- [x] Share character property objects for matching font, size, and color.
- [x] Support the supplied fonts (Arial, Calibri, Verdana), colors (Red, Blue,
      Black), and sizes (12, 14, 16).
- [x] Allow document creation and editing with different character properties.
- [x] Keep created character property objects; do not add property deletion.
- [x] Save documents to a file and load them from a file.
- [x] Save `HelloWorldCS5800` in the driver with at least four property variations.

### Proxy

- [x] Model song title, artist, and album as strings and duration as an integer.
- [x] Preserve the supplied `SongService` interface methods:
      `Song searchById(Integer songID)`,
      `List<Song> searchByTitle(String title)`, and
      `List<Song> searchByAlbum(String album)`.
- [x] Fetch metadata on demand through a proxy and cache it in memory.
- [x] Access the metadata through the `SongService` interface.
- [x] Populate 5–10 songs and demonstrate all three search methods.
- [x] Add the suggested one-second delay to each real-service search method,
      with no artificial delay in the proxy.
- [x] Demonstrate repeated searches using the cache. Music playback is not needed.

## Implementation checkpoints

1. Project setup and runnable driver placeholders (complete).
2. Character property flyweight factory (complete).
3. Document creation and editing (complete).
4. Document save and load (complete).
5. Flyweight demonstration and verification (complete).
6. Song metadata model and real service (complete).
7. Caching proxy and demonstration (complete).
8. Final verification and run documentation (complete).
