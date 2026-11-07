package flyweight;

/** Entry point for the Flyweight text editor demonstration. */
public class FlyweightDriver {
    public static void main(String[] args) {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
        CharacterProperties first = factory.getProperties("Arial", 12, "Red");
        CharacterProperties repeated = factory.getProperties("Arial", 12, "Red");
        CharacterProperties second = factory.getProperties("Calibri", 14, "Blue");
        CharacterProperties third = factory.getProperties("Verdana", 16, "Black");

        System.out.println("First properties: " + first);
        System.out.println("Second properties: " + second);
        System.out.println("Third properties: " + third);
        System.out.println("Matching properties share an object: " + (first == repeated));
        System.out.println("Unique property objects: " + factory.getPropertyCount());
        Document document = new Document(factory);
        document.append("Hello", "Arial", 12, "Red");
        document.append("World", "Calibri", 14, "Blue");
        document.insert(5, " ", "Arial", 12, "Red");
        document.setCharacter(0, 'h');
        document.format(6, 11, "Verdana", 16, "Black");
        System.out.println("Edited text: " + document.getText());
        System.out.println("First character shares existing properties: "
                + (document.getPropertiesAt(0) == first));
        System.out.println("World formatting: " + document.getPropertiesAt(6));
        System.out.println("Save/load is available; the full file demonstration comes next.");
    }
}
