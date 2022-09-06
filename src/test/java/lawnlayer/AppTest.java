
package lawnlayer;

import processing.core.PApplet;
import org.junit.jupiter.api.*;
// import org.junit.jupiter.api.BeforeAll;


/* Hello Sam! Or whoever may end up marking my code.
 * I just want to mention here that after paricipating in this assingmnet I have realised that
 * perfectly encapsulated code, with good OOP structures makes testing absolutely horrible (not that I have perfect OOP).
 * All my objects are interconnected and interdependent.
 * If I want to test Ball I need a Level, Nodes, Tiles, Apps, you name it, ball probably needs it. 
 * The same can be said about all my code :(.
 * While designing I tried making the most secure and robust design I could think of and had time to implement.
 * Sorry for the rant ;-;. 
 * I have tried my best to test as much as possible to test as much code as remotely possible down below.
*/

import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;


public class AppTest extends App {

    // STATIC VARIABLE required by the game
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int SPRITESIZE = 20;
    private static final int TOPBAR = 80;
    private static final int LEVEL = 1;
    private static final int FPS = 100;
    //////


    private boolean testCondition;

    @BeforeEach
    public void setupApp() {
        testCondition = false;
        PApplet.runSketch(new String[] {"App"}, this);
        delay(1000);
        noLoop();
        this.configPath = "config_og.json";
        //write some tests
    }

    @AfterEach
    public void destroyApp() {
        dispose();

    }

    // @Test
    // public void missingConfigTest() {
    //     setupApp();
    //     configPath = "missingFile.JSON";
    //     setup();
    //     assertTrue(testCondition);
    //     testCondition = false;

    // }

    
    // Tests if the map loads
    @Test
    public void testMapLoads() {
        for(int i = 0; i < 2400; i++) {
            draw();
        }
        
    }


    // Tests if next level loads without error
    @Test
    public void testLevelLoads() {
        setup();
        this.keyCode = 74;
        keyPressed();
        for(int i = 0; i < 600; i++) {
            draw();
        }        
    }

    // Tests if next level loads without error, and then gameover
    @Test
    public void testLevelLoads2() {
        setup();
        this.keyCode = 74;
        keyPressed();
        for(int i = 0; i < 600; i++) {
            draw();
            keyPressed();
        }        
    }


    // Tests that the ball spawns in the right position
    @Test
    public void testInitialBallposition() {
        // setupApp();
        setup();
        assertEquals(0, getBall().getXPos());
        assertEquals(0, getBall().getYPos());

    }

    // Tests a basic key press
    @Test
    public void testKeyPressBasic() {
        // setupApp();
        setup();
        this.keyCode = RIGHT;
        keyPressed();
        draw();

        assertEquals(2, getBall().getXPos());
        assertEquals(0, getBall().getYPos());

    }

    // Tests a basic sawuence of key presses
    @Test
    public void testKeyPressBasic2() {
        // setupApp();
        setup();
        this.keyCode = RIGHT;
        keyPressed();

        for(int i = 0; i < 10; i++)
            draw();

        assertEquals(20, getBall().getXPos());
        assertEquals(0, getBall().getYPos());

    }

    // Tests a complext progression of key presses
    @Test
    public void testKeyPressComplex() {
        // setupApp();
        setup();
        this.keyCode = RIGHT;
        keyPressed();

        for(int i = 0; i < 30; i++)
            draw();

        this.keyCode = DOWN;
        keyPressed();

        for(int i = 0; i < 30; i++)
            draw();

        assertEquals(60, getBall().getXPos());
        assertEquals(60, getBall().getYPos());

    }

    // Test a very difficult progression of key presses 
    @Test
    public void testKeyPressComplex2() {
        // setupApp();
        setup();
        getLevel().freezeEnemies();        
        this.keyCode = RIGHT;
        keyPressed();

        for(int i = 0; i < 10; i++) {
            draw();
            this.keyCode = DOWN;
            keyPressed();
        }

        assertEquals(20, getBall().getXPos());
        assertEquals(0, getBall().getYPos());

    }


    // Tests if grass is filled with basic key progression
    @Test
    public void testLawnlayerBasic() {
        // setupApp();
        this.configPath = "src/test/java/lawnlayer/testConfig.json";
        setup();
        getLevel().freezeEnemies();   
        
        this.keyCode = RIGHT;
        keyPressed();


        for(int i = 0; i < 20; i++) {
            draw();
        }

        this.keyCode = DOWN;
            keyPressed();

        for(int i = 0; i < 20; i++) {
            draw();
        }

        this.keyCode = LEFT;
        keyPressed();

        for(int i = 0; i < 20; i++) {
            draw();
        }

        assertEquals(4, getLevel().returnGrass());

    }


