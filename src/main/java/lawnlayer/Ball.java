package lawnlayer;

import processing.core.PImage;
import processing.core.PApplet;

// Enum for the ball class direction and movement
enum Direction{
    NONE,
    LEFT,
    RIGHT,
    UP,
    DOWN;
}


public final class Ball extends Mob {

    // Basic varible related to the state and postion of the ball
    protected Direction direction; // direction last pressed
    private int directionCount; // directionCount and directionLovk lock the direction in place until the ball fully moves to the next block
    private Direction directionLock; // direction the ball is heading in
    private Direction prevDirectionLock;
    private Node currNode;
    private Node prevNode;
    private Node nextNode;
    private boolean firstFlag; // used to set value the first time update is called.
    private boolean tiling;
    private int startingTileX; // Coordinates of the starting green tile.
    private int startingTileY;
    private int speed;
    private int counter;
    private boolean resetSpeed;

    /**
     * Constructor for the ball class. Calls the mob constructor and sets basic attributes.
     * @param image the sprite of the ball
     * @param x the initial x coordinate of the ball
     * @param y the inital y coordinate of the ball
     * @param level the level a ball is currently on
     * @param app the app the ball is associated with
     */
    public Ball(PImage image, int x, int y, Level level, App app) {
        super(image);
        this.directionCount = 0;
        this.direction = Direction.NONE;
        this.directionLock = Direction.NONE;
        this.firstFlag = true;
        this.tiling = false;
        this.startingTileX = -1;
        this.startingTileY = -1;
        this.mobsWorld = level;
        this.app = app;
        this.speed = 2;
        this.counter = 10;
        this.resetSpeed = false; // Used for the mushroom powerup

    }


    // Sets a new level for the ball
    public void setLevel(Level level) {
        this.mobsWorld = level;
    }


    // Increments speed of the ball for the mushrrom powerup
    public void incrementSpeed() {
        this.speed = 4;
        this.counter = 5;
    }


    // Sets the flag to decrement the speed of the ball after mushrrom powerup not ineffect.
    public void decrementSpeed() {
        this.resetSpeed = true;
    }


    // resets the speed of the ball after mushrrom powerup not ineffect.
    public void resetSpeed() {
        this.speed = 2;
        this.counter = 10;
        this.resetSpeed = false; 
    }


