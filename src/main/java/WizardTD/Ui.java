package WizardTD;

/**
 * The Ui class manages the user interface elements in the WizardTD game.
 * This includes buttons for game controls, displaying the current wave, mana bars, 
 * and other interactive elements.
 */
public class Ui {
    // Variables representing the current state of the game map and various UI elements.
    public Map map;
    public int waveSeconds;
    public boolean ff = false;
    public boolean ffHov = false;
    public boolean paused = false;
    public boolean pausedHov = false;
    public boolean placeTower = false;
    public boolean placeTowerHov = false;
    public boolean upgradeRange = false;
    public boolean upgradeRangeHov = false;
    public boolean upgradeSpeed = false;
    public boolean upgradeSpeedHov = false;
    public boolean upgradeDamage = false;
    public boolean upgradeDamageHov = false;
    public boolean manaPoolHov = false;
    public boolean poisonHov = false;

    /**
     * Initializes the Ui with the provided map.
     * @param map The game map.
     */
    public Ui(Map map) {
        this.map = map;
    }

    /**
     * Computes the current wave time in seconds.
     * @return The wave time in seconds.
     */
    public int updateWaveSeconds() {
        return (int) Math.floorDiv((int) this.map.getWaveTime(), App.FPS);
    }

    /**
     * Determines whether the given coordinates are within the game map boundaries.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return True if within the map, false otherwise.
     */
    static public boolean isMouseInMap(int x, int y) {
        return x > 0 && x < App.CELLSIZE * App.BOARD_WIDTH && y > App.TOPBAR && y < App.HEIGHT;
    }

    /**
     * Displays the wave countdown and current wave number on the screen.
     * @param app The application object used for drawing.
     */
    public void waveCountdown(App app) {
        if (!this.map.getLastWave()) {
            app.fill(0, 0, 0);
            app.textSize(25);
            app.text("Wave " + (this.map.getWaveNumber() + 2) + " starts: " + this.waveSeconds, 10, 30);
        }
    }

    /**
     * Renders the mana bar on the screen, showing the player's current mana.
     * @param app The application object used for drawing.
     */
    public void manaBar(App app) {
        float manaProp = (float) (this.map.getMana().getCurrMana() / this.map.getMana().getCap());
        app.stroke(0, 0, 0);
        app.strokeWeight(2);
        app.fill(5, 210, 215);
        app.rect(App.MANA_X, App.MANA_Y, App.MANA_LENGTH * manaProp, App.MANA_WIDTH);
        app.fill(255, 255, 255);
        app.rect(App.MANA_X + (App.MANA_LENGTH * manaProp), App.MANA_Y, (App.MANA_LENGTH * (1 - manaProp)),
                App.MANA_WIDTH);
    }

    /**
     * Displays the current mana value as text on the screen.
     * @param app The application object used for drawing.
     */
    public void manaText(App app) {
        app.fill(0, 0, 0);
        app.textSize(17);
        app.text("MANA:", App.MANA_TEXT_X, App.MANA_TEXT_Y);
        app.text((int) this.map.getMana().getCurrMana() + " / " + (int) this.map.getMana().getCap(),
                App.MANA_TEXT_X + App.MANA_CURR_SHIFT, App.MANA_TEXT_Y);
    }

    /**
     * Handles the logic for toggling various game buttons and controls.
     * @param app The application object.
     * @param buttonNO The button number that was activated.
     */
    public void toggleSwitch(App app, int buttonNO) {
        switch (buttonNO) {
            case 1:
                this.ff = !this.ff;
                app.doubleRate = app.doubleRate == 1 ? 2 : 1;
                app.rate = app.doubleRate * app.pauseRate;
                break;
            case 2:
                this.paused = !this.paused;
                app.pauseRate = this.paused ? 0 : 1;
                app.rate = app.doubleRate * app.pauseRate;
                break;
            case 3:
                this.placeTower = !this.placeTower;
                break;
            case 4:
                this.upgradeRange = !this.upgradeRange;
                break;
            case 5:
                this.upgradeSpeed = !this.upgradeSpeed;
                break;
            case 6:
                this.upgradeDamage = !this.upgradeDamage;
                break;
            case 7:
                this.map.getMana().clickPoolSpell();
                break;
            case 8:
                this.map.togglePoison();
        }
    }

