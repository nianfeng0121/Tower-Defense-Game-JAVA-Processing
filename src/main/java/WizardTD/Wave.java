package WizardTD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import processing.data.JSONObject;
import java.util.Random;

/**
 * The Wave class represents a single wave of monsters in the game.
 */
public class Wave {
    // List to store all the monsters in the current wave
    private ArrayList<Monster> monsters = new ArrayList<Monster>();
    
    // A map storing the path and their respective directions for each monster to follow
    private HashMap<Path, ArrayList<Direction>> routes = new HashMap<Path, ArrayList<Direction>>();
    
    // JSON object containing the data for the wave
    private JSONObject waveData;
    
    // Reference to the main app for access to shared resources and methods
    private App app;
    
    // The number of frames between each monster spawn
    private int framesPerSpawn;
    
    // Total number of frames the wave lasts
    private int waveFrames;
    
    // Current frame counter
    private int currentFrame;
    
    // List to store the count of each type of monster to be spawned
    private ArrayList<Integer> monsterTypeCounts = new ArrayList<>();
    
    // Total number of monsters remaining to be spawned
    private int monstersRemaining = 0;
    
    // Array to store the possible paths where monsters can spawn
    private Path[] spawnPaths;
    
    // Flag to indicate if the wave is complete
    private boolean waveComplete = false;

    /**
     * Constructor for the Wave class.
     * @param waveData JSON object containing the wave data.
     * @param routes Map of paths and their respective directions.
     * @param app Reference to the main app.
     */
    public Wave(JSONObject waveData, HashMap<Path, ArrayList<Direction>> routes, App app) {
        this.routes = routes;
        this.waveData = waveData;
        this.app = app;
        this.waveFrames = (int)(waveData.getDouble("duration") * App.FPS);
        this.spawnPaths = routes.keySet().toArray(new Path[routes.size()]);

        // Initialize monsterTypeCounts and calculate total monsters to be spawned
        for(int i = 0; i < waveData.getJSONArray("monsters").size(); i++) {
            this.monsterTypeCounts.add(
                waveData.getJSONArray("monsters").getJSONObject(i).getInt("quantity")
            );
            this.monstersRemaining += this.monsterTypeCounts.get(i);
        }

        // Calculate framesPerSpawn based on total monsters and wave duration
        this.framesPerSpawn = this.monstersRemaining == 0 ? 0 : this.waveFrames / this.monstersRemaining;

        System.out.println("Wave created");
    }

    // Getter method for wave data
    public JSONObject getData() {
        return this.waveData;
    }

    // Getter method to check if the wave is complete
    public boolean getWaveComplete() {
        return this.waveComplete;
    }

    // Getter method for the list of monsters
    public ArrayList<Monster> getMonsters() {
        return this.monsters;
    }

    /**
     * Iterates through all the monsters in the wave, updating their state and removing any that no longer exist.
     */
    public void iterateThroughMonsters() {
        Iterator<Monster> monsterIterator = this.monsters.iterator();
        while(monsterIterator.hasNext()) {
            Monster monster = monsterIterator.next();
            monster.tick();

            if (!monster.getExists()) {
                monsterIterator.remove();
            }
        }
    }

    /**
     * Creates a random monster from the available types and adds it to the wave.
     */
    public void createRandomMonster() {
        Random rand = new Random();
        int randMonsterType = rand.nextInt(this.monsterTypeCounts.size());
        JSONObject type = this.waveData.getJSONArray("monsters").getJSONObject(randMonsterType);

        int randSpawnPath = rand.nextInt(this.spawnPaths.length);
        Path spawnPath = this.spawnPaths[randSpawnPath];

        this.monsters.add(new Monster(
            spawnPath.getX(), spawnPath.getY(), type.getDouble("speed"), 
            type.getDouble("hp"), type.getDouble("armour"), 
            this.routes.get(spawnPath), this.app, type.getDouble("mana_gained_on_kill")
        ));

        monsterTypeCounts.set(randMonsterType, monsterTypeCounts.get(randMonsterType) - 1);
        this.monstersRemaining -= 1;

        for(int i = 0; i < monsterTypeCounts.size(); i++) {
            if (monsterTypeCounts.get(i) == 0) {
                monsterTypeCounts.remove(i);
            }
        }
    }

    /**
     * Updates the state of the wave, spawning monsters and checking for completion.
     */
    public void tick() {
        this.currentFrame += app.rate;

        this.iterateThroughMonsters();

        if (this.monstersRemaining > 0 && this.currentFrame >= this.framesPerSpawn && this.spawnPaths.length > 0) {
            this.createRandomMonster();
            this.currentFrame = 0;
        }

        if (this.monstersRemaining == 0 && this.monsters.size() == 0) {
            this.waveComplete = true;
        }
    }

    /**
     * Draws all the monsters in the wave.
     */
    public void draw() {
        for(Monster monster : this.monsters) {
            monster.draw(this.app);
        }
    }
}
