package lawnlayer;

// Libraries needed for implementaion
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;
import java.util.Scanner;
import java.lang.Math;
import java.io.FileNotFoundException;
/////

// JSON and PApplet required by Assignment
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.data.JSONArray;
/////

import java.lang.annotation.*;

// For testing purposes
// @Documented
// @Retention(RUNTIME)
// @Target({TYPE, METHOD})
// public @interface Generated {
// }

// Level class stores nodes representing each tile, enemies,
// the ball, powerups.

public class Level {

    // Level Name, and details
    private String levelName;
    private Node[][] nodalMatrix; // Matrix layout of the entire level.

    // Used to keep track of frames so red tile propogrates every 3 frames.
    private int collisionCounter;

    // Variables that store information that dynamically changes based on the state of the level
    private Node currentTile;
    private HostileMob[] enemiesArray; // Store all enemies
    private LinkedList<Powerup> powerupsList; // Stores all active powerups
    private LinkedList<HashSet<Node>> BFSList; // Stores all enclosures 
    private Ball currPlayer; // Ball refernce
    private App currApp; // App reference

    // Keeps initial frame count.
    // Used to spawn in powerups every 10 seconds
    private int initialFrameCount;


    // Information extracted from the config file
    private JSONArray enemies;
    private JSONArray powerups;
    private boolean hasPowerups;
    private float goal;
    private int totalDirt; // Total Amount of dirt in the map. Does not change as grass is filled.
    private int totalGrass; // Current grass tally.
    private boolean slowEnemies;
    private boolean freezeEnemies;

    // Keeps a track of visited nodes while traversing the level.
    private ArrayList<Node> visistedList;

    /**
     * Creates the basic layout of the level's map.
     * Adds dirt to the nodal matrix.
    */
    private void createNodalGrid() {

        for(int i = 0; i < 32; i++) {
            for(int j = 0; j < 64; j++) {
                nodalMatrix[i][j] = new Node("D", j * 20, i * 20, true);
            }
        }

    }


    /**
     * Constructor for level
     * Creates the entire level from the config file and associated level.txt file
     * initaliases level attributes, and most importanly initialises the map 
     * @param levelName filename of the level.txt used to load in a level map
     * @param level the JSON object for the current level. Stores all level info like enemies and powerups.
     * @param ball the ball object a level is associated with
     * @param app the app object a level is associated with
     */
    public Level(String levelName, JSONObject level, Ball ball, App app) {
        
        // Declares the matrix
        this.nodalMatrix = new Node[32][64];

        // Fills in the nodal frid.
        createNodalGrid();

        // Declares various collection that were specified above
        this.visistedList = new ArrayList<Node>(10);
        this.BFSList = new LinkedList<HashSet<Node>>();
        this.powerupsList = new LinkedList<Powerup>();

        // Initialises total dirt to the map size.
        this.totalDirt = App.getWidth() * App.getHeight();
        
        // Information from JSON declared into variables
        this.levelName = level.getString("outlay");
        this.enemies = level.getJSONArray("enemies");
        this.powerups = level.getJSONArray("powerups");
        this.goal= level.getFloat("goal");
        this.enemiesArray = new HostileMob[enemies.size()];

        // Powerup states
        this.slowEnemies = false;
        this.freezeEnemies = false;

        // ERROR HANDELLING, bad goal
        if (0.0 >= goal || goal > 1.0) {
            System.out.println("ERROR: The level file " + levelName + ", is invalid. Must have a goal between 0 and 1 had goal of: " + goal);
            System.exit(1);
        }

        // The initial framecount
        this.initialFrameCount = app.frameCount();

        // If we dont have poweeups set the powerup flag to false.
        if(this.powerups == null) {
            hasPowerups = false;
        
        } else {
            hasPowerups = true;

        }

        // Sets references to related files.
        this.currPlayer = ball;
        this.currApp = app;

        // Array list for holding data while traversing throught the level.txt file.
        ArrayList<ArrayList<Character>> levelMatrix = new ArrayList<ArrayList<Character>>();


        // Handels if level file is missing
        // Loads level text file
        try {
            
            // Basic loading of file
            File f = new File(levelName);
            Scanner fileReader = new Scanner(f);

            // Reads through lines in level.txt
            while(fileReader.hasNextLine()) {

                // Reads one line
                String thisLine = fileReader.nextLine();
                
                // If empty then break
                if (thisLine == "") {
                    break;
                }

                // gets char array to traverse in for-each loop
                char[] tempArray = (thisLine.toCharArray());

                // One line in an array list of character. To be added to levelMatrix.
                ArrayList<Character> charList = new ArrayList<Character>();

                // Loops through the characters in the line
                for(int i = 0; i < tempArray.length; i++) {
                    charList.add(tempArray[i]);
                }   
                
                // Adds the arrayList representing the line into LevelMatrix.
                levelMatrix.add(charList);
            
         
            }
        
        // Handles error
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }

        // Loops through the levelMatrix, creates object for the respective tile,
        // and adds it to the nodalMatrix
        // nodalMatrix stores Nodes
        // evelMatrix stores chars
        for (int i = 0; i < 32; i++) {
            for(int j = 0; j < 64; j++) {

                // If theres an X make concrete, as dirt objects are already in level matrix.
                if (levelMatrix.get(i).get(j) == 'X') {
                    this.nodalMatrix[i][j] = new Node("C", j * 20, i * 20, false);
                    this.totalDirt--;

                }
            }
        }

        System.out.println(this.totalDirt);
        
        // Calls func that initaliases enemies
        initialiseEnemies();
    }


