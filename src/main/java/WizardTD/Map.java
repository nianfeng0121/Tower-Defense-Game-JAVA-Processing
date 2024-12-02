package WizardTD;

import processing.core.PApplet;
import processing.data.JSONObject;
import processing.data.JSONArray;

import java.util.*;

enum Direction {
    UP, DOWN, LEFT, RIGHT, NONE;
}

public class Map {
    // Constants and class variables
    static final int BOARD_WIDTH = App.BOARD_WIDTH;
    static final int FPS = App.FPS;

    private Tile[][] land = new Tile[BOARD_WIDTH][BOARD_WIDTH];
    private int[] wizCordsXY = new int[2];
    private App app;
    private Wizard wizard;
    private HashMap<Path, ArrayList<Direction>> routes = new HashMap<Path, ArrayList<Direction>>();
    private JSONObject data;
    private ArrayList<Wave> waveList = new ArrayList<>();
    private int waveNumber = 0;
    private double waveTime;
    private boolean lastWave = false;
    private Mana mana;
    private ArrayList<Tower> towerList = new ArrayList<>();
    private boolean poison = false;
    private double poisonFrames;

    public Map(Iterable<String> mapIterable, App app, JSONObject data) {
        this.poisonFrames = app.poisonFrames;
        this.app = app;
        this.land = this.iterator2Matrix(mapIterable.iterator());
        this.updateAllPaths();
        this.wizard.determineWizDists();
        this.createRoutes();
        this.data = data;
        JSONObject tempWave = this.data.getJSONArray("waves").getJSONObject(this.waveNumber);
        this.waveList.add(new Wave(tempWave, this.routes, this.app));
        this.waveTime = this.addWaveTimes() + tempWave.getDouble("pre_wave_pause") * FPS;
        this.mana = new Mana(
                this.data.getDouble("initial_mana"),
                this.data.getDouble("initial_mana_cap"),
                this.data.getDouble("initial_mana_gained_per_second"),
                this.data.getDouble("mana_pool_spell_initial_cost"),
                this.data.getDouble("mana_pool_spell_cost_increase_per_use"),
                this.data.getDouble("mana_pool_spell_cap_multiplier"),
                this.data.getDouble("mana_pool_spell_mana_gained_multiplier"));
    }

    // Getters for various properties
    public Tile[][] getLand() {
        return this.land;
    }

    public ArrayList<Tower> getTowerList() {
        return this.towerList;
    }

    public App getApp() {
        return this.app;
    }

    public boolean getLastWave() {
        return this.lastWave;
    }

    public int getWaveNumber() {
        return this.waveNumber;
    }

    public ArrayList<Wave> getWaves() {
        return this.waveList;
    }

    public double getWaveTime() {
        return this.waveTime;
    }

    public Mana getMana() {
        return this.mana;
    }

    public double getTowerCost() {
        return this.data.getDouble("tower_cost");
    }

    public double getInitialTowerRange() {
        return this.data.getDouble("initial_tower_range");
    }

    public double getInitialTowerFiringSpeed() {
        return this.data.getDouble("initial_tower_firing_speed");
    }

    public double getInitialTowerDamage() {
        return this.data.getDouble("initial_tower_damage");
    }

    public boolean getPoison() {
        return this.poison;
    }

    public HashMap<Path, ArrayList<Direction>> getRoutes() {
        return this.routes;
    }

    public JSONObject getData() {
        return this.data;
    }

    /**
     * Toggles poison effect based on mana availability
     */
    public void togglePoison() {
        if (!this.poison && this.mana.updateMana(-1 * this.app.poisonCost)) {
            this.poison = true;
        }
    }

    /**
     * Convert the map's iterable representation into a 2D matrix of tiles
     */
    public Tile[][] iterator2Matrix(Iterator<String> scan) {
        Tile[][] matrix = new Tile[BOARD_WIDTH][BOARD_WIDTH];
        int i;
        for (int j = 0; j < BOARD_WIDTH; j++) {
            i = 0;
            for (char c : scan.next().toCharArray()) {
                switch (c) {
                    case 'S':
                        matrix[i][j] = new Shrub(i, j, this);
                        break;
                    case 'X':
                        matrix[i][j] = new Path(i, j, this);
                        break;
                    case 'W':
                        this.wizard = new Wizard(i, j, this);
                        matrix[i][j] = this.wizard;
                        this.wizCordsXY[0] = i;
                        this.wizCordsXY[1] = j;
                        break;
                    case ' ':
                        matrix[i][j] = new Grass(i, j, this);
                        break;
                }
                i++;
                if (i >= BOARD_WIDTH) {
                    break;
                }
            }
            while (i < BOARD_WIDTH) {
                matrix[i][j] = new Grass(i, j, this);
                i++;
            }
        }
        return matrix;
    }

