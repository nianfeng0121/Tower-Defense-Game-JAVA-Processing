package WizardTD;

// Class representing a grass tile in the game, extending the generic Tile class
public class Grass extends Tile {
    private static final String spritePath = "src/main/resources/WizardTD/grass.png";  // Path to the grass tile image

    // Constructor for creating a new grass tile
    public Grass(int x, int y, Map map) {
        super(x, y, map, Grass.spritePath);  // Calling the constructor of the superclass (Tile) to initialize the grass tile
    }
}
