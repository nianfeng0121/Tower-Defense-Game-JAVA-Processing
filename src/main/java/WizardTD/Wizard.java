package WizardTD;

import processing.core.PApplet;

/**
 * The Wizard class represents a wizard tower in the game.
 * It inherits from WizOrPath, as a wizard tower can be considered a special kind of cell on the game map.
 */
public class Wizard extends WizOrPath {
    // The path to the sprite image for the wizard tower.
    private static final String spritePath = "src/main/resources/WizardTD/wizard_house.png";

    /**
     * Constructor for the Wizard class.
     * @param x The x-coordinate of the wizard tower on the map (in terms of cells, not pixels).
     * @param y The y-coordinate of the wizard tower on the map (in terms of cells, not pixels).
     * @param map A reference to the map object, allowing the wizard tower to interact with other game elements.
     */
    public Wizard(int x, int y, Map map) {
        // Call to the superclass (WizOrPath) constructor.
        super(x, y, map, Wizard.spritePath);
        
        // Initialize the wizard's distance from the path to 0 (as it's not on a path).
        this.wizDist = 0;
        
        // Initialize the optimal direction to NONE, as the wizard doesn't need to move.
        this.optimal = Direction.NONE;
    }

    /**
     * The draw method is used to display the wizard tower on the screen.
     * @param app A reference to the PApplet object, used to draw the wizard tower.
     */
    @Override
    public void draw(PApplet app) {
        // Draw the wizard tower's sprite on the screen.
        // The position is adjusted to match the cell position on the map, with additional shifts for proper alignment.
        app.image(
            this.sprite, 
            this.x * CELLSIZE + App.WIZ_SHIFT_X, 
            this.y * CELLSIZE + App.WIZ_SHIFT_Y + App.TOPBAR
        );
    }
}
