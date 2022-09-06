package lawnlayer;

import processing.core.PImage;
import processing.core.PApplet;
import java.lang.Math;

// Direction for hostile mobs
enum DirectionHostileMob{
    
    NONE, 
    NE, // North East
    NW, // North West
    SE, // South East
    SW; // South West

    // Reflect the direction of a Hostile Mob in the oppostite wall
    public static DirectionHostileMob opposite(DirectionHostileMob dir) {
        if(dir == NONE) {
            return null;
        
        } else if (dir == NE) {
            return SW;

        } else if (dir == SE) {
            return NW;

        } else if (dir == SW) {
            return NE;

        } else if (dir == NW) {
            return SE;

        } else {
            return NONE;

        }

    }


    // Reflect the direction of a Hostile Mob depending on the wall it collided with
    public static DirectionHostileMob reflect(DirectionHostileMob dir, boolean reflectOnX) {

        // if it collided on a horizontal wall
        if (reflectOnX) {
            if(dir == NONE) {
                return null;
            
            } else if (dir == NE) {
                return SE;

            } else if (dir == SE) {
                return NE;

            } else if (dir == SW) {
                return NW;

            } else if (dir == NW) {
                return SW;

            } else {
                return NONE;

            }

        // if it collided on a vertical wall
        } else {
            if(dir == NONE) {
                return null;
            
            } else if (dir == NE) {
                return NW;

            } else if (dir == SE) {
                return SW;

            } else if (dir == SW) {
                return SE;

            } else if (dir == NW) {
                return NE;

            } else {
                return NONE;

            }
        }
    }
}

public class HostileMob extends Mob {


    // Attributes a hostile mob has and its state during the game
    // Similar to thos of ball
    protected DirectionHostileMob direction;
    protected int directionCount;
    private Ball ball;
    private int counterMax;
    private int speed;
    private boolean resetSpeed; // For pwerups
    private boolean slowEnemy;

    /**
     * Constructor for hostile mob class calls Mobs constructor and then sets a few class atributes.
     * @param image the sprite of the HostileMob
     * @param x the initial x coordinate of the hostile mob
     * @param y the intial y coordinate of the hostile
     * @param random whether a hostile mob spwans at a random location
     * @param level the level associated with a hostile mob
     * @param ball the ball associated with a hostile mob
     * @param app the app assocaite with a hostile mob
     */
    public HostileMob(PImage image, int x, int y, boolean random, Level level, Ball ball, App app) {

        // Calls the mob class constructore
        super(image, x, y);

        // Sets attriobutes 
        this.mobsWorld = level;
        this.direction = DirectionHostileMob.NONE;
        this.directionCount = 0;
        this.ball = ball;
        this.app = app;
        this.speed = 2;
        this.counterMax = 10;
        this.resetSpeed = false;
        this.slowEnemy = false;;

        // If random flag then spawn in the enmies in random locations
        if (random) {
            randomSpawn(level);
        
        }
    }


    // If the pinapple powerup was consumed by the ball then slow then set the slow enemy flag as true.
    public void slowEnemy() {
        this.slowEnemy = true;
    }


    // If the pinapple powerup has completed its effect then set the reset speed flag as true.
    public void regularEnemy() {
        this.resetSpeed = true;
    }

    /**
     * Updates the location of the Hostil Mob. Quite similar to that Ball.update().
     * @param level currLevel the hostile mob is associated with
     * @param app the app that the hostile mob is associated with
     */
    public void updater(Level level, App app) {
        
        // If the mob is not heading in a direction or the direction was reset then set some direction
        if (this.direction == DirectionHostileMob.NONE) {
            setRandomDirection();
        }

        // If the hostile mob has move 10 times in the direction check for the next blocks
        if (this.directionCount == counterMax || this.directionCount == 0) {

            // reset the counter
            this.directionCount = 0;

            // Call bounce on the next block
            this.bounce(level.getNextNode(xPos, yPos, direction), xPos, yPos);

            // If we need to reset speed then rest the spped
            if(resetSpeed) {
                this.speed = 2;
                this.counterMax = 10;
                this.resetSpeed = false;
            }


            // If we need to slow the enmey slow the enemy
            if(this.slowEnemy) {
                this.speed = 1;
                this.counterMax = 20;
                this.slowEnemy = false;
            }
        }


        // Based on the direction the enmey is heading in, move the enmey
        if (this.direction == DirectionHostileMob.NE) {

            this.xPos += speed;
            this.yPos -= speed;
            directionCount++;

        } else if (this.direction == DirectionHostileMob.SE) {
            this.xPos += speed;
            this.yPos += speed;
            directionCount++;

        } else if (this.direction == DirectionHostileMob.SW) {
            this.xPos -= speed;
            this.yPos += speed;
            directionCount++;

        } else if (this.direction == DirectionHostileMob.NW) {
            this.xPos -= speed;
            this.yPos -= speed;
            directionCount++;

        }
    }


