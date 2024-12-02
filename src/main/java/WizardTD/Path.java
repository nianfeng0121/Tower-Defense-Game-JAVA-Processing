package WizardTD;

import processing.core.PImage;

/**
 * The Path class represents a path tile in the game.
 */
public class Path extends WizOrPath {
    // The type of path (straight, corner, T-junction, crossroad)
    private int pathType;
    // The number of 90-degree rotations applied to the path sprite.
    private int rotates;
    // Default sprite path which will be overridden based on the path type.
    private static final String spritePathToBeOverriden = "src/main/resources/WizardTD/path0.png";

    /**
     * Constructor for initializing a new Path object.
     * @param x The x-coordinate of the path tile.
     * @param y The y-coordinate of the path tile.
     * @param map The map the path tile belongs to.
     */
    public Path(int x, int y, Map map) {
        super(x, y, map, Path.spritePathToBeOverriden);
    }

    /**
     * Updates the path type and rotates the sprite accordingly.
     */
    public void updatePath() {
        this.pathTypeRotate();
        this.createImage();
    }

    /**
     * Determines the path type and rotation based on adjacent tiles.
     */
    public void pathTypeRotate() {
        // Booleans to track whether there is a path in each direction.
        boolean left = false, right = false, up = false, down = false;
        
        // Check for adjacent non-terminal paths.
        boolean[] directionsThatArentTerminal = this.findDirectionsThatExist();

        // Determine the directions where paths are present.
        right = !directionsThatArentTerminal[0] || this.adj.get(Direction.RIGHT) instanceof WizOrPath;
        up    = !directionsThatArentTerminal[1] || this.adj.get(Direction.UP)    instanceof WizOrPath;
        left  = !directionsThatArentTerminal[2] || this.adj.get(Direction.LEFT)  instanceof WizOrPath;
        down  = !directionsThatArentTerminal[3] || this.adj.get(Direction.DOWN)  instanceof WizOrPath;

        // Determine path type and rotation based on adjacent paths.
        if (left && right && up && down) {
            this.pathType = 3; // Crossroad
            this.rotates = 0;
        } else if (left && right && up) {
            this.pathType = 2; // T-junction
            this.rotates = 2;
        } else if (left && right && down) {
            this.pathType = 2; // T-junction
            this.rotates = 0;
        } else if (left && up && down) {
            this.pathType = 2; // T-junction
            this.rotates = 3;
        } else if (right && up && down) {
            this.pathType = 2; // T-junction
            this.rotates = 1;
        } else if (left && right) {
            this.pathType = 0; // Straight path
            this.rotates = 0;
        } else if (up && down) {
            this.pathType = 0; // Straight path
            this.rotates = 1;
        } else if (left && up) {
            this.pathType = 1; // Corner
            this.rotates = 3;
        } else if (left && down) {
            this.pathType = 1; // Corner
            this.rotates = 0;
        } else if (right && up) {
            this.pathType = 1; // Corner
            this.rotates = 2;
        } else if (right && down) {
            this.pathType = 1; // Corner
            this.rotates = 1;
        } else if (left || right) {
            this.pathType = 0; // Straight path
            this.rotates = 0;
        } else if (up || down) {
            this.pathType = 0; // Straight path
            this.rotates = 1;
        } else {
            System.out.println("Lone path: " + this);
            this.pathType = 3; // Crossroad
            this.rotates = 0;
        }
    }

    /**
     * Creates and sets the sprite image based on the path type and rotation.
     */
    public void createImage() {
        PImage noRotate;
        noRotate = this.map.getApp().loadImage("src/main/resources/WizardTD/path" + this.pathType + ".png"); 
        this.sprite = this.map.getApp().rotateImageByDegrees(noRotate, this.rotates * -90);
    }
}
