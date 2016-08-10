package org.knoxcraft.turtle3d;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.world.World;

public class WorkThread extends Thread {
    
    private WorkMap work;
    private boolean done=false;
    private World world;
    private long sleepTime=200;
    private SpongeExecutorService minecraftSyncExecutor;
    private Logger log;
    
    public WorkThread(WorkMap work, World world, SpongeExecutorService minecraftSyncExecutor, Logger log) {
        this.work = work;
        this.world = world;
        this.minecraftSyncExecutor = minecraftSyncExecutor;
        this.log = log;
    }
    
    public void shutdown() {
        this.done=true;
    }
    
    public void run() {
        while (!done) {
//            log.info("waiting for some work...");
            Queue<KCTWorldBlockInfo> queue = work.getWork();
//            log.info("Doing some work.");

            minecraftSyncExecutor.submit(new Runnable() {
                public void run() {
                    while (queue != null && !queue.isEmpty()) {
                        KCTWorldBlockInfo block = queue.poll();
                        world.setBlock(block.getLoc(), block.getNewBlock());
                        
//                        log.info("Setting block at: " + block.getLoc());
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
