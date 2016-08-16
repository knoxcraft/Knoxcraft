package org.knoxcraft.serverturtle;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.knoxcraft.turtle3d.KCTBlockTypes;
import org.knoxcraft.turtle3d.KCTBlockTypesBuilder;
import org.knoxcraft.turtle3d.KCTCommand;
import org.knoxcraft.turtle3d.KCTScript;
import org.knoxcraft.turtle3d.KCTWorldBlockInfo;
import org.knoxcraft.turtle3d.TurtleCommandException;
import org.knoxcraft.turtle3d.TurtleDirection;
import org.knoxcraft.turtle3d.WorkChunk;
import org.knoxcraft.turtle3d.Workload;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

/**
 * SpongeTurtle is responsible for taking a turtle script in JSON format and converting it into a
 * chunk of work that can be run by the WorkThread. 
 * @author kakoijohn
 *
 */
public class SpongeTurtle {

    private Logger log;

    private Vector3i startLoc;
    private Vector3i curLoc;
    // player location:originPos
    private Player player;
    private String senderName;
    private TurtleDirection dir;
    private World world;
    private int workChunkSize;
    private int jobNum;
    
    private BlockState block = KCTBlockTypesBuilder.getBlockState(KCTBlockTypes.STONE);
    private boolean blockPlaceMode = true;
    
    private KCTScript script;
    
    private WorkChunkManager workChunkManager;
    
    /**
     * Private inner class that manages the creation of a Workload object in the correct format that the
     * WorkThread can use to run its script.
     * @author kakoijohn
     *
     */
    private class WorkChunkManager {
        private int chunkNum;
        private Queue<KCTWorldBlockInfo> blockChunk;
        private Workload workload;
        
        /**
         * Constructor
         */
        public WorkChunkManager() {
            chunkNum = 0;
            blockChunk = new LinkedList<KCTWorldBlockInfo>();
            workload = new Workload();
        }
        
        /**
         * Adds a block to the block queue. Once the size of the blockChunk reaches a specified limit,
         * the work is pushed onto the workload queue and the blockChunk is cleared.
         * @param block
         */
        public void add(KCTWorldBlockInfo block) {
            if (blockChunk.size() > workChunkSize) {
                workload.add(new WorkChunk(new LinkedList<KCTWorldBlockInfo>(blockChunk), script.getScriptName(), player, jobNum, chunkNum, workChunkSize));
//                log.info("Adding to queue: " + blockChunk.peek().getLoc());
                blockChunk.clear();
                chunkNum++;
            }
            blockChunk.add(block);
        }
        
        /**
         * Adds the rest of the blockChunk queue to the Workload regardless of the size of the blockChunk queue.
         */
        public void addRest() {
            workload.add(new WorkChunk(new LinkedList<KCTWorldBlockInfo>(blockChunk), script.getScriptName(), player, jobNum, chunkNum, workChunkSize));
            blockChunk.clear();
            chunkNum++;
        }
        
        /**
         * Returns the workload that this class has created.
         * @return
         */
        public Workload getWorkload() {
            return this.workload;
        }
        
        /**
         * return the size of the workload queue
         * @return
         */
        public int getJobSize() {
            return (workload.remainingWorkSize() - 1) * workChunkSize + workload.peekLast().getQueueSize();
        }
    }
    
    /**
     * Constructor. Must also set some parameters before calling execute script.
     * This includes:
     *   - setLoc(location)
     *   - setTurtleDirection(direction)
     *   - setWorld(world)
     *   - setScript(script)
     *   - setSenderName(name)
     *   - setWorkChunkSie(size)
     *   - setJobNum(jobNum)
     * After all of these parameters are set, you can call executeScript()
     * @param logger
     */
    public SpongeTurtle(Logger logger) {
        this.log = logger;
    }

    /**
     * Sets the starting location for the turtle script to run.
     * Normally this defaults to the current position of the player when the invoke command is called.
     * @param startLocation
     */
    public void setLoc(Vector3i startLocation) {
        this.startLoc = startLocation;
        this.curLoc = startLocation;
    }

    /**
     * Sets the initial heading of the turtle. Directions are all of the cardinal directions
     * plus the intermediate directions. ie: North, East, South, West, NorthEast, SouthEast, SouthWest, NorthWest.
     * @param d
     */
    public void setTurtleDirection(TurtleDirection d) {
        this.dir = d;
    }

    /**
     * Sets the world that the turtle will be producing the work for.
     * @param w
     */
    public void setWorld(World w) {
        this.world = w;
    }

    /**
     * Sets the user script that the turtle will be running.
     * @param script
     */
    public void setScript(KCTScript script) {
        this.script = script;
    }

    /**
     * Sets the name of the player that invoked the script.
     * @param name
     */
    public void setSenderName(String name) {
        this.senderName = name;
    }
    
    /**
     * Sets the size of the build chunk queue that is built at one time by the WorkThread
     * @param size
     */
    public void setWorkChunkSize(int size) {
        this.workChunkSize = size;
    }
    
    /**
     * Sets the unique job number for the latest build.
     * @param jobNum
     */
    public void setJobNum(int jobNum) {
        this.jobNum = jobNum;
    }

    /**
     * Gets the name of the player who created the turtle.
     * @return
     */
    public String getSenderName() {
        return this.senderName;
    }

    private static int toInt(Object o) {
        if (o instanceof Long) {
            return (int) ((Long) o).longValue();
        } else {
            return (Integer) o;
        }
    }

