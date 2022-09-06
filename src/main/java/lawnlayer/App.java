package lawnlayer;

// Importing required modules
import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PGraphics;
/////

// Libraries needed for implementaion
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
/////

// JSON and PFONT required by Assignment
import processing.data.JSONObject;
import processing.data.JSONArray;
import processing.core.PFont;


/**
 * Main Class that runs all code.
 * Entry point is this class's main method. Also part of the code that takes user input.
*/
public class App extends PApplet {


    // STATIC VARIABLE required by the game
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int SPRITESIZE = 20;
    private static final int TOPBAR = 80;
    private static final int LEVEL = 1;
    private static final int FPS = 60;
    //////

    
	
    // Various sprites that are implemented by the game
	private PImage grass;
    private PImage concrete;
    private PImage worm;
    private PImage beetle;
    private PImage gameover;
    private PImage mushroom;
    private PImage heart;
    private PImage pheart;
    private PImage bheart;
    private PImage pinapple;
    private PImage freeze;
    private Ball ball;
    private Level currLevel;
    //////

    // Boolean flags that help keep track of which keys are being pressed currently.
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean upPressed;
    private boolean downPressed;
    //////

    // Attributes related to the state of the game.
    private boolean collision;
    private int numLives;
    private boolean gameOver;
    private boolean gameWon;
    private boolean escape;
    //////

    // Attributes related to storing information before the game starts.
    protected String configPath;
    private JSONObject configJSON;
    private JSONArray levelsJSON;
    private JSONObject currLevelJSON;
    private int numLevels;
    private int levelCount;
    //////

    // Display related attributes.
    private PFont font;
    private PFont medFont;
    private PFont smallFont;
    private int fontSize;
    //////

    // Starting position of the ball
    private int ballStartinX;
    private int ballStartinY;
    //////

    // Returns the total no of tiles in the width of the 
    public static int getWidth() {
        return App.WIDTH / 20;
    }


    // Returns the height of the game fields.
    public static int getHeight() {
        return (App.HEIGHT - 80) / 20;
    }
    

    /**
     * Constructor for the main app class.
     * Attributes set here that do not require to be in setup.
    */
    public App() {
        this.configPath = "config.json";
        this.levelCount = 1;
        this.gameOver = false;
        this.gameWon = false;
        this.fontSize = 30;
        this.ballStartinX = 0;
        this.ballStartinY = 0;
        this.escape = false;
        // this.numLives = json.numLives

        this.leftPressed = false;
        this.rightPressed = false;
        this.upPressed = false;
        this.downPressed = false;

        this.collision = false;
    }


    /**
     * Initialise the setting of the window size.
    */
    public void settings() {
        size(WIDTH, HEIGHT);
    }


    // Returns FPS
    public int getFPS() {
        return App.FPS;
    }


    // Returns currLevel
    public Level getLevel() {
        return this.currLevel;
    }


    // Returns ball
    public Ball getBall() {
        return this.ball;
    }