    //Returns the ball a level is associated with.
    public Ball getPlayer() {
        return this.currPlayer;
    }

    
    //Returns the App a level is associated with.
    public App getApp() {
        return this.currApp;
    }
    

    /**
     * Draws all objects in the level (excluding ball).
     * Handles many of the powerup and enemey operarions aswell
    */
    public void draw() {

        if(((currApp.frameCount() - initialFrameCount) % 600 == 0) && hasPowerups) {
            addPowerup();
        }

        // Loops through nodalMatrix prints the object to screen
        for(int i = 0; i < 32; i++) {
            for(int j = 0; j < 64; j++) {

                // Draws each respective object
        
                if (nodalMatrix[i][j].returnTileType() == TileType.CONCERTE) {
                    currApp.image(currApp.getImages("c"), j * 20, i * 20 + 80);
                
                } else if (nodalMatrix[i][j].returnTileType() == TileType.GREENTILE) {
                    currApp.fill(0, 230, 0);
                    currApp.rect(j * 20 + 2, i * 20 + 80 + 2, 16, 16);

                } else if (nodalMatrix[i][j].returnTileType() == TileType.REDTILE) {
                    currApp.fill(136, 8, 8);
                    currApp.rect(j * 20 + 2, i * 20 + 80 + 2, 16, 16);

                } else if (nodalMatrix[i][j].returnTileType() == TileType.GRASS) {
                    currApp.image(currApp.getImages("g"), j * 20, i * 20 + 80);
                    

                } 
            }
        }

        // Draws all enemies extracted from the config file.
        drawEnemies();

        // Draws powerups if there are any and they were specified in the config file
        if(hasPowerups) {
            drawPowerups();
        }

    }


    /**
     * Loads in the enemies from the JSON file into HostileMob objects
    */
    public void initialiseEnemies() {

        // Loop throught the enmies JSONArray
        for(int i = 0; i < this.enemies.size(); i++) {

            // Get one enemy
            JSONObject currEnemy = this.enemies.getJSONObject(i);

            // Just default values. If all fails then they spawn in the top left corner
            int x = 0;
            int y = 0;

            // Get the type of the enemy
            int type = currEnemy.getInt("type");
            
            // Check if the spawn location of the HostileMob is set to renadom
            boolean random = currEnemy.getString("spawn").equals("random");

            // If the spawn loacation is not random. Spawn it in the specified location.
            if(!random) {
                String[] str = currEnemy.getString("spawn").split(",");

                // ERROR HANDELLING 
                if(str.length != 2) {
                    System.out.println("UNKOWN ERROR 004 - Invalid Length");
                    System.exit(4);
                }

                // Sets x and y as specified
                x = Integer.parseInt(str[0]) * 20;
                y = Integer.parseInt(str[1]) * 20;
            }


            // Depending on the type of the HostileMob make the object
            if (type == 0) { // Worm
                this.enemiesArray[i] = new Worm(currApp.getImages("w"), x, y, random, this, currPlayer, currApp);
            
            } else if (type == 1) { // Beetle
                this.enemiesArray[i] = new Beetle(currApp.getImages("b"), x, y, random, this, currPlayer, currApp);
            
            // ERROR HANDELLING
            } else {
                System.out.println("UNKOWN ERROR 005 - Invalid Enemy");
                System.exit(5);

            }            
        }
    }


