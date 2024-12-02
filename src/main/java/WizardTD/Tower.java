package WizardTD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import processing.core.PApplet;

// The Tower class represents a defense structure in the WizardTD game that can shoot projectiles at monsters.
public class Tower extends Tile {

    // Variables to keep track of the tower's attributes and levels.
    private int rangeLevel; 
    private int firingSpeedLevel;
    private int damageLevel;
    private int lowestLevel;
    private double range;
    private double firingSpeed;
    private double damage;
    private double initialTowerDamage;
    private int framesCounter = 0;
    private int centerX;
    private int centerY;

    // List to store the projectiles (fireballs) shot by the tower.
    private ArrayList<Fireball> projectiles = new ArrayList<Fireball>();

    // Default path to the tower's image.
    private static final String spritePathToBeOveriden = "src/main/resources/WizardTD/tower0.png";

    // Constructor to initialize the tower with its attributes.
    Tower(int x, int y, double initialRange, 
          double initialFiringSpeed, double initialDamage, 
          boolean initialRangeLevel, boolean initialFiringSpeedLevel,
          boolean initialDamageLevel, Map map
    ) {
        super(x, y, map, Tower.spritePathToBeOveriden);
        this.centerX = x * App.CELLSIZE + App.CELLSIZE / 2;
        this.centerY = y * App.CELLSIZE + App.CELLSIZE / 2 + App.TOPBAR;
        this.range = initialRange;
        this.firingSpeed = initialFiringSpeed;
        this.damage = initialDamage;
        this.initialTowerDamage = initialDamage;

        if (initialRangeLevel) {
            this.upgradeRange();
        }
        if (initialFiringSpeedLevel) {
            this.upgradeFiringSpeed();
        }
        if (initialDamageLevel) {
            this.upgradeDamage();
        }
    }


    // Methods to get the cost of upgrading different attributes.
     
    public double getRangeCost() {
        return 20 + 10 * this.rangeLevel;
    }

    public double getFiringSpeedCost() {
        return 20 + 10 * this.firingSpeedLevel;
    }

    public double getDamageCost() {
        return 20 + 10 * this.damageLevel;
    }

    // Getter methods for tower attributes and projectiles.
    public double getRange() {
        return this.range;
    }

    public ArrayList<Fireball> getProjectiles() {
        return this.projectiles;
    }

    // Method to set the frames counter for testing.
    public void setFramesCounter(int framesCounter) {
        this.framesCounter = framesCounter;
    }

    public void findLowestLevel() {
        if (this.rangeLevel <= this.firingSpeedLevel && this.rangeLevel <= this.damageLevel) {
            this.lowestLevel = this.rangeLevel;
        } else if (this.firingSpeedLevel <= this.rangeLevel && this.firingSpeedLevel <= this.damageLevel) {
            this.lowestLevel = this.firingSpeedLevel;
        } else {
            this.lowestLevel = this.damageLevel;
        }
        if (this.lowestLevel > 2) {
            this.lowestLevel = 2;
        }

        this.sprite = this.map.getApp().loadImage(
                "src/main/resources/WizardTD/tower" + this.lowestLevel + ".png");
    }

    // Methods to upgrade the tower's attributes.
    public void upgradeRange() {
        this.rangeLevel++;
        this.range += 32;
    }

    public void upgradeFiringSpeed() {
        this.firingSpeedLevel++;
        this.firingSpeed += 0.5;
    }

    public void upgradeDamage() {
        this.damageLevel++;
        this.damage += this.initialTowerDamage / 2;
    }

    // Tower's method to shoot a projectile at monsters.
    public void shoot() {
        ArrayList<Monster> enemiesInRange = new ArrayList<Monster>();
        for (Wave wave : this.map.getWaves()) {
            for (Monster monster : wave.getMonsters()) {
                double spriteCentreX = monster.getPixelX() + App.SPRITE_SHIFT;
                double spriteCentreY = monster.getPixelY() + App.SPRITE_SHIFT;

                if (App.scalarDistance(
                        this.centerX, this.centerY,
                        spriteCentreX, spriteCentreY) <= this.range) {
                    enemiesInRange.add(monster);
                }
            }
        }

        if (enemiesInRange.size() > 0) {
            Random rand = new Random();
            int randIndex = rand.nextInt(enemiesInRange.size());
            Monster target = enemiesInRange.get(randIndex);

            projectiles.add(new Fireball(
                    this.centerX, this.centerY, target, this.damage, this.map.getApp()));
        }
    }

