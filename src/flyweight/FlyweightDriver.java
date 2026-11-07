package flyweight;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Entry point for the Flyweight text editor demonstration. */
public class FlyweightDriver {
    public static void main(String[] args) throws IOException {
        Path output = Paths.get(args.length > 0 ? args[0] : "build/hello-world.bin");
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
        Document document = new Document(factory);
        document.append("hello", "Arial", 12, "Red");
        document.append("World", "Calibri", 14, "Blue");
        document.append("5800", "Arial", 12, "Red");
        System.out.println("Before editing: " + document.getText());

        document.setCharacter(0, 'H');
        document.insert(10, "CS", "Verdana", 16, "Black");
        document.format(12, 16, "Arial", 16, "Blue");
        System.out.println("Edited text: " + document.getText());

        Files.createDirectories(output.toAbsolutePath().getParent());
        document.save(output);
        System.out.println("Saved to: " + output.toAbsolutePath());

        // A fresh factory demonstrates rebuilding sharing from disk.
        CharacterPropertiesFactory loadedFactory = new CharacterPropertiesFactory();
        Document loaded = Document.load(output, loadedFactory);
        System.out.println("Loaded text: " + loaded.getText());
        printFormatting(loaded, 0, 5);
        printFormatting(loaded, 5, 10);
        printFormatting(loaded, 10, 12);
        printFormatting(loaded, 12, 16);
        System.out.println("Characters: " + loaded.length());
        System.out.println("Unique shared property objects: " + loadedFactory.getPropertyCount());
        System.out.println("H and e share the same property object: "
                + (loaded.getPropertiesAt(0) == loaded.getPropertiesAt(1)));
        System.out.println("Repeated factory lookup reuses loaded properties: "
                + (loaded.getPropertiesAt(0) == loadedFactory.getProperties("Arial", 12, "Red")));
    }

    private static void printFormatting(Document document, int start, int end) {
        System.out.println(document.getText().substring(start, end) + ": "
                + document.getPropertiesAt(start));
    }
}