    /**
     * Updates the hover status of a specific button.
     * @param buttonNO The button number.
     * @param hover Whether the mouse is hovering over the button.
     */
    public void setHoveredButton(int buttonNO, boolean hover) {
        switch (buttonNO) {
            case 1:
                this.ffHov = hover;
                break;
            case 2:
                this.pausedHov = hover;
                break;
            case 3:
                this.placeTowerHov = hover;
                break;
            case 4:
                this.upgradeRangeHov = hover;
                break;
            case 5:
                this.upgradeSpeedHov = hover;
                break;
            case 6:
                this.upgradeDamageHov = hover;
                break;
            case 7:
                this.manaPoolHov = hover;
                break;
            case 8:
                this.poisonHov = hover;
        }
    }

    /**
     * Renders the game control buttons on the screen.
     * @param app The application object used for drawing.
     * @param buttonNO The button number to draw.
     */
    public void buttonDraw(App app, int buttonNO) {
        Boolean light = null;
        Boolean hover = null;
        Boolean hasHoverText = null;
        Integer cost = null;
        String text0 = null;
        String text1 = null;
        String text2 = null;

        switch (buttonNO) {
            case 1:
                light = this.ff;
                hover = this.ffHov;
                hasHoverText = false;
                text0 = "FF";
                text1 = "2x speed";
                text2 = " ";
                break;
            case 2:
                light = this.paused;
                hover = this.pausedHov;
                hasHoverText = false;
                text0 = "P";
                text1 = "PAUSE";
                text2 = " ";
                break;
            case 3:
                light = this.placeTower;
                hover = this.placeTowerHov;
                hasHoverText = true;
                cost = (int) this.map.getTowerCost();
                text0 = "T";
                text1 = "Build";
                text2 = "tower";
                break;
            case 4:
                light = this.upgradeRange;
                hover = this.upgradeRangeHov;
                hasHoverText = false;
                text0 = "U1";
                text1 = "Upgrade";
                text2 = "range";
                break;
            case 5:
                light = this.upgradeSpeed;
                hover = this.upgradeSpeedHov;
                hasHoverText = false;
                text0 = "U2";
                text1 = "Upgrade";
                text2 = "speed";
                break;
            case 6:
                light = this.upgradeDamage;
                hover = this.upgradeDamageHov;
                hasHoverText = false;
                text0 = "U3";
                text1 = "Upgrade";
                text2 = "damage";
                break;
            case 7:
                light = false;
                hover = this.manaPoolHov;
                hasHoverText = true;
                cost = (int) this.map.getMana().getPoolCost();
                text0 = "M";
                text1 = "Mana pool";
                text2 = "cost: " + cost;
                break;
            case 8:
                light = this.map.getPoison();
                hover = this.poisonHov;
                hasHoverText = true;
                cost = (int) this.map.getApp().poisonCost;
                text0 = "U4";
                text1 = "Poison all";
                text2 = "cost: " + cost;
                break;
        }

        if (light) {
            app.fill(255, 255, 0);
        } else if (hover) {
            app.fill(200, 200, 200);
        } else {
            app.noFill();
        }
        int y = App.TOPBAR + buttonNO * App.BUTTON_SPACING + (buttonNO - 1) * App.BUTTON_SIZE;
        app.stroke(0, 0, 0);
        app.strokeWeight(2);
        app.rect(App.BUTTON_X, y, App.BUTTON_SIZE, App.BUTTON_SIZE);

        app.fill(0, 0, 0);
        app.textSize(App.BUTTON_TEXT_0_SIZE);
        app.text(text0, App.BUTTON_X + App.BUTTON_TEXT_SHIFT_X, y + App.BUTTON_TEXT_0_SHIFT_Y);

        app.textSize(App.BUTTON_TEXT_12_SIZE);
        app.text(text1, App.BUTTON_X + App.BUTTON_SIZE + App.BUTTON_TEXT_SHIFT_X, y + App.BUTTON_TEXT_1_SHIFT_Y);
        app.text(text2, App.BUTTON_X + App.BUTTON_SIZE + App.BUTTON_TEXT_SHIFT_X, y + App.BUTTON_TEXT_2_SHIFT_Y);

        if (hasHoverText && hover) {
            app.fill(255, 255, 255);
            app.rect(App.BUTTON_HOVER_X, y, App.BUTTON_HOVER_LENGTH, App.BUTTON_HOVER_HEIGHT);

            app.fill(0, 0, 0);
            app.textSize(App.BUTTON_HOVER_TEXT_SIZE);
            app.text("Cost: " + cost, App.BUTTON_HOVER_TEXT_X, y + App.BUTTON_HOVER_TEXT_SHIFT_Y);
        }
    }

