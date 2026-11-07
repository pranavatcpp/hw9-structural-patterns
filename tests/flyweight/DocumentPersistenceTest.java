package flyweight;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/** Checks file round trips, shared formatting, and malformed input. */
public class DocumentPersistenceTest {
    public static void main(String[] args) throws IOException {
        Path file = Files.createTempFile("flyweight-document-", ".bin");
        try {
            CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
            Document original = new Document(factory);
            original.append("Hello\n\t\u03A9\uD83D\uDE00", "Arial", 12, "Red");
            original.append("World", "Calibri", 14, "Blue");
            original.append("!", "Arial", 12, "Red");
            original.save(file);

            CharacterPropertiesFactory freshFactory = new CharacterPropertiesFactory();
            Document loaded = Document.load(file, freshFactory);
            check(loaded.getText().equals(original.getText()), "Text must survive a round trip.");
            for (int i = 0; i < original.length(); i++) {
                check(loaded.getPropertiesAt(i).equals(original.getPropertiesAt(i)),
                        "Formatting must survive at every position.");
            }
            check(freshFactory.getPropertyCount() == 2, "Loading must reuse matching formats.");
            check(loaded.getPropertiesAt(0) == loaded.getPropertiesAt(loaded.length() - 1),
                    "Nonadjacent characters must share the same loaded property object.");
            Document shared = Document.load(file, factory);
            check(shared.getPropertiesAt(0) == original.getPropertiesAt(0),
                    "Loading with an existing factory must reuse its objects.");
            loaded.setCharacter(0, 'h');
            check(original.getText().startsWith("Hello"), "Loaded documents edit independently.");
            loaded.save(file);
            check(Document.load(file, freshFactory).getText().equals(loaded.getText()),
                    "An edited document can overwrite and reload a file.");

            byte[] valid = Files.readAllBytes(file);
            Files.write(file, Arrays.copyOf(valid, valid.length - 1));
            expectUnreadable(file);
            byte[] trailing = Arrays.copyOf(valid, valid.length + 1);
            Files.write(file, trailing);
            expectUnreadable(file);
            writeInvalid(file, "WRONG-FORMAT", 0, false);
            expectUnreadable(file);
            writeInvalid(file, "CS5800-DOCUMENT-1", -1, false);
            expectUnreadable(file);
            writeInvalid(file, "CS5800-DOCUMENT-1", 1, true);
            expectUnreadable(file);

            new Document(factory).save(file);
            check(Document.load(file, freshFactory).getText().isEmpty(),
                    "An empty document must overwrite and reload correctly.");
            Files.delete(file);
            expectUnreadable(file);
            System.out.println("Document persistence checks passed.");
        } finally {
            Files.deleteIfExists(file);
        }
    }

    private static void writeInvalid(Path path, String header, int count, boolean badSize)
            throws IOException {
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(path))) {
            output.writeUTF(header);
            output.writeInt(count);
            if (badSize) {
                output.writeChar('A');
                output.writeUTF("Arial");
                output.writeInt(0);
                output.writeUTF("Red");
            }
        }
    }

    private static void expectUnreadable(Path path) {
        try {
            Document.load(path, new CharacterPropertiesFactory());
        } catch (IOException expected) {
            return;
        }
        throw new AssertionError("Invalid or missing files must report an IOException.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
