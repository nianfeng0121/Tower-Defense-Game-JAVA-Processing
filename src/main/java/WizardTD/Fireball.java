package WizardTD;

import processing.core.PImage;

// Class representing a fireball projectile in the game
public class Fireball {
    private int pixelX;  // The X-coordinate of the fireball in pixels
    private int pixelY;  // The Y-coordinate of the fireball in pixels
    private PImage sprite;  // The image representing the fireball
    private App app;  // Reference to the main app, used to access global settings and functions
    private Monster target;  // The monster that the fireball is targeting
    private double damage;  // The damage that the fireball will deal to the monster
    private boolean exists = true;  // Boolean flag indicating whether the fireball still exists

    // Constructor for creating a new fireball
    public Fireball(int x, int y, Monster target, double damage, App app) {
        this.pixelX = x + App.FIREBALL_RADIUS;  // Adjusting the X-coordinate to account for the fireball's radius
        this.pixelY = y + App.FIREBALL_RADIUS;  // Adjusting the Y-coordinate to account for the fireball's radius
        this.app = app;  // Storing the reference to the main app
        this.target = target;  // Storing the target monster
        this.damage = damage;  // Storing the damage value
        this.sprite = app.loadImage("src/main/resources/WizardTD/fireball.png");  // Loading the fireball image
    }

    // Getter method to check if the fireball still exists
    public boolean getExists() {
        return this.exists;
    }

    // Update method, called on each frame to update the fireball's state
    public void tick() {
        // Calculating the center coordinates of the target monster
        double targetCentreX = this.target.getPixelX() + App.SPRITE_SHIFT;
        double targetCentreY = this.target.getPixelY() + App.SPRITE_SHIFT;

        // Updating the fireball's position to move towards the target monster
        this.pixelX += App.PROJ_SPEED / App.scalarDistance(
            this.pixelX, this.pixelY, 
            targetCentreX, targetCentreY
            ) * (targetCentreX - this.pixelX);

        this.pixelY += App.PROJ_SPEED / App.scalarDistance(
            this.pixelX, this.pixelY, 
            targetCentreX, targetCentreY
            ) * (targetCentreY - this.pixelY);

        // Checking if the fireball has hit the target monster
        if (App.scalarDistance(
            this.pixelX, this.pixelY, 
            targetCentreX, targetCentreY
            ) <= App.MONSTER_RADIUS) {
            this.target.takeDamage(this.damage);  // Applying damage to the target
            this.exists = false;  // Setting exists to false, effectively removing the fireball
        }
    }

    // Draw method, called on each frame to draw the fireball
    public void draw() {
        this.app.image(this.sprite, this.pixelX, this.pixelY);  // Drawing the fireball image at its current position
    }
}