    /**
     * Returns the sprite for the object requested. 
     * str representes a PImage object. 
     * @param str
     * @return returns a PI image
     */
    public PImage getImages(String str) {

        if (str.equals("w")) {
            return this.worm;

        } else if(str.equals("b")) {
            return this.beetle;

        } else if(str.equals("c")) {
            return this.concrete;

        } else if(str.equals("g")) {
            return this.grass;

        } else if(str.equals("h")) {
            return this.heart;

        } else if(str.equals("m")) {
            return this.mushroom;

        } else if(str.equals("ph")) {
            return this.pheart;

        } else if(str.equals("bh")) {
            return this.bheart;

        } else if(str.equals("p")) {
            return this.pinapple;

        } else if(str.equals("f")) {
            return this.freeze;
        

        // ERROR HANDELLING
        } else {
            System.out.println("ERROR: UNKNOWN ERROR 003 - INVALID IMAGE.");
            System.exit(1);

        }

        // TODO ERROR HANDELLING
        return this.worm;
    }


    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
    */
    public void setup() {
        frameRate(FPS);

        // Load images during setup
		// Load images during setup
		// this.grass = loadImage(this.getClass().getResource("grass.png").getPath());
        // this.concrete = loadImage(this.getClass().getResource("concrete_tile.png").getPath());
        // this.worm = loadImage(this.getClass().getResource("worm.png").getPath());
        // this.beetle = loadImage(this.getClass().getResource("beetle.png").getPath());


        this.grass = (this.loadImage("src/main/resources/lawnlayer/grass.png"));
        this.concrete = (this.loadImage("src/main/resources/lawnlayer/concrete_tile.png"));
        this.worm =  (this.loadImage("src/main/resources/lawnlayer/worm.png"));
        this.beetle = (this.loadImage("src/main/resources/lawnlayer/beetle.png"));
        this.gameover = (this.loadImage("src/main/resources/lawnlayer/game_over.png"));
        this.heart = (this.loadImage("src/main/resources/lawnlayer/Heart.png"));
        this.pheart = (this.loadImage("src/main/resources/lawnlayer/Pheart.png"));
        this.bheart = (this.loadImage("src/main/resources/lawnlayer/BHeart.png"));
        this.mushroom = (this.loadImage("src/main/resources/lawnlayer/Pmushroom_powerup.png"));
        this.pinapple = (this.loadImage("src/main/resources/lawnlayer/pinapple.png"));
        this.freeze = (this.loadImage("src/main/resources/lawnlayer/freeze.png"));

        // Loads in the ball
        this.ball = new Ball(this.loadImage("src/main/resources/lawnlayer/ball.png"), this.ballStartinX, this.ballStartinY, currLevel, this);

        // Universal font for the appp initialised here
        this.font = createFont("monospaced", 30);
        textFont(this.font);

        this.medFont = createFont("monospaced", 20);
        // textFont(this.medFont);

        this.smallFont = createFont("monospaced", 15);
        // textFont(this.smallFont);


        // Checks if JSON file exits
        try{
            this.configJSON = loadJSONObject(this.configPath);
        
        // ERROR HANDELLING: missing file
        } catch (java.lang.NullPointerException NPE) {
            // NPE.printStackTrace();
            System.out.println("ERROR: Config File is missing.");
            exit();
            return;

        }
        

        // Loads in a few properties from the config file.
        this.numLives = this.configJSON.getInt("lives");
        this.levelsJSON = this.configJSON.getJSONArray("levels");
        this.numLevels = this.levelsJSON.size();


        // If no levels then quit.
        if(numLevels == 0) {
            System.out.println("ERROR: NO LEVELS SPECIFIED IN CONFIG FILE");
            exit();
        }

        // Set the current level
        this.currLevelJSON = levelsJSON.getJSONObject(0);

        // Get the first level txt file
        String firstLevelName = currLevelJSON.getString("outlay");

        // Create new level object
        this.currLevel = new Level(firstLevelName, currLevelJSON, this.ball, this);
        this.ball.setLevel(currLevel);

    }


    // Return current set frameCount.
    public int frameCount() {
        return this.frameCount;
    }
	

    /**
     * Draw all elements in the game by current frame. 
    */
    public void draw() {
        
        if(this.escape && !this.gameOver && !this.gameWon) {
            fill(36, 37, 38, 1);
            rect(0, 0, 1280, 720);
            // fill(0);
            // rect(640 - 250 + 40, 320 + 80 - 200 - 40, 500, 400);
            fill(150, 75, 0);
            rect(640 - 250, 320 + 80 - 200, 500, 400);
            fill(255);
            text("PRESS ESC TO RESUME", 640 - 250 + 65, 320 + 80 - 250 + 120);
            return;
        }

        if(this.gameWon) {
            background(0, 0, 0);
            fill(255);
            text("YOU WIN", 580, 360);
            return;
        }

        // Check if we have sufficient lives to continue the game
        if (this.numLives <= 0 || this.gameOver) {
            background(0, 0, 0);
            image(this.gameover, 640 - 101, 320 - 175 + 80);
            return;

        }

        background(150, 75, 0); 
        
        // Draws the level which includes
        // concrete, dirt, grass, coloured tile
        // blocks, enemies, and powerups.
        currLevel.draw();

        // Updates the position of the ball
        this.ball.updater(currLevel, this);
        // Draws the ball
        this.ball.draw(this);   

        // Draws the text at the top of the screen,
        // with current info.
        drawInfo();

        // Checks if redTilePropgration is underway
        // If tile collision func returns -1 red tile prop
        // has caught up to the ball. 
        if (this.collision) {
            if (currLevel.tileColision(null) == 1) {
                appBallDies();
            }
        }
    }


