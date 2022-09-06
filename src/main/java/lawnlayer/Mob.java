package lawnlayer;

import processing.core.PImage;
import processing.core.PApplet;

// Mob object encompasses moving objects and powerups
public abstract class Mob implements GameObject{
    
    // The image a mob has
    protected PImage sprite;

    // Its coordinates and references to the app and world its asscoaited with
    protected int xPos;
    protected int yPos;
    protected Level mobsWorld;
    protected App app;

    /**
     * Constructor for the mob class
     * @param image image to be set for mob object
     * @param x the x coordinate to set the mob at
     * @param y the y coordinate to set the mob at
     */
    public Mob(PImage image, int x, int y) {
        this.sprite = image;
        this.xPos = x;
        this.yPos = y;

    }


    /**
     * Constructor for the mob class where coordinates are not specified
     * @param image image to be set for mob object
     */
    public Mob(PImage image) {
        this.sprite = image;
        this.xPos = 0;
        this.yPos = 0;

    }


    // Gets the sprite of the mob
    public void setSprite(PImage sprite) {
        this.sprite = sprite;
    }


    /**
     * Method to update coordinate of a mob. Called every frame and updates values of the mob depending on the mob. 
     * @param level the level a mob is ascoaited with
     * @param app the app a mob is associated with
     */
    public abstract void updater(Level level, App app);


    // Draw function takes a reference to app to draw itself into the sketch
    public void draw(PApplet app) {
        app.image(this.sprite, this.xPos, this.yPos + 80);
    }


    // returns x coordinate of mob
    public int getXPos() {
        return this.xPos;
    }


    // returns y coordinate of mob
    public int getYPos() {
        return this.yPos;
    }

    // returns sprite of a mob
    public PImage getSprite() {
        return this.sprite;
    }

    
    // NOTE: Mob is an abstract class, and does need to specify interface implementations.
    // Doing this as Not all mobs have same implementation types.
}

    