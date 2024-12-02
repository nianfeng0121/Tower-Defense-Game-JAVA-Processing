package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

// Abstract class representing a generic tile in the WizardTD game.
abstract class Tile {
    
    // X-coordinate of the tile on the game grid.
    protected int x;

    // Y-coordinate of the tile on the game grid.
    protected int y;

    // Reference to the game map this tile belongs to.
    protected Map map;

    // Graphical representation of the tile.
    protected PImage sprite;

    // Constructor initializes tile with its position, parent map, and its visual representation.
    public Tile(int x, int y, Map map, String spritePath) {
        this.x = x;
        this.y = y;
        this.map = map;

        // Load the image for this tile using the path provided.
        this.sprite = map.getApp().loadImage(spritePath);
    }

    // Returns the X-coordinate of the tile.
    public int getX() {
        return this.x;
    }

    // Returns the Y-coordinate of the tile.
    public int getY() {
        return this.y;
    }

    // Provides a string representation of the tile, useful for debugging.
    public String toString() {
        return this.getClass().getName() + " @ x=" + this.x + ", y=" + this.y;
    }

    // Render the tile on the game screen using the Processing library.
    public void draw(PApplet app) {
        app.image(this.sprite, this.x * App.CELLSIZE, this.y * App.CELLSIZE + App.TOPBAR);
    }
}