    /**
     * The collision flag is set to true when a worm hits a greentile.
    */
    public void drawEnemies() {

        // If the enmies are frozen tint them ice blue before you draw them
        if(this.freezeEnemies) {
            currApp.tint(192, 246, 251);
        }

        // If the enmies are slowed tint them a vomit yellow before you draw them
        if(this.slowEnemies) {
            currApp.tint(249, 215, 39);
        }

        // Loop through enmies and draw them
        for(HostileMob hm : this.enemiesArray) {

            // If enmies are frozen don't update them
            if (!this.freezeEnemies) {
                hm.updater(this, currApp);
            }

            // Draws enemey
            hm.draw(currApp);
        }

        // Removes any tint if present
        currApp.noTint();
    }


    /**
     * Called if we have powerups specified in the config file.
     * Only called every 600 framse: roughly 10 seconds.
     * Adds a random powerup to the linkedList
    */
    public void addPowerup() {
        int type = (int) Math.floor(Math.random() * 3.99);

        // Mushroom -- Speeds the player up
        if (type == 0) {
            powerupsList.add(new Powerup(currApp.getImages("m"), this, powerups.getJSONObject(type).getInt("despawn"), type, powerups.getJSONObject(type).getInt("duration")));
        
        // Heart -- Provides player with an extra heart
        } else if (type == 1) {
            powerupsList.add(new Powerup(currApp.getImages("ph"), this, powerups.getJSONObject(type).getInt("despawn"), type, powerups.getJSONObject(type).getInt("duration")));

        // Pinapple -- slows enmeies down
        } else if (type == 2) {
            powerupsList.add(new Powerup(currApp.getImages("p"), this, powerups.getJSONObject(type).getInt("despawn"), type, powerups.getJSONObject(type).getInt("duration")));

        // Snowflake/Freeze -- Freezes the enmies in place
        } else if (type == 3) {
            powerupsList.add(new Powerup(currApp.getImages("f"), this, powerups.getJSONObject(type).getInt("despawn"), type, powerups.getJSONObject(type).getInt("duration")));

        }
    }

    /**
     * Draws in powerups stored in the linked list
     * Only called if powerups specified in the config file
    */
    public void drawPowerups() {

        // If we have no powerups yet. Nothing to draw.
        if(this.powerupsList.size() == 0) {
            return;
        }

        // If we do have powerups loop through the linked list and draw them all
        // There only is one powerup on the screen at a time
        // But say we wanted a powerup that took long to despawn or didnt (like a bad powerup - e.g. a bomb)
        // This would allow for it.
        for(Powerup pu : powerupsList) { 
            pu.updater(this, currApp);
            pu.draw();
        }
    } 

    // returns enemey list
    public HostileMob[] returnEnemies() {
        return this.enemiesArray;
    }

    // returns poweup lisr
    // public LinkedList<Powerup> returnPowerups() {
    //     return this.powerupsList;
    // }

    /**
     * Called when pinnaple powerup is consumed
     * Slows all enemeis in the current map
    */ 
    public void slowEnemies() {

        // If theres already a status effect then do nothing
        if(this.freezeEnemies || this.slowEnemies) {
            return;
        }

        // loop through each enemy and slow them
        for(HostileMob hm : enemiesArray) {
            hm.slowEnemy();
        }

        // Set the slow enemy flag
        this.slowEnemies = true;

    }


    /**
     * Called when pinnaple powerup is consumed following its effect completion
     * Reset all enmies back to regular speed
    */ 
    public void regularEnemies() {

        // Sets slow enmies flag to false
        this.slowEnemies = false;

        // Loops through each enemy and changes them back to regular speed
        for(HostileMob hm : enemiesArray) {
            hm.regularEnemy();
        }
    }


    /**
     * Called when snowflake powerup is consumed
     * Freezes all enemeis in the current map
    */ 
    public void freezeEnemies() {
        if(this.freezeEnemies || this.slowEnemies) {
            return;
        }
        this.freezeEnemies = true;

    }