    private void turn(boolean left, int degrees) {
        // get current TurtleDirection
        double d = degrees / 360.0 * 8;
        int x = (int) Math.round(d);
        int currentDir = dir.getIntValue();

        if (left) {
            int newDir = currentDir - x;
            while (newDir < 0) {
                newDir += 8;
            }
            dir = TurtleDirection.valueOf(newDir);
        } else {
            int newdir = currentDir + x;
            newdir = newdir % 8;
            dir = TurtleDirection.valueOf(newdir);
        }
    }

    private void move(int distance, TurtleDirection turtleDirection) {
        for (int i = 1; i <= distance; i++) {
            curLoc = curLoc.add(turtleDirection.direction);
            try {
                if (blockPlaceMode)
                    workChunkManager.add(new KCTWorldBlockInfo(curLoc, block, world.getBlock(curLoc)));
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
            
        }
    }
    
    private void executeCommand(KCTCommand c) throws TurtleCommandException {
        // get command info
        Map<String, Object> m = c.getArguments();
        String commandName = c.getCommandName();
        // execute command
        if (commandName.equals(KCTCommand.FORWARD)) {
            //          log.info("is m null? " + (m == null));
            //          log.info("m argument =" + m);
            // go forward
            int distance;
            if (!m.containsKey(KCTCommand.DIST)) {
                distance = 1; // default
            } else {
                distance = toInt(m.get(KCTCommand.DIST));
            }
            move(distance, dir);
        } else if (commandName.equals(KCTCommand.BACKWARD)) {
            // go backward
            int distance;
            if (!m.containsKey(KCTCommand.DIST)) {
                distance = 1;
            } else {
                distance = toInt(m.get(KCTCommand.DIST));
            }
            move(distance, dir.flip());
        } else if (commandName.equals(KCTCommand.TURNRIGHT)) {
            int degrees;
            if (!m.containsKey(KCTCommand.DEGREES)) {
                degrees = 90;
            } else {
                degrees = toInt(m.get(KCTCommand.DEGREES));
            }
            turn(false, degrees);
        } else if (commandName.equals(KCTCommand.TURNLEFT)) {
            int degrees;
            if (!m.containsKey(KCTCommand.DEGREES)) {
                degrees = 90;
            } else {
                degrees = toInt(m.get(KCTCommand.DEGREES));
            }
            turn(true, degrees);
        } else if (commandName.equals(KCTCommand.RIGHT)) {
            // strafe right
            int distance;
            if (!m.containsKey(KCTCommand.DIST)) {
                distance = 1;
            } else {
                distance = toInt(m.get(KCTCommand.DIST));
            }
            move(distance, dir.turn(false, 2));
        } else if (commandName.equals(KCTCommand.LEFT)) {
            // strafe left
            int distance;
            if (!m.containsKey(KCTCommand.DIST)) {
                distance = 1;
            } else {
                distance = toInt(m.get(KCTCommand.DIST));
            }
            move(distance, dir.turn(true, 2));
        } else if (commandName.equals(KCTCommand.UP)) {
            // go up
            int distance;
            if (!m.containsKey(KCTCommand.DIST)) {
                distance = 1;
            } else {
                distance = toInt(m.get(KCTCommand.DIST));
            }
            move(distance, TurtleDirection.UP);
        } else if (commandName.equals(KCTCommand.DOWN)) {
            // go down
            int distance;
            if (!m.containsKey(KCTCommand.DIST)) {
                distance = 1;
            } else {
                distance = toInt(m.get(KCTCommand.DIST));
            }
            move(distance, TurtleDirection.DOWN);
        } else if (commandName.equals(KCTCommand.SETPOSITION)) {
            //set a new position for the turtle.
            int posX = Integer.parseInt(m.get(KCTCommand.X).toString());
            int posY = Integer.parseInt(m.get(KCTCommand.Y).toString());
            int posZ = Integer.parseInt(m.get(KCTCommand.Z).toString());
            curLoc = startLoc.add(new Vector3i(posX, posY, posZ));
        } else if (commandName.equals(KCTCommand.SETBLOCK)) {
            String blockName = m.get(KCTCommand.BLOCKTYPE).toString();
            block = KCTBlockTypesBuilder.getBlockState(KCTBlockTypes.valueOf(blockName));
        } else if (commandName.equals(KCTCommand.PLACEBLOCKS)) {
            blockPlaceMode = Boolean.parseBoolean(m.get(KCTCommand.BLOCKPLACEMODE).toString());
        } else {
            log.warn("An unhandled command was passed.");
            log.warn("Unhandled Command: " + c.getCommandName());
        }
    }
    
    /**
     * Executes the KCTScript and produces a Workload that represents that job in a queue of work to be done.
     * This work is a queue of blocks to be placed in the world.
     * @param script A specified script to be run.
     * @return Workload queue
     */
    public Workload executeScript(KCTScript script) {
        workChunkManager = new WorkChunkManager();
        
        for (KCTCommand c : script.getCommands()) {
            try {
                executeCommand(c);
            } catch (TurtleCommandException e) {
                log.warn("Unable to execute Turtle script:" + script.getScriptName());
                return null;
            }
        }
        
        workChunkManager.addRest();
        
        return workChunkManager.getWorkload(); 
    }
    
    /**
     * Executes the KCTScript and produces a Workload that represents that job in a queue of work to be done.
     * This work is a queue of blocks to be placed in the world.
     * @return Workload queue
     */
    public Workload executeScript() {
        return executeScript(this.script);
    }
    
    /**
     * Gets the Workload created by executing the script. 
     * @return
     */
    public Workload getWorkload() {
        return workChunkManager.getWorkload();
    }
    
    /**
     * Get the number of blocks the script will create.
     * @return
     */
    public int getJobSize() {
        return workChunkManager.getJobSize();
    }

    public void setPlayer(Player player) {
        this.player=player;
    }
}
