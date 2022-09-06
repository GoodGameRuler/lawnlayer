package lawnlayer;

import processing.core.PImage;
import processing.core.PApplet;
import java.lang.Math;

public class Powerup extends Mob {

    // Basic attribures of a powerup
    private boolean completed;
    private int timer; // Counts down frames from the total duration top zero.
    private int type; // The type of the powerup
    private int duration; // The total duration the powerup effect is available for
    private int inEffectCounter; // Counts frames collowing consumption of a powerup
    private boolean consumed; // If it has been consumed
    
    /**
     * The constructor for a powerup. Calls the mob constructor and then sets a powerup specific attributes.
     * @param sprite the sprite of a powerup
     * @param level the level a powerup is in
     * @param despawnTimer the total time the pwerup is displayed on the map
     * @param type the type of the powerup
     * @param duration the duration of the powerup
     */
    public Powerup(PImage sprite, Level level, int despawnTimer, int type, int duration) {
        super(sprite, 1260, 620 + 80);

        this.mobsWorld = level;
        this.timer = despawnTimer * 60;
        this.duration = duration * 60;
        this.inEffectCounter = 0;
        this.completed = false;
        this.consumed = false;
        this.type = type;

        // Picks a random location to spawn the powerup
        randomSpawn();
    }


    /**
     * Upater function checks if the powerup has been consumed, how long the left for the powerup, 
     * and how long the powerup has for effects.
     * Unlike the other updaters does not update the location of the mob.
     * @param level the level a powerup is in
     * @param app the app a powerup is associated with
     */
    public void updater(Level level, App app) {

        // If the powerup has been consumred 
        if (consumed) {


            // Shows how much of the powerup is remaining time of the powerup effect is remaining
            {
                app.fill(0); //Black
                app.arc(1240, 680, 30, 30, 0, app.PI+app.PI);
                app.fill(255); //white
                float angle = app.radians(360 * (duration - inEffectCounter)/duration) - app.HALF_PI;
                app.arc(1240, 680, 30, 30, angle, app.PI+app.HALF_PI, app.PIE);
                
                //origninal text
                // app.text(String.format("%d", (int) Math.floor((duration - inEffectCounter) / 60)), 1240, 40); 
            }
            // If the powerup effect timer is up then despawn
            if(inEffectCounter >= duration) {
                despawn();
            }
 
            // increment the framcounter
            inEffectCounter++;
        }

        // If the powerup has not been consumed and the timer runs out then despawn it 
        else if(timer == 0) {
            despawn();
        }

        // Decrement the timer
        timer--;
        
    }

    /**
     * The effect function manages all the effects of all types of powerups.
     * Calls the effect on all effected mobs and GameObjects.
     */
    private void effect() {

        // If mushrrom
        if(type == 0) {
            mobsWorld.getPlayer().incrementSpeed();

        // If heart
        } else if(type == 1) {
            mobsWorld.getApp().incrementLives();
        
        // If pinapple
        } else if(type == 2) {
            mobsWorld.slowEnemies();
        
        // If snowflake
        } else if(type == 3) {
            mobsWorld.freezeEnemies();

        }
    } 


    /**
     * The unEffect function manages all removing effects of all types of powerups.
     * Removes the effect of a powerup, when required, on all effected mobs and GameObjects.
     */
    public void unEffect() {

        // If mushroom
        if(type == 0) {
            mobsWorld.getPlayer().decrementSpeed();

        // If heart
        } else if(type == 1) {
            // Nothing should be done         
        
        // If pinapple
        } else if(type == 2) {
            mobsWorld.regularEnemies();    
        
        // If snowflake
        } else if(type == 3) {
            mobsWorld.unFreezeEnemies();

        }        
    }


    // consumes a powerup and activates its effect
    public void consume() {
        this.consumed = true;
        this.effect();
    }

    // Not needed quite yet
    public void bounce(Node node, int mobMinX, int mobMinY) { 

    }

    // Not needed quite yet
    public void collision(Node node, GameObject gameObj2, int mobMinX, int mobMinY) {
    
    }

    // Overides the draw method in mob. Only draws if not consumed and nott completed
    // @Override
    public void draw() {
        if(!completed && !consumed){
            super.draw(mobsWorld.getApp());
        } 
    }


    /**
     * Handles when a hostile needs a random spawn. Usually only the case when it is specified in config.
     */
    private void randomSpawn() {

        // Spawn in random node
        this.xPos = (int) Math.round(Math.random() * 63) * 20;
        this.yPos = (int) Math.round(Math.random() * 31) * 20;

        // loop untill the node, the poewrup spwans in, does not have an enemy or pwerup 
        while(mobsWorld.getTileType(xPos, yPos) != TileType.DIRT || mobsWorld.hasPowerup(mobsWorld.getNode(xPos, yPos)) || mobsWorld.hasEnemy(mobsWorld.getNode(xPos, yPos))) {
            this.xPos = (int) Math.round(Math.random() * 63) * 20;
            this.yPos = (int) Math.round(Math.random() * 31) * 20;

        }
    }


    /**
     * Handles despwaning a powerup.
     * Either when the timer runs out and it wasn't consumed, or
     * when it was consumed but the effect timer ran out.
     */
    public void despawn() {
        this.completed = true;
        this.mobsWorld.removePowerup(this); // Removes powerup from powerup list
        unEffect();

    }
}