    /**
     * Called when snowflake powerup is consumed following its effect completion
     * unfreezes all enemies
    */ 
    public void unFreezeEnemies() {
        this.freezeEnemies = false;

    }

    
    /**
     * Given a node in an enclosed region it fills in the entire region with grass.
     * @param x x coordinate of the current node that the fill algorithm is filling
     * @param y y coordinate of the current node that the fill algorithm is filling
    */ 
    public void fillEnclosed(int x, int y) {

        // Gets the current tile type
        TileType currentTileType = getTileType(x, y); 

        // If the tiletype is safe then return
        if (currentTileType.isSafe()) { // currentTileType == TileType.GREENTILE || currentTileType == TileType.REDTILE || currentTileType == TileType.CONCERTE OG CODE
            return;
        
        } 
        
        // Set the current tile as safe
        setGrass(x, y).returnTileType();

        // Traverse all nodes neighbouring

        // UP
        fillEnclosed(x, y - 20);

        // DOWN
        fillEnclosed(x, y + 20);

        //LEFT
        fillEnclosed(x - 20, y);

        //RIGHT
        fillEnclosed(x + 20, y);       

    }


    /**
     * Traverses the entire map to find the total number of enclosed regions.
     * The idea was inspired from the union-find data structure.
     * If we have not visited a node add it to a set then add the set to the BFSList.
     * If we move to a neighbour that has been visited but in a different set the current node,
     * combine the sets.
     * If we move to a neighbour that has been visited but in the same set we simply return.
     * The linked list BFSList keeps a track of all sets.
     * We only add nodes that are dirt (or other unsafe tiles which I have not implemented).
     * If the tile is safe then we return. Safeness is defined in Tile.java.
     * Note tile and node are the same thing. Node simply encapsulates tile, but can be though of as one object.
     * @param x x coordinate of the current node that the traverseCheck algorithm is currently visiting
     * @param y y coordinate of the current node that the traverseCheck algorithm is visiting
    */ 
    public void BFSTraverseCheck(int x, int y) {

        // Get the node for the current position we are at
        Node currNode = getNode(x, y);

        // If we have already visited the node
        if (currNode.isVisited()) {

            // If the current tile is safe return
            if(currNode.returnTileType().isSafe()) {
                return;
            }

            // The hashset below contains the sets that the neighbour nodes (that are dirt) are in. 
            // Nodes can be in atmost one set or they would have been combined.
            // Only adds the set a neighbour is in if it is not the same set as the one the currNode's in.
            // It technically adds all neighbours' sets, but since hashsets cant retain duplicates. It only stores
            // all the distinct neighbours' sets
            // Then combines all the different sets

            // Initialises the hash set
            HashSet<HashSet<Node>> multipleSets = new HashSet<HashSet<Node>>();

            // loops through all the current sets and then checks which one each neighbour is in
            for (HashSet<Node> set : this.BFSList) {
                
                // If we are not only leftmost point
                if(x != 0) {
                    if(set.contains(getNode(x - 20, y))) {
                        multipleSets.add(set);

                    }
                }

                // If we are not on the rightmost point
                if(x <= 1240) {
                    if (set.contains(getNode(x + 20, y))) {
                        multipleSets.add(set);

                    }
                }
                
                // If we are not on the topmost point
                if(y != 0) {
                    if(set.contains(getNode(x, y - 20))) {
                        multipleSets.add(set);

                    }
                } 

                // If we are not on the bottommost point
                if(y <= 600) {
                    if(set.contains(getNode(x, y + 20))) {
                        multipleSets.add(set);

                    }   
                }
            }


            // If there is more than one distinct set loop throuygh them and combine them
            if(multipleSets.size() > 1) {
                HashSet<Node> firstSet = null;
                int hashSetSize = 0;

                // Just for the purpose of making the algo more effecient ///////////// Start


                // Finds the largest set in multiSets
                // So the combine is on the largest set
                for(HashSet<Node> currSet : multipleSets) {
                    if(currSet.size() > hashSetSize) {
                        hashSetSize = currSet.size();
                        firstSet = currSet;
                    
                    } 
                }

                // Remove the  first set 
                multipleSets.remove(firstSet);

                ///////////// End

                // loops through the multi set adding all the elments to the first set and removing the set from the BFS list 
                for(HashSet<Node> setConflict : multipleSets) {

                    // If we haven't set the firstSet set it now
                    if(firstSet == null) {
                        firstSet = setConflict;
                    
                    // Otherwise start combining
                    } else {

                        // Add all the elments into the first set.
                        firstSet.addAll(setConflict);
                        
                        // Remove from the BFS list/
                        this.BFSList.remove(setConflict);

                    }                        
                }
            }

            return;
        }

        // If it hasn't been visited yet set the added flag to false
        boolean added = false;
        
        // If the current block is dirt (or in general not sage)
        if (!currNode.returnTileType().isSafe()) {

            // Loop throguh the sets and then check if any of the neighbours are in a set
            // Add the current node to the first neighbour's set it finds.
            for (HashSet<Node> set : this.BFSList) {
                
                if(x != 0) {

                    if(set.contains(getNode(x - 20, y)) && !added) {
                        set.add(currNode);
                        added = true;
                        break;

                    }
                }
                
                if(x <= 1240) {

                    if (set.contains(getNode(x + 20, y)) && !added) {
                        set.add(currNode);
                        added = true;
                        break;

                    }
                }
                
                 if(y != 0) {

                    if(set.contains(getNode(x, y - 20)) && !added) {
                        set.add(currNode);
                        added = true;
                        break;

                    }
                } 
                
                if(y <= 600) {

                    if(set.contains(getNode(x, y + 20)) && !added) {
                        set.add(currNode);
                        added = true;
                        break;

                    }
                }
            }

            // if it hasn't been added/ none of the neighbours were in sets yet...
            // create a new set and the currNode to it.
            // Then add the new list to the BFSList.
            if (!added) {
                HashSet<Node> set = new HashSet<Node>();
                set.add(currNode);
                this.BFSList.add(set);
                added = true;

            }
        }        

        // Set the currNode to visited
        currNode.setVisited();

        // Traverse to the neighbours

        // LEFT
        if (x != 0) {
            BFSTraverseCheck(x - 20, y);
        }

        // RIGHT
        if (x <= 1240) {
            BFSTraverseCheck(x + 20, y);
        }

        //UP
        if (y != 0) {
            BFSTraverseCheck(x, y - 20);
        }

        //DOWN
        if (y <= 600) {
            BFSTraverseCheck(x, y + 20);
        }
    }


