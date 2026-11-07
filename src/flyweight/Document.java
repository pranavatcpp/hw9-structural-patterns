package flyweight;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Editable text whose characters refer to shared, immutable formatting. */
public final class Document {
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
