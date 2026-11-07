package flyweight;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Editable text whose characters refer to shared, immutable formatting. */
public final class Document {
    private static final String FILE_HEADER = "CS5800-DOCUMENT-1";
    private final CharacterPropertiesFactory factory;
    private final List<CharacterEntry> characters = new ArrayList<>();

    public Document(CharacterPropertiesFactory factory) {
        this.factory = Objects.requireNonNull(factory, "Factory must not be null.");
    }

    public int length() {
        return characters.size();
    }

    public String getText() {
        StringBuilder text = new StringBuilder(characters.size());
        for (CharacterEntry character : characters) {
            text.append(character.value);
        }
        return text.toString();
    }

    public CharacterProperties getPropertiesAt(int index) {
        return characters.get(index).properties;
    }

    public void append(String text, String font, int size, String color) {
        insert(length(), text, font, size, color);
    }

    /** Inserts before index; index equal to length appends. Indices use Java char units. */
    public void insert(int index, String text, String font, int size, String color) {
        if (index < 0 || index > length()) {
            throw new IndexOutOfBoundsException("Invalid insertion index: " + index);
        }
        Objects.requireNonNull(text, "Text must not be null.");
        CharacterProperties properties = factory.getProperties(font, size, color);
        List<CharacterEntry> inserted = new ArrayList<>(text.length());
        for (int i = 0; i < text.length(); i++) {
            inserted.add(new CharacterEntry(text.charAt(i), properties));
        }
        characters.addAll(index, inserted);
    }

    /** Replaces a character while preserving its formatting. */
    public void setCharacter(int index, char value) {
        characters.get(index).value = value;
    }

    /** Applies formatting to [start, end), leaving the text unchanged. */
    public void format(int start, int end, String font, int size, String color) {
        checkRange(start, end);
        CharacterProperties properties = factory.getProperties(font, size, color);
        for (int i = start; i < end; i++) {
            characters.get(i).properties = properties;
        }
    }

    /** Removes text in [start, end); shared properties remain in the factory. */
    public void delete(int start, int end) {
        checkRange(start, end);
        characters.subList(start, end).clear();
    }

    /** Saves text and formatting in the versioned binary document format. */
    public void save(Path path) throws IOException {
        try (DataOutputStream output = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(path)))) {
            output.writeUTF(FILE_HEADER);
            output.writeInt(length());
            for (CharacterEntry character : characters) {
                output.writeChar(character.value);
                output.writeUTF(character.properties.getFont());
                output.writeInt(character.properties.getSize());
                output.writeUTF(character.properties.getColor());
            }
        }
    }

    /** Loads a new document, resolving every format through the supplied factory. */
    public static Document load(Path path, CharacterPropertiesFactory factory) throws IOException {
        Objects.requireNonNull(factory, "Factory must not be null.");
        Document loaded = new Document(factory);
        try (DataInputStream input = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(path)))) {
            if (!FILE_HEADER.equals(input.readUTF())) {
                throw new IOException("Unsupported document format.");
            }
            int count = input.readInt();
            if (count < 0) {
                throw new IOException("Invalid character count.");
            }
            for (int i = 0; i < count; i++) {
                char value = input.readChar();
                String font = input.readUTF();
                int size = input.readInt();
                String color = input.readUTF();
                try {
                    CharacterProperties properties = factory.getProperties(font, size, color);
                    loaded.characters.add(new CharacterEntry(value, properties));
                } catch (IllegalArgumentException invalidProperties) {
                    throw new IOException("Invalid formatting at character " + i, invalidProperties);
                }
            }
            if (input.read() != -1) {
                throw new IOException("Unexpected data after document contents.");
            }
        }
        return loaded;
    }

    private void checkRange(int start, int end) {
        if (start < 0 || end < start || end > length()) {
            throw new IndexOutOfBoundsException("Invalid range: " + start + " to " + end);
        }
    }

    private static final class CharacterEntry {
        private char value;
        private CharacterProperties properties;

        private CharacterEntry(char value, CharacterProperties properties) {
            this.value = value;
            this.properties = properties;
        }
    }
}
