package flyweight;

/** Dependency-free checks for editing behavior and property sharing. */
public class DocumentTest {
    public static void main(String[] args) {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
        Document document = new Document(factory);
        check(document.length() == 0 && document.getText().isEmpty(), "New document is empty.");
        document.append("HelloWorld", "Arial", 12, "Red");
        CharacterProperties original = document.getPropertiesAt(0);
        check(document.getPropertiesAt(9) == original, "Characters share formatting.");
        document.insert(5, " ", "Calibri", 14, "Blue");
        check(document.getText().equals("Hello World"), "Insert preserves surrounding text.");
        document.setCharacter(0, 'h');
        check(document.getPropertiesAt(0) == original, "Text edits preserve formatting.");
        document.format(6, 11, "Verdana", 16, "Black");
        check(document.getText().equals("hello World"), "Formatting preserves text.");
        check(document.getPropertiesAt(6) == document.getPropertiesAt(10),
                "Formatted range shares one object.");
        check(document.getPropertiesAt(4) == original, "Formatting leaves other characters alone.");

        Document other = new Document(factory);
        other.append("A", "Arial", 12, "Red");
        check(other.getPropertiesAt(0) == original, "Documents can share properties.");
        document.format(0, 1, "Calibri", 14, "Blue");
        check(other.getPropertiesAt(0) == original, "Formatting one document leaves another alone.");
        document.delete(5, 6);
        check(document.getText().equals("helloWorld"), "Delete removes only selected text.");

        expectInvalid(() -> document.insert(-1, "x", "Arial", 12, "Red"));
        expectInvalid(() -> document.insert(11, "x", "Arial", 12, "Red"));
        expectInvalid(() -> document.setCharacter(10, 'x'));
        expectInvalid(() -> document.delete(3, 2));
        expectInvalid(() -> document.format(0, 11, "Arial", 12, "Red"));
        expectInvalid(() -> document.format(0, 1, "Arial", 0, "Red"));
        check(document.getText().equals("helloWorld"), "Invalid edits leave text unchanged.");
        check(document.getPropertiesAt(0).getFont().equals("Calibri"),
                "Invalid formatting leaves existing properties unchanged.");
        document.delete(0, document.length());
        check(document.getText().isEmpty(), "Entire document can be cleared.");
        check(factory.getPropertyCount() == 3, "Deleting text never deletes cached properties.");
        document.insert(0, "X", "Arial", 12, "Red");
        document.insert(document.length(), "Y", "Arial", 12, "Red");
        document.delete(1, 1);
        check(document.getText().equals("XY"), "Boundary inserts and empty deletion work.");
        check(document.getPropertiesAt(0) == original, "Properties remain reusable after deletion.");
        System.out.println("Document checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expectInvalid(Runnable edit) {
        try {
            edit.run();
        } catch (IndexOutOfBoundsException | IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected invalid edit to be rejected.");
    }
}
