package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

// Main class for the Wizard Tower Defense game
public class App extends PApplet {

    // Constants defining various sizes and positions used in the game
    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;
    public static final int WIZ_SHIFT_X = -8;
    public static final int WIZ_SHIFT_Y = -5;
    public static final int GHOST_SHIFT_X = 5;
    public static final int GHOST_SHIFT_Y = 5;
    public static final int HEALTH_SHIFT_X = -5;
    public static final int HEALTH_SHIFT_Y = -5;
    public static final int HEALTH_LENGTH = 30;
    public static final int HEALTH_WIDTH = 2;
    public static final int MANA_X = 375;
    public static final int MANA_Y = 10;
    public static final int MANA_LENGTH = 320;
    public static final int MANA_WIDTH = 18;
    public static final int MANA_TEXT_X = MANA_X - 60;
    public static final int MANA_TEXT_Y = MANA_Y + 16;
    public static final int MANA_CURR_SHIFT = 173;
    public static final int LOST_X = 240;
    public static final int LOST_Y = 227;
    public static final int BUTTON_X = CELLSIZE * BOARD_WIDTH + 6;
    public static final int BUTTON_SIZE = 42;
    public static final int BUTTON_SPACING = 10;
    public static final int BUTTON_TEXT_0_SIZE = 25;
    public static final int BUTTON_TEXT_12_SIZE = 12;
    public static final int BUTTON_TEXT_SHIFT_X = 5;
    public static final int BUTTON_TEXT_0_SHIFT_Y = 30;
    public static final int BUTTON_TEXT_1_SHIFT_Y = 15;
    public static final int BUTTON_TEXT_2_SHIFT_Y = BUTTON_TEXT_1_SHIFT_Y + 20;
    public static final int BUTTON_HOVER_LENGTH = 72;
    public static final int BUTTON_HOVER_HEIGHT = 20;
    public static final int BUTTON_HOVER_X = CELLSIZE * BOARD_WIDTH - BUTTON_HOVER_LENGTH - 7;
    public static final int BUTTON_HOVER_TEXT_X = BUTTON_HOVER_X + 4;
    public static final int BUTTON_HOVER_TEXT_SIZE = BUTTON_TEXT_12_SIZE;
    public static final int BUTTON_HOVER_TEXT_SHIFT_Y = BUTTON_TEXT_1_SHIFT_Y;
    public static final int TOWER_FIRST_UPGRADE_SHIFT_X = 5;
    public static final int TOWER_FIRST_UPGRADE_SHIFT_Y = 2;
    public static final int RANGE_UPGRADE_DIAMETER = 6;
    public static final int TOWER_FIRST_UPGRADE_DMG_SHIFT_Y = 25;
    public static final int TOWER_UPGRADE_CIRCLE_DIST = 2;
    public static final int TOWER_UPGRADE_CROSS_DIST = 5;
    public static final int TOWER_DAMAGE_CROSS_LENGTH_X = RANGE_UPGRADE_DIAMETER - 3;
    public static final int TOWER_DAMAGE_CROSS_LENGTH_Y = RANGE_UPGRADE_DIAMETER;
    public static final int TOWER_SPEED_SQUARE_SHIFT = 5;
    public static final int TOWER_SPEED_SQUARE_LENGTH = 20;
    public static final int UPGRADE_BUBBLE_X = CELLSIZE * BOARD_WIDTH + 10;
    public static final int UPGRADE_BUBBLE_Y = 16 * CELLSIZE + TOPBAR;
    public static final int UPGRADE_BUBBLE_LENGTH = BUTTON_SIZE * 2;
    public static final int UPGRADE_BUBBLE_HEIGHT = BUTTON_SIZE / 2;
    public static final int UPGRADE_BUBBLE_TEXT_SIZE = 12;
    public static final int UPGRADE_BUBBLE_TEXT_SHIFT_X = 3;
    public static final int UPGRADE_BUBBLE_TEXT_SHIFT_Y = UPGRADE_BUBBLE_HEIGHT / 2 + 5;
    public static final int SPRITE_SHIFT = GHOST_SHIFT_X + 10;
    public static final int PROJ_SPEED = 5;
    public static final int MONSTER_RADIUS = 20 / 2;
    public static final int FIREBALL_RADIUS = 6 / 3;
    public static final char[] BUTTONS = {' ', 'F', 'P', 'T', '1', '2', '3', 'M', '4'};
    public static final int NUMBER_OF_BUTTONS = BUTTONS.length - 1;
    public static final int FPS = 60;

    // Calculated width and height of the window based on cell size, board width, and sidebar/topbar sizes
    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH * CELLSIZE + TOPBAR;

    // Game state variables
    public int doubleRate = 1;
    public int pauseRate = 1;
    public int rate = doubleRate * pauseRate;
    public boolean onWinScreen = false;
    public boolean onLossScreen = false;

    // Configuration and game objects
    public String configPath;
    public JSONObject config;
    public Map map;
    public Ui ui;
    public Iterable<String> mapIterable;

    // Poison tower settings
    public double poisonCost;
    public double poisonFrames;
    public double poisonDamage;
    
    public Monster monster;

