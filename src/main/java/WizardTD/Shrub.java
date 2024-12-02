package WizardTD;

// The Shrub class represents a specific type of tile in the WizardTD game.
public class Shrub extends Tile {

    // Path to the image used to visually represent a shrub in the game.
    private static final String spritePath = "src/main/resources/WizardTD/shrub.png";

    // Constructor initializes the shrub tile with its position, parent map, and its visual representation.
    public Shrub(int x, int y, Map map) {
        super(x, y, map, Shrub.spritePath);
    }
}
