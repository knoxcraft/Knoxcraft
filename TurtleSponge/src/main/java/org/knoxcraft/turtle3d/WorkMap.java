package org.knoxcraft.turtle3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

/**
 * WorkMap is a synchronized class responsible for making sure the work to be added
 * is done in a thread safe way. The producer and consumer are on separate threads so this class is here
 * in order to make sure that we are not reading and writing at the same time to the workList.
 * @author kakoijohn
 *
 */
public class WorkMap {
    
    private Map<String, Integer> userWorkMap; 
    private ArrayList<Workload> workList;
    private Map<String, Stack<Workload>> workArchive;
    private int nextIndex=0;
    private int startIndex=0;
    private Logger log;
    
    /**
     * Constructor for WorkMap
     * @param log
     */
    public WorkMap(Logger log) {
        this.log = log;
        userWorkMap = new HashMap<String, Integer>();
        workList = new ArrayList<Workload>();
        workArchive = new HashMap<String, Stack<Workload>>();
    }
    
    /**
     * Adds work to the queue to be done. Each job is tied to a specific user and their queue.
     * If there is no user currently in the map, we must create a new slot in the map for them
     * and add the work to be done with a new instance of Workload.
     * Once we have a user, we add a job to the workList with the corresponding Workload.
     * We also are saving all of the jobs that have been done to a workArchive which is going to be used
     * in case the player would like to undo a job that they invoked.
     * @param playerName 
     * @param workload Workload is a class that holds the deconstructed turtle script with all of the
     * blocks to be placed and the world positions.
     */
    public synchronized void addWork(String playerName, Workload workload) {
        log.info("Work is being added...");
        if (!userWorkMap.containsKey(playerName)) {
            userWorkMap.put(playerName, nextIndex);
            
            workList.add(new Workload());
            workArchive.put(playerName, new Stack<Workload>());
            
            nextIndex++;
        }
        int index=userWorkMap.get(playerName);
        
        workList.get(index).addAll(workload);
        
        workArchive.get(playerName).add((new Workload(workload)).setAsUndoWork());
        
        this.notifyAll();
    }
    
    /**
     * Adds an undo script to the workList.
     * The undo script is taken from the workArchive of that player and transferred to the list.
     * If there are no scripts or the player has not invoked any, they are notified of the specific error.
     * @param src CommandSource is the class attached to whatever invoked the command in Minecraft.
     * @param numUndo The number of scripts to undo.
     */
    public synchronized void addUndo(CommandSource src, int numUndo) {
        String playerName = src.getName().toLowerCase();
        if (!workArchive.containsKey(playerName)) {
            src.sendMessage(Text.of("You haven't invoked any scripts yet!"));
        } else if (workArchive.get(playerName).size() == 0) {
            src.sendMessage(Text.of("You have no more scripts to undo!"));
        } else {
            for (int i = 0; i < numUndo; i++) {
                Workload undoWork = workArchive.get(src.getName().toLowerCase()).pop();
                if (undoWork == null) {
                    src.sendMessage(Text.of("You don't have enough scripts to undo!"));
                    src.sendMessage(Text.of("You attempted to invoke " + numUndo + " scripts. You only have " + i + "!"));
                    break;
                }
//                log.info("Undo Work Size: " + undoWork.getWorkChunks().peek().getBlockChunk().size());
                int index = userWorkMap.get(playerName);
                workList.get(index).addAll(undoWork);
            }
        }
        
        this.notifyAll();
    }
    
    /**
     * Cancels any job currently running for that user.
     * This method can be run in between getWork() calls to cancel the job midway through execution.
     * Canceling a job clears the entire work queue for that player.
     * In order to make sure that we do not have undo data that is being affected, we remove all of the
     * archived work that has not been executed yet. In effect, this means that any job that has not started
     * is removed from the archive and the section of a job that has not been created yet will be removed from
     * the archive. No work already done is removed from the archive and is still undo-able.
     * @param src CommandSource is the class attached to whatever invoked the command in Minecraft.
     */
    public synchronized void cancel(CommandSource src) {
        String playerName = src.getName().toLowerCase();
        int index = userWorkMap.get(playerName);
        Workload cancelWork = workList.get(index);
        if (cancelWork.hasWork()) {
            
            WorkChunk cancelChunk;
            int curJobNum = -1;
            
            while ((cancelChunk = cancelWork.pollFirst()) != null) {
                if (cancelChunk.getJobNum() != curJobNum) {
                    //if we are on a new job, we must remove the remaining work from the archive.
                    curJobNum = cancelChunk.getJobNum();
                    int workChunkNum = cancelChunk.getWorkChunkNum();
                    
                    for (Workload archiveWork : workArchive.get(playerName)) {
                        if (archiveWork.peekWork().getJobNum() == curJobNum && workChunkNum == 0) {
                            //if the build has not started, we just remove it completely
                            workArchive.get(playerName).pop();
                            break;
                        } else if (archiveWork.peekWork().getJobNum() == curJobNum) {
                            //if the build has started, we must remove the remaining unbuilt archive work.
//                            log.info("Chunk Num: " + workChunkNum + " Work Size: " + archiveWork.remainingWorkSize());
                            int remainingWork = archiveWork.remainingWorkSize();
                            for (int i = workChunkNum; i < remainingWork; i++)
                                archiveWork.popLast();
//                                log.info("Popping " + i + ": " + archiveWork.popLast().getWorkChunkNum());
                            break;
                        }
                    }
                }
            }
        } else {
            src.sendMessage(Text.of("You don't have any scripts currently running!"));
        }
        
        this.notifyAll();
    }
    
    /**
     * A blocking call that must wait for a notify by the other synchronized methods of addWork() addUndo() 
     * and cancel() to complete before it can take any work away from the list.
     * 
     * This method returns the latest chunk of work to be done. The next chunk is determined by iterating round robin
     * fashion through the list of users and polling the latest work in their queue. This is done in order to ensure
     * fairness and provide the same amount of work time by the WorkThread to each player to build their structures.
     * @return A WorkChunk is returned which is a queue of any configured size to be built by the WorkThread.
     * Once the method has been notified, it will return a WorkChunk which is a section of the next script to be
     * placed in the Minecraft world.
     */
    public synchronized WorkChunk getWork() {
        while (true) {
            for (int i=0; i<workList.size(); i++) {
//                log.info("Getting some work.");
                int index=(i+startIndex) % workList.size();
                WorkChunk workChunk = workList.get(index).pollFirst();
                
                if (workChunk == null)
                    continue;
                
                startIndex=(index+1)%workList.size();
//                log.info("returning work: " + workChunk.getUserName() + ", Job #: " + workChunk.getJobNum() + ", Chunk #: " + workChunk.getWorkChunkNum());
//                log.info("Location of first block: " + workChunk.getBlockChunk().peek().getLoc());
                return workChunk;
            }
            try {
//                log.info("Waiting...");
                this.wait();
//                log.info("Waking up!");
            } catch (InterruptedException e) {
                log.error("work thread interrupted");
            }
        }
        
    }
}