    /**
     * For debugging. Prints the all the enclosed regions
     */
    // @Generated
    // public void printBFSList() {
        
    //     // loops through each node of each set in the BFS list print the tiletype
    //     for(HashSet<Node> set : this.BFSList) {
    //         for (Node node : set) {
    //             System.out.print(node.returnTileType() + " ");

    //         }

    //         // Seperators between enclosures/sets
    //         System.out.println(" = " + set.size());
    //         System.out.println(" --------------------------------------------------------------------------------------");
    //     }
    // }


    /**
     * Clears the BFS list after one BFSTraverseCheck
     */
    public void clearList() {
        this.BFSList.clear();
    }


    /**
     * Finds and returns the a node in the largest enclosure that has no enemies.
     * If all enclosures have enemies then it returns null.
     * @return returns a Node object
     */
    public Node findFirstNode() {
        
        // Initialises some variables
        HashSet<Node> largestSet = null;
        int hashSetSize = 0; // Keeps track of the largest current HashSet

        // Keep looping untill we have removed all elements from the BFSList
        while(this.BFSList.peekFirst() != null) {

            // Find the current largest HashSet
            for(int i = 0; i < this.BFSList.size(); i++) {
                if (this.BFSList.get(i).size() > hashSetSize) {
                    largestSet = this.BFSList.get(i);
                    hashSetSize = largestSet.size();

                }
            }

            // If the enclosure has no enmies and has atleast one dirt block
            if(!hasEnemy(largestSet) && largestSet.size() >= 1) {
                return largestSet.iterator().next(); // Return the first node in the set

            }

            // If the enclosure had enemies remvoe the enclosure
            this.BFSList.remove(largestSet);

            // Since we have remvoed the enclosure reset variables to find new larges enclosure
            largestSet = null; 
            hashSetSize = 0;

        }

        // If we have finished goin through all enclosures and non of them are appropriate to fill then return null
        return null;

    }


    /**
     * Loops through the enemies array and checks whether the input enclosure set has an enemy
     * Returns true if it finds at least one enemy who is in the enclosure. false othersise
     * @param set the set the has enemy func checks enemies for
     * @return returns (boolean) whether an enemey is present
     */
    public boolean hasEnemy(HashSet<Node> set) {

        // If the set we have been given is null
        if(set == null) {
            System.out.println("UNKNOWN ERROR 006: Invalid Set");
            System.exit(6);
        }

        // loops through enemies and check if they are in the current set
        for(HostileMob hm : enemiesArray) {
            if(set.contains(getNode(hm))) {
                return true;

            }
        }

        // If no enemies were found inside the enclosure return false
        return false;

    }


