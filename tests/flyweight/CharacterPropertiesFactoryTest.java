package flyweight;

/** Dependency-free checks; failures throw AssertionError without needing -ea. */
public class CharacterPropertiesFactoryTest {
    public static void main(String[] args) {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
        String[] fonts = {"Arial", "Calibri", "Verdana"};
        int[] sizes = {12, 14, 16};
        String[] colors = {"Red", "Blue", "Black"};

        for (String font : fonts) {
            for (int size : sizes) {
                for (String color : colors) {
                    int previousCount = factory.getPropertyCount();
                    CharacterProperties first = factory.getProperties(font, size, color);
                    check(factory.getPropertyCount() == previousCount + 1,
                            "Different combinations must have distinct cached objects.");
                    CharacterProperties repeated = factory.getProperties(
                            new String(font), size, new String(color));
                    check(first == repeated, "Matching values must reuse the same instance.");
                    check(factory.getPropertyCount() == previousCount + 1,
                            "Repeated requests must not grow the cache.");
                    check(first.getFont().equals(font) && first.getSize() == size
                            && first.getColor().equals(color), "Properties must be preserved.");
                }
            }
        }
        check(factory.getPropertyCount() == 27, "All 27 combinations must be supported.");
        expectInvalid(() -> factory.getProperties(null, 12, "Red"));
        expectInvalid(() -> factory.getProperties(" ", 12, "Red"));
        expectInvalid(() -> factory.getProperties("Arial", 0, "Red"));
        expectInvalid(() -> factory.getProperties("Arial", -1, "Red"));
        expectInvalid(() -> factory.getProperties("Arial", 12, null));
        expectInvalid(() -> factory.getProperties("Arial", 12, " "));
        check(factory.getPropertyCount() == 27, "Invalid requests must not change the cache.");
        System.out.println("CharacterPropertiesFactory checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expectInvalid(Runnable request) {
        try {
            request.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected invalid properties to be rejected.");
    }
}