    /**
     * Handles mouse clicks for tower placement and upgrades.
     * @param app The application object.
     */
    public void click(App app) {
        if (isMouseInMap(app.mouseX, app.mouseY)) {
            if (this.placeTower) {
                this.map.place(app.mouseX, app.mouseY, this.upgradeRange, this.upgradeSpeed, this.upgradeDamage);
            }
            this.map.upgrade(app.mouseX, app.mouseY, this.upgradeRange, this.upgradeSpeed, this.upgradeDamage);
        }
    }

    /**
     * Renders a hover effect when the player is placing a tower.
     * @param app The application object used for drawing.
     */
    public void hoverPlace(App app) {
        app.noFill();
        app.stroke(0, 0, 0);
        app.strokeWeight(2);
        int x1 = app.mouseX - App.CELLSIZE / 2;
        int y1 = app.mouseY - App.CELLSIZE / 2;
        int x2 = app.mouseX + App.CELLSIZE / 2;
        int y2 = app.mouseY + App.CELLSIZE / 2;
        int circleGrow = 10;
        int crossGrow = -10;

        if (this.placeTower) {
            app.image(app.loadImage("src/main/resources/WizardTD/towerGrey.png"), x1, y1);
        }
        if (this.upgradeSpeed) {
            app.rect(x1, y1, App.CELLSIZE, App.CELLSIZE);
        }
        if (this.upgradeRange) {
            app.ellipse(app.mouseX, app.mouseY, App.CELLSIZE + circleGrow, App.CELLSIZE + circleGrow);
        }
        if (this.upgradeDamage) {
            app.line(x1 - crossGrow, y1 - crossGrow, x2 + crossGrow, y2 + crossGrow);
            app.line(x1 - crossGrow, y2 + crossGrow, x2 + crossGrow, y1 - crossGrow);
        }
    }