    /**
     * Checks if the current node has an enemey on it.
     * @param node the node we are checking enemies for
     * @return returns (boolean) whether an enemey is present
     */
    public boolean hasEnemy(Node node) {

        // loops through enemies and check if they are on the current node
        for(HostileMob hm : enemiesArray) {
            if(node.equals(getNode(hm.getXPos(), hm.getYPos()))) {
                return true;

            }
        }

        // If no enemies were found inside the enclosure return false
        return false;

    }


    /**
     * Returns the first enemy it finds on a particular node.
     * Usually called after hasEnemy(Node node);
     * Returns null if it finds no enemy.
     * @param node the node we are checking enemies for
     * @return returns an enemy if present, null if absent
     */
    public HostileMob returnEnemy(Node node) {

        // Loops through the enemies array and find the first enemy that is on the ndoe
        for(HostileMob hm : enemiesArray) {
            if(node.equals(getNode(hm.getXPos(), hm.getYPos()))) {
                return hm;

            }
        }

        // If it does not find an enemy then returns null
        return null;

    }


    /**
     * Checks if the current node has a powerup.
     * Returns a boolean.
     * @param node the node we are checking powerups for
     * @return returns (boolean) whether an pwerup is present
     */
    public boolean hasPowerup(Node node) {

        // If we have no powerups added yet then return fasle
        if (powerupsList.size() == 0) {
            return false;

        }

        // Loop throught the poweruplist and return true if we find a powerup
        for(Powerup pu : powerupsList) {
            if(node.equals(getNode(pu.getXPos(), pu.getYPos()))) {
                return true;
            }
        }

        // If no powerups were found then return
        return false;

    }


    /**
     * Removes a powerup currently active in the powerup list.
     * @param pu the powerup to be removed 
     */
    public void removePowerup(Powerup pu) {
        this.powerupsList.remove(pu);

    }


    /**
     * Returns a powerup if there is powerup on the node.
     * Returns null if it can't find any powerups.
     * @param node the node we are checking powerups for
     * @return returns a powerup if present, null if absent
     */
    public Powerup returnPowerup(Node node) {

        if (powerupsList.size() == 0) {
            return null;
        }

        for(Powerup pu : powerupsList) {
            if(node.equals(getNode(pu.getXPos(), pu.getYPos()))) {
                return pu;
            }
        }

        return null;
    }


    /**
     * @return returns the (float) ratio of how much of the map was completed
     */
    public float returnCompleted() {
        return (this.totalGrass / (float) this.totalDirt);

    }


    // Returns total amount of grass
    public int returnGrass() {
        return (this.totalGrass);

    }


    /**
     * @return returns the (float) goal requriement of the map. 
     */
    public float returnGoal() {
        return this.goal;

    }


    /**
     * Resets all the visited flags of nodes after a traversal.
     * @param x the x coordinate of current node that resetVisited is visiting to unset visited.
     * @param y the y coordinate of current node that resetVisited is visiting to unset visited.
     */
    public void resetVisited(int x, int y) {

        // Get the node from the x and y
        Node currNode = getNode(x, y);

        // If its been not been visited by the resetVisited func then unset visited
        if (currNode.isVisited()) {
            currNode.unsetVisited();
            
        // If we have already visited it then return
        } else {
            return;

        }

        // Starts at the top left of the map so it can visit all nodes if it simply goes down and right

        // DOWN
        if (x <= 1240) {
            resetVisited(x + 20, y);
        }

        // RIGHT
        if (y <= 600) {
            resetVisited(x, y + 20);
        }
    }


    /**
     * Returns the node at a current pixel coordinate.
     * Scaled up 80 to not account for the extra 80 pixels.
     * i.e. 0, 0 as input (x, y) actually represents 0, 80 in pixel coordinates.
     * @param x the x pixel coordinate of the node to be gotten
     * @param y the y pixel coordinate of the node to be gotten
     */
    public Node getNode(int x, int y) {

        // Initialise returnNode;
        Node returnNode = null;
        
        // Try finding the node on the nodalMatrix
        try {
            returnNode = nodalMatrix[y / 20][x / 20];
        
        // If its out of bounds exit
        } catch (java.lang.ArrayIndexOutOfBoundsException AIOBE) {
            AIOBE.printStackTrace();
            System.out.println("UNKNOWN ERROR 007 - Array out of Bounds");
            System.exit(7);

        }

        // Return a node if it finds one
        return returnNode;
    }

