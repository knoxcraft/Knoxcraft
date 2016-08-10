package org.knoxcraft.turtle3d;

import java.util.Queue;

public class WorkChunk {
    private String userName;
    private int jobNum;
    private int workChunkNum;
    private int workChunkSize;
    
    private Queue<KCTWorldBlockInfo> blockChunk;
    
    public WorkChunk(Queue<KCTWorldBlockInfo> blockChunk, String userName, int jobNum, int workChunkNum, int workChunkSize) {
        this.blockChunk = blockChunk;
        this.userName = userName;
        this.jobNum = jobNum;
        this.workChunkNum = workChunkNum;
        this.workChunkSize = workChunkSize;
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
    
    public int getWorkChunkSize() {
        return this.workChunkSize;
    }
}