    // Updates the ball poisiton
    public void updater(Level level, App app) {

        // If this is the first tim calling updater set curr, next, and previous ndoes.
        if (this.firstFlag) {
            this.firstFlag = false;
            this.currNode = level.getNode(this.xPos, this.yPos);
            this.prevNode = this.currNode;
            this.nextNode = this.currNode;            
        }


        // If we have been tilling and the next tile is safe then 
        if(getNextTileType().isSafe() && tiling == true) {
            tiling = false;

            // Reset redtile prop if any
            app.resetTileCollision();

            // Check for enclosure
            level.BFSTraverseCheck(0, 0);
            // app.thread("BFSTraverseCheck"); // THREADING SOL to solve lag

            // Reset cisitations
            level.resetVisited(0, 0);


            /////// COMMENT OUT FOR THREADING /////// START

            // Get the first node in an enclosure
            Node firstNode = level.findFirstNode();

            // If the first node exits then fill the enclosure.
            if(firstNode == null) {
                System.out.println("Enemies Everywhere!");
                
            } else {
                level.fillEnclosed(firstNode.returnXMin(), firstNode.returnYMin());

            }

            // Clear the BFS list
            level.clearList();

            /////// COMMENT OUT FOR THREADING /////// END

            // Set the direction to none
            this.direction = Direction.NONE;


            // dispose();
            // Fill in the path with grass
            grassThePath(level);

        }

        // If the ball is not moving in any direction
        if (directionLock == Direction.NONE) {


            // Prevents the ball from moving backward while tiling
            if (((prevDirectionLock == Direction.LEFT && direction == Direction.RIGHT) || 
                (prevDirectionLock == Direction.RIGHT && direction == Direction.LEFT) || 
                (prevDirectionLock == Direction.UP && direction == Direction.DOWN) || 
                (prevDirectionLock == Direction.DOWN && direction == Direction.UP)) && 
                (currNode.returnTileType() == TileType.GREENTILE || 
                currNode.returnTileType() == TileType.DIRT || 
                currNode.returnTileType() == TileType.REDTILE)) 
            
            {
                // If it tries to move backwards continue moving forwards
                directionLock = prevDirectionLock;
            
            // Otherwise change direction
            } else {
                directionLock = direction;
                
            }


            // Reset speed called after ball moves fully onto a tile
            if(this.resetSpeed){
                resetSpeed();
            }


            // Check if we have collided with a mob (enemy or powerup or another ball??)
            collisionDetection(this.mobsWorld.getNode(xPos, yPos));

            // If we are tiling and we arent hiting our own trail then set a tile
            if (tiling && currNode.returnTileType() != TileType.GREENTILE && currNode.returnTileType() != TileType.REDTILE) {
                currNode = level.setTile(xPos, yPos);
                prevNode.setNextColouredTile(currNode);

            }
        }

        // Checks which direction we are heading in
        // Moves in the direction of directionLock which resets to Direction.NONE after this.counter times;
        // If we are on the edge of the map doesnt allow it to move.
        // Gets the next node a block is heading and chaanges current and previous nodes

        if (direction == Direction.LEFT || directionLock == Direction.LEFT) {

            if(this.xPos == 0) {
                direction = Direction.NONE;

                if (directionLock == Direction.LEFT) {
                    directionCount = 0;
                    directionLock = Direction.NONE;
                    prevDirectionLock = Direction.NONE;
                }
                
                return;
            }

            this.nextNode = level.getNextNode(this.currNode.returnXMin(), this.currNode.returnYMin(), directionLock, this.nextNode);

            if (directionLock == Direction.LEFT) {
                
                xPos -= this.speed;
                directionCount++;

                // resets the direction lock;
                if(directionCount == this.counter) {
                    this.prevNode = this.currNode;
                    this.currNode = this.nextNode;
                    
                    directionCount = 0;
                    prevDirectionLock = directionLock;
                    directionLock = Direction.NONE;
                }
            }
        }

        if (direction == Direction.RIGHT || directionLock == Direction.RIGHT) {

            if(this.xPos == 1260) {
                direction = Direction.NONE;

                if (directionLock == Direction.RIGHT) {
                    directionCount = 0;
                    directionLock = Direction.NONE;
                    prevDirectionLock = Direction.NONE;
                }

                return;
            }

            this.nextNode = level.getNextNode(this.currNode.returnXMin(), this.currNode.returnYMin(), directionLock, this.nextNode);
    
            if (directionLock == Direction.RIGHT) {

                xPos += this.speed;
                directionCount++;
                
                // resets the direction lock;
                if(directionCount == this.counter) {
                    this.prevNode = this.currNode;
                    this.currNode = this.nextNode;

                    directionCount = 0;
                    prevDirectionLock = directionLock;
                    directionLock = Direction.NONE;
                }
            }
        }

        if (direction == Direction.UP || directionLock == Direction.UP) {

            if(this.yPos == 0) {
                direction = Direction.NONE;

                if (directionLock == Direction.UP) {
                    directionCount = 0;
                    directionLock = Direction.NONE;
                    prevDirectionLock = Direction.NONE;
                }
                
                return;
            }

            this.nextNode = level.getNextNode(this.currNode.returnXMin(), this.currNode.returnYMin(), directionLock, this.nextNode);

            if (directionLock == Direction.UP) {

                yPos -= this.speed;
                directionCount++;

                // resets the direction lock;
                if(directionCount == this.counter) {
                    this.prevNode = this.currNode;
                    this.currNode = this.nextNode;

                    directionCount = 0;
                    prevDirectionLock = directionLock;
                    directionLock = Direction.NONE;
                }
            }

        }

        if (direction == Direction.DOWN || directionLock == Direction.DOWN) {

            if(this.yPos == 620) {
                direction = Direction.NONE;

                if (directionLock == Direction.DOWN) {
                    directionCount = 0;
                    directionLock = Direction.NONE;
                    prevDirectionLock = Direction.NONE;
                }
                
                return;
            }

            this.nextNode = level.getNextNode(this.currNode.returnXMin(), this.currNode.returnYMin(), directionLock, this.nextNode);

            if (directionLock == Direction.DOWN) {

                yPos += this.speed;
                directionCount++;

                // resets the direction lock;
                if(directionCount == this.counter) {
                    this.prevNode = this.currNode;
                    this.currNode = this.nextNode;
                    
                    directionCount = 0;
                    prevDirectionLock = directionLock;
                    directionLock = Direction.NONE;
                }
            }

        }


        // If the next tile is a concrete and we are not pressing down on any keys then stop the ball
        if(getNextTileType() == TileType.CONCERTE && getNodeTileType() != TileType.CONCERTE && !app.keyPressed) {
            direction = Direction.NONE;
        }

        // If we move from a safe tile to dirt and tiling is false then set tiling
        if(getNodeTileType() != TileType.DIRT && getNextTileType() == TileType.DIRT && tiling == false) {
            tiling = true;
            this.startingTileX = nextNode.returnXMin();
            this.startingTileY = nextNode.returnYMin();

        }

        // if the next tile is part of the trail then ball dies
        if(getNextTileType() == TileType.GREENTILE || getNextTileType() == TileType.REDTILE) {
            app.appBallDies();

        }
    }


