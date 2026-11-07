package flyweight;

import java.util.Objects;

/** Immutable formatting shared by characters with the same properties. */
public final class CharacterProperties {
    private final String font;
    private final int size;
    private final String color;

    // Clients obtain shared instances through CharacterPropertiesFactory.
    CharacterProperties(String font, int size, String color) {
        if (font == null || font.trim().isEmpty()) {
            throw new IllegalArgumentException("Font must not be blank.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive.");
        }
        if (color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException("Color must not be blank.");
        }
        this.font = font;
        this.size = size;
        this.color = color;
    }

    public String getFont() {
        return font;
    }

    public int getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CharacterProperties)) {
            return false;
        }
        CharacterProperties properties = (CharacterProperties) other;
        return size == properties.size
                && font.equals(properties.font)
                && color.equals(properties.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(font, size, color);
    }

    @Override
    public String toString() {
        return font + ", " + size + ", " + color;
    }
}