    // Returns a string representation of the tower for debugging purposes.
    @Override
    public String toString() {
        return this.rangeLevel + " " + this.firingSpeedLevel + " " +
                this.damageLevel + " tower at (" + this.x + ", " + this.y + ")";
    }

    // Updates the tower's state every frame.
    public void tick() {
        double framesPerFireball = App.FPS / this.firingSpeed;
        if (this.framesCounter > framesPerFireball) {
            this.shoot();
            this.framesCounter = 0;
        }
        this.framesCounter += this.map.getApp().rate;

        Iterator<Fireball> fireballIterator = this.projectiles.iterator();
        while (fireballIterator.hasNext()) {
            Fireball fireball = fireballIterator.next();
            fireball.tick();

            if (!(fireball.getExists())) {
                fireballIterator.remove();
            }
        }
    }

    // Renders the tower and its upgrades on the screen.
    @Override
    public void draw(PApplet app) {
        this.findLowestLevel();
        app.image(this.sprite, this.x * App.CELLSIZE, this.y * App.CELLSIZE + App.TOPBAR);

        int tileX = this.x * App.CELLSIZE;
        int tileY = this.y * App.CELLSIZE + App.TOPBAR;
        app.noFill();

        if (this.firingSpeedLevel - this.lowestLevel >= 1)
        {
            app.stroke(120, 180, 255);
            app.strokeWeight((this.firingSpeedLevel - this.lowestLevel)* 2); 
            app.rect(
                tileX + App.TOWER_SPEED_SQUARE_SHIFT, tileY + App.TOWER_SPEED_SQUARE_SHIFT,
                App.TOWER_SPEED_SQUARE_LENGTH, App.TOWER_SPEED_SQUARE_LENGTH
            );
        }

        app.stroke(255, 0, 255);
        app.strokeWeight(1);
        
        for(int i = 0; i < this.rangeLevel - this.lowestLevel; i++) {
            app.ellipse( 
                tileX + App.TOWER_FIRST_UPGRADE_SHIFT_X + 
                i * (App.RANGE_UPGRADE_DIAMETER + App.TOWER_UPGRADE_CIRCLE_DIST),
                tileY + App.TOWER_FIRST_UPGRADE_SHIFT_Y, 
                App.RANGE_UPGRADE_DIAMETER, App.RANGE_UPGRADE_DIAMETER
            );
        }

        for(int i = 0; i < this.damageLevel - this.lowestLevel; i++) {
            app.line(
                tileX + i * (App.TOWER_DAMAGE_CROSS_LENGTH_X + App.TOWER_UPGRADE_CROSS_DIST),
                tileY + App.TOWER_FIRST_UPGRADE_DMG_SHIFT_Y,
                tileX + App.TOWER_FIRST_UPGRADE_SHIFT_X +
                (i+1) * App.TOWER_DAMAGE_CROSS_LENGTH_X + i * App.TOWER_UPGRADE_CROSS_DIST,
                tileY + App.TOWER_FIRST_UPGRADE_DMG_SHIFT_Y + App.TOWER_DAMAGE_CROSS_LENGTH_Y
            );
            app.line(
                tileX + i * (App.TOWER_DAMAGE_CROSS_LENGTH_X + App.TOWER_UPGRADE_CROSS_DIST),
                tileY + App.TOWER_FIRST_UPGRADE_DMG_SHIFT_Y + App.TOWER_DAMAGE_CROSS_LENGTH_Y,
                tileX + App.TOWER_FIRST_UPGRADE_SHIFT_X +
                (i+1) * App.TOWER_DAMAGE_CROSS_LENGTH_X + i * App.TOWER_UPGRADE_CROSS_DIST,
                tileY + App.TOWER_FIRST_UPGRADE_DMG_SHIFT_Y
            );
        }
    }
}
