package org.knoxcraft.turtle3d;

import java.util.LinkedList;
import java.util.Queue;

import org.spongepowered.api.entity.living.player.Player;

/**
 * Class that holds the chunk of work to be done and the metadata about the work.
 * The only class that builds these workChunks is the inner class WorkChunkManager in SpongeTurtle. 
 * @author kakoijohn
 *
 */
public class WorkChunk {
    private Player player;
    private String userName;
    private String jobName;
    private int jobNum;
    private int workChunkNum;
    private int workChunkSize;
    
    private boolean isUndoScript;
    
    private Queue<KCTWorldBlockInfo> blockChunk;
    
   
    /**
     * Constructor
     * @param blockChunk
     * @param jobName
     * @param player
     * @param jobNum
     * @param workChunkNum
     * @param workChunkSize
     */
    public WorkChunk(Queue<KCTWorldBlockInfo> blockChunk, String jobName, Player player, int jobNum, int workChunkNum, int workChunkSize) {
        this.blockChunk = blockChunk;
        this.jobName = jobName;
        this.player = player;
        this.userName = player.getName();
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
        this.jobName = workChunk.jobName;
        this.player = workChunk.player;
        this.userName = workChunk.userName;
        this.jobNum = workChunk.jobNum;
        this.workChunkNum = workChunk.workChunkNum;
        this.workChunkSize = workChunk.workChunkSize;
    }
    
    public Queue<KCTWorldBlockInfo> getBlockChunk() {
        return this.blockChunk;
    }
    
    public Player getPlayer() {
        return this.player;
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
    
    public int getQueueSize() {
        return blockChunk.size();
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
