package lawnlayer;


// GameObject is for all object that move and interact.
public interface GameObject {
    
    /**
     * Interaction between node and non-node GameObject
     * @param node the node to bounce off
     * @param mobMinX the mobs x coordinate
     * @param mobMinY the mobs y coordinate
     */
    public void bounce(Node node, int mobMinX, int mobMinY);


    /**
     * Interaction between non-node gameObject with another non-node GameObject
     * Called by one GameObject on another 
     * @param node the node the collision happened at
     * @param gameObj2 the game object the collison happened with
     * @param mobMinX the x coordinate of the calling mob 
     * @param mobMinY the y coodinate of the calling mob
     */
    public void collision(Node node, GameObject gameObj2, int mobMinX, int mobMinY);

}