    // Tests if grass is filled with basic key progression
    @Test
    public void testLawnlayerBasic2() {
        // setupApp();
        this.configPath = "src/test/java/lawnlayer/testConfig.json";
        setup();
        getLevel().freezeEnemies();   
        
        this.keyCode = RIGHT;
        keyPressed();


        for(int i = 0; i < 100; i++) {
            draw();
        }

        this.keyCode = DOWN;
            keyPressed();

        for(int i = 0; i < 100; i++) {
            draw();
        }

        this.keyCode = LEFT;
        keyPressed();

        for(int i = 0; i < 100; i++) {
            draw();
        }

        assertEquals(100, getLevel().returnGrass());

    }

    // Tests if grass is filled with complex key progression
    @Test
    public void testLawnlayerComplex1() {
        // setupApp();
        this.configPath = "src/test/java/lawnlayer/testConfig.json";
        setup();
        getLevel().freezeEnemies();   
        
        this.keyCode = DOWN;
        keyPressed();


        for(int i = 0; i < 100; i++) {
            draw();
        }

        this.keyCode = RIGHT;
            keyPressed();

        for(int i = 0; i < 20; i++) {
            draw();
        }

        this.keyCode = UP;
        keyPressed();

        for(int i = 0; i < 20; i++) {
            draw();
        }

        this.keyCode = RIGHT;
            keyPressed();

        for(int i = 0; i < 20; i++) {
            draw();
        }

        this.keyCode = UP;
        keyPressed();

        for(int i = 0; i < 20; i++) {
            draw();
        }

        this.keyCode = RIGHT;
            keyPressed();

        for(int i = 0; i < 40; i++) {
            draw();
        }

        this.keyCode = UP;
        keyPressed();

        for(int i = 0; i < 40; i++) {
            draw();
        }

        this.keyCode = RIGHT;
            keyPressed();

        for(int i = 0; i < 20; i++) {
            draw();
        }

        this.keyCode = UP;
        keyPressed();

        for(int i = 0; i < 20; i++) {
            draw();
        }


        assertEquals(64, getLevel().returnGrass());
    }


    @Test
    public void testsLoadsLongConfig() {
        this.configPath = "config_5.json";
        setup();
        draw();
        assertTrue(true);
    }

    @Test
    @RepeatedTest(10)
    public void testsLoadsPowerupConfig() {
        this.configPath = "config.json";
        setup();

        for(int i = 0; i < 100; i++)
            draw();
        
        assertTrue(true);
    }

    // Tests the pause menu
    @Test
    public void testPauseMenu() {
        setup();
        this.keyCode = ESC;
        keyPressed();
        for(int i = 0; i < 60; i++) {
            draw();
            this.keyCode = ESC;
            keyPressed();
        }

        assertEquals(0, getBall().getXPos());
        assertEquals(0, getBall().getYPos());
    }

    //// TESTING POWERUPS ////
    
    @Test
    public void testFreezeFunctionality(){
        setup();
        HostileMob[] hmArray = getLevel().returnEnemies();
        int[][] hmCheckArray = new int[getLevel().returnEnemies().length][2];
        
        for(int j = 0; j < hmCheckArray.length && j < hmArray.length; j++) {
            hmCheckArray[j][0] = hmArray[j].getXPos();
            hmCheckArray[j][1] = hmArray[j].getYPos();
        }

        getLevel().freezeEnemies();

        for(int i = 0; i < 600; i++) {
            for(int j = 0; j < hmCheckArray.length && j < hmArray.length; j++) {
                assertEquals(hmCheckArray[j][0], hmArray[j].getXPos());
                assertEquals(hmCheckArray[j][1], hmArray[j].getYPos());
                draw();
            }
        }
    }


    @Test
    public void testKill(){
        setup();
        
        this.keyCode = RIGHT;
        keyPressed();


        for(int i = 0; i < 100; i++) {
            draw();
            keyPressed();
        }

        this.keyCode = DOWN;
        keyPressed();

        for(int i = 0; i < 100; i++) {
            draw();
        }

        this.keyCode = 75;
        keyPressed();

        for(int i = 0; i < 100; i++) {
            draw();
        }


        assertEquals(0, getBall().getXPos());
        assertEquals(0, getBall().getYPos());


    }


    //// TESTING COLLISIONS ////

    @Override
    public void exit() {
        testCondition = true;
        super.exit();

    }
}

