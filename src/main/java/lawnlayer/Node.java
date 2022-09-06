package lawnlayer;

public class Node{

    // Specific properties of each node, which differ by tiletpye
    private Tile tile;
    private TileType tileType;
    private int rangeXMin;
    private int rangeYMin;
    private boolean allowsHostileMob;
    private Node nextColouredTile;
    private boolean visited;

    // Only applies when block is dirt block
    // Used for spawning in enemies
    private boolean occupied;
    
    /**
     * Constructor for a node. Generates an object that has a tiletype, and x,y coordinates.
     * @param tileType Type of tile the node is. Tile and Node work together to form a block.
     * @param rangeX The top left x value of the node.
     * @param rangeY The top left y value of the node.
     * @param allowsHostileMob If it allows hostileMobs on the block.
     */
    public Node(String tileType, int rangeX, int rangeY, boolean allowsHostileMob) {
        this.tile = new Tile(tileType);
        this.tileType = this.tile.returnTileType();
        this.rangeXMin = rangeX;
        this.rangeYMin = rangeY;
        this.allowsHostileMob = allowsHostileMob;
        this.nextColouredTile = null;
        this.occupied = false;

    }


    // returns the tile tpye of the node
    public TileType returnTileType() {
        return this.tileType;
    }


    // returns the top left x coordinate of the node
    public int returnXMin() {
        return this.rangeXMin;
    }


    // returns the top left y coordinate of the node
    public int returnYMin() {
        return this.rangeYMin;
    }


    // gets the next coloured tile for the case of green/red tiles
    public Node getNextColouredTile() {
        return this.nextColouredTile;
    }


    // Sets the next couloured tile for the case for gree/red tiles
    public void setNextColouredTile(Node nextNode) {
        this.nextColouredTile = nextNode;
    }


    // Sets the tile type of the current node
    public void setTileType(TileType tileType) {
        this.tileType = tileType;
        tile.setTileType(tileType);   
    }


    /**
     * Gets the occupation of a node. Used for spawning in enmies.
     * @return a boolean 
     */
    public boolean getOccupied() {
        return this.occupied;

    }


    // Sets the occupation of a node/
    public void setOccupied() {
        this.occupied = true;

    }


    // unsets the occupation of a node
    public void unsetOccupied() {
        this.occupied = false;
    }


    // Marks a node as visited
    public void setVisited() {
        this.visited = true;
    }


    // Marks a node as not visited
    public void unsetVisited() {
        this.visited = false;
    }


    // returns a boolean whether a node has been visited
    public boolean isVisited() {
        return this.visited;
    }
}