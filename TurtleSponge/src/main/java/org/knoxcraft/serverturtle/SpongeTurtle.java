package org.knoxcraft.serverturtle;

import java.util.Map;
import java.util.Stack;

import org.knoxcraft.turtle3d.KCTBlockTypes;
import org.knoxcraft.turtle3d.KCTBlockTypesBuilder;
import org.knoxcraft.turtle3d.KCTCommand;
import org.knoxcraft.turtle3d.KCTScript;
import org.knoxcraft.turtle3d.KCTWorldBlockInfo;
import org.knoxcraft.turtle3d.TurtleCommandException;
import org.knoxcraft.turtle3d.TurtleDirection;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;

public class SpongeTurtle {

    @Inject
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
    private Vector3i startLoc;
    // player location:originPos
    private String senderName;
    private TurtleDirection dir;
    private TurtleDirection startDir;
    private World world;
    private BlockState block = KCTBlockTypesBuilder.getBlockState(KCTBlockTypes.STONE);

    private KCTScript script;
    private Stack<KCTWorldBlockInfo> undoStack;
    private boolean undo = false;
    
    private SpongeExecutorService minecraftSyncExecutor; 
    private boolean forceThreadSync = false;
    private Stack<KCTWorldBlockInfo> buildPool;
    private int buildPoolSize = 1000;

    public SpongeTurtle(Logger logger) {
        this.log = logger;
        buildPool = new Stack<KCTWorldBlockInfo>();
    }
    
    public SpongeTurtle() {
        buildPool = new Stack<KCTWorldBlockInfo>();
    }

    public void setLoc(Vector3i curLoc) {
        this.curLoc = curLoc;
        this.startLoc = curLoc;
    }