    /**
     * The collision flag is set to true when a worm hits a greentile.
     * resetTileCollision resets tile collision when the ball successfully makes it to a safe tile.
    */
    public void resetTileCollision() {
        this.collision = false;
        currLevel.resetTiling();
    }


    /**
     * The collision flag is set to true when a worm hits a greentile.
    */
    public void setCollision() {
        this.collision = true;
    }


    /**
     * Draws all the text at the top of the screen.
     * Number of lives, progress, goals, and level number.
    */
    public void drawInfo() {

        fill(255); // White



        // Original Code
        // Level info + drawn
        // String levelInfo = String.format("Level %d out of %d", levelCount, numLevels);
        // text(levelInfo, 900, 40);

        // This code block draws the current level indicators
        // Displays the current level, the two previous levels, and the next level.
        // If there are no more levels it displays the three previous levels
        // Displays at most 4 levels at one time (in case there are 20 Levels)
        
        {
            // Values for the level rectanhgles
            int length = 50;
            int x = 950;
            int y = 15;


            // Default value for i in the foor loop.
            int index = 1;

            // If there are more than 5 levels than find the index that we can represent the 4 values with
            if (numLevels >= 5) {

                // If theres are more than 4 levels left
                if(numLevels - levelCount > 3) {

                    // If we are not on the first two levels
                    if(levelCount > 2) {
                        index = levelCount - 2;
                        
                    // If we are on the first two levels start on Level 1
                    } else {
                        index = 1;
                    
                    }
                
                // if there are more than 3 levls and we are not one the first two levels AND there aare less than 4 levels left
                } else if (numLevels > 3 && levelCount > 2) {

                    // If we are on the last level then display the last 4
                    if (levelCount == numLevels) {
                        index = levelCount - 3;
                    
                    // If not display 2 levels weve completed, the level we are on, and the last level.
                    } else {
                        index = levelCount - 2;
                    
                    }
                }
                

            }
            
            // loops through 4 levels starting at the index specified and displays them
            for(int i = index, j = 0; i <= numLevels && j < 4; i++, j++) {


                // For the current level display a yellow box with the level number, and a red outline
                // Outlinse specfies current level.
                if(i == levelCount) {

                    // Boxes are 60 wide with a 15 pixel gap between them. Hence the 75.

                    fill(255, 255, 135);
                    stroke(217, 33, 33); // Changes the colour of outlines (or any lines)
                    strokeWeight(2); // Changes thickness of outlines (or anly lines)
                    rect(x + 75 * (j), y, length + 10, length);
                    
                    // Reset to default
                    strokeWeight(1);
                    stroke(0); // Black
                    
                // If weve completed the level then just display it in yellow
                } else if(i < levelCount) {
                    fill(255, 255, 135);
                    rect(x + 75 * (j), y, length + 10, length);


                // If we havent completed the level display it in a grayed out box
                } else {
                    fill(80, 45, 35);
                    rect(x + 75 * (j), y, length + 10, length);

                }

                // Text colour black
                fill(0);
                String levelInfo = String.format("LV.%d", i); // Level Displayed
                textFont(this.medFont); // Changes font to a smaller font than the main font

                // Dispays texts in the boxes. Slightly repositioned to be recentred to text.
                text(levelInfo, x + 75 * (j) + 7, y + 30);

                // resets font size
                textFont(this.font);
            } 
        }

        // Original Code
        // Live info + drawn
        // String liveInfo = String.format("%d Lives", numLives);

        // loops through the number of lives we have and prints hearts out.
        // Has a gap of 5 pixels
        for(int i = 0; i < this.numLives; i++) {
            image(this.bheart, 10 + 65 * i, 10);
        }


        // Original Code
        // Progress info + drawn
        // String progressInfo = String.format("Progress: %.1f  Goal: %.1f", 100 * currLevel.returnCompleted(), 100 * currLevel.returnGoal());
        // text(progressInfo, 300, 40);

        // Code displays the progress in a progress bar
        // Comprises of a small variable length rect inside a larger fixed length rect
        // A red line displays the goal requirement of the  level
        // length of smaller rect shows current progrss
        {

            // Values for the smaller rect
            int length = 440 - 40;
            int height = 30;
            int x = 380;
            int y = 25;

            // Dark gray rect for total pogress bar
            fill(65, 35, 15);
            rect(x - 10, y - 10, length + 40, height + 20);

            // Dark Ocean Blue prograss bar (kinda looks like a MP bar but I like it)
            // Length of the bar depends on the total amount of grass on the feild.
            fill(57, 20, 255);
            rect(x, y, (int) length *  currLevel.returnCompleted() + 20, height);

            // Red line for goal indication on prgoress bar
            stroke(217, 33, 33);
            strokeWeight(3);
            line(x + (int) length * currLevel.returnGoal() + 20, y + 1, x + (int) length * currLevel.returnGoal() + 20, y + height - 1);
            
            // Reset top default
            strokeWeight(1);
            stroke(0); // Black

            // Text displayed on progress bar
            String goalInfo = String.format("%d", (int) Math.ceil(100 * currLevel.returnGoal()));
            String progressInfo = String.format("%d", (int) Math.floor(100 * currLevel.returnCompleted()));
            
            // Change font to medium font
            textFont(this.medFont);
           
            // Black colour for progress text 
            fill(0);

            // Needed a bit more space the more digits there are
            // If triple digits
            if ((int) Math.round(100 * currLevel.returnCompleted()) == 100) {
                text(progressInfo, x + (int) length *  currLevel.returnCompleted() - 18, y + 1 + 20);

            // If double digits
            } else if ((int) Math.round(100 * currLevel.returnCompleted()) >= 10) {
                text(progressInfo, x + (int) length *  currLevel.returnCompleted() - 7, y + 1 + 20);
            
            // If progress is in single digits
            } else {
                text(progressInfo, x + (int) length *  currLevel.returnCompleted() + 4, y + 1 + 20);
            }
            

            // Red text for the goal info
            fill(217, 33, 33);
            text(goalInfo, x + (int) length * currLevel.returnGoal() + 30, y + 1 + 20);
            textFont(this.font);
        }

        // If we have completed the current level
        if(currLevel.returnCompleted() > 0.8) {
            delay(800); // Just a delay for smooth transition
            finishedLevel(); // Call finish level
        }
    }


