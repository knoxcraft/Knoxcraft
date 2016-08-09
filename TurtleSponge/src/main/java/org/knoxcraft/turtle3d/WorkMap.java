package org.knoxcraft.turtle3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

public class WorkMap {
    
    private Map<String, Integer> userWork = new HashMap<String, Integer>();
    private ArrayList<Workload> workList = new ArrayList<Workload>();
    private Map<String, Stack<Workload>> workArchive = new HashMap<String, Stack<Workload>>();
    private int nextIndex=0;
    private int startIndex=0;
    private Logger log;
    
    public WorkMap(Logger log) {
        this.log = log;
    }
    
    public synchronized void addWork(String playerName, Workload workload) {
        log.info("Work is being added...");
        if (!userWork.containsKey(playerName)) {
            userWork.put(playerName, nextIndex);
            
            workList.add(new Workload());
            workArchive.put(playerName, new Stack<Workload>());
            
            nextIndex++;
        }
        int index=userWork.get(playerName);
        
        workList.get(index).addAll(workload);
        
        workArchive.get(playerName).add((new Workload(workload)).setAsUndoWork());
        
        this.notifyAll();
    }
    
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
                log.info("Undo Work Size: " + undoWork.getWorkChunks().peek().getBlockChunk().size());
                int index = userWork.get(playerName);
                workList.get(index).addAll(undoWork);
            }
        }
        
        this.notifyAll();
    }
    
    public synchronized WorkChunk getWork() {
        while (true) {
            for (int i=0; i<workList.size(); i++) {
//                log.info("Getting some work.");
                int index=(i+startIndex) % workList.size();
                WorkChunk workChunk = workList.get(index).pollWork();
                
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