    public Random random = new Random();

    // Constructor
    public App() {
    }

    // Setup the size of the window
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    // Initialization function, called once when the program starts
	@Override
    public void setup() {
        frameRate(FPS);
        this.configPath = "config.json";
        this.config = readJSON(configPath);
        this.createStuff();
    }

    // Function to initialize game objects and settings
    public void createStuff() {
        // Initialize poison tower settings, with defaults if not specified in config
        if (this.config.hasKey("poison_cost")) {
            this.poisonCost = this.config.getDouble("poison_cost");
        } else {
            this.poisonCost = 100;
        }
        if (this.config.hasKey("poison_time")) {
            this.poisonFrames = this.config.getDouble("poison_time") * FPS;
        } else {
            this.poisonFrames = 5 * FPS;
        }
        if (this.config.hasKey("poison_damage_per_second")) {
            this.poisonDamage = this.config.getDouble("poison_damage_per_second");
        } else {
            this.poisonDamage = 1;
        }

        // Initialize the game map and UI
        Scanner scan = fileIO(this.config.getString("layout"));
        this.mapIterable = scan2Iterable(scan);
        scan.close();

        this.map = new Map(this.mapIterable, this, this.config);
        this.ui = new Ui(this.map);
    }

    // Utility function to read from a file
    static Scanner fileIO(String loc) {
        File f = new File(loc);
        Scanner scan;
        try {
            scan = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            return null;
        }
        return scan;
    }

    // Utility function to parse a JSON file
    static JSONObject readJSON(String path) {
        String json = "";
        try {
            json = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            System.out.println("File not found!");
            return null;
        }
        return JSONObject.parse(json);
    }

    // Convert the content of a Scanner to an Iterable of Strings
    static Iterable<String> scan2Iterable(Scanner scan) {
        ArrayList<String> lines = new ArrayList<String>();
        while (scan.hasNextLine()) {
            lines.add(scan.nextLine());
        }
        return lines;
    }

    // Calculate the distance between two points
    static double scalarDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    // Handle key press events
	@Override
    public void keyPressed() {
        if (this.onLossScreen) {
            if (this.keyCode == 'R') {
                this.onLossScreen = false;
                this.doubleRate = 1;
                this.pauseRate = 1;
                this.rate = doubleRate * pauseRate;
                this.map = new Map(this.mapIterable, this, this.config);
                this.ui = new Ui(this.map);
            }
        } else if (this.onWinScreen) {
        } else {
            for (int i = 1; i < App.BUTTONS.length; i++) {
                if (this.keyCode == App.BUTTONS[i]) {
                    this.ui.toggleSwitch(this, i);
                }
            }
        }
    }

    // Handle key release events
	@Override
    public void keyReleased() {
    }

    // Handle mouse press events
    @Override
    public void mousePressed(MouseEvent e) {
        for (int buttonNO = 1; buttonNO <= App.NUMBER_OF_BUTTONS; buttonNO++) {
            if (isMouseOverButton(buttonNO)) {
                this.ui.toggleSwitch(this, buttonNO);
            }
        }
        this.ui.click(this);
    }

    // Handle mouse release events
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    // Check if the mouse is hovering over any buttons
    public void mouseHover() {
        for (int buttonNO = 1; buttonNO <= App.NUMBER_OF_BUTTONS; buttonNO++) {
            if (isMouseOverButton(buttonNO)) {
                this.ui.setHoveredButton(buttonNO, true);
            } else {
                this.ui.setHoveredButton(buttonNO, false);
            }
        }
    }

    // Check if the mouse is over a specific button
    public boolean isMouseOverButton(int buttonNO) {
        return (this.mouseX > BUTTON_X &&
                this.mouseX < BUTTON_X + BUTTON_SIZE &&
                this.mouseY > TOPBAR + buttonNO * BUTTON_SPACING + (buttonNO - 1) * BUTTON_SIZE &&
                this.mouseY < buttonNO * (BUTTON_SPACING + BUTTON_SIZE) + TOPBAR);
    }

    // Update game state on each frame
    public void tick() {
        this.map.tick();
        this.ui.tick();
        this.mouseHover();
    }

    // Main drawing function, called on each frame
	@Override
    public void draw() {
        if (!this.onLossScreen && !this.onWinScreen) {
            this.tick();
            this.map.draw(this);
            this.noStroke();
            this.fill(131, 111, 75);
            this.rect(0, 0, WIDTH, TOPBAR);
            this.rect(CELLSIZE * BOARD_WIDTH, 0, SIDEBAR, HEIGHT);
            this.ui.draw(this);
        }
        if (this.onLossScreen) {
            this.fill(0, 255, 0);
            this.textSize(35);
            this.text("YOU LOST", LOST_X, LOST_Y);
            this.textSize(20);
            this.text("Press 'r' to restart", LOST_X - 7, LOST_Y + 30);
        }
        if (this.onWinScreen) {
            this.fill(255, 0, 255);
            this.textSize(35);
            this.text("YOU WIN", LOST_X, LOST_Y);
        }
    }

    // Main function to start the game
    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    // Function to rotate an image
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, RGB);

        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }
}
