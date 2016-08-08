package org.knoxcraft.turtle3d;

import java.util.LinkedList;
import java.util.Queue;

public class Workload {
    private Queue<WorkChunk> workChunks;
    
    public Workload() {
        workChunks = new LinkedList<WorkChunk>();
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
    
    public WorkChunk pollWork() {
        return workChunks.poll();
    }
    
    public Queue<WorkChunk> getWorkChunks() {
        return workChunks;
    }
}