    /**
     * Sets the direction of the ball. Usually called by keypress.
     * @param dir string represents a direction. 
     */
    public void setDir(String dir) {

        if (dir == "LEFT") {
            this.direction = Direction.LEFT;

        } else if (dir == "RIGHT") {
            this.direction = Direction.RIGHT;
        
        } else if (dir == "UP") {
            this.direction = Direction.UP;
        
        } else if (dir == "DOWN") {
            this.direction = Direction.DOWN;
        
        } else if (dir == "NONE") {
            this.direction = Direction.NONE;
        }
    }


    // gets the tile type of the current node the ball is at
    public TileType getNodeTileType() {
        return currNode.returnTileType();
    }


    // gets the tile type of the node the ball is heading to
    public TileType getNextTileType() {
        return nextNode.returnTileType();
    }


    // gets the starting green (or red tile) tile while tiling 
    public Node getStartingTile(Level level) {
        if (this.startingTileX == -1 && this.startingTileY == -1) {
            return null;
        }
        
        return level.getNode(startingTileX, startingTileY);
    }


    /**
     * resets the ball following a new level or ball death
     * @param level the currLevel the ball is on.
     */
    public void ballReset(Level level) {
        this.xPos = 0;
        this.yPos = 0;

        this.firstFlag = true;

        this.direction = Direction.NONE;
        this.directionLock = Direction.NONE;
        this.directionCount = 0;

        this.tiling = false;

        // remvoes any green/red tiles
        resetAllColouredTiles(level);
        
        this.startingTileX = -1;
        this.startingTileY = -1;
    }


    /**
     * loops through all the coloured tiles and converts them back into dirt
     * @param level the currlevel the ball is on.
     */
    public void resetAllColouredTiles(Level level) {

        Node tileNode = getStartingTile(level);
        
        // While the next node is not null keep untilling
        while(tileNode != null) {
            level.resetTile(tileNode.returnXMin(), tileNode.returnYMin());
            tileNode = tileNode.getNextColouredTile();         
            
        }
    }


    /**
     * Goes through all the coloured tiles and converts them into grass tiles.
     * @param level
     */
    public void grassThePath(Level level) {

        Node tileNode = getStartingTile(level);
        Node nextTileNode = null; // temp node to store the next node while replacing the grass block
        
        // Loops through the coloured tiles till ther is none left
        while(tileNode != null) {
            nextTileNode = tileNode.getNextColouredTile();   
            level.setGrass(tileNode.returnXMin(), tileNode.returnYMin());
            tileNode = nextTileNode;     
            
        }
    }

    // Not needed for ball, FOR NOW. Might implement bouncy thing.
    public void bounce(Node node, int mobMinX, int mobMinY) {
    }


    public void collision(Node node, GameObject gameObj2, int mobMinX, int mobMinY) {

        // When the ball collides with a powerup and there are no enemies nearby
        if (mobsWorld.hasPowerup(node) && !mobsWorld.hasEnemy(node)) {
            Powerup pu = (Powerup) gameObj2;
            pu.consume();
            return;
        }

        // If the ball collides with an enemy

        HostileMob hm = (HostileMob) gameObj2;
        
        // When a ball collides with a mob
        hm.opposite(); // change the direction of the mob to its opposite
        this.app.appBallDies(); // kill the ball

    }

    /**
     * Collistion detection checks every update() call if the ball has collided with anything
     * if it has then only call collision
     * @param node node the ball is currently at
     */
    public void collisionDetection(Node node) {

        // Collides with enemy
        if(mobsWorld.hasEnemy(node)) {
            HostileMob hm = mobsWorld.returnEnemy(node);
            collision(node, hm, hm.getXPos(), hm.getXPos());
        
        // Collides with powerup
        } else if(mobsWorld.hasPowerup(node)) {
            Powerup pu = mobsWorld.returnPowerup(node);
            collision(node, pu, pu.getXPos(), pu.getXPos());
        
        }
    }
}
