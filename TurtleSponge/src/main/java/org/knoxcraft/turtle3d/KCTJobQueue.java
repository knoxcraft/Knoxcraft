package org.knoxcraft.turtle3d;

import org.knoxcraft.serverturtle.SpongeTurtle;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.world.World;

/**
 * KCTJobQueue is responsible for queue-ing Turtle scripts that the students have submitted using the /invoke
 * command in Minecraft. KCTJobQueue works directly with WorkMap to queue work in a thread safe way.
 * This class should be used at the top level plugin to add turtle scripts to be placed in the Minecraft world.
 * @author kakoijohn
 *
 */
public class KCTJobQueue {
    
    private Logger log;
    
    private WorkMap workMap; // PlayerName->buffer
    
    private SpongeExecutorService minecraftSyncExecutor;
    
    private WorkThread workThread;
    
    /**
     * This constructor spawns and starts the work thread.
     * @param minecraftSyncExecutor The Sync Executor is needed to place blocks in Minecraft from a separate thread.
     * Sync Executor syncs with the main Minecraft thread and world.setblock() methods can be called.
     * @param log
     * @param world
     * @param sleepTime
     */
    public KCTJobQueue(SpongeExecutorService minecraftSyncExecutor, 
            Logger log, World world, long sleepTime, int minBuildHeight, int maxBuildHeight) {
        this.log = log;

        this.minecraftSyncExecutor = minecraftSyncExecutor;
        
        workMap = new WorkMap(log);
        workThread = new WorkThread(workMap, world, sleepTime, minBuildHeight, maxBuildHeight, minecraftSyncExecutor, log);
        workThread.start();
    }
    
    /**
     * Adds work to the workMap
     * @param job A turtle job that has been executed is needed in order for this method to work.
     * Call turtle.executeScript() before passing the SpongeTurtle.
     */
    public void add(SpongeTurtle job) {
        workMap.addWork(job.getSenderName(), job.getWorkload());
    }
    
    /**
     * Adds undo work to the workMap
     * @param src CommandSource is the class attached to whatever invoked the command in Minecraft.
     * @param numUndo The number of scripts to undo.
     */
    public void undoScript(CommandSource src, int numUndo) {
        workMap.addUndo(src, numUndo);
    }
    
    /**
     * Tells the workMap to cancel the currently running script.
     * @param src CommandSource is the class attached to whatever invoked the command in Minecraft.
     */
    public void cancelScript(CommandSource src) {
        workMap.cancel(src);
    }
    
    /**
     * Clears all queues in WorkMap. 
     */
    public void killAll() {
        workMap.killAll();
    }
    
    /**
     * When the server has finished running and is in it's cleanup and shutdown phase, 
     * you must also stop the thread that this class has spawned at its construction. 
     */
    public void shutdown() {
        log.debug("Shutting down Sponge Executors and Threads.");
        workThread.shutdown();
        minecraftSyncExecutor.shutdown();
    }
}