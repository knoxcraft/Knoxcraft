package edu.knox.minecraft.serverturtle;

import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;

public class BlockRecord {
    private Block block;
    private World world;
    
    /**
     * Constructor
     * 
     * @param type
     * @param pos
     */
    public BlockRecord(Block block, World world)  {
        this.block = block;
        this.world = world;
    } 
    
    /**
     * Return this block to this state.
     */
    public void revert()  {
        world.setBlockAt(block.getPosition(), block);
        
    }
    
    @Override
    public String toString()  {
        return "" + block;
    }
}