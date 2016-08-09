package org.knoxcraft.turtle3d;

import java.util.LinkedList;
import java.util.Queue;

public class WorkChunk {
    private String userName;
    private int jobNum;
    private int workChunkNum;
    private int workChunkSize;
    
    private boolean isUndoScript;
    
    private Queue<KCTWorldBlockInfo> blockChunk;
    
    public WorkChunk(Queue<KCTWorldBlockInfo> blockChunk, String userName, int jobNum, int workChunkNum, int workChunkSize) {
        this.blockChunk = blockChunk;
        this.userName = userName;
        this.jobNum = jobNum;
        this.workChunkNum = workChunkNum;
        this.workChunkSize = workChunkSize;
    }
    
    public WorkChunk(WorkChunk workChunk) {
        this.blockChunk = new LinkedList<KCTWorldBlockInfo>(workChunk.blockChunk);
        this.userName = workChunk.userName;
        this.jobNum = workChunk.jobNum;
        this.workChunkNum = workChunk.workChunkNum;
        this.workChunkSize = workChunk.workChunkSize;
    }
    
    public Queue<KCTWorldBlockInfo> getBlockChunk() {
        return this.blockChunk;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public int getJobNum() {
        return this.jobNum;
    }
    
    public int getWorkChunkNum() {
        return this.workChunkNum;
    }
    
    public int getWorkChunkMaxSize() {
        return this.workChunkSize;
    }
    
    public boolean isUndoScript() {
        return isUndoScript;
    }
    
    public void setUndoScript(boolean state) {
        this.isUndoScript = state;
    }
}