    /**
     * Get node function but gets a node that a mob object is at.
     * @param mob any moving game object (includes powerups though they dont move)
     * @return returns a mob object
     */
    public Node getNode(Mob mob) {

        // Gets the postion of the mob
        int x = mob.getXPos();
        int y = mob.getYPos();
        
        // Initialise returnNode
        Node returnNode = null;

        // Attempts to get node from the matrix
        try {
            returnNode = nodalMatrix[y / 20][x / 20];
        
        // If the pixel coordinates not in the range then 
        } catch (java.lang.ArrayIndexOutOfBoundsException AIOBE) {
            AIOBE.printStackTrace();
            System.out.println("UNKNOWN ERROR 007.1 - Array out of Bounds");
            System.exit(7);
        }

        // return a the node found
        return returnNode;
    }


    /**
     * Gets the next node a object is moving in based on the current x, y, and the Direction
     * @param x the x coordinate of the currNode an object is at
     * @param y the y coordinate of the currNode an object is at
     * @param direction the direction the object is heading in
     * @param currNextNode the current next node the object has 
     * @return returns a Node object
     */
    public Node getNextNode(int x, int y, Direction direction, Node currNextNode) {

        // Based on the direction return the next node
        if (direction == Direction.LEFT) {
            return nodalMatrix[y / 20][x / 20 - 1];

        } else if (direction == Direction.RIGHT) {
            return nodalMatrix[y / 20][x / 20 + 1];
            
        } else if (direction == Direction.UP) {
            return nodalMatrix[y / 20 - 1][x / 20];

            
        } else if (direction == Direction.DOWN) {
            return nodalMatrix[y / 20 + 1][x / 20];
            
        } else {
            return currNextNode;
        }
    }

    
    /**
     * Gets the next node a object is moving in based on the current x, y, and the Direction
     * For HostileMobs
     * Assumes that HostileMob is not paritally on a block, but on fully on one block.
     * @param x the x coordinate of the currNode an Hostile Mob object is at
     * @param y the y coordinate of the currNode an Hostile Mob object is at
     * @param direction the direction the object is heading in
     * @return returns a Node object
     */
    public Node getNextNode(int x, int y, DirectionHostileMob direction) {

        // Based on the hostile mob direction return the next node
        if (direction == DirectionHostileMob.NE) {
            x += 20;
            y -= 20;

        } else if (direction == DirectionHostileMob.SE) {
            x += 20;
            y += 20;

        } else if (direction == DirectionHostileMob.SW) {
            x -= 20;
            y += 20;

        } else if (direction == DirectionHostileMob.NW) {
            x -= 20;
            y -= 20;

        }

        return getNode(x, y);

    }


    /**
     * Gets the next nodes a object is moving in based on the current x, y, and the Direction
     * For hostile mobs
     * Assumes that HostileMob is not paritally on a block, but on fully on one block.
     * Gets the node the hostileMob is heading in the y direction
     * Gets the node the hostileMob is headin in the x direction
     * @param x the x coordinate of the currNode an Hostile Mob object is at
     * @param y the y coordinate of the currNode an Hostile Mob object is at
     * @param direction the direction the Hostile Mob object is heading in
     * @return returns a collection of Node objects of size 2
     */
    public Node[] getNextNodes(int x, int y, DirectionHostileMob direction) {

        // Initialises new x and new y
        int next_x = x;
        int next_y = y;

        // Changes the next x and y depending on the hostile mob direction
        if (direction == DirectionHostileMob.NE) {

            next_x = next_x + 20;
            next_y = next_y - 20;

        } else if (direction == DirectionHostileMob.SE) {
            next_x = next_x + 20;
            next_y = next_y + 20;

        } else if (direction == DirectionHostileMob.SW) {
            next_x = next_x - 20;
            next_y = next_y + 20;

        } else if (direction == DirectionHostileMob.NW) {
            next_x = next_x - 20;
            next_y = next_y - 20;

        } else {
            System.out.println("UNKNOWN ERROR 0008 - UNKNOW DIRECTION");
            System.exit(8);

        }

        // Creates a new array to store the next nodes in the y and x direction
        Node[] tempNodeArray = new Node[2];
        tempNodeArray[0] = getNode(x, next_y);
        tempNodeArray[1] = getNode(next_x, y);

        return tempNodeArray;
    }


