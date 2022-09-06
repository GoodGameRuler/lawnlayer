<h2>INFO1113 Assignment 1 Report</h2>

<h4>Description</h4>

There are three general three general groups of classes that work together to ensure that the game runs smoothly. 

Firstly in its own section is `App.java`. `App.java's` functionality greatly differs from the rest of the groups so its in its own section. App.java stores much of the resources required for the game. It also is the main class whose functions sketch the game into the screen, and whos functions take input from the user. That is why I have kept all information regarding resource file (e.g. pngs), and  information the config file in App. 

In the next group is `Level.java`, `Node.java`, `Tile.java`. These files store information about the map, information about where all the moving objects are, and what type of tile is where. Level is arguable the most important class. It stores all the nodes (blocks) present on the map, the ball, the powerups present, and all the enemies on the map. Node and Tile together make a one block on the map. A node is a means of accessing a tile. These two together store the type of block a location is, and the properties of each of these blocks. I used an `enum` to store the tileType which allowed me get creative with the `isSafe()` method. Which returns whether a tileTpye is safe or not.

Lastly `GameObject`, `Mob`, `HostileMob`, `Powerup`,` Ball`, `Worm`, and `Beetle` make up all the moving objects in the game. `GameObject` is an interface for moving/interactive objects. The interface has two methods: bounce and collisions. These two methods handle interactions between a `GameObject` and everything else present in the map, including other `GameObject`s. Mob is an abstract class that implements basic methods that an interactive object will need. Updater and Draw are the most notable one among them. Instead of drawing and updating moving objects in `App.draw()`, the code species a draw function for moving objects (instances of `Mob`), and they are called in `App.draw()`. `updater` works similarly, updating the location and or state of a moving object. I chose to have the top most moving object as an interface as I wanted to open `GameObject` up for other objects that may not be come under `Mob` . Unfortunately did not get to implement that but `GameObject` is still quite important as all objects on the map need to be able to interact, and  `GameObject` allows for just that.

`HostileMobs` and the `Ball` are sibling objects. Their implementations are quite similar but different enough to implement as different objects. For example both use `enum`s as a means of storing the direction the respective object is heading in however the `enum` instances and methods are quite different. `HostileMob` while not a abstract class, is meant to act similar to one. I am quite proud of how little code my `Worm.java` and `Beetle.java` have. This is because most enemies (was considering adding more) have very similar behaviours, and so I compiled the general behaviour of an enemy into `HostileMob`.

<h4>Extension and Other</h4>

Firstly, (not part of the extension) I would like to mention that I have left in debugging parts of the code in the case that make it easier for you to go through the program. I have commented them out however, as they are not part of the game functionality. 

- Pressing K - Initiates a red tile propagation from the first green tile.
- Pressing J - Completes the current level, and moves to the next.
- Pressing F - Don't recommend using. It starts the fill algo from the block (1, 1).
- Pressing B - Starts the BFSTraverseAlgorithm.



Now moving onto the extension. For the extension I have implemented some GUI features and powerups. 

- The number of lives are displayed as a number of hearts on the top left.
- The progress and goals are shown as text but they are also represented by a progress bar. With the goal marked on the progress bar. 
- The levels are shown on the top right with the information regarding which level the player is currently on, if there is one more left, and also a few already completed levels. The level info shows at most 4 levels. I have provided a slightly modified config file to show this clearly (`config_5.json`).
- ![image-20220503012748821](C:\Users\udits\AppData\Roaming\Typora\typora-user-images\image-20220503012748821.png)
- I have also implemented 4 powerups. All powerup information can be changed in the config file. Each powerup in the config file has to have specified its type, effect duration, and despawn time. Usually these will always be specified as less than 10, as we should only have one powerup on the map per second, however I wanted to be able to extend powerups to negative ones that stayed on the map. So a duration larger than 10 can be specified.  Powerups have to specified in the JSON file for them to spawn. Please have a look at `config.json`.
  - Mushroom - Speed up the player for a given duration
  - Heart - Gives the player an extra life
  - Pineapple - Slows down the enemy for a given duration (Yellow Effect shown on enemies)
  - Freeze/Snowflake - Freeze the enemy for a duration. Hitting a frozen enemy still kills the player (Blue Effect shown on enemies).
- Also while I did not fully get to implement this. Pressing `ESC` pauses the screen, greys out the screen, and renders the player unable to continue the game until he resumes the game by pressing resume. 
- ![image-20220503013557401](C:\Users\udits\AppData\Roaming\Typora\typora-user-images\image-20220503013557401.png)