    /**
     * Increments the number of lives.
     * Called by powerup only so far.
     * Cant get more lives if you have more than 5
    */
    public void incrementLives() {
        if(this.numLevels < 5){
            this.numLives++;
        }
    }


    /**
     * Following the ball dieing this is called.
     * Stops any red tile propgation if their was any,
     * resets the ball, decremnts lives, and resets any other
     * properties that were set while tilling.
    */

    public void appBallDies() {
        stopRedTilePropogation();

        // Just gives allow time for the user to get frustrated
        delay(800);

        // Reset the ball to 0,0
        this.ball.ballReset(this.currLevel);
        
        // Reset the tiling variable that represents placing of green tiles
        currLevel.resetTiling();

        // We have lost a life :(
        this.numLives --;
    }


    /**
     * Called if level is finished.
     * Loads in the next level if there are more levels left to play.
    */
    public void finishedLevel() {
        
        // Checks if we are at the final level
        if (this.numLevels <= this.levelCount) {
            this.gameWon = true;
            return;
        }

        // Increments the current level counter.
        levelCount++;
        
        // Loads in the next levelJSON and the next level.txt file name.
        String nextLevelName = levelsJSON.getJSONObject(levelCount - 1).getString("outlay");
        this.currLevelJSON = levelsJSON.getJSONObject(levelCount - 1);

        // Creates a new level object
        this.currLevel = new Level(nextLevelName, currLevelJSON, this.ball, this);
        
        // Sets a referecnce of the current level in ball.
        ball.setLevel(currLevel);

        // Resets the ball into its spawn postion 0, 0.
        ball.ballReset(currLevel);
    }


