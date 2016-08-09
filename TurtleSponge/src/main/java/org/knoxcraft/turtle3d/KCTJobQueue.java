package org.knoxcraft.turtle3d;

import org.knoxcraft.serverturtle.SpongeTurtle;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.world.World;

public class KCTJobQueue {
    
    private Logger log;
    
    private WorkMap workMap; // PlayerName->buffer
    
    private SpongeExecutorService minecraftSyncExecutor;
    private SpongeExecutorService minecraftAsyncExecutor;
    
    private WorkThread workThread;
    
    public KCTJobQueue(SpongeExecutorService minecraftSyncExecutor, SpongeExecutorService minecraftAsyncExecutor, Logger log, World world) {
        this.log = log;
        
        this.minecraftSyncExecutor = minecraftSyncExecutor;
        this.minecraftAsyncExecutor = minecraftAsyncExecutor;
        
        workMap = new WorkMap(log);
        workThread = new WorkThread(workMap, world, minecraftSyncExecutor, log);
        workThread.start();
    }
    
    public void add(SpongeTurtle job) {
        workMap.addWork(job.getSenderName(), job.getWorkload());
    }
    
    public void undoScript(CommandSource src, int numUndo) {
        workMap.addUndo(src, numUndo);
    }
    
    public void shutdownExecutor() {
        log.debug("Shutting down Sponge Executors and Threads.");
        workThread.shutdown();
        minecraftSyncExecutor.shutdown();
        minecraftAsyncExecutor.shutdown();
    }
}