    public void setTurtleDirection(TurtleDirection d) {
        this.dir = d;
        this.startDir = d;
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
    
    private void setWorldBlock(int x, int y, int z, BlockState block) {
        buildPool.add(new KCTWorldBlockInfo(new Vector3i(x, y, z), block));
        
        if (buildPool.size() > buildPoolSize) {
            @SuppressWarnings("unchecked")
            Stack<KCTWorldBlockInfo> buildPoolCopy = (Stack<KCTWorldBlockInfo>) buildPool.clone();
            buildPool.clear();
            
            if (forceThreadSync) {
                minecraftSyncExecutor.submit(new Runnable() {
                    public void run() {
                        while (!buildPoolCopy.empty()) {
                            KCTWorldBlockInfo bi = buildPoolCopy.pop();
                            world.setBlock(bi.getLoc(), bi.getBlock());
                        }
                    }
                });
                
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                while (!buildPoolCopy.empty()) {
                    KCTWorldBlockInfo bi = buildPoolCopy.pop();
                    world.setBlock(bi.getLoc(), bi.getBlock());
                }
            }
        }
    }
    
    private void buildAndClearBlockPool() {
        if (forceThreadSync) {
            minecraftSyncExecutor.submit(new Runnable() {
                public void run() {
                    while (!buildPool.empty()) {
                        KCTWorldBlockInfo bi = buildPool.pop();
                        world.setBlock(bi.getLoc(), bi.getBlock());
                    }
                }
            });
        } else {
            while (!buildPool.empty()) {
                KCTWorldBlockInfo bi = buildPool.pop();
                world.setBlock(bi.getLoc(), bi.getBlock());
            }
        }
    }
    
    private void setWorldBlock(Vector3i location, BlockState block) {
        setWorldBlock(location.getX(), location.getY(), location.getZ(), block);
    }

    private void move(int distance, TurtleDirection turtleDirection, Stack<KCTWorldBlockInfo> undoStack) {

        int x = curLoc.getX();
        int y = curLoc.getY();
        int z = curLoc.getZ();

        //		log.info("current location: " + x + ", " + y + ", " + z);

        for (int i = 1; i <= distance; i++) {
            /*
             * Sponge does this backwards for north, sponge does it as (x, y,
             * z-1)
             * 
             * when we thought we figured it out to be (x, y, z+1)
             */
            if (turtleDirection == TurtleDirection.NORTH) {
                curLoc = curLoc.add(0, 0, 1);
                if (undo == false) {
                    undoStack.add(new KCTWorldBlockInfo(x, y, z + i, world.getBlock(x, y, z + i)));
                    setWorldBlock(x, y, z + i, block);
                } else {
                    KCTWorldBlockInfo undoInfo = undoStack.pop();
                    setWorldBlock(undoInfo.getLoc(), undoInfo.getBlock());
                }
            } else if (turtleDirection == TurtleDirection.NORTHEAST) {
                curLoc = curLoc.add(-1, 0, 1);
                if (undo == false) {
                    undoStack.add(new KCTWorldBlockInfo(x - i, y, z + i, world.getBlock(x - i, y, z + i)));
                    setWorldBlock(x - i, y, z + i, block);
                } else {
                    KCTWorldBlockInfo undoInfo = undoStack.pop();
                    setWorldBlock(undoInfo.getLoc(), undoInfo.getBlock());
                }
            } else if (turtleDirection == TurtleDirection.EAST) {
                curLoc = curLoc.add(-1, 0, 0);
                if (undo == false) {
                    undoStack.add(new KCTWorldBlockInfo(x - i, y, z, world.getBlock(x - i, y, z)));
                    setWorldBlock(x - i, y, z, block);
                } else {
                    KCTWorldBlockInfo undoInfo = undoStack.pop();
                    setWorldBlock(undoInfo.getLoc(), undoInfo.getBlock());
                }
            } else if (turtleDirection == TurtleDirection.SOUTHEAST) {
                curLoc = curLoc.add(-1, 0, -1);
                if (undo == false) {
                    undoStack.add(new KCTWorldBlockInfo(x - i, y, z - i, world.getBlock(x - i, y, z - i)));
                    setWorldBlock(x - i, y, z - i, block);
                } else {
                    KCTWorldBlockInfo undoInfo = undoStack.pop();
                    setWorldBlock(undoInfo.getLoc(), undoInfo.getBlock());
                }
            } else if (turtleDirection == TurtleDirection.SOUTH) {
                curLoc = curLoc.add(0, 0, -1);
                if (undo == false) {
                    undoStack.add(new KCTWorldBlockInfo(x, y, z - i, world.getBlock(x, y, z - i)));
                    setWorldBlock(x, y, z - i, block);
                } else {
                    KCTWorldBlockInfo undoInfo = undoStack.pop();
                    setWorldBlock(undoInfo.getLoc(), undoInfo.getBlock());
                }
            } else if (turtleDirection == TurtleDirection.SOUTHWEST) {
                curLoc = curLoc.add(1, 0, -1);
                if (undo == false) {
                    undoStack.add(new KCTWorldBlockInfo(x + i, y, z - i, world.getBlock(x + i, y, z - i)));
                    setWorldBlock(x + i, y, z - i, block);
                } else {
                    KCTWorldBlockInfo undoInfo = undoStack.pop();
                    setWorldBlock(undoInfo.getLoc(), undoInfo.getBlock());
                }
            } else if (turtleDirection == TurtleDirection.WEST) {
                curLoc = curLoc.add(1, 0, 0);
                if (undo == false) {
                    undoStack.add(new KCTWorldBlockInfo(x + i, y, z, world.getBlock(x + i, y, z)));
                    setWorldBlock(x + i, y, z, block);
                } else {
                    KCTWorldBlockInfo undoInfo = undoStack.pop();
                    setWorldBlock(undoInfo.getLoc(), undoInfo.getBlock());
                }
            } else if (turtleDirection == TurtleDirection.NORTHWEST) {
                curLoc = curLoc.add(1, 0, 1);
                if (undo == false) {
                    undoStack.add(new KCTWorldBlockInfo(x + i, y, z + i, world.getBlock(x + i, y, z + i)));
                    setWorldBlock(x + i, y, z + i, block);
                } else {
                    KCTWorldBlockInfo undoInfo = undoStack.pop();
                    setWorldBlock(undoInfo.getLoc(), undoInfo.getBlock());
                }
            } else if (turtleDirection == TurtleDirection.UP) {
                curLoc = curLoc.add(0, 1, 0);
                if (undo == false) {
                    undoStack.add(new KCTWorldBlockInfo(x, y + i, z, world.getBlock(x, y + i, z)));
                    setWorldBlock(x, y + i, z, block);
                } else {
                    KCTWorldBlockInfo undoInfo = undoStack.pop();
                    setWorldBlock(undoInfo.getLoc(), undoInfo.getBlock());
                }
            } else if (turtleDirection == TurtleDirection.DOWN) {
                curLoc = curLoc.add(0, -1, 0);
                if (undo == false) {
                    undoStack.add(new KCTWorldBlockInfo(x, y - i, z, world.getBlock(x, y - i, z)));
                    setWorldBlock(x, y - i, z, block);
                } else {
                    KCTWorldBlockInfo undoInfo = undoStack.pop();
                    setWorldBlock(undoInfo.getLoc(), undoInfo.getBlock());
                }
            } else {
                throw new RuntimeException("TurtleDirection invalid=" + turtleDirection);
            }
        }
    }
    
    private void executeCommand(KCTCommand c, Stack<KCTWorldBlockInfo> undoStack) throws TurtleCommandException {
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
            move(distance, dir, undoStack);
        } else if (commandName.equals(KCTCommand.BACKWARD)) {
            // go backward
            int distance;
            if (!m.containsKey(KCTCommand.DIST)) {
                distance = 1;
            } else {
                distance = toInt(m.get(KCTCommand.DIST));
            }
            move(distance, dir.flip(), undoStack);

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
            move(distance, dir.turn(false, 2), undoStack);
        } else if (commandName.equals(KCTCommand.LEFT)) {
            // strafe left
            int distance;
            if (!m.containsKey(KCTCommand.DIST)) {
                distance = 1;
            } else {
                distance = toInt(m.get(KCTCommand.DIST));
            }
            move(distance, dir.turn(true, 2), undoStack);
        } else if (commandName.equals(KCTCommand.UP)) {
            // go up
            int distance;
            if (!m.containsKey(KCTCommand.DIST)) {
                distance = 1;
            } else {
                distance = toInt(m.get(KCTCommand.DIST));
            }
            move(distance, TurtleDirection.UP, undoStack);

        } else if (commandName.equals(KCTCommand.DOWN)) {
            // go down
            int distance;
            if (!m.containsKey(KCTCommand.DIST)) {
                distance = 1;
            } else {
                distance = toInt(m.get(KCTCommand.DIST));
            }
            move(distance, TurtleDirection.DOWN, undoStack);
        } else if (commandName.equals(KCTCommand.SETBLOCK)) {
            String blockName = m.get(KCTCommand.BLOCKTYPE).toString();
            block = KCTBlockTypesBuilder.getBlockState(KCTBlockTypes.valueOf(blockName));
        }
    }
    