    public void BFSTraverseCheck() {
        currLevel.BFSTraverseCheck(0, 0);

        // Get the first node in an enclosure
        Node firstNode = currLevel.findFirstNode();

        // If the first node exits then fill the enclosure.
        if(firstNode == null) {
            System.out.println("Enemies Everywhere!");
            
        } else {
            currLevel.fillEnclosed(firstNode.returnXMin(), firstNode.returnYMin());

        }

        // Clear the BFS list
        currLevel.clearList();
    }


    /**
     * Called when a key is pressed.
     * A Continous press on the key calls the function multiple times. 
    */
    public void keyPressed() {

        // Left: 37
        // Up: 38
        // Right: 39
        // Down: 40
        

        // Many of these are for debugging. Maybe be also considered cheats.

        // Left arrow key
        if (this.keyCode == LEFT) {
            this.ball.setDir("LEFT");
            leftPressed = true;

        // Right arrow key
        } else if (this.keyCode == RIGHT) {
            this.ball.setDir("RIGHT");
            rightPressed = true;

        // Up arrow key
        } else if (this.keyCode == UP) {
            this.ball.setDir("UP");
            upPressed = true;

        // Down arrow key
        } else if (this.keyCode == DOWN) {
            this.ball.setDir("DOWN");
            downPressed = true;

        // ESCAPE key
        } else if (this.key == ESC || this.keyCode == ESC) {
            key = 0;
            this.escape = !this.escape;
        
        // Simulates a enemy collinding with first freen tile to be tiled.
        // Red tile propogation begins
        } else if (this.key == 'k' || this.keyCode == 75) {
            this.collision = true;
        
        // Simulates the completion of a level
        } else if (this.key == 'j' || this.keyCode == 74) {
            finishedLevel();
        
        // // Simulates a filling of an enclosure at the 20, 20 block
        // } else if (this.key == 'f' || this.keyCode == 70) {
        //     currLevel.fillEnclosed(20, 20);

        // // Depreciated 
        // // Simuate the seperation of the map into 1 or more enclosures drawn by the path
        // } else if (this.key == 'b' || this.keyCode == 66) {
            // currLevel.BFSTraverseCheck(0, 0);
            // currLevel.resetVisited(0, 0);
            // currLevel.printBFSList();
            // Node firstNode = currLevel.findFirstNode();

            // if(firstNode == null) {
            //     System.out.println("Enemies Everywhere!");
                
            // } else {
            //     currLevel.fillEnclosed(firstNode.returnXMin(), firstNode.returnYMin());

            // }
        
        // Just a deubg feauture prints out they key of the pressed button if is not any of the above
        } else {
            System.out.println("key code: " + this.keyCode);

        }
    }


    /**
     * Called if a key is released.
     * A Continous press on the key calls the function multiple times. 
    */
    public void keyReleased() {

        // Left: 37
        // Up: 38
        // Right: 39
        // Down: 40
        // numOfKeyPressed--;

        // Set the pressed boolean flags to false if relesead
        // These boolean flags help when multiple keys are being pressed but only one is released.

        if (this.keyCode == LEFT) {
            leftPressed = false;

        } else if (this.keyCode == RIGHT) {
            rightPressed = false;

        } else if (this.keyCode == UP) {
            upPressed = false;

        } else if (this.keyCode == DOWN) {
            downPressed = false;
        }

        // If no keys are being pressed and we moving to a concrete, then STOP.
        if(!leftPressed && !rightPressed && !upPressed && !downPressed && (ball.getNextTileType() == TileType.CONCERTE)) {
            this.ball.setDir("NONE");

        }

    }


    /**
     * Stops reg tile propogation.
     * Deactivates propogation flag in level aswell.
    */
    public void stopRedTilePropogation() {
        this.collision = false;
        currLevel.resetTiling();
    }

    
    // Entry point of the code.
    public static void main(String[] args) {
        PApplet.main("lawnlayer.App");
    }
}
