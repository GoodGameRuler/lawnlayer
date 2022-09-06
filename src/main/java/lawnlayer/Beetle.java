package lawnlayer;

import processing.core.PImage;
import processing.core.PApplet;
import java.lang.Math;

import lawnlayer.Node;

public class Beetle extends HostileMob {
    
    /**
     * The constructor for beetle. Just calls the Hostile Mob constructor.
     * @param image the sprite of the beetle
     * @param x the x coordinate of the ball
     * @param y the y coordinate of the ball
     * @param random boolena whether beetle is spawned randomly
     * @param level the level of currLevel the beetle is associated with.
     * @param ball the ball the beetle is associated with
     * @param app the app a ball is assocaited with
     */
    public Beetle(PImage image, int x, int y, boolean random, Level level, Ball ball, App app) {
        super(image, x, y, random, level, ball, app);
    }

    // Same function as in hostile mob. Calls the super as you can see. However it has one extra functionality which is eating grass.
    // The overriden funtion handle the eating of grass of a beetle.
    @Override
    public void bounce(Node node, int mobMinX, int mobMinY) {

        // Store the current next blocks as they will change after super.bounce is called (direction is changed so next block will change)
        Node[] nextNodes = this.mobsWorld.getNextNodes(mobMinX, mobMinY, this.direction);
        Node nextNodeOnX = nextNodes[0];
        Node nextNodeOnY = nextNodes[1];

        // Change the direction of the beetle
        super.bounce(node, mobMinX, mobMinY);

        // Now check for various differnt block scenarious and remvoe grass if the beetle touches a grass block.
        // If the beetle hits a concave corner I'd argue its touching two grass blocks, so I remove both.
        
        // If the beetle hits a convex corner it just heads backaward after eating the grass.

        // I have done the if statements like this because if the beetle bounces of a right grass wall lets say the block it removes its the next node
        // since its moving diagonally. The grass block it removes it the one on the right of it. Not the one its heading to.
        if(nextNodeOnX.returnTileType() == TileType.GRASS || nextNodeOnY.returnTileType() == TileType.GRASS) {

            // If one if then the beetle hit a gras wall
            // If both ifs then the beetle hit a grass concave corner.

            if(nextNodeOnX.returnTileType() == TileType.GRASS) {
                this.mobsWorld.resetTile(nextNodeOnX.returnXMin(), nextNodeOnX.returnYMin());
            }
            
            if(nextNodeOnY.returnTileType() == TileType.GRASS) {
                this.mobsWorld.resetTile(nextNodeOnY.returnXMin(), nextNodeOnY.returnYMin());
            }
        
        // Convex corner case
        } else if (nextNodeOnX.returnTileType() != TileType.GRASS && nextNodeOnY.returnTileType() != TileType.GRASS && node.returnTileType() == TileType.GRASS) {
            
            if(nextNodeOnX.returnTileType() == TileType.GRASS) {
                this.mobsWorld.resetTile(node.returnXMin(), node.returnYMin());
            }
        }    
    }
}