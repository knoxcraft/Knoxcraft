package org.knoxcraft.turtle3d;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.knoxcraft.serverturtle.SpongeTurtle;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;

public class KCTJobQueue {
    
    private Logger log;
    
    private Queue<SpongeTurtle> queue;
    private HashMap<String, Stack<Stack<KCTWorldBlockInfo>>> undoBuffer; // PlayerName->buffer
    
    private SpongeExecutorService minecraftSyncExecutor;
    private SpongeExecutorService minecraftAsyncExecutor;
    
    private SpongeTurtle undoWorkerTurtle;
    
    public KCTJobQueue(SpongeExecutorService minecraftSyncExecutor, SpongeExecutorService minecraftAsyncExecutor, Logger log) {
        this.log = log;
        
        queue = new LinkedList<SpongeTurtle>();
        undoBuffer = new HashMap<String, Stack<Stack<KCTWorldBlockInfo>>>();
        undoWorkerTurtle = new SpongeTurtle(log);
        this.minecraftSyncExecutor = minecraftSyncExecutor;
        this.minecraftAsyncExecutor = minecraftAsyncExecutor;
    }
    
    public void add(SpongeTurtle job) {
        queue.add(job);
        spongeExecuteQueuedJobs();
    }
    
    private void spongeExecuteQueuedJobs() {
        while (!queue.isEmpty()) {
            SpongeTurtle job = queue.poll();
            
            minecraftAsyncExecutor.submit(new Runnable() {
                public void run() {
                    job.executeScript(minecraftSyncExecutor);
                    if (!undoBuffer.containsKey(job.getSenderName()))
                        undoBuffer.put(job.getSenderName(), new Stack<Stack<KCTWorldBlockInfo>>());
                    undoBuffer.get(job.getSenderName()).add(job.getUndoStack());
                }
            });
            
        }
    }
    
    public void undoScript(CommandSource src, int numUndo) {
        String senderName = src.getName().toLowerCase();
        if (src instanceof Player) {
            undoWorkerTurtle.setWorld(((Player) src).getWorld());
            
            if (!undoBuffer.containsKey(senderName)) {
                src.sendMessage(Text.of("You have not executed any scripts to undo!"));
            } else { // buffer exists
                // get buffer
                Stack<Stack<KCTWorldBlockInfo>> undoUserScripts = undoBuffer.get(senderName);

                if (undoUserScripts == null) { // buffer empty
                    src.sendMessage(Text.of("There were no scripts invoked by the player!"));
                } else {
                    for (int i = 0; i < numUndo; i++) {
                        try {
                            Stack<KCTWorldBlockInfo> undoJobStack = undoUserScripts.pop();
                            minecraftAsyncExecutor.submit(new Runnable() {
                                public void run() {
                                    undoWorkerTurtle.executeUndoStack(undoJobStack, minecraftSyncExecutor);
                                }
                            });
                        } catch (EmptyStackException e) {
                            src.sendMessage(Text.of("There are no more scripts to undo!"));
                            break;
                        }   
                    }
                }
            }
        }
    }
    
    public void shutdownExecutor() {
        minecraftSyncExecutor.shutdown();
        minecraftAsyncExecutor.shutdown();
    }
}