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
//        String senderName = src.getName().toLowerCase();
//        if (src instanceof Player) {
//            undoWorkerTurtle.setWorld(((Player) src).getWorld());
//            
//            if (!userBuildPools.containsKey(senderName)) {
//                src.sendMessage(Text.of("You have not executed any scripts to undo!"));
//            } else { // buffer exists
//                // get buffer
//                Stack<Stack<KCTWorldBlockInfo>> undoUserScripts = userBuildPools.get(senderName);
//
//                if (undoUserScripts == null) { // buffer empty
//                    src.sendMessage(Text.of("There were no scripts invoked by the player!"));
//                } else {
//                    for (int i = 0; i < numUndo; i++) {
//                        try {
//                            Stack<KCTWorldBlockInfo> undoJobStack = undoUserScripts.pop();
//                            minecraftAsyncExecutor.submit(new Runnable() {
//                                public void run() {
////                                    undoWorkerTurtle.executeUndoStack(undoJobStack, minecraftSyncExecutor);
//                                }
//                            });
//                        } catch (EmptyStackException e) {
//                            src.sendMessage(Text.of("There are no more scripts to undo!"));
//                            break;
//                        }   
//                    }
//                }
//            }
//        }
    }
    
    public void shutdownExecutor() {
        log.debug("Shutting down Sponge Executors and Threads.");
        minecraftSyncExecutor.shutdown();
        minecraftAsyncExecutor.shutdown();
    }
}