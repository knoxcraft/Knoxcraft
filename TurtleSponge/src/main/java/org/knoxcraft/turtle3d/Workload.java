package org.knoxcraft.turtle3d;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class is an abstraction of how block queues are stored before placing them into the world.
 * The executed turtle scripts need to be put into this format and the WorkMap uses this class tied to a
 * specific user as the work queue for that player.
 * @author kakoijohn
 *
 */
public class Workload {
    private LinkedList<WorkChunk> workChunks;
    
    /**
     * Constructor
     * creates a new queue of workChunks. The workChunks is a queue of block pools each its own queue of blocks
     * that is a size specified to the SpongeTurtle before executing its script.  
     */
    public Workload() {
        workChunks = new LinkedList<WorkChunk>();
    }
    
    /**
     * Copy Constructor
     * @param workload In order to make a secure copy of all of the data inside of Workload, you must call this
     * constructor and make a new instance of it. The constrcutor also makes a new instance of all of its queues.
     */
    public Workload(Workload workload) {
        LinkedList<WorkChunk> workChunksCopy = new LinkedList<WorkChunk>();
        for (WorkChunk chunk : workload.workChunks)
            workChunksCopy.add(new WorkChunk(chunk));
        this.workChunks = workChunksCopy;
    }
    
    /**
     * Adds the entire queue of workChunks to the master workChunks Queue.
     * @param workChunks a Queue of type WorkChunk.
     */
    public void addAll(Queue<WorkChunk> workChunks) {
        workChunks.addAll(workChunks);
    }
    
    /**
     * Adds the entire workload to this workload. This method is useful for adding more work for a specific player.
     * More work is created by the execution of the turtle script and can be added directly to this class.
     * @param workload 
     */
    public void addAll(Workload workload) {
        workChunks.addAll(workload.workChunks);
    }
    
    /**
     * Add a single WorkChunk to the queue.
     * @param workChunk
     */
    public void add(WorkChunk workChunk) {
        workChunks.add(workChunk);
    }
    
    /**
     * Retrieves and removes the head (first element) of this queue.
     * @return
     */
    public WorkChunk pollFirst() {
        return workChunks.poll();
    }
    
    /**
     * Retrieves and removes the last element of this queue, or returns null if this queue is empty.
     * @return
     */
    public WorkChunk popLast() {
        return workChunks.pollLast();
    }
    
    /**
     * Retrieves, but does not remove, the head (first element) of this queue.
     * @return
     */
    public WorkChunk peekWork() {
        return workChunks.peek();
    }
    
    /**
     * Returns false if there are no more workChunks in the queue.
     * @return
     */
    public boolean hasWork() {
        return !workChunks.isEmpty();
    }
    
    /**
     * Returns the size of the WorkChunk queue.
     * @return
     */
    public int remainingWorkSize() {
        return workChunks.size();
    }
    
    /**
     * Returns the instance of the WorkChunk Queue. There is most likely no need to directly interact with this
     * queue unless you wanted to manipulate the data structure yourself. Otherwise, there are other method calls
     * that are in place to do the work for you. 
     * @return
     */
    public Queue<WorkChunk> getWorkChunks() {
        return workChunks;
    }
    
    /**
     * In order to specify that the work is an undo script, we must set this boolean flag. All this method does is
     * set all of the workChunks isUndo flags to true. WorkThread reads if the script is an undo and places either the
     * new block or the old block depending on if its an undo script.
     * @return the instance of itself if there was any need to manipulate it further after setting the flag.
     */
    public Workload setAsUndoWork() {
        for (WorkChunk chunk : workChunks)
            chunk.setUndoScript(true);
        
        return this;
    }
}
