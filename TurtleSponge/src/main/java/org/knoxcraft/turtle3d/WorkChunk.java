package org.knoxcraft.turtle3d;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Class that holds the chunk of work to be done and the metadata about the work.
 * The only class that builds these workChunks is the inner class WorkChunkManager in SpongeTurtle. 
 * @author kakoijohn
 *
 */
public class WorkChunk {
    private String userName;
    private String jobName;
    private int jobNum;
    private int workChunkNum;
    private int workChunkSize;
    
    private boolean isUndoScript;
    
    private Queue<KCTWorldBlockInfo> blockChunk;
    
    /**
     * Constructor
     * @param blockChunk queue of KCTWorldBlockInfo blocks
     * @param userName 
     * @param jobNum
     * @param workChunkNum
     * @param workChunkSize
     */
    public WorkChunk(Queue<KCTWorldBlockInfo> blockChunk, String jobName, String userName, int jobNum, int workChunkNum, int workChunkSize) {
        this.blockChunk = blockChunk;
        this.jobName = jobName;
        this.userName = userName;
        this.jobNum = jobNum;
        this.workChunkNum = workChunkNum;
        this.workChunkSize = workChunkSize;
    }
    
    /**
     * Copy Constructor used to create a clone of the data structure and make sure that the queue is properly copied.
     * @param workChunk
     */
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
    
    public String getJobName() {
        return this.jobName;
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
    
    /**
     * @return true if the flag isUndoScript is true.
     */
    public boolean isUndoScript() {
        return isUndoScript;
    }
    
    /**
     * @param state sets the state of isUndoScript to the parameter. 
     */
    public void setUndoScript(boolean state) {
        this.isUndoScript = state;
    }
}
