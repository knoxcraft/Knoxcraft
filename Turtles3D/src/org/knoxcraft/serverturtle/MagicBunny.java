package org.knoxcraft.serverturtle;

import net.canarymod.api.ai.AIBase;
import net.canarymod.api.ai.AIManager;
import net.canarymod.api.entity.living.LivingBase;
import net.canarymod.api.entity.living.animal.CanaryRabbit;
import net.canarymod.api.entity.throwable.EntityThrowable;
import net.canarymod.logger.Logman;
import net.minecraft.entity.passive.EntityRabbit;

class MagicBunny extends CanaryRabbit implements EntityThrowable {
    private Logman logger;
    
    public MagicBunny(CanaryRabbit rabbit, Logman logger){
        super((EntityRabbit)rabbit.getHandle());
        this.logger=logger;
        // remove the AI
        flushAITasks();
    }
    
    public void flushAITasks(AIBase taskToKeep) {
        AIManager aiman=getAITaskManager();
        boolean done=false;
        while (!done) {
            AIBase task=aiman.getTask(AIBase.class);
            if (task==null) {
                break;
            }
            // Don't remove a certain kind of task
            if (taskToKeep !=null && task.getClass().equals(taskToKeep.getClass())) {
                aiman.addTask(Integer.MAX_VALUE, task);
            }
            
            logger.info("removing task with class %s\n", task.getClass());
            //done=!aiman.removeTask(AIBase.class);
            //System.out.println("removing task!");
        }
    }
    
    public void flushAITasks() {
        flushAITasks(null);
    }

    @Override
    public void setProjectileHeading(double motionX, double motionY,
        double motionZ, float rotationYaw, float rotationPitch)
    {
        // TODO Auto-generated method stub
        
    }
    @Override
    public LivingBase getThrower() {
        // TODO Auto-generated method stub
        return this;
    }
    @Override
    public float getGravity() {
        
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public void setGravity(float velocity) {
        // TODO Auto-generated method stub
        
    }
    
    
}