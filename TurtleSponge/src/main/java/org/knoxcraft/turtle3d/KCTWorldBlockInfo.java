package org.knoxcraft.turtle3d;

import org.spongepowered.api.block.BlockState;

import com.flowpowered.math.vector.Vector3i;

/**
 * Class that contains the position in the Minecraft world, the new block to be placed in the world,
 * and the current block in that world.
 * @author kakoijohn
 *
 */
public class KCTWorldBlockInfo {
	private Vector3i location;
	private BlockState newBlock;
	private BlockState curBlock;
	
	/**
	 * Constructor
	 * @param location Location in the world
	 * @param newBlock New block to be placed.
	 * @param curBlock The current block that is in the world.
	 */
	public KCTWorldBlockInfo(Vector3i location, BlockState newBlock, BlockState curBlock) {
        this.location = location;
        this.newBlock = newBlock;
        this.curBlock = curBlock;
    }
	
	/**
     * Constructor
     * @param x X location in the world
     * @param y Y location in the world
     * @param z Z location in the world
     * @param newBlock New block to be placed.
     * @param curBlock The current block that is in the world.
     */
    public KCTWorldBlockInfo(int x, int y, int z, BlockState newBlock, BlockState curBlock) {
        this(new Vector3i(x, y, z), newBlock, curBlock);
    }
	
	/**
	 * @return the location in the world for the block to be placed.
	 */
	public Vector3i getLoc() {
		return location;
	}
	
	/**
	 * @return the new block
	 */
	public BlockState getNewBlock() {
		return newBlock;
	}
	
	/**
	 * @return the old block
	 */
	public BlockState getOldBlock() {
	    return curBlock;
	}
}