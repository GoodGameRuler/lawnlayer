package lawnlayer;

enum TileType{
    
    GRASS(true), 
    DIRT(false), 
    CONCERTE(true), 
    GREENTILE(true), 
    REDTILE(true), 
    ERROR(false), 
    NONE(false);


    private final boolean isSafe;

    // Constructor for tiletype
    private TileType(boolean isSafe) {
        this.isSafe = isSafe;
    }


    // returns if the tiletype is safe
    public boolean isSafe() {
        return this.isSafe;
    
    }
}

public class Tile {
    private TileType tileType;

    /**
     * Cosntructs a tile type to set in node. Changing and making tiles types separate from node made thing a bit easier. 
     * @param tileType the tiletype to be set
     */
    public Tile(String tileType) {

        if (tileType == "G") {
            this.tileType = TileType.GRASS;
        
        } else if (tileType == "D") {
            this.tileType = TileType.DIRT;
        
        } else if (tileType == "C") {
            this.tileType = TileType.CONCERTE;
        
        } else if (tileType == "GT") {
            this.tileType = TileType.GREENTILE;
            
        } else if (tileType == "RT") {
            this.tileType = TileType.REDTILE;
            
        } else if (tileType == "e") {
            this.tileType = TileType.ERROR;
        
        } else {
            this.tileType = TileType.NONE;
        
        }

    }


    // Sets the tiletype of the current tile
    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }


    // returns the tile type of the current tile
    public TileType returnTileType() {
        return this.tileType;
    }


}