    public Stack<KCTWorldBlockInfo> executeScript(KCTScript script) {
        Stack<KCTWorldBlockInfo> undoStack = new Stack<KCTWorldBlockInfo>();
        undo = false;
        forceThreadSync = false;

        for (KCTCommand c : script.getCommands()) {
            try {
                executeCommand(c, undoStack);
            } catch (TurtleCommandException e) {
                log.info("Unable to execute Turtle script:" + script.getScriptName());
                return null;
            }
        }
        
        buildAndClearBlockPool();

        this.undoStack = undoStack;
        return undoStack;
    }

    public Stack<KCTWorldBlockInfo> executeScript() {
        return executeScript(this.script);
    }
    
    public Stack<KCTWorldBlockInfo> executeScript(KCTScript script, SpongeExecutorService minecraftSyncExecutor) {
        Stack<KCTWorldBlockInfo> undoStack = new Stack<KCTWorldBlockInfo>();
        undo = false;
        this.minecraftSyncExecutor = minecraftSyncExecutor;
        forceThreadSync = true;

        for (KCTCommand c : script.getCommands()) {
            try {
                executeCommand(c, undoStack);
            } catch (TurtleCommandException e) {
                log.info("Unable to execute Turtle script:" + script.getScriptName());
            }
        }
        
        buildAndClearBlockPool();
        
        this.undoStack = undoStack;
        return undoStack;
    }
    
    public Stack<KCTWorldBlockInfo> executeScript(SpongeExecutorService minecraftSyncExecutor) {
        return executeScript(this.script, minecraftSyncExecutor);
    }

    public void executeUndoStack(Stack<KCTWorldBlockInfo> undoStack) {
        undo = true;
        forceThreadSync = false;

        while (!undoStack.empty()) {
            KCTWorldBlockInfo worldBlock = undoStack.pop();
            setWorldBlock(worldBlock.getLoc(), worldBlock.getBlock());
        }
        
        buildAndClearBlockPool();
    }

    public void executeUndoStack(Stack<KCTWorldBlockInfo> undoStack, SpongeExecutorService minecraftSyncExecutor) {
        undo = true;
        forceThreadSync = true;
        
        while (!undoStack.empty()) {
            KCTWorldBlockInfo worldBlock = undoStack.pop();
            setWorldBlock(worldBlock.getLoc(), worldBlock.getBlock());
        }
        
        buildAndClearBlockPool();
    }
    
    public Stack<KCTWorldBlockInfo> getUndoStack() {
        return undoStack;
    }
}