    /**
     * Displays an upgrade bubble showing potential tower upgrades.
     * @param app The application object used for drawing.
     * @param tower The tower being considered for upgrade.
     */
    public void upgradeBubble(App app, Tower tower) {
        boolean wantAffordRange = this.upgradeRange
                && (int) tower.getRangeCost() < (int) this.map.getMana().getCurrMana();
        int wARange = (wantAffordRange) ? 1 : 0;
        boolean wantAffordSpeed = this.upgradeSpeed && (int) tower.getFiringSpeedCost()
                + wARange * (int) tower.getRangeCost() < (int) this.map.getMana().getCurrMana();
        int wASpeed = (wantAffordSpeed) ? 1 : 0;
        boolean wantAffordDamage = this.upgradeDamage
                && (int) tower.getDamageCost() + wASpeed * (int) tower.getFiringSpeedCost()
                        + wARange * (int) tower.getRangeCost() < (int) this.map.getMana().getCurrMana();
        int wADamage = (wantAffordDamage) ? 1 : 0;
        int upgrades = wADamage + wASpeed + wARange;
        int textTally = 0;
        int totalCost = wARange * (int) tower.getRangeCost() + wASpeed * (int) tower.getFiringSpeedCost()
                + wADamage * (int) tower.getDamageCost();
        app.stroke(0, 0, 0);
        app.strokeWeight(2);
        app.fill(255, 255, 255);

        app.rect(App.UPGRADE_BUBBLE_X, App.UPGRADE_BUBBLE_Y, App.UPGRADE_BUBBLE_LENGTH, App.UPGRADE_BUBBLE_HEIGHT);
        app.rect(App.UPGRADE_BUBBLE_X, App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_HEIGHT, App.UPGRADE_BUBBLE_LENGTH,
                App.UPGRADE_BUBBLE_HEIGHT * upgrades);
        app.rect(App.UPGRADE_BUBBLE_X, App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_HEIGHT * (upgrades + 1),
                App.UPGRADE_BUBBLE_LENGTH, App.UPGRADE_BUBBLE_HEIGHT);

        app.fill(0, 0, 0);
        app.textSize(App.UPGRADE_BUBBLE_TEXT_SIZE);
        app.text("Upgrade cost", App.UPGRADE_BUBBLE_X + App.UPGRADE_BUBBLE_TEXT_SHIFT_X,
                App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_TEXT_SHIFT_Y + textTally * (App.UPGRADE_BUBBLE_HEIGHT));
        textTally++;

        if (wARange == 1) {
            app.text("range:     " + (int) tower.getRangeCost(), App.UPGRADE_BUBBLE_X + App.UPGRADE_BUBBLE_TEXT_SHIFT_X,
                    App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_TEXT_SHIFT_Y + textTally * (App.UPGRADE_BUBBLE_HEIGHT));
            textTally++;
        }
        if (wASpeed == 1) {
            app.text("speed:     " + (int) tower.getFiringSpeedCost(),
                    App.UPGRADE_BUBBLE_X + App.UPGRADE_BUBBLE_TEXT_SHIFT_X,
                    App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_TEXT_SHIFT_Y + textTally * (App.UPGRADE_BUBBLE_HEIGHT));
            textTally++;
        }
        if (wADamage == 1) {
            app.text("damage: " + (int) tower.getDamageCost(), App.UPGRADE_BUBBLE_X + App.UPGRADE_BUBBLE_TEXT_SHIFT_X,
                    App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_TEXT_SHIFT_Y + textTally * (App.UPGRADE_BUBBLE_HEIGHT));
            textTally++;
        }

        app.text("Total:      " + totalCost, App.UPGRADE_BUBBLE_X + App.UPGRADE_BUBBLE_TEXT_SHIFT_X,
                App.UPGRADE_BUBBLE_Y + App.UPGRADE_BUBBLE_TEXT_SHIFT_Y + textTally * (App.UPGRADE_BUBBLE_HEIGHT));
    }

    
    /**
     * Updates the wave counter every tick.
     */
    public void tick() {
        this.waveSeconds = this.updateWaveSeconds();
    }

    /**
     * Renders all UI elements on the screen.
     * @param app The application object used for drawing.
     */
    public void draw(App app) {
        this.waveCountdown(app);

        this.manaBar(app);
        this.manaText(app);

        for(int i = 1; i <= App.NUMBER_OF_BUTTONS; i++) {
            this.buttonDraw(app, i);
        }

        hoverPlace(app);

        Tile potentialTower = this.map.mouse2Land(app.mouseX, app.mouseY);

        if (potentialTower instanceof Tower && (this.upgradeRange || this.upgradeSpeed || this.upgradeDamage)) {
            Tower tower = (Tower) potentialTower;
            upgradeBubble(app, tower);
        }
    }
}
