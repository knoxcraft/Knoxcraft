package org.knoxcraft.serverturtle;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.knoxcraft.turtle3d.KCTCommand;
import org.knoxcraft.turtle3d.TurtleCommandException;

import net.canarymod.api.ai.AIBase;
import net.canarymod.logger.Logman;

public class KCTAITask implements AIBase
{
    private TheoreticalTurtle turtle;
    private Logman logger;
    private HashMap<String, Stack<Stack<BlockRecord>>> undoBuffers;
    
    public KCTAITask(TheoreticalTurtle turtle, HashMap<String, Stack<Stack<BlockRecord>>> undoBuffers)
    {
        this.turtle=turtle;
        this.logger=turtle.getLogger();
        this.undoBuffers=undoBuffers;
    }
    
    /* (non-Javadoc)
     * @see net.canarymod.api.ai.CanaryAIBase#shouldExecute()
     */
    @Override
    public boolean shouldExecute() {
        return true;
    }

    /* (non-Javadoc)
     * @see net.canarymod.api.ai.CanaryAIBase#continueExecuting()
     */
    @Override
    public boolean continueExecuting() {
        if (turtle.hasMoreCommand()) {
            return true;
        }
        turtle.destroy();
        try  {            
            //create buffer if doesn't exist
            if (!undoBuffers.containsKey(turtle.getPlayerName())) {  
                undoBuffers.put(turtle.getPlayerName(), new Stack<Stack<BlockRecord>>());
            }    
            //add to buffer
            undoBuffers.get(turtle.getPlayerName()).push(turtle.getOldBlocks());            
        }  catch (Exception e)  {
            turtle.sConsole("Failed to add to undo buffer!");
            logger.error("Faile to add to undo buffer", e);
        }
        return turtle.hasMoreCommand();
    }

    /* (non-Javadoc)
     * @see net.canarymod.api.ai.CanaryAIBase#isContinuous()
     */
    @Override
    public boolean isContinuous() {
        return true;
    }

    /* (non-Javadoc)
     * @see net.canarymod.api.ai.CanaryAIBase#startExecuting()
     */
    @Override
    public void startExecuting() {
        // TODO what do we do in here?
    }

    /* (non-Javadoc)
     * @see net.canarymod.api.ai.CanaryAIBase#resetTask()
     */
    @Override
    public void resetTask() {
        turtle.destroy();
    }

    /* (non-Javadoc)
     * @see net.canarymod.api.ai.CanaryAIBase#updateTask()
     */
    @Override
    public void updateTask() {
        if (turtle.hasMoreCommand()) {
            try {
                List<KCTCommand> result=turtle.executeNextCommands(1);
                for (KCTCommand c : result) {
                    logger.info("executing one command: "+c.getCommandName());
                }
                //Thread.sleep(100);
            } catch (TurtleCommandException e) {
                logger.error(e.getMessage());
                logger.error("Unable to execute Sprite program "+turtle.getScript().getScriptName());
                turtle.sConsole("Unable to execute Sprite program "+turtle.getScript().getScriptName());
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
            }
        }
        // flush all AI tasks but the current one
        turtle.getMagicBunny().flushAITasks(this);
    }
}
