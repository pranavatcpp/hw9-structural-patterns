package flyweight;

import java.util.HashMap;
import java.util.Map;

/** Retains one shared instance per exact font, size, and color combination. */
public final class CharacterPropertiesFactory {
    private final Map<CharacterProperties, CharacterProperties> properties = new HashMap<>();

    public CharacterProperties getProperties(String font, int size, String color) {
        CharacterProperties requested = new CharacterProperties(font, size, color);
        CharacterProperties shared = properties.get(requested);
        if (shared == null) {
            properties.put(requested, requested);
            shared = requested;
        }
        return shared;
    }

    public int getPropertyCount() {
        return properties.size();
    }
}
