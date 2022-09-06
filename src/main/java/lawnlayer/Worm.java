package lawnlayer;

import processing.core.PImage;
import processing.core.PApplet;
import java.lang.Math;

public class Worm extends HostileMob {
    
    /**
     * Worm class simply calls the constructor of Hostile Mob. Very simple hostile mob has no extra features.
     * @param image the sprite of the worm
     * @param x the x coordinate of the ball
     * @param y the y coordinate of the ball
     * @param random boolena whether worm is spawned randomly
     * @param level the level of currLevel the worm is associated with.
     * @param ball the ball the worm is associated with
     * @param app the app a ball is assocaited with
     */
    public Worm(PImage image, int x, int y, boolean random, Level level, Ball ball, App app) {
        super(image, x, y, random, level, ball, app);
    }

}