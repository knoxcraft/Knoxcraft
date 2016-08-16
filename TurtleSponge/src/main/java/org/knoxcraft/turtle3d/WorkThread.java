package org.knoxcraft.turtle3d;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.knoxcraft.serverturtle.TurtlePlugin;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.plugin.PluginContainer;
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
    
    //Defaults changed at runtime automatically. Do not use this as a reference for what is actually here.
    //Go into config/knoxcraft.conf to change these values.
    private long sleepTime = 200;
    private int minBuildHeight = 3;
    private int maxBuildHeight = 256;
    
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
    public WorkThread(WorkMap work, World world, long sleepTime, int minBuildHeight, int maxBuildHeight, 
            SpongeExecutorService minecraftSyncExecutor, Logger log) {
        this.work = work;
        this.world = world;
        this.sleepTime = sleepTime;
        this.minBuildHeight = minBuildHeight;
        this.maxBuildHeight = maxBuildHeight;
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
    
    private static class CommandLineCause {
        private CommandLineCause(){
        }
    }
    private static final CommandLineCause COMMAND_LINE_CAUSE=new CommandLineCause();
    public static final String COMMAND_LINE="COMMANDLINE";
    
    public void run() {
        while (!done) {
            log.trace("waiting for some work...");
            WorkChunk workChunk = work.getWork();
            boolean isUndoScript = workChunk.isUndoScript();
            log.debug("Is undo script " + isUndoScript);
            Queue<KCTWorldBlockInfo> queue = workChunk.getBlockChunk();
            
            log.trace("Doing some work.");

            minecraftSyncExecutor.submit(new Runnable() {
                public void run() {
                    while (queue != null && !queue.isEmpty()) {
                        KCTWorldBlockInfo block = queue.poll();
                        
                        BlockState minecraftBlock;
                        
                        if (isUndoScript)
                            minecraftBlock = block.getOldBlock();
                        else
                            minecraftBlock = block.getNewBlock();
                        
                        if (block.getLoc().getY() < maxBuildHeight && block.getLoc().getY() > minBuildHeight)
                            try {
                                log.trace("Before World set block");
                                // The cause needs to have the plugin container as the root
                                // https://forums.spongepowered.org/t/root-of-setblock/11292/14
                                PluginContainer pluginContainer=Sponge.getPluginManager().getPlugin(TurtlePlugin.ID).get();
                                List<NamedCause> causeList=new LinkedList<>();
                                causeList.add(NamedCause.of(TurtlePlugin.ID, pluginContainer));
                                causeList.add(NamedCause.of(COMMAND_LINE, COMMAND_LINE_CAUSE));
                                
                                
                                Cause cause=Cause.builder().addAll(causeList).build();
                                world.setBlock(block.getLoc(), minecraftBlock, false, cause);
                                log.trace("After world set block");
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }
                        else {
                            log.debug("Player attempted to build above or below allowed height. " + block.getLoc());
                        }
                        log.trace("Setting block at: " + block.getNewBlock().getId() + " " + block.getLoc());
                    }
                }
            });
            
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // but should be safe to ignore, but log anyway
                log.warn("WorkThread woke up while sleeping. Not necessarily an error, but still a bit strange");
            }
            
        }
    }

}