    /**
     * Returns the tileType of a node given its pixel coordinates
     * @param x the x coordinate of the currNode an object is at
     * @param y the y coordinate of the currNode an object is at
     * @return returns a TileType object
     */
    public TileType getTileType(int x, int y) {
        Node tempNode = getNode(x, y);
        return tempNode.returnTileType(); // Calls the node tileType func 
        
    }


    /**
     * Sets the tileType of a node to greenTile given its pixel coordinates
     * @param x the x coordinate of a node to be set as a green tile
     * @param y the y coordinate of a node to be set as a green tile
     * @return returns the new Node object
     */
    public Node setTile(int x, int y) {
        nodalMatrix[y / 20][x / 20] = new Node("GT", x, y, false);
        return nodalMatrix[y / 20][x / 20];

    }
    
    /**
     * Sets the tileType of a node to grass given its pixel coordinates
     * @param x the x coordinate of a node to be set as grass
     * @param y the y coordinate of a node to be set as grass
     * @return returns the new Node object
     */
    public Node setGrass(int x, int y) {
        nodalMatrix[y / 20][x / 20] = new Node("G", x, y, false);
        this.totalGrass++; // Increases the grassCount.
        return nodalMatrix[y / 20][x / 20];

    }

    
    /**
     * Resets the tileType of a node to dirt given its pixel coordinates
     * @param x the x coordinate of a node to be set as dirt
     * @param y the y coordinate of a node to be set as dirt
     * @return returns the new Node object
     */
    public Node resetTile(int x, int y) {

        // If the old tile type was grass then decrement the gras counter
        // Mostly only used for the beetle
        if(nodalMatrix[y / 20][x / 20].returnTileType() == TileType.GRASS) {
            this.totalGrass--;
        }

        nodalMatrix[y / 20][x / 20] = new Node("D", x, y, true);
        return nodalMatrix[y / 20][x / 20];

    }


    /**
     * Tile collision is called when a HostileMob hits a green tile of the ball
     * Begins redTile propogation and it is called every frame to update the redTile propgration.
     * Adds a red tile every 3 frams = 20 Tile/S
     * @param currentTile the node that the tile collision is at right now.
     * @return a status integer. 0 = Ball OK, 1 = Ball Dead
     */
    public int tileColision(Node currentTile) {

        // If the current tille is not null then set the currentTile
        // This occurs when the first time we call tileCollision
        // If we have specified where to start from start there otherwise
        // we will start red tile propogation at the first green tile
        if (currentTile != null) {
            this.currentTile = currentTile;
            
        }
        
        // Keeps a track of the frames since frist imapct
        collisionCounter++;

        // Every three frames ...
        if (collisionCounter % 3 == 0) {

            // If the currentTile is null ie. we did not specify first tile. Set the first tile to the first green Tile
            if (this.currentTile == null) {
                this.currentTile = currPlayer.getStartingTile(this);

            }

            // Change the tile type to a redtile
            this.currentTile.setTileType(TileType.REDTILE);

            // If there is no next green tile we have caught up to the ball. returns 1 to kill ball
            if (this.currentTile.getNextColouredTile() == null) {
                return 1;

            // If there is a next green tile then change the current tile to the next green tile.
            } else {
                this.currentTile = this.currentTile.getNextColouredTile();
            
            }
        }

        // Return 0 to signal that the ball has not died quite yet
        return 0;
    }


    /**
     * Resets the tilling property after the ball dies or makes it a safe tile following
     * a tile collision call.
     */
    public void resetTiling() {
        this.currentTile = null;
    }

    
    /**
     * Checks if the current tile is occupied while spawning in enmies.
     * Prevents two enemies from spawning randomly on the same tile (however unlikely).
     * @param x the x coordinate of a node to be checked
     * @param y the y coordinate of a node to be checked
     * @return returns whether a node is occupied
     */
    public boolean getOccupied(int x, int y) {
        Node tempNode = getNode(x, y);
        return tempNode.getOccupied();

    }

    /**
     * Sets the occupation of a node by an enemy
     * @param x the x coordinate of a node to be set as occupied
     * @param y the y coordinate of a node to be set as occupied
     */
    public void setOccupied(int x, int y) {
        Node tempNode = getNode(x, y);
        tempNode.setOccupied();

    }

    /**
     * Unsets the occupation of a tile by an enemy 
     * @param x the x coordinate of a node to be set as unoccupied
     * @param y the y coordinate of a node to be set as unoccupied
     */
    public void unsetOccupied(int x, int y) {
        Node tempNode = getNode(x, y);
        tempNode.unsetOccupied();

    }
}