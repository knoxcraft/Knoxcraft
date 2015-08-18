package org.knoxcraft.serverturtle;

import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;

public class BlockRecord {
    
    private Block block;
    private Block prevBlock;
    private World world;
    
    /**
     * Constructor
     * 
     * @param block
     * @param world
     */
    public BlockRecord(Block block, World world)  {
        this.block = block;
        this.world = world;
    } 
    
    /**
     * Return this block to this state.
     */
    public void revert()  {
        world.setBlockAt(block.getPosition(), prevBlock);
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
            //equal if in same position in same world
            return (this.block.getPosition().equals(br.getBlock().getPosition())) &&
                    (this.world.equals(br.getWorld()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((block.getPosition() == null) ? 0 : block.getPosition().hashCode());
        result = prime * result + ((world == null) ? 0 : world.hashCode());
        return result;
    }
}