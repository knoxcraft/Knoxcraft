package org.knoxcraft.turtle3d;

import java.util.LinkedList;
import java.util.Queue;

public class Workload {
    private LinkedList<WorkChunk> workChunks;
    
    public Workload() {
        workChunks = new LinkedList<WorkChunk>();
    }
    
    public Workload(Workload workload) {
        LinkedList<WorkChunk> workChunksCopy = new LinkedList<WorkChunk>();
        for (WorkChunk chunk : workload.workChunks)
            workChunksCopy.add(new WorkChunk(chunk));
        this.workChunks = workChunksCopy;
    }
    
    public void addAll(Queue<WorkChunk> workChunks) {
        workChunks.addAll(workChunks);
    }
    
    public void addAll(Workload workload) {
        workChunks.addAll(workload.workChunks);
    }
    
    public void add(WorkChunk workChunk) {
        workChunks.add(workChunk);
    }
    
    public WorkChunk pollFirst() {
        return workChunks.poll();
    }
    
    public WorkChunk popLast() {
        return workChunks.pollLast();
    }
    
    public WorkChunk peekWork() {
        return workChunks.peek();
    }
    
    public boolean hasWork() {
        return !workChunks.isEmpty();
    }
    
    public int remainingWorkSize() {
        return workChunks.size();
    }
    
    public Queue<WorkChunk> getWorkChunks() {
        return workChunks;
    }
    
    public Workload setAsUndoWork() {
        for (WorkChunk chunk : workChunks)
            chunk.setUndoScript(true);
        
        return this;
    }
}
