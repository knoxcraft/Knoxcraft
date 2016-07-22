package org.knoxcraft.turtle3d;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.knoxcraft.serverturtle.SpongeTurtle;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.scheduler.Task;

public class KCTJobQueue {
	private Queue<SpongeTurtle> queue;
	private HashMap<String, Stack<KCTUndoScript>> undoBuffer; // PlayerName->buffer
	
	SpongeExecutorService minecraftExecutor;
	
	public KCTJobQueue(SpongeExecutorService minecraftExecutor) {
		queue = new LinkedList<SpongeTurtle>();
		this.minecraftExecutor = minecraftExecutor;
	}
	
	public void add(SpongeTurtle job) {
		queue.add(job);
		this.executeQueuedJobs();
	}
	
	private void executeQueuedJobs() {
		while (!queue.isEmpty()) {
			SpongeTurtle job = queue.poll();
			
			minecraftExecutor.submit(
			    new Runnable() {
			    	public void run() {
				    	job.executeScript();
				    	if (!undoBuffer.containsKey(job.getSenderName()))
							undoBuffer.put(job.getSenderName(), new Stack<KCTUndoScript>());
						undoBuffer.get(job.getSenderName()).add(job.getUndoScript());
			    	}
			    }
			);
			
		}
	}
	
	public void shutdownExecutor() {
		minecraftExecutor.shutdown();
	}
}