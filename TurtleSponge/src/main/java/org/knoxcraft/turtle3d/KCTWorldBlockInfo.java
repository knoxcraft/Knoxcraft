package org.knoxcraft.turtle3d;

import org.spongepowered.api.block.BlockState;

import com.flowpowered.math.vector.Vector3i;

public class KCTWorldBlockInfo {
	private Vector3i location;
	private BlockState newBlock;
	private BlockState curBlock;
	
	public KCTWorldBlockInfo(int x, int y, int z, BlockState newBlock, BlockState curBlock) {
	    location = new Vector3i(x, y, z);
		this.newBlock = newBlock;
		this.curBlock = curBlock;
	}
	
	public KCTWorldBlockInfo(Vector3i location, BlockState newBlock, BlockState curBlock) {
        this.location = location;
        this.newBlock = newBlock;
        this.curBlock = curBlock;
    }
	
	public Vector3i getLoc() {
		return location;
	}
	
	public BlockState getNewBlock() {
		return newBlock;
	}
	
	public BlockState getOldBlock() {
	    return curBlock;
	}
}