    // Sets the direction of a hostile mob
    public void setDirection(DirectionHostileMob direction) {
        this.direction = direction;
    }


    /**
     * Handles when a hostile needs a random spawn. Usually only the case when it is specified in config. 
     * @param level the current level a hostile mob is associate with
     */
    protected void randomSpawn(Level level) {

        // Picks a random tile to spwan in
        this.xPos = (int) Math.round(Math.random() * 63) * 20;
        this.yPos = (int) Math.round(Math.random() * 31) * 20;

        // While the tile is not dirt and free keep finding a new tile.
        while(level.getTileType(xPos, yPos) != TileType.DIRT || level.getOccupied(xPos, yPos) == true) {
            this.xPos = (int) Math.round(Math.random() * 63) * 20;
            this.yPos = (int) Math.round(Math.random() * 31) * 20;

        }

        // Set the current tile as occupied
        level.setOccupied(xPos, yPos);

    }

    
    /**
     * Sets the direction of a hostile mob in a random direction 
     */
    protected void setRandomDirection() {

        // Picks a number betwen 1 and 4
        int tempIndex = (int) Math.floor(Math.random() * 3.99 + 1);

        this.direction = DirectionHostileMob.values()[tempIndex];
        this.directionCount = 0;

    }


    // Calls the enum oppisite() funtion. Changes the direction of the hostile mob in the opposite direction
    public void opposite() {
        this.direction = DirectionHostileMob.opposite(this.direction);
    }


    /**
     * The bounce function for hostile mob. Bounces of safe tiles (walls and corners)
     * @param node the next node the hostile mob is heading towards
     * @param mobMinX the current x postion
     * @param mobMinY the current y postion
     */
    public void bounce(Node node, int mobMinX, int mobMinY) {

        // Gets the next nodes 
        Node[] nextNodes = this.mobsWorld.getNextNodes(mobMinX, mobMinY, this.direction);
        Node nextNodeOnX = nextNodes[0]; // The next node in the x direction
        Node nextNodeOnY = nextNodes[1]; // The next node in the y direction


        // If the next tile is a green tile start redtile propogation
        if(node.returnTileType() == TileType.GREENTILE) {
            this.app.setCollision();
            this.mobsWorld.tileColision(node);

        }

        // If either the next tile, the next tile in the x dir, or the next tile in the y dir is not a dirt then
        if(node.returnTileType() != TileType.DIRT || nextNodeOnX.returnTileType() != TileType.DIRT || nextNodeOnY.returnTileType() != TileType.DIRT) {
            
            // Bounce off concave corner
            if(nextNodeOnX.returnTileType() != TileType.DIRT && nextNodeOnY.returnTileType() != TileType.DIRT) {
                this.direction = DirectionHostileMob.opposite(this.direction);

            // Bounce off wall
            } else if(nextNodeOnX.returnTileType() != TileType.DIRT) {
                this.direction = DirectionHostileMob.reflect(this.direction, true);

            // Bounce off wall
            } else if(nextNodeOnY.returnTileType() != TileType.DIRT) {
                this.direction = DirectionHostileMob.reflect(this.direction, false);
            
            // Bounce off convex coner
            } else {
                this.direction = DirectionHostileMob.opposite(this.direction);

            }
        }
    }


    /**
     * Interaction between HostileMob with another HostileMob or Powerup
     * @param node the node the collision happened at
     * @param gameObj2 the game object the collison happened with
     * @param mobMinX the x coordinate of the hostile mob 
     * @param mobMinY the y coodinate of the hostile mob
     */
    public void collision(Node node, GameObject gameObj2, int mobMinX, int mobMinY) {

        // If the object the hostile mob collided with was the ball then kill it
        if(this.mobsWorld.getPlayer() == gameObj2) {
            this.mobsWorld.getApp().appBallDies();
        
        // Otherwise reflect the mob in the opposite direction
        } else {
            this.direction = DirectionHostileMob.opposite(this.direction);
        
        }
    }    

}