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
        //world.setBlock(block);  //does this work better?
        
    }  
    
    /**
     * Get this BlockRecord's block.
     * 
     * @return the block
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Get this BlockRecord's world.
     * 
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    @Override
    public String toString()  {
        return "" + block;
    }
    
    @Override
    public boolean equals(Object other)  {
        if (other instanceof BlockRecord)  {
            BlockRecord br = (BlockRecord)other;        
            //equal if in same posiiton in same world
            return (this.block.getPosition().equals(br.getBlock().getPosition())) &&
                    (this.world.equals(br.getWorld()));
        }
        return false;
    }
}