package flyweight;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Verifies that the actual driver saves the required text and formatting. */
public class FlyweightDriverTest {
    public static void main(String[] args) throws IOException {
        Path directory = Files.createTempDirectory("flyweight-driver-");
        Path file = directory.resolve("demo.bin");
        try {
            FlyweightDriver.main(new String[] {file.toString()});
            CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
            Document loaded = Document.load(file, factory);
            check(loaded.getText().equals("HelloWorldCS5800"), "Driver must save the exact text.");
            check(factory.getPropertyCount() == 4, "Driver must save four distinct formats.");
            checkRange(loaded, factory, 0, 5, "Arial", 12, "Red");
            checkRange(loaded, factory, 5, 10, "Calibri", 14, "Blue");
            checkRange(loaded, factory, 10, 12, "Verdana", 16, "Black");
            checkRange(loaded, factory, 12, 16, "Arial", 16, "Blue");
            System.out.println("Flyweight driver checks passed.");
        } finally {
            Files.deleteIfExists(file);
            Files.deleteIfExists(directory);
        }
    }

    private static void checkRange(Document document, CharacterPropertiesFactory factory,
            int start, int end, String font, int size, String color) {
        CharacterProperties expected = factory.getProperties(font, size, color);
        for (int i = start; i < end; i++) {
            check(document.getPropertiesAt(i) == expected,
                    "Saved character " + i + " must share the expected formatting.");
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
