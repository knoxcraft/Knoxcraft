package org.knoxcraft.turtle3d;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.knoxcraft.serverturtle.SpongeTurtle;
import org.spongepowered.api.scheduler.SpongeExecutorService;

public class KCTJobQueue {
	private Queue<SpongeTurtle> queue;
	private HashMap<String, Stack<KCTUndoScript>> undoBuffer; // PlayerName->buffer
	
	private SpongeExecutorService minecraftSyncExecutor;
	private SpongeExecutorService minecraftAsyncExecutor;
	
	public KCTJobQueue(SpongeExecutorService minecraftSyncExecutor, SpongeExecutorService minecraftAsyncExecutor) {
		queue = new LinkedList<SpongeTurtle>();
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
			
            minecraftAsyncExecutor.execute(new Runnable() {
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