    /**
     * Calculate the wave time by adding current wave time and next wave's pre-wave pause
     */
    public double addWaveTimes() {
        JSONArray waves = this.data.getJSONArray("waves");
        if (waves.size() == 1) {
            this.lastWave = true;
            return -1 * (waves.getJSONObject(this.waveNumber).getDouble("pre_wave_pause") * FPS + 1);
        }
        return (waves.getJSONObject(this.waveNumber).getDouble("duration")
                + waves.getJSONObject(this.waveNumber + 1).getDouble("pre_wave_pause")) * FPS;
    }

    /**
     * Assign properties to each path on the map
     */
    public void updateAllPaths() { 
        for(Tile[] row: this.land) {
            for(Tile entry: row) {
                if (entry instanceof WizOrPath) {
                    ((WizOrPath)entry).assignProperties();
                    if (entry instanceof Path && ((Path)entry).termArray[0] != Direction.NONE) {
                        this.routes.put((Path)entry, null);
                    }
                }
                if (entry instanceof Path) {
                    ((Path)entry).updatePath();
                }
            }
        }
    }

    public ArrayList<Direction> createRoute(Path spawn) { 
        ArrayList<Direction> route = new ArrayList<Direction>();
        WizOrPath current = spawn;
        while(!(current instanceof Wizard)) {
            Direction currentDirection = ((Path)current).optimal;
            route.add(currentDirection);
            current = (WizOrPath)current.adj.get(currentDirection);
        }
        return route;
    }

    public void createRoutes() {
        Iterator<Path> spawnIterator = this.routes.keySet().iterator();
        while(spawnIterator.hasNext()) {
            Path spawn = spawnIterator.next();
            if (spawn.wizDist > 0){
                this.routes.put(spawn, this.createRoute(spawn));
            } else {
                spawnIterator.remove();
            }
        }
    }

    public void nextWave() {
        this.waveNumber++;
        this.waveList.add(new Wave(this.data.getJSONArray("waves").getJSONObject(waveNumber), this.routes, this.app));
        if (this.waveNumber == this.data.getJSONArray("waves").size() - 1) { 
            this.lastWave = true;
        } else {
            this.waveTime = this.addWaveTimes();
        }
    }

    static int[] mouse2Tile(int x, int y) {
        int[] tileCords = new int[2];
        tileCords[0] = Math.floorDiv(x, App.CELLSIZE);
        tileCords[1] = Math.floorDiv(y - App.TOPBAR, App.CELLSIZE);
        return tileCords;
    }

    public boolean place(
        int x, int y, boolean initialRangeLevel, 
        boolean initialFiringSpeedLevel, boolean initialDamageLevel) {
        int noOfUpgrades = (initialRangeLevel ? 1 : 0) + 
                           (initialFiringSpeedLevel ? 1 : 0) + 
                           (initialDamageLevel ? 1 : 0);
        int[] tileCords = mouse2Tile(x, y);
        if (this.land[tileCords[0]][tileCords[1]] instanceof Grass) {
            while(noOfUpgrades >= 0 && !this.mana.updateMana(-1 * (this.getTowerCost() + 20 * noOfUpgrades))) {
                noOfUpgrades--;
            } 
            if (noOfUpgrades < 0) {
                return false;
            } 
            boolean[] upgrades = this.determineUpgrades(noOfUpgrades, initialRangeLevel, initialFiringSpeedLevel, initialDamageLevel);
            Tower tower = new Tower(tileCords[0], tileCords[1], this.getInitialTowerRange(), this.getInitialTowerFiringSpeed(), this.getInitialTowerDamage(), upgrades[0], upgrades[1], upgrades[2], this);
            this.land[tileCords[0]][tileCords[1]] = tower;
            this.towerList.add(tower);
            return true;
        }
        return false;
    }

    public boolean[] determineUpgrades(int noOfUpgrades, boolean initialRangeLevel, boolean initialFiringSpeedLevel, boolean initialDamageLevel) {
        boolean range = false;
        boolean speed = false;
        boolean dmg = false;
        if (noOfUpgrades == 3) {
            range = true;
            speed = true;
            dmg = true;
        } else if (noOfUpgrades == 2) {
            if (initialRangeLevel) {
                range = true;
            } 
            if (initialFiringSpeedLevel) {
                speed = true;
            } 
            if (initialDamageLevel && !(range && speed)) {
                dmg = true;
            }
        } else if (noOfUpgrades == 1) {
            if (initialRangeLevel) {
                range = true;
            } else if (initialFiringSpeedLevel) {
                speed = true;
            } else {
                dmg = true;
            }
        }
        return new boolean[] {range, speed, dmg};
    }

