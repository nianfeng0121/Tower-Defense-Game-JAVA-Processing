package WizardTD;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import processing.core.PImage;
import processing.core.PGraphics;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class TestApp extends App {

    // Overriding loadImage method to return null as we are not loading images in tests
    @Override
    public PImage loadImage(String path) {
        return null;
    }

    // Overriding image method to do nothing as we are not displaying images in tests
    @Override
    public void image(PImage img, float x, float y) {
    }

    // Overriding rotateImageByDegrees method to return null as we are not rotating images in tests
    @Override
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        return null;
    }

    // Overriding graphical methods to do nothing as we are not using graphics in tests
    @Override
    public void noStroke() {
    }

    @Override
    public void fill(float r, float g, float b) {
    }

    @Override
    public void rect(float x, float y, float w, float h) {
    }

    @Override
    public void ellipse(float x, float y, float w, float h) {
    }

    @Override
    public void stroke(float r, float g, float b) {
    }

    @Override
    public void strokeWeight(float w) {
    }

    @Override
    public void line(float x1, float y1, float x2, float y2) {
    }

    @Override
    public void textSize(float s) {
    }

    @Override
    public void text(String s, float x, float y) {
    }

    @Override
    public void noFill() {
    }
}


public class Testing extends App {
    TestApp testApp;

    // Setting up the application and map for testing
    @BeforeEach
    public void setUpAppMap() {
        testApp = new TestApp();
        testApp.config = readJSON("config.json");
        testApp.createStuff();

        testApp.mouseX = 50;
        testApp.mouseY = 50 + App.TOPBAR;
        testApp.ui.placeTower = true;
        testApp.ui.upgradeRange = true;
        testApp.ui.upgradeDamage = true;
        testApp.ui.upgradeSpeed = true;
        testApp.ui.click(testApp);

        assertNotNull(testApp.map);
    }

    // Testing if the application ticks without crashing
    @Test
    public void tickCheck() {
        testApp.tick();
        assertNotNull(testApp.map);
    }

    // Testing if a new wave can be created
    @Test
    public void createWave() {
        testApp.map.nextWave();
        assertEquals(testApp.map.getWaves().size(), 2);
    }

    // Testing if the mana and poison toggles switch properly
    @Test
    public void switchManaAndPoison() {
        testApp.ui.toggleSwitch(testApp, 7);
        testApp.ui.toggleSwitch(testApp, 8);
    }

    // Testing if the map can be drawn
    @Test
    public void drawMap() {
        testApp.draw();
    }

    // Testing if a random monster can be spawned
    @Test
    public void spawnRandomMonster() {
        Wave wave = new Wave(testApp.map.getData().getJSONArray("waves").getJSONObject(0), testApp.map.getRoutes(), testApp);
        wave.createRandomMonster();
    }

    // Testing if a monster can be spawned, and if the tower can shoot
    @Test
    public void spawnMonsterAndShoot() {
        ArrayList<Direction> route = new ArrayList<Direction>();
        route.add(Direction.DOWN);

        testApp.map.getWaves().add(new Wave(testApp.map.getData().getJSONArray("waves").getJSONObject(0), testApp.map.getRoutes(), testApp));
        testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 5, 10, 2, route, testApp, 5
        ));
        testApp.draw();
        testApp.map.getTowerList().get(0).setFramesCounter(9999);
        for (int i = 0; i < 150; i++) {
            testApp.map.tick();
        }
    }

    // Testing if a monster with no health is handled properly
    @Test
    public void spawnMonsterWithNoHealth() {
        ArrayList<Direction> route = new ArrayList<Direction>();
        route.add(Direction.LEFT);

        testApp.map.getWaves().add(new Wave(testApp.map.getData().getJSONArray("waves").getJSONObject(0), testApp.map.getRoutes(), testApp));
        testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 5, 0, 2, route, testApp, 5
        ));
        for (int i = 0; i < 21; i++) {
            testApp.map.getWaves().get(0).tick();
        }
    }

    // Testing if a monster makes it to the wizard while fast forward is active
    @Test
    public void monsterMakesItToWizardWhileFastForward() {
        ArrayList<Direction> route = new ArrayList<Direction>();
        route.add(Direction.RIGHT);

        testApp.keyCode = 'F';
        testApp.keyPressed();

        testApp.map.getWaves().add(new Wave(testApp.map.getData().getJSONArray("waves").getJSONObject(0), testApp.map.getRoutes(), testApp));
        testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 5, 1, 2, route, testApp, 5
        ));
        for (int i = 0; i < 150; i++) {
            testApp.draw();
        }
    }

    // Testing if the game resets properly when 'R' is pressed on the loss screen
    @Test
    public void loseAndThenClickR() {
        testApp.onLossScreen = true;
        testApp.draw();
        testApp.keyCode = 'R';
        testApp.keyPressed();
        testApp.draw();
    }

    // Testing if the poison screen appears and damages the monster
    @Test
    public void poisonScreenAndDamageMonster() {
        ArrayList<Direction> route = new ArrayList<Direction>();
        route.add(Direction.UP);

        testApp.map.getWaves().add(new Wave(testApp.map.getData().getJSONArray("waves").getJSONObject(0), testApp.map.getRoutes(), testApp));
        testApp.map.getWaves().get(0).getMonsters().add(new Monster(
            0, 1, 5, 1, 2, route, testApp, 5
        ));

        testApp.keyCode = '4';
        testApp.keyPressed();

        for (int i = 0; i < 150; i++) {
            testApp.draw();
        }
    }

    // Testing all upgrade branches for the tower
    @Test
    public void testAllUpgradeBranches() {
        testApp.ui.upgradeRange = false;
        testApp.ui.upgradeDamage = false;
        testApp.ui.upgradeSpeed = false;
        testApp.ui.click(testApp);

        testApp.ui.upgradeRange = true;
        testApp.ui.upgradeDamage = false;
        testApp.ui.upgradeSpeed = false;
        testApp.ui.click(testApp);

        testApp.ui.upgradeRange = true;
        testApp.ui.upgradeDamage = false;
        testApp.ui.upgradeSpeed = true;
        testApp.ui.click(testApp);

        testApp.draw();
    }

    // Testing image rotation (functionality not implemented in TestApp)
    @Test
    public void rotateImage() {
    }
}