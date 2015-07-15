package edu.knox.minecraft.serverturtle;

import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Position;

public class BlockRecord {
    private BlockType type;
    private Position pos;
    private World world;
    
    /**
     * Constructor
     * 
     * @param type
     * @param pos
     */
    public BlockRecord(BlockType type, Position pos, World world)  {
        this.type = type;
        this.pos = pos;
        this.world = world;
    }

    /**
     * @return the type
     */
    public BlockType getType() {
        return type;
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
    public void replace()  {
        world.setBlockAt(pos, type);
    }
}