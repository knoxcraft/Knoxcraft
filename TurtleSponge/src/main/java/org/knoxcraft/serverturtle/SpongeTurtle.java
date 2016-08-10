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
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

public class SpongeTurtle {

    private Logger log;

    // need to know initial location
    // vector 3i, or XYZ, get location from turtle plugin, put turtle one block
    // oin front of player.
    /*
     * EXAMPLE CODE Vector3i pos=loc.getBlockPosition(); int x=pos.getX(); int
     * z=pos.getZ();
     */
    // turtle location:relPos
    private Vector3i curLoc;
    // player location:originPos
    private String senderName;
    private TurtleDirection dir;
    private World world;
    private int workChunkSize;
    private int jobNum;
    private BlockState block = KCTBlockTypesBuilder.getBlockState(KCTBlockTypes.STONE);
    
    private KCTScript script;
    
    private WorkChunkManager workChunkManager;
    
    private class WorkChunkManager {
        private int chunkNum;
        private Queue<KCTWorldBlockInfo> blockChunk;
        private Workload workload;
        
        public WorkChunkManager() {
            chunkNum = 0;
            blockChunk = new LinkedList<KCTWorldBlockInfo>();
            workload = new Workload();
        }
        
        public void add(KCTWorldBlockInfo block) {
            if (blockChunk.size() > workChunkSize) {
                workload.add(new WorkChunk(new LinkedList<KCTWorldBlockInfo>(blockChunk), senderName, jobNum, chunkNum, workChunkSize));
                log.info("Adding to queue: " + blockChunk.peek().getLoc());
                blockChunk.clear();
                chunkNum++;
            }
            
            blockChunk.add(block);
        }
        
        public void addRest() {
            workload.add(new WorkChunk(new LinkedList<KCTWorldBlockInfo>(blockChunk), senderName, jobNum, chunkNum, workChunkSize));
            blockChunk.clear();
            chunkNum++;
        }
        
        public Workload getWorkload() {
            return this.workload;
        }
    }
    
    public SpongeTurtle(Logger logger) {
        this.log = logger;
    }

    public void setLoc(Vector3i startLocation) {
        this.curLoc = startLocation;
    }

    public void setTurtleDirection(TurtleDirection d) {
        this.dir = d;
    }

    public void setWorld(World w) {
        this.world = w;
    }

    public void setScript(KCTScript script) {
        this.script = script;
    }

    public void setSenderName(String name) {
        this.senderName = name;
    }
    
    public void setWorkChunkSize(int size) {
        this.workChunkSize = size;
    }
    
    public void setJobNum(int jobNum) {
        this.jobNum = jobNum;
    }

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
            
            workChunkManager.add(new KCTWorldBlockInfo(curLoc, block, world.getBlock(curLoc)));
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
        } else if (commandName.equals(KCTCommand.SETBLOCK)) {
            String blockName = m.get(KCTCommand.BLOCKTYPE).toString();
            block = KCTBlockTypesBuilder.getBlockState(KCTBlockTypes.valueOf(blockName));
        }
    }
    
    public Workload executeScript(KCTScript script) {
        workChunkManager = new WorkChunkManager();
        
        for (KCTCommand c : script.getCommands()) {
            try {
                executeCommand(c);
            } catch (TurtleCommandException e) {
                log.info("Unable to execute Turtle script:" + script.getScriptName());
                return null;
            }
        }
        
        workChunkManager.addRest();
        
        return workChunkManager.getWorkload(); 
    }
    
    public Workload executeScript() {
        return executeScript(this.script);
    }
    
    public Workload getWorkload() {
        return workChunkManager.getWorkload();
    }
}
