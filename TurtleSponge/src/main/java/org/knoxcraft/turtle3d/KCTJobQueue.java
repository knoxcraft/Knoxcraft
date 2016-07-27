package org.knoxcraft.turtle3d;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.knoxcraft.serverturtle.SpongeTurtle;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;

public class KCTJobQueue {
	private Queue<SpongeTurtle> queue;
	private HashMap<String, Stack<KCTUndoScript>> undoBuffer; // PlayerName->buffer
	
	private SpongeExecutorService minecraftSyncExecutor;
	private SpongeExecutorService minecraftAsyncExecutor;
	
	public KCTJobQueue(SpongeExecutorService minecraftSyncExecutor, SpongeExecutorService minecraftAsyncExecutor) {
		queue = new LinkedList<SpongeTurtle>();
		undoBuffer = new HashMap<String, Stack<KCTUndoScript>>();
		this.minecraftSyncExecutor = minecraftSyncExecutor;
		this.minecraftAsyncExecutor = minecraftAsyncExecutor;
	}
	
	public void add(SpongeTurtle job) {
		queue.add(job);
		spongeExecuteQueuedJobs();
	}
	
	public void undoScript(CommandSource src, int numUndo) {
	    String senderName = src.getName().toLowerCase();
	    
	    if (!undoBuffer.containsKey(senderName)) {
            src.sendMessage(Text.of("You have not executed any scripts to undo!"));
        } else { // buffer exists
            // get buffer
            Stack<KCTUndoScript> undoUserScripts = undoBuffer.get(senderName);

            if (undoUserScripts == null) { // buffer empty
                src.sendMessage(Text.of("There were no scripts invoked by the player!"));
            } else {
                for (int i = 0; i < numUndo; i++) {
                    try {
                        KCTUndoScript undoScript = undoUserScripts.pop();
                        
                        minecraftAsyncExecutor.submit(new Runnable() {
                            public void run() {
                                undoScript.executeUndo(minecraftSyncExecutor);
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
	
	private void spongeExecuteQueuedJobs() {
		while (!queue.isEmpty()) {
			SpongeTurtle job = queue.poll();
			
            minecraftAsyncExecutor.submit(new Runnable() {
                public void run() {
                    job.executeScript(minecraftSyncExecutor);
                    if (!undoBuffer.containsKey(job.getSenderName()))
                        undoBuffer.put(job.getSenderName(), new Stack<KCTUndoScript>());
                    undoBuffer.get(job.getSenderName()).add(job.getUndoScript());
                }
            });
            
		}
	}
	
	public void shutdownExecutor() {
		minecraftSyncExecutor.shutdown();
		minecraftAsyncExecutor.shutdown();
	}
}