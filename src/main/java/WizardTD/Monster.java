package WizardTD;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

public class Monster {
    // Attributes for the monster's position and speed
    private double pixelX, pixelY;  // The monster's position in pixels
    private double pixSpeed;        // The monster's speed in pixels per frame
    private double maxHealth;       // The monster's maximum health
    private double currHealth;      // The monster's current health
    private double armour;          // The monster's armour, reducing incoming damage
    private App app;                // Reference to the main application
    private double healthProp;      // Proportion of health remaining, for rendering the health bar
    private boolean alive = true;   // Flag indicating if the monster is alive
    private boolean exists = true;  // Flag indicating if the monster should be rendered
    private int deathTick = 0;      // Counter for frames since the monster's death, for death animation
    private int tileNo = 0;         // Current position in the route
    private ArrayList<Direction> route = new ArrayList<>();  // List of directions for the monster to follow
    private PImage sprite;          // Image representing the monster
    private int moves;              // Number of moves made in the current direction
    private double manaOnKill;      // Amount of mana gained when the monster is killed
    private int tileX, tileY;       // The monster's position in tile coordinates
    private boolean firstTimeSpawning = true; // Flag indicating if this is the monster's first spawn

    // Constructor for initializing a new Monster object
    public Monster(
        int tileX, int tileY, double pixSpeed, double maxHealth, 
        double armour, ArrayList<Direction> route, App app, double manaOnKill
    ) {
        for (Direction dir : route) {
            this.route.add(dir);
        }

        this.tileX = tileX;
        this.tileY = tileY;
        this.pixSpeed = pixSpeed;
        this.maxHealth = maxHealth;
        this.currHealth = maxHealth;
        this.armour = armour;
        this.app = app;
        this.manaOnKill = manaOnKill;
        this.pixelX = tileX * App.CELLSIZE + App.GHOST_SHIFT_X;
        this.pixelY = tileY * App.CELLSIZE + App.GHOST_SHIFT_Y + App.TOPBAR;
        this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin.png");
        this.spawnShift();
        System.out.println("Created " + this);
    }

    // Getters
    public boolean getExists() {
        return this.exists;
    }

    public double getPixelX() {
        return this.pixelX;
    }

    public double getPixelY() {
        return this.pixelY;
    }

    // Adjusts the monster's initial position based on its spawning location
    public void spawnShift() {
        switch (((WizOrPath) this.app.map.getLand()[this.tileX][this.tileY]).getTerminals()[0]) {
            case UP:
                this.pixelY -= App.CELLSIZE;
                if (this.firstTimeSpawning) {
                    this.route.add(0, Direction.DOWN);
                }
                break;
            case DOWN:
                this.pixelY += App.CELLSIZE;
                if (this.firstTimeSpawning) {
                    this.route.add(0, Direction.UP);
                }
                break;
            case LEFT:
                this.pixelX -= App.CELLSIZE;
                if (this.firstTimeSpawning) {
                    this.route.add(0, Direction.RIGHT);
                }
                break;
            case RIGHT:
                this.pixelX += App.CELLSIZE;
                if (this.firstTimeSpawning) {
                    this.route.add(0, Direction.LEFT);
                }
                break;
            case NONE:
                System.out.println("Monster spawned on non terminal path!");
                break;
        }
        this.firstTimeSpawning = false;
    }

    // Makes the monster take damage, considering its armour
    public void takeDamage(double damage) {
        this.currHealth -= damage * this.armour;
        System.out.println("Did " + damage + " damage to " + this);
    }

    // Moves the monster along its route
    public void move() {
        if (this.tileNo < this.route.size() && this.alive) {
            switch (this.route.get(tileNo)) {
                case UP:
                    this.pixelY -= this.pixSpeed;
                    break;
                case DOWN:
                    this.pixelY += this.pixSpeed;
                    break;
                case LEFT:
                    this.pixelX -= this.pixSpeed;
                    break;
                case RIGHT:
                    this.pixelX += this.pixSpeed;
                    break;
                case NONE:
                    System.out.println("Monster has no route");
                    break;
            }
            this.moves += 1;
            double difference = this.pixSpeed * this.moves - App.CELLSIZE;

            if (difference >= 0) {
                switch (this.route.get(tileNo)) {
                    case UP:
                        this.pixelY += difference;
                        break;
                    case DOWN:
                        this.pixelY -= difference;
                        break;
                    case LEFT:
                        this.pixelX += difference;
                        break;
                    case RIGHT:
                        this.pixelX -= difference;
                        break;
                    case NONE:
                        break;
                }
                this.tileNo++;
                this.moves = 0;
            }
        } else if (this.tileNo >= this.route.size() && this.alive) {
            this.pixelX = tileX * App.CELLSIZE + App.GHOST_SHIFT_X;
            this.pixelY = tileY * App.CELLSIZE + App.GHOST_SHIFT_Y + App.TOPBAR;
            this.spawnShift();
            this.tileNo = 0;
            this.moves = 0;

            if (!app.map.getMana().updateMana(-1 * this.currHealth)) {
                app.map.getMana().makeManaZero();
                app.onLossScreen = true;
            }
        }
    }

    // Handles the monster's death animation
    public void changeSpriteDuringKillAnimation() {
        this.deathTick += app.rate;
        if (this.deathTick > 20) {
            this.exists = false;
        } else if (this.deathTick > 16) {
            this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin5.png");
        } else if (this.deathTick > 12) {
            this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin4.png");
        } else if (this.deathTick > 8) {
            this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin3.png");
        } else if (this.deathTick > 4) {
            this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin2.png");
        } else if (this.deathTick > 0) {
            this.sprite = app.loadImage("src/main/resources/WizardTD/gremlin1.png");
        }
    }

    // Returns a string representation of the monster for debugging purposes
    @Override
    public String toString() {
        return this.currHealth + " hp monster at (" + this.tileX + ", " + this.tileY + ")";
    }

    // Updates the monster's state
    public void tick() {
        if (this.app.map.getPoison()) {
            this.takeDamage(this.app.poisonDamage * this.app.rate);
        }

        this.healthProp = this.currHealth / this.maxHealth;

        for (int i = 0; i < app.rate; i++) {
            if (this.alive && this.currHealth <= 0) {
                this.alive = false;
                app.map.getMana().updateMana(manaOnKill);
            }
            this.move();
        }

        if (!this.alive) {
            this.changeSpriteDuringKillAnimation();
        }
    }

    // Renders the monster on the screen
    public void draw(PApplet app) {
        app.image(this.sprite, (float)this.pixelX, (float)this.pixelY);

        if (this.alive) {
            app.noStroke();
            app.fill(0, 255, 0);
            app.rect(
                (float)this.pixelX + App.HEALTH_SHIFT_X, (float)this.pixelY + App.HEALTH_SHIFT_Y,
                (int) (App.HEALTH_LENGTH * healthProp), App.HEALTH_WIDTH
            );
            
            app.fill(255, 0, 0);
            app.rect(
                (float)(this.pixelX + App.HEALTH_SHIFT_X + (App.HEALTH_LENGTH * healthProp)),
                (float)(this.pixelY + App.HEALTH_SHIFT_Y), 
                (float) (App.HEALTH_LENGTH * (1 - healthProp)), 
                App.HEALTH_WIDTH
            );
        }
    }
}
