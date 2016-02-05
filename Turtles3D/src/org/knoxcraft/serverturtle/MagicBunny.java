package org.knoxcraft.serverturtle;

import net.canarymod.api.entity.living.LivingBase;
import net.canarymod.api.entity.living.animal.CanaryRabbit;
import net.canarymod.api.entity.throwable.EntityThrowable;
import net.minecraft.entity.passive.EntityRabbit;

class MagicBunny extends CanaryRabbit implements EntityThrowable {
    public MagicBunny(CanaryRabbit rabbit){
        super((EntityRabbit)rabbit.getHandle());
        // remove the AI
        TurtlePlugin.removeAITasks(this);
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