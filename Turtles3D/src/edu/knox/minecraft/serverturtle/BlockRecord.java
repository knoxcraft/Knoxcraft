package edu.knox.minecraft.serverturtle;

import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.position.Position;

public class BlockRecord {
    private Block block;
    private Position pos;
    private World world;
    
    /**
     * Constructor
     * 
     * @param type
     * @param pos
     */
    public BlockRecord(Block block, Position pos, World world)  {
        this.block = block;
        this.pos = pos;
        this.world = world;
    }

    /**
     * @return the type
     */
    public Block getBlock() {
        return block;
    }

    /**
     * @return the pos
     */
    public Position getPos() {
        return pos;
    }    
    
    /**
     * Return this block to this state.
     */
    public void revert()  {
        world.setBlockAt(pos, block);
    }
}