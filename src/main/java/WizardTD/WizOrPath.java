package WizardTD;

import java.util.HashMap;

/**
 * WizOrPath is an abstract class that represents either a wizard tower or a path on the game map.
 * It extends the Tile class, adding additional properties and methods specific to wizard towers and paths.
 */
abstract class WizOrPath extends Tile {
    // Constant representing the size of a cell on the game map.
    public static final int CELLSIZE = App.CELLSIZE;

    // Distance from the nearest wizard tower, used for game mechanics.
    protected int wizDist = 0;
    
    // A map representing adjacent tiles in each direction.
    protected HashMap<Direction, Tile> adj = new HashMap<Direction, Tile>();
    
    // The optimal direction to move towards, used for pathfinding.
    protected Direction optimal;
    
    // An array representing terminal directions (edges of the map).
    protected Direction[] termArray = new Direction[2];

    /**
     * Constructor for the WizOrPath class.
     * @param x The x-coordinate of the tile on the map.
     * @param y The y-coordinate of the tile on the map.
     * @param map A reference to the Map object.
     * @param spritePath The file path to the sprite image for the tile.
     */
    public WizOrPath(int x, int y, Map map, String spritePath) {
        // Call to the superclass (Tile) constructor.
        super(x, y, map, spritePath);
    }

    /**
     * Get the terminal directions of this tile.
     * @return An array of Direction representing the terminal directions.
     */
    public Direction[] getTerminals() {
        return this.termArray;
    }

    /**
     * Assign properties to the tile based on its position and adjacent tiles.
     */
    public void assignProperties() {
        this.termArray = this.findTerminality();
        this.adj = this.buildAdj();
    }

    /**
     * Find terminal directions based on the tile's position.
     * @return An array of Direction representing the terminal directions.
     */
    public Direction[] findTerminality() {
        Direction[] termArray = new Direction[2];
        int index = 0;

        // Check if the tile is at the edge of the map and assign terminal directions accordingly.
        if (this.x == 19) {
            termArray[index] = Direction.RIGHT;
            index++;
        } else if (this.x == 0) {
            termArray[index] = Direction.LEFT;
            index++;
        }
        if (this.y == 0) {
            termArray[index] = Direction.UP;
            index++;
        } else if (this.y == 19) {
            termArray[index] = Direction.DOWN;
            index++;
        }
        
        // Fill remaining terminal directions with NONE.
        for(int i = index; i < 2; i++) {
            termArray[i] = Direction.NONE;
        }

        return termArray;
    }

    /**
     * Find directions that exist based on the tile's terminal directions.
     * @return An array of boolean values representing the existence of directions.
     */
    public boolean[] findDirectionsThatExist() {
        boolean[] directionsThatExist = new boolean[4];

        // Initialize all directions to exist.
        for(int i = 0; i < 4; i++) {
            directionsThatExist[i] = true;
        }

        // Set directions that are terminal to not exist.
        for(Direction dir: this.termArray) {
            if (dir == Direction.RIGHT) {
                directionsThatExist[0] = false;
            } else if (dir == Direction.UP) {
                directionsThatExist[1] = false;
            } else if (dir == Direction.LEFT) {
                directionsThatExist[2] = false;
            } else if (dir == Direction.DOWN) {
                directionsThatExist[3] = false;
            }
        }
        return directionsThatExist;
    }

    /**
     * Build a map of adjacent tiles in each direction.
     * @return A HashMap of Direction to Tile representing adjacent tiles.
     */
    public HashMap<Direction, Tile> buildAdj() {
        HashMap<Direction, Tile> adj = new HashMap<Direction, Tile>();

        boolean[] directionsThatExist = this.findDirectionsThatExist();
        
        // Set adjacent tiles based on the existence of directions.
        if (directionsThatExist[0]) {
            adj.put(Direction.RIGHT, this.map.getLand()[this.x + 1][this.y]);
        } else {
            adj.put(Direction.RIGHT, null);
        }
        if (directionsThatExist[1]) {
            adj.put(Direction.UP, this.map.getLand()[this.x][this.y - 1]);
        } else {
            adj.put(Direction.UP, null);
        }
        if (directionsThatExist[2]) {
            adj.put(Direction.LEFT, this.map.getLand()[this.x - 1][this.y]);
        } else {
            adj.put(Direction.LEFT, null);
        }
        if (directionsThatExist[3]) {
            adj.put(Direction.DOWN, this.map.getLand()[this.x][this.y + 1]);
        } else {
            adj.put(Direction.DOWN, null);
        }
        
        return adj;
    }

    /**
     * Determine the wizard distances for adjacent path tiles.
     */
    public void determineWizDists() {
        for(Tile i: this.adj.values()) {
            if (
                i instanceof Path
                && (((Path)i).wizDist == 0
                || ((Path)i).wizDist > this.wizDist + 1)
                ) {
                ((Path)i).wizDist = this.wizDist + 1;
                
                for(Direction j: ((Path)i).adj.keySet()) {
                    if (((Path)i).adj.get(j) == this) {
                        ((Path)i).optimal = j;
                    }
                }
                ((Path)i).determineWizDists();
            }
        }
    }
}
