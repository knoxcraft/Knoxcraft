package org.knoxcraft.turtle3d;

import java.util.Queue;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.world.World;

/**
 * WorkThread class is responsible for placing the blocks into the Minecraft world.
 * The job of the WorkThread is to take a queued job from the WorkMap asynchronously and 
 * place the actual blocks into the Minecraft world.
 * 
 * The run() method takes work from the WorkMap and does it as quickly as it can.
 * Essentially, the method gets work from WorkMap then in the Synced Executor, places the blocks into the world.
 * Then, the thread sleeps for a specified amount of time before getting more work. This is in oder to give
 * the main Minecraft thread some time to catch up and not cause it to lag behind in ticks from giving it too 
 * much work to do all at once.
 * @author kakoijohn
 *
 */
public class WorkThread extends Thread {
    
    private WorkMap work;
    private boolean done=false;
    private World world;
    private long sleepTime = 200;
    private SpongeExecutorService minecraftSyncExecutor;
    private Logger log;
    
    /**
     * Constructor
     * @param work This should be passed the same instance of the WorkMap class used in adding jobs.
     * @param world The current Minecraft world to place the blocks in.
     * @param sleepTime Time for the thread sleep after each work cycle.
     * @param minecraftSyncExecutor The Sync Executor is important for placing blocks in the world because
     * Minecraft is inherently single threaded. This means that if we are running a separate thread and want to
     * make changes to the world, we must first sync that thread with the main Minecraft thread in order to
     * be able to place blocks safely. The Sync Executor is taken from the TurtlePlugin from the line
     * Sponge.getScheduler().createSyncExecutor(this);
     * @param log
     */
    public WorkThread(WorkMap work, World world, long sleepTime, SpongeExecutorService minecraftSyncExecutor, Logger log) {
        this.work = work;
        this.world = world;
        this.sleepTime = sleepTime;
        this.minecraftSyncExecutor = minecraftSyncExecutor;
        this.log = log;
    }
    
    /**
     * In order to stop the run() method which is basically running an infinite while loop, you must call
     * this method in order to tell the run() method to finish and subsiquently terminate the thread.
     */
    public void shutdown() {
        this.done=true;
    }
    
    public void run() {
        while (!done) {
//            log.info("waiting for some work...");
            WorkChunk workChunk = work.getWork();
            boolean isUndoScript = workChunk.isUndoScript();
//            log.info("Is undo script " + isUndoScript);
            Queue<KCTWorldBlockInfo> queue = workChunk.getBlockChunk();
//            log.info("Doing some work.");

            minecraftSyncExecutor.submit(new Runnable() {
                public void run() {
                    while (queue != null && !queue.isEmpty()) {
                        KCTWorldBlockInfo block = queue.poll();
                        
                        if (isUndoScript)
                            world.setBlock(block.getLoc(), block.getOldBlock());
                        else
                            world.setBlock(block.getLoc(), block.getNewBlock());
                        
//                        log.info("Setting block at: " + block.getNewBlock().getId() + " " + block.getLoc());
                    }
                }
            });
            
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // TODO: log, but should be safe to ignore
            }
            
        }
    }

}