    public void upgrade(int x, int y, boolean range, boolean speed, boolean dmg) {
        int[] tileCords = mouse2Tile(x, y);
        if (this.land[tileCords[0]][tileCords[1]] instanceof Tower) {
            Tower tower = (Tower)this.land[tileCords[0]][tileCords[1]];
            int rangeInt = range ? 1 : 0;
            int speedInt = speed ? 1 : 0;
            int dmgInt = dmg ? 1 : 0;
            if (range && speed && dmg && this.mana.updateMana(-1 * (rangeInt * tower.getRangeCost() + speedInt * tower.getFiringSpeedCost() + dmgInt * tower.getDamageCost()))) {
                tower.upgradeRange();
                tower.upgradeFiringSpeed();
                tower.upgradeDamage();
            } else if (range && speed && this.mana.updateMana(-1 * (rangeInt * tower.getRangeCost() + speedInt * tower.getFiringSpeedCost()))) {
                tower.upgradeRange();
                tower.upgradeFiringSpeed();
            } else if (range && dmg && this.mana.updateMana(-1 * (rangeInt * tower.getRangeCost() + dmgInt * tower.getDamageCost()))) {
                tower.upgradeRange();
                tower.upgradeDamage();
            } else if (speed && dmg && this.mana.updateMana(-1 * (speedInt * tower.getFiringSpeedCost() + dmgInt * tower.getDamageCost()))) {
                tower.upgradeFiringSpeed();
                tower.upgradeDamage();
            } else if (range && this.mana.updateMana(-1 * (rangeInt * tower.getRangeCost()))) {
                tower.upgradeRange();
            } else if (speed && this.mana.updateMana(-1 * (speedInt * tower.getFiringSpeedCost()))) {
                tower.upgradeFiringSpeed();
            } else if (dmg && this.mana.updateMana(-1 * (dmgInt * tower.getDamageCost()))) {
                tower.upgradeDamage();
            }
        }
    }

    public void drawRangeCircle(App app) {
        Tile potentialTower = this.mouse2Land(app.mouseX, app.mouseY);
        if (potentialTower instanceof Tower) {
            Tower tower = (Tower)potentialTower;
            tower.draw(this.app);
            int centerX = App.CELLSIZE * tower.getX() + App.CELLSIZE / 2;
            int centerY = App.CELLSIZE * tower.getY() + App.TOPBAR + App.CELLSIZE / 2;
            float diameter = (float)(tower.getRange() * 2);
            app.noFill();
            app.stroke(255, 255, 0);
            app.strokeWeight(2);
            app.ellipse(centerX, centerY, diameter, diameter);
        }
    }

    public Tile mouse2Land(int x, int y) {
        if (Ui.isMouseInMap(x, y)) {
            int[] tileCords = mouse2Tile(x, y);
            return this.land[tileCords[0]][tileCords[1]];
        } else {
            return null;
        }
    }

    /**
     * Progress the game state by one frame, updating all game elements accordingly
     */
    public void tick() {
        if (!(waveNumber == 0 && this.waveTime > this.addWaveTimes())) {
            Iterator<Wave> waveIterator = this.waveList.iterator();
            while (waveIterator.hasNext()) {
                Wave wave = waveIterator.next();
                wave.tick();
                if (wave.getWaveComplete()) {
                    waveIterator.remove();
                }
            }
            this.waveTime -= app.rate;
            if (this.lastWave && this.waveList.size() == 0) {
                this.app.onWinScreen = true;
            }
            if (this.waveTime < 0 && !this.lastWave) {
                this.nextWave();
            }
        } else {
            this.waveTime -= app.rate;
        }
        this.mana.tick(this.app);
        for (Tower tower : this.towerList) {
            tower.tick();
        }
        if (this.poison && this.poisonFrames <= 0) {
            this.poison = false;
            this.poisonFrames = this.app.poisonFrames;
        } else {
            this.poisonFrames -= this.app.rate;
        }
    }

    /**
     * Draw the game map and all its elements on the screen
     */
    public void draw(PApplet app) {
        for(Tile[] row: this.land) {
            for(Tile entry: row) {
                if (!(entry instanceof Wizard)) {
                    entry.draw(app); 
                } else {
                    Tile wizGrass = new Grass(wizCordsXY[0], wizCordsXY[1], this);
                    wizGrass.draw(app);
                }
            }
        }
        for(Wave wave: this.waveList) {
            wave.draw();
        }
        this.land[wizCordsXY[0]][wizCordsXY[1]].draw(app); 
        drawRangeCircle(this.app);
        for(Tower tower: this.towerList) {
            for(Fireball fireball: tower.getProjectiles()) {
                fireball.draw();
            }
        }
    }
}
