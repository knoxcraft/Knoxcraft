package org.knoxcraft.turtle3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;

public class WorkMap {
    
    private Map<String, Integer> work=new HashMap<String, Integer>();
    private ArrayList<Workload> list=new ArrayList<Workload>();
    private int nextIndex=0;
    private int startIndex=0;
    private Logger log;
    
    public WorkMap(Logger log) {
        this.log = log;
    }
    
    public synchronized void addWork(String playerName, Workload workload) {
        log.info("Work is being added...");
        if (!work.containsKey(playerName)) {
            work.put(playerName, nextIndex);
            list.add(new Workload());
            nextIndex++;
        }
        int index=work.get(playerName);
        list.get(index).addAll(workload);
        this.notifyAll();
    }
    
    public synchronized Queue<KCTWorldBlockInfo> getWork() {
        while (true) {
            for (int i=0; i<list.size(); i++) {
//                log.info("Getting some work.");
                int index=(i+startIndex) % list.size();
                WorkChunk workChunk = list.get(index).pollWork();
                
                if (workChunk == null)
                    continue;
                
                startIndex=(index+1)%list.size();
                log.info("returning work: " + workChunk.getUserName() + ", Job #: " + workChunk.getJobNum() + ", Chunk #: " + workChunk.getWorkChunkNum());
                log.info("Loc of first block: " + workChunk.getBlockChunk().peek().getLoc());
                return workChunk.getBlockChunk();
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
